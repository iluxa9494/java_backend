package ru.skillbox.socialnetwork.friend.common.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skillbox.socialnetwork.friend.common.security.JwtUserDetailsImpl;

@Configuration
public class AccountServiceClientConfig {

    @Bean
    public RequestInterceptor accountServiceAuthInterceptor() {
        return template -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetailsImpl userDetails) {
                template.header("Authorization", "Bearer " + userDetails.getToken());
            }
        };
    }
}