package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Airline;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
}
