package ru.skillbox.socialnetwork.integration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("JwtAuthenticationFilter processing request: {}", request.getRequestURI());

        String token = extractToken(request);

        if (token != null) {
            try {
                log.debug("JWT token found, attempting authentication");
                Authentication authentication = new UsernamePasswordAuthenticationToken(null, token);
                Authentication authenticated = authenticationManager.authenticate(authentication);

                if (authenticated != null && authenticated.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authenticated);
                    log.debug("Authentication successful for request: {}", request.getRequestURI());
                }
            } catch (Exception e) {
                log.error("Authentication failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("No JWT token found for request: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
