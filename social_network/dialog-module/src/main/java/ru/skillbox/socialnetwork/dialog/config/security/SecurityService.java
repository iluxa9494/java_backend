package ru.skillbox.socialnetwork.dialog.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.dialog.exception.UserNotAuthenticatedException;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SecurityService {

    public Optional<UUID> getCurrentUserIdOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            log.warn("[dialog] No authenticated principal in SecurityContext");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return Optional.ofNullable(((CustomUserPrincipal) principal).getUserId());
        }

        if (principal instanceof UUID uuid) {
            return Optional.of(uuid);
        }

        if (principal instanceof String) {
            try {
                return Optional.of(UUID.fromString((String) principal));
            } catch (IllegalArgumentException ignored) {
            }
        }

        Optional<UUID> fromJwt = extractFromJwtPrincipal(principal);
        if (fromJwt.isPresent()) {
            return fromJwt;
        }

        if (authentication.getName() != null) {
            try {
                return Optional.of(UUID.fromString(authentication.getName()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return Optional.empty();
    }

    public UUID getCurrentUserIdOrThrow() {
        return getCurrentUserIdOptional()
                .orElseThrow(UserNotAuthenticatedException::new);
    }

    private Optional<UUID> extractFromJwtPrincipal(Object principal) {
        if (principal == null || !principal.getClass().getName().equals("org.springframework.security.oauth2.jwt.Jwt")) {
            return Optional.empty();
        }
        try {
            Method getClaim = principal.getClass().getMethod("getClaim", String.class);
            Method getSubject = principal.getClass().getMethod("getSubject");
            Object claimUserId = getClaim.invoke(principal, "userId");
            Object claimId = getClaim.invoke(principal, "id");
            String candidate = claimUserId != null ? claimUserId.toString()
                    : (claimId != null ? claimId.toString() : String.valueOf(getSubject.invoke(principal)));
            return Optional.of(UUID.fromString(candidate));
        } catch (Exception ex) {
            log.warn("[dialog] Failed to read Jwt principal claims: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
