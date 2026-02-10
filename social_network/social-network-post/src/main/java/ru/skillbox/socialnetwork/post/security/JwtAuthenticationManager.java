package ru.skillbox.socialnetwork.post.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationManager implements AuthenticationManager {

    private final RestTemplate restTemplate;

    @Value("${security.authenticate.url}")
    private String authenticateUrl;

    @Value("${security.authenticate.path}")
    private String authenticatePath;

    public JwtAuthenticationManager(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    authenticateUrl + authenticatePath +
                            "?token=" + token,
                    HttpMethod.GET,
                    null,
                    Boolean.class
            );

            if (response.getBody() != null && response.getStatusCode().is2xxSuccessful() && response.getBody()) {
                UUID userId = extractUserIdFromTokenWithoutSignatureCheck(token);
                return new UsernamePasswordAuthenticationToken(userId, token, List.of());
            }

        } catch (Exception e) {
            throw new BadCredentialsException("Token validation failed: " + e.getMessage());
        }

        throw new BadCredentialsException("Invalid token");

    }

    private UUID extractUserIdFromTokenWithoutSignatureCheck(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            ObjectMapper mapper = new ObjectMapper();

            java.util.Map<String, Object> claims = mapper.readValue(payloadJson,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.HashMap<String, Object>>() {
                    });

            String subject = (String) claims.get("sub");

            if (subject == null || subject.isBlank()) {
                throw new RuntimeException("Subject (sub) is missing in the token");
            }

            return UUID.fromString(subject);

        } catch (Exception e) {
            log.error("Failed to extract user ID from token", e);
            throw new RuntimeException("Failed to extract user ID from token", e);
        }
    }
}
