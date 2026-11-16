package ua.kneu.aviakvytok.excel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.kneu.aviakvytok.excel.dto.ImportResult;
import ua.kneu.aviakvytok.excel.dto.SaleDto;
import ua.kneu.aviakvytok.excel.dto.StatsDto;
import ua.kneu.aviakvytok.excel.repository.TicketSaleRepository;
import ua.kneu.aviakvytok.excel.service.ExcelService;
import ua.kneu.aviakvytok.excel.service.StatisticsService;

import java.util.List;
import java.util.Map;

/** REST API імпорту та аналітики. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImportController {

    private final ExcelService excelService;
    private final StatisticsService statisticsService;
    private final TicketSaleRepository repository;

    /** Приймає файл Excel (multipart/form-data, поле «file») і повертає підсумок імпорту. */
    @PostMapping(value = "/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult importExcel(@RequestParam("file") MultipartFile file) {
        return excelService.save(file);
    }

    /** Агреговані дані для графіків. */
    @GetMapping("/stats")
    public StatsDto stats() {
        return statisticsService.stats();
    }

    /** Десять останніх імпортованих записів. */
    @GetMapping("/sales/recent")
    public List<SaleDto> recent() {
        return statisticsService.recent();
    }

    /** Очищення таблиці (для повторної демонстрації імпорту). */
    @DeleteMapping("/sales")
    @Transactional
    public Map<String, Long> clear() {
        long count = repository.count();
        repository.deleteAllInBatch();
        return Map.of("deleted", count);
    }
}
