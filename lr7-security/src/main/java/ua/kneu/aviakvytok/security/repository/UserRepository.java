package ua.kneu.aviakvytok.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kneu.aviakvytok.security.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByOrderByIdAsc();
}
