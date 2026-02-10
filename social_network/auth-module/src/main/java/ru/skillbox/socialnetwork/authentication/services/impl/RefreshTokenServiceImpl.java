package ru.skillbox.socialnetwork.authentication.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.authentication.entities.RefreshToken;
import ru.skillbox.socialnetwork.authentication.exceptions.RefreshTokenException;
import ru.skillbox.socialnetwork.authentication.repositories.RefreshTokenRepository;
import ru.skillbox.socialnetwork.authentication.services.RefreshTokenService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public Optional<RefreshToken> findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(UUID userId) {
        var existingTokenOpt = refreshTokenRepository.findByUserId(userId);

        if (existingTokenOpt.isPresent()) {
            var existingToken = existingTokenOpt.get();

            if (existingToken.getExpiryDate().isAfter(Instant.now())) {
                Duration timeLeft = Duration.between(Instant.now(), existingToken.getExpiryDate());
                if (timeLeft.toDays() > 3) {
                    return existingToken;
                } else {
                    log.info("Refresh token for user {} expires soon, regenerating...", userId);
                }
            } else {
                log.info("Refresh token for user {} expired, regenerating...", userId);
            }
        }
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration.toMillis()));
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken checkRefreshToken(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Repeat signing action!");
        }
        return token;
    }

    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}