package ru.skillbox.socialnetwork.account.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skillbox.socialnetwork.authentication.api.AuthTokenService;
import ru.skillbox.socialnetwork.account.service.UserActivityService;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component("accountJwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private AuthTokenService authTokenService;
    
    @Autowired
    private UserActivityService userActivityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (isAlreadyAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        if (token != null) {
            try {
                boolean isValid = authTokenService.validateToken(token);

                if (isValid) {
                    UUID userId = authTokenService.extractUserId(token);
                    userActivityService.recordUserActivity(userId);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userId, token, Collections.emptyList()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("[account] Token validation failed for request {}", request.getRequestURI());
                }
            } catch (Exception e) {
                log.warn("[account] Token validation error for request {}: {}", request.getRequestURI(), e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("[account] Token not found for request {}", request.getRequestURI());
        }

        chain.doFilter(request, response);
    }


    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);
            return (token == null || token.isBlank()) ? null : token;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    return (token == null || token.isBlank()) ? null : token;
                }
            }
        }
        return null;
    }

    private boolean isAlreadyAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
