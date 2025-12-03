package ru.skillbox.socialnetwork.post.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class CurrentUserProvider {

    private static final String USER_HEADER = "X-User-Id";

    public UUID getCurrentUserId() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No request context");
        }
        String raw = attrs.getRequest().getHeader(USER_HEADER);
        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing header " + USER_HEADER);
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + USER_HEADER + " value");
        }
    }
}