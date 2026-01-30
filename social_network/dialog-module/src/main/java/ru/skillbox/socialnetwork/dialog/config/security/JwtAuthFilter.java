package ru.skillbox.socialnetwork.dialog.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.skillbox.socialnetwork.authentication.api.AuthTokenService;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component("dialogJwtAuthFilter")
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    public JwtAuthFilter(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
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
        if (!authTokenService.validateToken(token)) {
            log.error("Invalid token: {}", token);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        UUID userId = authTokenService.extractUserId(token);
        CustomUserPrincipal principal = new CustomUserPrincipal(userId);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
