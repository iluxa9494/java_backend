package ru.skillbox.socialnetwork.dialog.config.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.dialog.exception.UserNotAuthenticatedException;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecurityService {

    public Optional<UUID> getCurrentUserIdOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return Optional.ofNullable(((CustomUserPrincipal) principal).getUserId());
        }

        if (principal instanceof String) {
            try {
                return Optional.of(UUID.fromString((String) principal));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return Optional.empty();
    }

    public UUID getCurrentUserIdOrThrow() {
        return getCurrentUserIdOptional()
                .orElseThrow(UserNotAuthenticatedException::new);
    }
}