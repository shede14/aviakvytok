package ua.kneu.aviakvytok.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Пасажир, на якого виписується квиток. */
@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
public class Passenger extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "passport_number", nullable = false, unique = true, length = 20)
    private String passportNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "passenger")
    private List<Ticket> tickets = new ArrayList<>();
}
