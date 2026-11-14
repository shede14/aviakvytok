package ua.kneu.aviakvytok.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Авіакомпанія-перевізник. */
@Entity
@Table(name = "airlines")
@Getter
@Setter
@NoArgsConstructor
public class Airline extends BaseEntity {

    @Column(name = "iata_code", nullable = false, unique = true, length = 2)
    private String iataCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 60)
    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Ієрархія One-to-Many: авіакомпанія -> рейси
    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flight> flights = new ArrayList<>();
}
