package ua.kneu.aviakvytok.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Рейс: конкретний виліт за маршрутом у визначений час. */
@Entity
@Table(name = "flights", indexes = {
        @Index(name = "idx_flights_flight_number", columnList = "flight_number"),
        @Index(name = "idx_flights_departure_time", columnList = "departure_time")
})
@Getter
@Setter
@NoArgsConstructor
public class Flight extends BaseEntity {

    @Column(name = "flight_number", nullable = false, length = 10)
    private String flightNumber;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FlightStatus status = FlightStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @OneToMany(mappedBy = "flight")
    private List<Ticket> tickets = new ArrayList<>();

    // Зворотний бік зв'язку Many-to-Many (власник - Booking)
    @ManyToMany(mappedBy = "flights")
    private Set<Booking> bookings = new HashSet<>();
}
