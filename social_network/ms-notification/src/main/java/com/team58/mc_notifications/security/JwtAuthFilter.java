package com.team58.mc_notifications.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${authentication.service.validation.url}")
    private String authenticationServiceValidationUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("JwtAuthFilter: [{}] {}", request.getMethod(), path);

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String url = UriComponentsBuilder.fromUriString(authenticationServiceValidationUrl)
                    .queryParam("token", token)
                    .toUriString();

            Boolean isValid = restTemplate.getForObject(url, Boolean.class);
            if (!Boolean.TRUE.equals(isValid)) {
                log.warn("JWT token is not valid according to auth service");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            UUID userId = extractUserIdFromToken(token);

            UserPrincipal principal = new UserPrincipal(userId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            Collections.emptyList()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT validated, userId={}", userId);
        } catch (Exception e) {
            log.error("Error while validating JWT token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private UUID extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            Map<String, Object> payload =
                    mapper.readValue(payloadJson, new TypeReference<>() {});

            Object sub = payload.get("sub");
            if (sub == null) {
                throw new IllegalArgumentException("JWT does not contain 'sub' claim");
            }

            return UUID.fromString(sub.toString());
        } catch (Exception e) {
            log.error("Failed to extract userId from JWT", e);
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}