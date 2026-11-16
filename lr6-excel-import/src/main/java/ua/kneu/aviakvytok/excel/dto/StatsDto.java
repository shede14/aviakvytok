package ua.kneu.aviakvytok.excel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Агреговані дані для сторінки аналітики. */
public record StatsDto(
        long total,
        BigDecimal paidRevenue,
        long directions,
        LocalDateTime lastImportAt,
        String lastImportFile,
        List<ChartItem> byDate,
        List<ChartItem> byDirection,
        List<ChartItem> byFareClass,
        List<ChartItem> byStatus
) {
}
