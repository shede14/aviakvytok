package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
