package ru.skillbox.socialnetwork.authentication.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.authentication.entities.PasswordResetToken;
import ru.skillbox.socialnetwork.authentication.repositories.PasswordResetTokenRepository;
import ru.skillbox.socialnetwork.authentication.services.PasswordResetTokenService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository repository;

    @Value("${app.password-reset.expiration-minutes:30}")
    private long expirationMinutes;

    @Override
    public PasswordResetToken createToken(UUID userId) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(Duration.ofMinutes(expirationMinutes)));
        return repository.save(token);
    }

    @Override
    public Optional<PasswordResetToken> validateToken(String token) {
        return repository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(Instant.now()));
    }

    @Override
    public void deleteToken(String token) {
        repository.deleteByToken(token);
    }
}