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

/** Клієнт агентства: користувач, який створює та оплачує бронювання. */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends BaseEntity {

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(length = 20)
    private String phone;

    // Ієрархія One-to-Many: клієнт -> бронювання
    @OneToMany(mappedBy = "customer")
    private List<Booking> bookings = new ArrayList<>();
}
