package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Airport;

public interface AirportRepository extends JpaRepository<Airport, Long> {
}
