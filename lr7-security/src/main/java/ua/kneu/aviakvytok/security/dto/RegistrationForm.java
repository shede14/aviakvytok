package ua.kneu.aviakvytok.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Дані форми реєстрації. */
@Getter
@Setter
public class RegistrationForm {

    @NotBlank(message = "Вкажіть логін")
    @Size(min = 3, max = 30, message = "Логін — від 3 до 30 символів")
    @Pattern(regexp = "[a-zA-Z0-9._-]*", message = "Логін: лише латинські літери, цифри та . _ -")
    private String username;

    @NotBlank(message = "Вкажіть пароль")
    @Size(min = 6, max = 64, message = "Пароль — від 6 до 64 символів")
    private String password;

    @NotBlank(message = "Повторіть пароль")
    private String confirmPassword;
}
