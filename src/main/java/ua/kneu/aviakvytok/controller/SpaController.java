package ua.kneu.aviakvytok.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Віддає зібраний Vue-застосунок (resources/static/index.html) для маршрутів
 * vue-router, щоб пряме відкриття або оновлення сторінки не давало 404.
 */
@Controller
public class SpaController {

    @GetMapping({"/flights", "/flights/**", "/airlines", "/about"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
