package ru.skillbox.socialnetwork.post.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isAlreadyAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

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
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isAlreadyAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
