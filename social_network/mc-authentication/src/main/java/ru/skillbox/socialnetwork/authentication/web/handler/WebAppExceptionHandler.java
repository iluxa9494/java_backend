package ru.skillbox.socialnetwork.authentication.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.skillbox.socialnetwork.authentication.exceptions.AlreadyExitsException;
import ru.skillbox.socialnetwork.authentication.exceptions.EntityNotFoundException;
import ru.skillbox.socialnetwork.authentication.exceptions.RefreshTokenException;

@RestControllerAdvice
public class WebAppExceptionHandler {

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> refreshTokenExceptionHandler(RefreshTokenException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    @ExceptionHandler(value = AlreadyExitsException.class)
    public ResponseEntity<ErrorResponseBody> alreadyExitsExceptionHandler(AlreadyExitsException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> entityNotFoundExceptionHandler(EntityNotFoundException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponseBody> runtimeExceptionHandler(RuntimeException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatus httpStatus, Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponseBody.builder()
                        .message(ex.getMessage())
                        .description(webRequest.getDescription(false))
                        .build());
    }
}