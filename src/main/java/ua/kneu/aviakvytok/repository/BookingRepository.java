package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
