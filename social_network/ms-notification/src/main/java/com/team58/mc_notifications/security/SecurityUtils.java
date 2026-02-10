package com.team58.mc_notifications.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityUtils {

    public Optional<UUID> getCurrentUserIdOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.ofNullable(userPrincipal.getUserId());
        }

        if (principal instanceof String s) {
            try {
                return Optional.of(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return Optional.empty();
    }

    public UUID getCurrentUserIdOrThrow() {
        return getCurrentUserIdOptional()
                .orElseThrow(() -> new SecurityException("Пользователь не аутентифицирован"));
    }
}