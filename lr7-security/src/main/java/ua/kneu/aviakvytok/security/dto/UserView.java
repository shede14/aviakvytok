package ua.kneu.aviakvytok.security.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Рядок таблиці користувачів на панелі адміністратора.
 *
 * @param passwordHash BCrypt-хеш з бази (показується, щоб переконатися, що пароль не зберігається відкрито)
 * @param activeSessions кількість активних сесій за даними SessionRegistry
 */
public record UserView(Long id, String username, List<String> roles, String passwordHash,
                       LocalDateTime createdAt, int activeSessions) {
}
