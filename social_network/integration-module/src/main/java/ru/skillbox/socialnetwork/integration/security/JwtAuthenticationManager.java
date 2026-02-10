package ru.skillbox.socialnetwork.integration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.authentication.api.AuthTokenService;

import java.util.List;
import java.util.UUID;

@Component("integrationJwtAuthenticationManager")
@RequiredArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager {

    private final AuthTokenService authTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try {
            if (!authTokenService.validateToken(token)) {
                throw new BadCredentialsException("Invalid token");
            }
            UUID userId = authTokenService.extractUserId(token);
            return new UsernamePasswordAuthenticationToken(userId, token, List.of());
        } catch (Exception e) {
            throw new BadCredentialsException("Token validation failed: " + e.getMessage());
        }
    }
}
