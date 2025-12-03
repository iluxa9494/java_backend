package ru.skillbox.socialnetwork.integration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager {

    private final RestTemplate restTemplate;

    @Value("${security.authenticate.url}")
    private String authenticateUrl;

    @Value("${security.authenticate.path}")
    private String authenticatePath;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    authenticateUrl + "/" + authenticatePath,
                    HttpMethod.GET,
                    entity,
                    Boolean.class
            );

            if (response.getBody() != null && response.getBody()) {
                return new UsernamePasswordAuthenticationToken(null, token, List.of());
            }

        } catch (Exception e) {
            throw new BadCredentialsException("Token validation failed: " + e.getMessage());
        }

        throw new BadCredentialsException("Invalid token");

    }
}
