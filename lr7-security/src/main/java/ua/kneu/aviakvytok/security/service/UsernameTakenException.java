package ua.kneu.aviakvytok.security.service;

/** Користувач із таким логіном уже існує. */
public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String username) {
        super("Логін «" + username + "» уже зайнятий");
    }
}
