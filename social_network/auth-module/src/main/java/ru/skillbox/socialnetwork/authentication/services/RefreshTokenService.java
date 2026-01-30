package ru.skillbox.socialnetwork.authentication.services;

import ru.skillbox.socialnetwork.authentication.entities.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {

    Optional<RefreshToken> findByRefreshToken(String token);

    RefreshToken createRefreshToken(UUID userId);

    RefreshToken checkRefreshToken(RefreshToken token);

    void deleteByUserId(UUID userId);
}
