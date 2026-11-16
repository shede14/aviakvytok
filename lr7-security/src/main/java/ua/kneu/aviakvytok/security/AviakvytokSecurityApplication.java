package ua.kneu.aviakvytok.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ЛР7. Модуль автентифікації та авторизації ТОВ «АвіаКвиток»:
 * ролі USER / MANAGER / ADMIN, BCrypt, обмеження однієї активної сесії на користувача.
 */
@SpringBootApplication
public class AviakvytokSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(AviakvytokSecurityApplication.class, args);
    }
}
