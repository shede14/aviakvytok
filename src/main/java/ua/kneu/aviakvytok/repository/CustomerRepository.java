package ua.kneu.aviakvytok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
