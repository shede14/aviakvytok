package ua.kneu.aviakvytok.excel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.kneu.aviakvytok.excel.dto.ImportResult;
import ua.kneu.aviakvytok.excel.dto.ImportResult.RowError;
import ua.kneu.aviakvytok.excel.entity.FareClass;
import ua.kneu.aviakvytok.excel.entity.SaleStatus;
import ua.kneu.aviakvytok.excel.entity.TicketSale;
import ua.kneu.aviakvytok.excel.exception.ExcelImportException;
import ua.kneu.aviakvytok.excel.repository.TicketSaleRepository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Пакетний імпорт продажів квитків з Excel (.xlsx) за допомогою Apache POI.
 * <p>
 * Структура аркуша (перший рядок — заголовок):
 * Код бронювання | Пасажир | Рейс | Напрямок | Дата продажу | Клас | Статус | Ціна, грн
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    public static final String[] HEADERS = {
            "Код бронювання", "Пасажир", "Рейс", "Напрямок", "Дата продажу", "Клас", "Статус", "Ціна, грн"
    };
    private static final int MAX_ROWS = 5000;
    /** Суворий розбір дат: 31.11.2026 — помилка, а не «30.11.2026». */
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("d.M.uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE);

    private final TicketSaleRepository repository;

    /** Рядок, успішно перетворений на сутність (номер рядка потрібен для повідомлень). */
    private record Parsed(int row, TicketSale sale) {
    }

    @Transactional
    public ImportResult save(MultipartFile file) {
        long t0 = System.currentTimeMillis();
        String fileName = validate(file);

        List<Parsed> parsed = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();
        Set<String> codesInFile = new HashSet<>();
        int totalRows = 0;

        // 1. Відкриваємо потік даних завантаженого файлу і читаємо книгу XSSFWorkbook
        try (InputStream in = file.getInputStream(); Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            checkHeader(sheet.getRow(0), formatter);

            // 2. Ітеруємо по рядках, починаючи з другого (заголовок пропускаємо)
            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isBlank(row, formatter)) {
                    continue;
                }
                if (++totalRows > MAX_ROWS) {
                    throw new ExcelImportException("У файлі понад " + MAX_ROWS + " рядків — розбийте його на частини.");
                }
                int excelRow = row.getRowNum() + 1;
                try {
                    // 3. Створюємо об'єкт сутності на основі значень клітинок
                    TicketSale sale = toEntity(row, formatter);
                    if (!codesInFile.add(sale.getBookingCode())) {
                        throw new IllegalArgumentException("код бронювання " + sale.getBookingCode() + " повторюється у файлі");
                    }
                    sale.setImportFile(fileName);
                    parsed.add(new Parsed(excelRow, sale));
                } catch (IllegalArgumentException | IllegalStateException e) {
                    errors.add(new RowError(excelRow, e.getMessage()));
                }
            }
        } catch (ExcelImportException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            log.warn("Не вдалося прочитати файл {}: {}", fileName, e.toString());
            throw new ExcelImportException("Не вдалося прочитати файл «" + fileName + "»: це не коректна книга Excel (.xlsx).", e);
        }

        // 4. Пропускаємо записи, коди бронювання яких уже є в базі (повторний імпорт того самого файлу)
        Set<String> existing = codesInFile.isEmpty() ? Set.of() : repository.findExistingCodes(codesInFile);
        List<TicketSale> toSave = new ArrayList<>();
        for (Parsed p : parsed) {
            if (existing.contains(p.sale().getBookingCode())) {
                errors.add(new RowError(p.row(), "код бронювання " + p.sale().getBookingCode() + " вже є в базі — рядок пропущено"));
            } else {
                toSave.add(p.sale());
            }
        }

        // 5. Масове збереження в базу даних
        repository.saveAll(toSave);
        errors.sort(Comparator.comparingInt(RowError::row));

        long ms = System.currentTimeMillis() - t0;
        log.info("Імпорт {}: рядків {}, збережено {}, пропущено {}, {} мс", fileName, totalRows, toSave.size(), errors.size(), ms);
        return new ImportResult(fileName, totalRows, toSave.size(), errors.size(), errors, ms);
    }

    // ------------------------------------------------------------------

    private String validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExcelImportException("Файл не вибрано або він порожній.");
        }
        String name = file.getOriginalFilename() == null ? "file.xlsx" : file.getOriginalFilename();
        if (!name.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new ExcelImportException("Підтримуються лише файли Excel у форматі .xlsx (отримано «" + name + "»).");
        }
        return name;
    }

    private void checkHeader(Row header, DataFormatter f) {
        if (header == null || !text(header.getCell(0), f).toLowerCase(Locale.ROOT).startsWith("код")
                || header.getLastCellNum() < HEADERS.length) {
            throw new ExcelImportException("Неправильна структура файлу. Перший рядок має містити колонки: "
                    + String.join(" | ", HEADERS) + ". Скористайтеся шаблоном template.xlsx.");
        }
    }

    TicketSale toEntity(Row row, DataFormatter f) {
        TicketSale s = new TicketSale();
        s.setBookingCode(required(row, 0, f).toUpperCase(Locale.ROOT));
        s.setPassengerName(required(row, 1, f));
        s.setFlightNumber(required(row, 2, f).toUpperCase(Locale.ROOT).replace(" ", ""));
        s.setDirection(required(row, 3, f).replaceAll("\\s*[-–—]\\s*", " – "));
        s.setSaleDate(date(row.getCell(4), f));
        s.setFareClass(FareClass.parse(required(row, 5, f)));
        s.setStatus(SaleStatus.parse(required(row, 6, f)));
        s.setPrice(price(row.getCell(7), f));
        if (s.getBookingCode().length() > 10) {
            throw new IllegalArgumentException("код бронювання довший за 10 символів");
        }
        return s;
    }

    private static String text(Cell cell, DataFormatter f) {
        return cell == null ? "" : f.formatCellValue(cell).trim();
    }

    private static String required(Row row, int col, DataFormatter f) {
        String v = text(row.getCell(col), f);
        if (v.isEmpty()) {
            throw new IllegalArgumentException("не заповнено колонку «" + HEADERS[col] + "»");
        }
        return v;
    }

    private static LocalDate date(Cell cell, DataFormatter f) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            // дата в Excel зберігається як число (кількість днів від 1900 року)
            return DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate()
                    : DateUtil.getLocalDateTime(cell.getNumericCellValue()).toLocalDate();
        }
        String v = text(cell, f);
        if (v.isEmpty()) {
            throw new IllegalArgumentException("не заповнено колонку «Дата продажу»");
        }
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(v, fmt);
            } catch (DateTimeParseException ignored) {
                // пробуємо наступний формат
            }
        }
        throw new IllegalArgumentException("некоректна дата «" + v + "» (очікується ДД.ММ.РРРР)");
    }

    private static BigDecimal price(Cell cell, DataFormatter f) {
        BigDecimal value;
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            value = BigDecimal.valueOf(cell.getNumericCellValue());
        } else {
            String v = text(cell, f).replaceAll("[\\s\u00A0]", "").replace(',', '.');
            if (v.isEmpty()) {
                throw new IllegalArgumentException("не заповнено колонку «Ціна, грн»");
            }
            try {
                value = new BigDecimal(v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("некоректна ціна «" + v + "»");
            }
        }
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("ціна має бути більшою за нуль");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean isBlank(Row row, DataFormatter f) {
        for (int c = 0; c < HEADERS.length; c++) {
            if (!text(row.getCell(c), f).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
