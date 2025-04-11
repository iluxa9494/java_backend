package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import searchengine.dto.search.ErrorResponse;

import java.nio.file.AccessDeniedException;

/**
 * Глобальный обработчик исключений, возвращающий корректные HTTP-ответы
 * с сообщениями об ошибках в формате JSON.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Некорректный запрос: " + ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(SecurityException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Требуется авторизация: " + ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Доступ запрещён: " + ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Ресурс не найден: " + ex.getRequestURL()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Метод не поддерживается: " + ex.getMethod()),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Внутренняя ошибка сервера: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}