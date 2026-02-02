package ru.skillbox.socialnetwork.post.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(
            OffsetDateTime timestamp,
            int status,
            String error,
            String path,
            Map<String, String> details
    ) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));

        var body = new ApiError(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                req.getRequestURI(),
                details
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex,
                                                     HttpServletRequest req) {
        var body = new ApiError(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                req.getRequestURI(),
                Map.of()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                             HttpServletRequest req) {
        var body = new ApiError(
                OffsetDateTime.now(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Request method '" + ex.getMethod() + "' is not supported for this endpoint.",
                req.getRequestURI(),
                Map.of()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex,
                                                HttpServletRequest req) {
        var body = new ApiError(
            OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal error",
                req.getRequestURI(),
                Map.of("message", String.valueOf(ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
