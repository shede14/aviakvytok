package ua.kneu.aviakvytok.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Аеропорт вильоту або прибуття. */
@Entity
@Table(name = "airports")
@Getter
@Setter
@NoArgsConstructor
public class Airport extends BaseEntity {

    @Column(name = "iata_code", nullable = false, unique = true, length = 3)
    private String iataCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 60)
    private String country;

    @OneToMany(mappedBy = "departureAirport")
    private List<Flight> departures = new ArrayList<>();

    @OneToMany(mappedBy = "arrivalAirport")
    private List<Flight> arrivals = new ArrayList<>();
}
