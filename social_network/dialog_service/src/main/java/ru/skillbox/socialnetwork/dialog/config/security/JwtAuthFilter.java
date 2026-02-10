package ru.skillbox.socialnetwork.dialog.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${authentication.service.validation.url}")
    private String authenticationServiceValidationUrl;

    public JwtAuthFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String url = UriComponentsBuilder
                .fromHttpUrl(authenticationServiceValidationUrl)
                .queryParam("token", token)
                .toUriString();
        Boolean isValid = restTemplate.getForObject(url, Boolean.class);

        if (!Boolean.TRUE.equals(isValid)) {
            log.error("Invalid token: {}", token);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        Map<String, Object> payload = decodePayload(token);
        UUID userId = UUID.fromString((String) payload.get("sub"));
        CustomUserPrincipal principal = new CustomUserPrincipal(userId);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }


    private Map<String, Object> decodePayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");

        String payloadJson = new String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
        );

        try {
            return mapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JWT payload", e);
        }
    }
}

