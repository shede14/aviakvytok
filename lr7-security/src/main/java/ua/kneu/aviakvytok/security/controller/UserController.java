package ua.kneu.aviakvytok.security.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.kneu.aviakvytok.security.dto.RegistrationForm;
import ua.kneu.aviakvytok.security.service.UserService;
import ua.kneu.aviakvytok.security.service.UsernameTakenException;

/** Сторінки застосунку: головна, вхід, реєстрація та сторінки для різних ролей. */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
        if (!result.hasFieldErrors("password") && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Паролі не збігаються");
        }
        if (result.hasErrors()) {
            return "registration";
        }
        try {
            userService.register(form.getUsername(), form.getPassword());
        } catch (UsernameTakenException e) {
            result.rejectValue("username", "taken", e.getMessage());
            return "registration";
        }
        return "redirect:/login?registered";
    }

    /** Сторінка для USER, MANAGER, ADMIN. */
    @GetMapping("/user")
    public String userPage(HttpSession session, Model model) {
        model.addAttribute("sessionId", session.getId());
        return "user-page";
    }

    /** Сторінка лише для MANAGER. */
    @GetMapping("/manager")
    public String managerPage(Model model) {
        model.addAttribute("sessions", userService.activeSessions());
        return "manager-page";
    }

    /** Панель адміністратора (лише ADMIN): користувачі, хеші паролів, активні сесії. */
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("sessions", userService.activeSessions());
        return "admin-page";
    }

    /** Сторінка «Доступ заборонено» зі статусом 403 Forbidden. */
    @RequestMapping("/access-denied")
    public String accessDenied(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // Spring Security передає сюди запит через forward — початкова адреса зберігається в атрибуті
        model.addAttribute("requestedUri", request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
        return "access-denied";
    }
}
