package ua.kneu.aviakvytok.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kneu.aviakvytok.security.entity.Role;
import ua.kneu.aviakvytok.security.entity.User;
import ua.kneu.aviakvytok.security.repository.UserRepository;
import ua.kneu.aviakvytok.security.service.UserService;

/**
 * Початкові дані: три ролі та демонстраційні облікові записи для перевірки доступу.
 * Паролі одразу хешуються BCrypt — у базі відкритого тексту немає.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role user = userService.findOrCreateRole(Role.USER);
        Role admin = userService.findOrCreateRole(Role.ADMIN);
        Role manager = userService.findOrCreateRole(Role.MANAGER);

        create("admin", "admin123", admin);
        create("manager", "manager123", manager);
        create("user", "user123", user);
    }

    private void create(String username, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User u = new User(username, passwordEncoder.encode(password));
        u.addRole(role);
        userRepository.save(u);
        log.info("Створено демонстраційного користувача {} ({})", username, role.getName());
    }
}
