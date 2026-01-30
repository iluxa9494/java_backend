package ru.skillbox.socialnetwork.authentication.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.skillbox.socialnetwork.authentication.entities.user.User;
import ru.skillbox.socialnetwork.authentication.repositories.UserRepository;
import ru.skillbox.socialnetwork.authentication.security.AppUserDetails;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwtToken = getToken(request);
            if (jwtToken != null) {
                if (jwtUtils.validate(jwtToken)) {
                    String userId = jwtUtils.getUserId(jwtToken);
                    UserDetails userDetails = loadUserById(UUID.fromString(userId));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired for request [{}]: {}", request.getRequestURI(), e.getMessage());
        } catch (Exception e) {
            log.error("Cannot set user authentication for {}: {}", request.getRequestURI(), e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return new AppUserDetails(user);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/captcha") ||
                path.startsWith("/api/v1/auth/validate") ||
                path.startsWith("/api/v1/auth/change-password-link") ||
                path.startsWith("/api/v1/auth/change-email-link") ||
                path.startsWith("/api/v1/auth/logout") ||
                path.startsWith("/api/v1/auth/password/recovery/");
    }
}