package ru.skillbox.socialnetwork.authentication.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.authentication.api.AuthTokenService;
import ru.skillbox.socialnetwork.authentication.security.jwt.JwtUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final JwtUtils jwtUtils;

    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validate(token);
    }

    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(jwtUtils.getUserId(token));
    }
}
