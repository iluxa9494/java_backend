package ru.fastdelivery.presentation.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight health endpoint for container checks.
 */
@RestController
public class HealthController {
    @GetMapping("/")
    public String root() {
        return "tariff-calculator-service";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
