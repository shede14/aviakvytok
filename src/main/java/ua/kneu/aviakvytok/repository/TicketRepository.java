package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
