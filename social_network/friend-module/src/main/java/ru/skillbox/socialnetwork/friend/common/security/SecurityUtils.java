package ru.skillbox.socialnetwork.friend.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Пользователь не аутентифицирован");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw new SecurityException("Не удалось определить ID текущего пользователя");
    }
}