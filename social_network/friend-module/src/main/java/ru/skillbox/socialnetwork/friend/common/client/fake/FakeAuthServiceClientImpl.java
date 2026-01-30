package ru.skillbox.socialnetwork.friend.common.client.fake;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.friend.common.client.AuthServiceClient;

// todo Класс заглушка для тестов - эмулирует обращение к внешнему сервису auth
// todo и по умолчанию всегда возвращает успешную валидацию JWT токена
@Component
@ConditionalOnProperty(name = "app.auth-service.jwt-validation.enabled", havingValue = "false")
@Slf4j
public class FakeAuthServiceClientImpl implements AuthServiceClient {
    @Override
    public Boolean validateToken(String token) {
        log.info("Token validate with FakeAuthServiceClient(always return true)");
        return true;
    }
}
