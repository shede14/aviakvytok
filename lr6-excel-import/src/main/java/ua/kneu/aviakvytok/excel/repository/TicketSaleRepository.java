package ua.kneu.aviakvytok.excel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.kneu.aviakvytok.excel.entity.TicketSale;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TicketSaleRepository extends JpaRepository<TicketSale, Long> {

    /** Які з переданих кодів бронювання вже є в базі (для пропуску дублікатів під час імпорту). */
    @Query("select s.bookingCode from TicketSale s where s.bookingCode in :codes")
    Set<String> findExistingCodes(Collection<String> codes);

    // ---------- агреговані дані для графіків: [мітка, кількість] ----------

    @Query("select s.saleDate, count(s) from TicketSale s group by s.saleDate order by s.saleDate")
    List<Object[]> countByDate();

    @Query("select s.direction, count(s) from TicketSale s group by s.direction order by count(s) desc, s.direction")
    List<Object[]> countByDirection();

    @Query("select s.fareClass, count(s) from TicketSale s group by s.fareClass order by s.fareClass")
    List<Object[]> countByFareClass();

    @Query("select s.status, count(s) from TicketSale s group by s.status order by s.status")
    List<Object[]> countByStatus();

    /** Виручка — сума лише оплачених квитків. */
    @Query("select coalesce(sum(s.price), 0) from TicketSale s where s.status = ua.kneu.aviakvytok.excel.entity.SaleStatus.PAID")
    BigDecimal paidRevenue();

    @Query("select count(distinct s.direction) from TicketSale s")
    long countDirections();

    TicketSale findFirstByOrderByImportedAtDescIdDesc();

    List<TicketSale> findTop10ByOrderByIdDesc();
}
