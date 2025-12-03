package searchengine.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер по умолчанию, обрабатывающий корневой путь ("/")
 * и возвращающий представление главной страницы.
 */
@Controller
public class DefaultController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}