package ru.skillbox.socialnetwork.account.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

        log.info("🔐 [1] JwtAuthenticationFilter: Начало обработки запроса: {}", request.getRequestURI());

        String token = extractToken(request);

        if (token != null) {
            log.info("🔐 [2] Токен извлечен (длина: {})", token.length());

            try {
                log.info("🔐 [3] Проверяем токен через auth module...");
                boolean isValid = authTokenService.validateToken(token);
                log.info("🔐 [4] Результат валидации токена: {}", isValid);

                if (isValid) {
                    log.info("🔐 [5] Токен валиден, извлекаем userId...");
                    UUID userId = authTokenService.extractUserId(token);
                    log.info("✅ [6] Token validated. User ID: {}", userId);
                    userActivityService.recordUserActivity(userId);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userId.toString(), token, Collections.emptyList()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("✅ [7] Аутентификация установлена в SecurityContext");
                } else {
                    log.error("❌ [ERROR] Токен не прошел валидацию в auth module");
                }
            } catch (Exception e) {
                log.error("❌ [ERROR] Ошибка валидации токена: {}", e.getMessage());
                log.error("❌ Stack trace:", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else {
            log.warn("⚠️ [WARN] Токен не найден в запросе");
        }

        log.info("🔐 [8] Продолжаем цепочку фильтров");
        chain.doFilter(request, response);
    }


    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // Удаляем "Bearer "
        }
        return null;
    }
}
