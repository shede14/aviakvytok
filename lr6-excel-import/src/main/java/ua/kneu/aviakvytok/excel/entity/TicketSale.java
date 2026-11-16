package ua.kneu.aviakvytok.excel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Продаж авіаквитка — один рядок імпортованого Excel-файлу.
 * Ідентифікатор генерується послідовністю з allocationSize = 50, щоб Hibernate
 * міг виконувати пакетні INSERT під час saveAll().
 */
@Entity
@Table(name = "ticket_sales", indexes = {
        @Index(name = "ix_ticket_sales_date", columnList = "sale_date"),
        @Index(name = "ix_ticket_sales_direction", columnList = "direction")
})
@Getter
@Setter
@NoArgsConstructor
public class TicketSale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_sales_seq")
    @SequenceGenerator(name = "ticket_sales_seq", sequenceName = "ticket_sales_seq", allocationSize = 50)
    private Long id;

    /** Код бронювання (PNR) — унікальний. */
    @Column(name = "booking_code", nullable = false, unique = true, length = 10)
    private String bookingCode;

    @Column(name = "passenger_name", nullable = false, length = 120)
    private String passengerName;

    @Column(name = "flight_number", nullable = false, length = 10)
    private String flightNumber;

    /** Напрямок (категорія), наприклад «Київ – Варшава». */
    @Column(nullable = false, length = 80)
    private String direction;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "fare_class", nullable = false, length = 20)
    private FareClass fareClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SaleStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Ім'я файлу, з якого імпортовано запис. */
    @Column(name = "import_file", length = 160)
    private String importFile;

    @CreationTimestamp
    @Column(name = "imported_at", nullable = false, updatable = false)
    private LocalDateTime importedAt;
}
