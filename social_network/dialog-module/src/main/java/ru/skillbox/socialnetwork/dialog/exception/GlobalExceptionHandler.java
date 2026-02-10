package ru.skillbox.socialnetwork.dialog.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skillbox.socialnetwork.dialog.responses.ErrorRs;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DialogNotFoundException.class)
    public ResponseEntity<ErrorRs> handleDialogNotFoundException(DialogNotFoundException ex) {
        log.error("Dialog not found: {}", ex.getMessage());
        ErrorRs errorRs = new ErrorRs(
                "Dialog Not Found",
                ex.getMessage(),
                (int) (System.currentTimeMillis() / 1000)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRs);
    }


    @ExceptionHandler(DialogAccessDeniedException.class)
    public ResponseEntity<ErrorRs> handleDialogAccessDenied(DialogAccessDeniedException ex) {
        log.error("Dialog access denied: {}", ex.getMessage());
        ErrorRs errorRs = new ErrorRs(
                "Access Denied",
                ex.getMessage(),
                (int) (System.currentTimeMillis() / 1000)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorRs);
    }


    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorRs> handleUserNotAuthenticated(UserNotAuthenticatedException ex) {
        log.error("User not authenticated: {}", ex.getMessage());
        ErrorRs errorRs = new ErrorRs(
                "User Not Authenticated",
                ex.getMessage(),
                (int) (System.currentTimeMillis() / 1000)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRs);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRs> handleAll(Exception ex) {
        log.error("Unexpected server error: {}", ex.getMessage());
        ErrorRs errorRs = new ErrorRs(
                "Unexpected Server Error",
                (int) (System.currentTimeMillis() / 1000)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRs);
    }
}
