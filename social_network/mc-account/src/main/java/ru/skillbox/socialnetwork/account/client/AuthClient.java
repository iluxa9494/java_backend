package ru.skillbox.socialnetwork.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "auth-client", url = "${auth.service.url:http://mc-authentication:8081}")
public interface AuthClient {

    /**
     * Проверяет валидность JWT токена.
     */
    @GetMapping("/api/v1/auth/validate")
    ResponseEntity<Boolean> validateToken(@RequestParam("token") String token);

    /**
     * Валидирует токен и возвращает User ID.
     */
    @GetMapping("/api/v1/auth/validate-token")
    UUID validateTokenAndGetUserId(@RequestParam("token") String token);
}