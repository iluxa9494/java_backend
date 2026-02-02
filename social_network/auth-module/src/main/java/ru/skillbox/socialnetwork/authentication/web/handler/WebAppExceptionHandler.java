package ru.skillbox.socialnetwork.authentication.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skillbox.socialnetwork.authentication.exceptions.AlreadyExitsException;
import ru.skillbox.socialnetwork.authentication.exceptions.EntityNotFoundException;
import ru.skillbox.socialnetwork.authentication.exceptions.RefreshTokenException;

@RestControllerAdvice
public class WebAppExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(WebAppExceptionHandler.class);

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> refreshTokenExceptionHandler(RefreshTokenException ex, WebRequest webRequest) {
        log.warn("RefreshTokenException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    @ExceptionHandler(value = AlreadyExitsException.class)
    public ResponseEntity<ErrorResponseBody> alreadyExitsExceptionHandler(AlreadyExitsException ex, WebRequest webRequest) {
        log.warn("AlreadyExitsException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> entityNotFoundExceptionHandler(EntityNotFoundException ex, WebRequest webRequest) {
        log.warn("EntityNotFoundException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponseBody> runtimeExceptionHandler(RuntimeException ex, WebRequest webRequest) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex,
                                                                                   WebRequest webRequest) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        log.warn("Validation failed: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseBody.builder()
                        .message(message)
                        .description(webRequest.getDescription(false))
                        .build());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponseBody> exceptionHandler(Exception ex, WebRequest webRequest) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, webRequest);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatus httpStatus, Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponseBody.builder()
                        .message(ex.getMessage())
                        .description(webRequest.getDescription(false))
                        .build());
    }
}
