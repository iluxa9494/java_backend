package ru.skillbox.socialnetwork.friend.common.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.socialnetwork.friend.common.configuration.AccountServiceClientConfig;

@FeignClient(
        name = "${feign.clients.auth-service.name}",
        url = "${feign.clients.auth-service.url}",
        configuration = AccountServiceClientConfig.class)
@ConditionalOnProperty(name = "app.auth-service.jwt-validation.enabled", havingValue = "true", matchIfMissing = true)
public interface RealAuthServiceClient extends AuthServiceClient {

    @GetMapping("/api/v1/auth/validate")
    Boolean validateToken(@RequestParam String token);

}