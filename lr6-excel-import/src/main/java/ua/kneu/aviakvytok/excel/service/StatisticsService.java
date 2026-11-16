package ua.kneu.aviakvytok.excel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.excel.dto.ChartItem;
import ua.kneu.aviakvytok.excel.dto.SaleDto;
import ua.kneu.aviakvytok.excel.dto.StatsDto;
import ua.kneu.aviakvytok.excel.entity.FareClass;
import ua.kneu.aviakvytok.excel.entity.SaleStatus;
import ua.kneu.aviakvytok.excel.entity.TicketSale;
import ua.kneu.aviakvytok.excel.repository.TicketSaleRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

/** Агрегація імпортованих продажів для графіків (кількість записів за категоріями). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd.MM");

    private final TicketSaleRepository repository;

    public StatsDto stats() {
        TicketSale last = repository.findFirstByOrderByImportedAtDescIdDesc();
        return new StatsDto(
                repository.count(),
                repository.paidRevenue(),
                repository.countDirections(),
                last == null ? null : last.getImportedAt(),
                last == null ? null : last.getImportFile(),
                items(repository.countByDate(), v -> ((LocalDate) v).format(DAY)),
                items(repository.countByDirection(), Object::toString),
                items(repository.countByFareClass(), v -> ((FareClass) v).getLabel()),
                items(repository.countByStatus(), v -> ((SaleStatus) v).getLabel()));
    }

    public List<SaleDto> recent() {
        return repository.findTop10ByOrderByIdDesc().stream().map(SaleDto::from).toList();
    }

    private static List<ChartItem> items(List<Object[]> rows, Function<Object, String> label) {
        return rows.stream()
                .map(r -> new ChartItem(label.apply(r[0]), ((Number) r[1]).longValue()))
                .toList();
    }
}
