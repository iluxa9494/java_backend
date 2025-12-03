package com.team58.mc_notifications.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> errorBody(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.name());
        body.put("code", status.value());
        body.put("message", message);
        if (req != null) {
            body.put("path", req.getRequestURI());
        }
        body.put("timestamp", OffsetDateTime.now().toString());
        return body;
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, HttpServletRequest req) {
        String msg = ex.getMessage();
        if (ex instanceof HttpMessageNotReadableException && msg != null && msg.length() > 400) {
            msg = "Malformed JSON or invalid value.";
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody(HttpStatus.BAD_REQUEST, msg, req));
    }

    @ExceptionHandler({AuthenticationException.class, InsufficientAuthenticationException.class})
    public ResponseEntity<Map<String, Object>> handleUnauthorized(Exception ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorBody(HttpStatus.UNAUTHORIZED, ex.getMessage(), req));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(Exception ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorBody(HttpStatus.FORBIDDEN, "Forbidden", req));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage(), req));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                        HttpServletRequest req) {
        String msg = "Request method '" + ex.getMethod() + "' is not supported for this endpoint.";
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorBody(HttpStatus.METHOD_NOT_ALLOWED, msg, req));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String raw = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        String msg = "Data integrity violation.";
        HttpStatus status = HttpStatus.CONFLICT;
        if (raw != null) {
            String lower = raw.toLowerCase();
            if (lower.contains("null value in column")) {
                status = HttpStatus.BAD_REQUEST;
                msg = raw;
            } else if (lower.contains("unique") && lower.contains("notification_settings") && lower.contains("user_id")) {
                status = HttpStatus.CONFLICT;
                msg = "Settings already exist for this user";
            } else {
                msg = raw.length() > 500 ? raw.substring(0, 500) + "..." : raw;
            }
        }

        return ResponseEntity.status(status).body(errorBody(status, msg, req));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        String msg = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorBody(HttpStatus.valueOf(ex.getStatusCode().value()), msg, req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle500(Exception ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", req));
    }
}