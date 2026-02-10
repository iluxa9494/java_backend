package ru.skillbox.socialnetwork.authentication.api;

import java.util.Optional;
import java.util.UUID;

public interface AuthTokenService {

    boolean validateToken(String token);

    UUID extractUserId(String token);

    default Optional<UUID> validateAndExtract(String token) {
        if (!validateToken(token)) {
            return Optional.empty();
        }
        return Optional.of(extractUserId(token));
    }
}
