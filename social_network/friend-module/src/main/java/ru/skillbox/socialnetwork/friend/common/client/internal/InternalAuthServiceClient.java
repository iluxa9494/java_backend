package ru.skillbox.socialnetwork.friend.common.client.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.authentication.api.AuthTokenService;
import ru.skillbox.socialnetwork.friend.common.client.AuthServiceClient;

@Component
@Primary
@ConditionalOnProperty(name = "app.auth-service.jwt-validation.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class InternalAuthServiceClient implements AuthServiceClient {

    private final AuthTokenService authTokenService;

    @Override
    public Boolean validateToken(String token) {
        return authTokenService.validateToken(token);
    }
}
