package ru.skillbox.socialnetwork.friend.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("[friend] SecurityContext has no authentication");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.warn("[friend] Authentication is anonymous or not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Optional<UUID> userId = extractUserId(authentication.getPrincipal(), authentication.getName());
        if (userId.isPresent()) {
            return userId.get();
        }

        log.warn("[friend] Failed to extract userId from principal type: {}",
                authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication principal");
    }

    private static Optional<UUID> extractUserId(Object principal, String authenticationName) {
        if (principal instanceof UUID uuid) {
            return Optional.of(uuid);
        }
        if (principal instanceof JwtUserDetailsImpl userDetails) {
            return Optional.ofNullable(userDetails.getId());
        }
        if (principal instanceof String s) {
            return parseUuid(s);
        }
        Optional<UUID> fromJwt = extractFromJwtPrincipal(principal);
        if (fromJwt.isPresent()) {
            return fromJwt;
        }
        return parseUuid(authenticationName);
    }

    private static Optional<UUID> extractFromJwtPrincipal(Object principal) {
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
            return parseUuid(candidate);
        } catch (Exception ex) {
            log.warn("[friend] Failed to read Jwt principal claims: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<UUID> parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            log.warn("[friend] Invalid UUID format: {}", value);
            return Optional.empty();
        }
    }
}
