package ua.kneu.aviakvytok.excel.service;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import ua.kneu.aviakvytok.excel.dto.ImportResult;
import ua.kneu.aviakvytok.excel.entity.FareClass;
import ua.kneu.aviakvytok.excel.entity.SaleStatus;
import ua.kneu.aviakvytok.excel.entity.TicketSale;
import ua.kneu.aviakvytok.excel.exception.ExcelImportException;
import ua.kneu.aviakvytok.excel.repository.TicketSaleRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

/** Модульні тести парсингу Excel (Apache POI) без бази даних: репозиторій замінено Mockito-заглушкою. */
class ExcelServiceTest {

    private final TicketSaleRepository repository = mock(TicketSaleRepository.class);
    private final ExcelService service = new ExcelService(repository);

    /** Створює .xlsx у пам'яті: заголовок + передані рядки (дата в колонці 4 — справжня дата Excel). */
    private static MockMultipartFile xlsx(String name, Object[]... rows) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Продажі");
            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd.mm.yyyy"));
            Row h = sheet.createRow(0);
            for (int i = 0; i < ExcelService.HEADERS.length; i++) {
                h.createCell(i).setCellValue(ExcelService.HEADERS[i]);
            }
            for (int r = 0; r < rows.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < rows[r].length; c++) {
                    Object v = rows[r][c];
                    if (v instanceof LocalDate d) {
                        row.createCell(c).setCellValue(d);
                        row.getCell(c).setCellStyle(dateStyle);
                    } else if (v instanceof Number n) {
                        row.createCell(c).setCellValue(n.doubleValue());
                    } else if (v != null) {
                        row.createCell(c).setCellValue(v.toString());
                    }
                }
            }
            wb.write(out);
            return new MockMultipartFile("file", name,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
        }
    }

    private static Object[] row(String code) {
        return new Object[]{code, "Іваненко Олена", "LO 379", "Київ-Варшава", LocalDate.of(2026, 11, 10), "Економ", "Оплачено", 3450.5};
    }

    @SuppressWarnings("unchecked")
    private List<TicketSale> saved() {
        ArgumentCaptor<List<TicketSale>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        return captor.getValue();
    }

    @Test
    void importsRowsSkippingHeader() throws Exception {
        when(repository.findExistingCodes(anyCollection())).thenReturn(Set.of());
        ImportResult r = service.save(xlsx("sales.xlsx", row("AB12CD"), row("EF34GH")));

        assertEquals(2, r.totalRows());
        assertEquals(2, r.imported());
        assertEquals(0, r.skipped());
        TicketSale s = saved().get(0);
        assertEquals("AB12CD", s.getBookingCode());
        assertEquals("LO379", s.getFlightNumber());               // пробіл прибрано
        assertEquals("Київ – Варшава", s.getDirection());         // дефіс нормалізовано
        assertEquals(LocalDate.of(2026, 11, 10), s.getSaleDate()); // дата Excel -> LocalDate
        assertEquals(FareClass.ECONOMY, s.getFareClass());
        assertEquals(SaleStatus.PAID, s.getStatus());
        assertEquals(new BigDecimal("3450.50"), s.getPrice());
        assertEquals("sales.xlsx", s.getImportFile());
    }

    @Test
    void invalidRowIsReportedWithExcelRowNumber() throws Exception {
        when(repository.findExistingCodes(anyCollection())).thenReturn(Set.of());
        Object[] bad = row("XY99ZZ");
        bad[5] = "Люкс";
        ImportResult r = service.save(xlsx("sales.xlsx", row("AB12CD"), bad));

        assertEquals(1, r.imported());
        assertEquals(1, r.skipped());
        assertEquals(3, r.errors().get(0).row());   // рядок 3 у Excel (1 — заголовок)
        assertTrue(r.errors().get(0).message().contains("Люкс"));
    }

    @Test
    void textDateAndTextPriceAreParsed() throws Exception {
        when(repository.findExistingCodes(anyCollection())).thenReturn(Set.of());
        Object[] r1 = row("AB12CD");
        r1[4] = "15.11.2026";
        r1[7] = "4 120,00";
        service.save(xlsx("sales.xlsx", r1));
        TicketSale s = saved().get(0);
        assertEquals(LocalDate.of(2026, 11, 15), s.getSaleDate());
        assertEquals(new BigDecimal("4120.00"), s.getPrice());
    }

    @Test
    void nonExistentDateIsRejected() throws Exception {
        when(repository.findExistingCodes(anyCollection())).thenReturn(Set.of());
        Object[] r1 = row("AB12CD");
        r1[4] = "31.11.2026";
        ImportResult r = service.save(xlsx("sales.xlsx", r1));
        assertEquals(0, r.imported());
        assertTrue(r.errors().get(0).message().contains("31.11.2026"));
    }

    @Test
    void duplicatesInFileAndInDatabaseAreSkipped() throws Exception {
        when(repository.findExistingCodes(anyCollection())).thenReturn(Set.of("EF34GH"));
        ImportResult r = service.save(xlsx("sales.xlsx", row("AB12CD"), row("AB12CD"), row("EF34GH")));

        assertEquals(1, r.imported());
        assertEquals(2, r.skipped());
        assertEquals(1, saved().size());
    }

    @Test
    void nonXlsxFileIsRejected() {
        MockMultipartFile csv = new MockMultipartFile("file", "sales.csv", "text/csv", "a;b".getBytes());
        assertThrows(ExcelImportException.class, () -> service.save(csv));
        verify(repository, never()).saveAll(any());
    }

    @Test
    void corruptedFileIsRejected() {
        MockMultipartFile broken = new MockMultipartFile("file", "sales.xlsx", "application/octet-stream", "not excel".getBytes());
        ExcelImportException e = assertThrows(ExcelImportException.class, () -> service.save(broken));
        assertTrue(e.getMessage().contains("не коректна книга Excel"));
    }

    @Test
    void wrongHeaderIsRejected() throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            wb.createSheet().createRow(0).createCell(0).setCellValue("Назва");
            wb.write(out);
            MockMultipartFile f = new MockMultipartFile("file", "x.xlsx", "application/octet-stream", out.toByteArray());
            ExcelImportException e = assertThrows(ExcelImportException.class, () -> service.save(f));
            assertTrue(e.getMessage().startsWith("Неправильна структура файлу"));
        }
    }
}
