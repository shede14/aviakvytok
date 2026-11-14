package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
