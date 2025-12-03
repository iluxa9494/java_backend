package ru.fastdelivery.presentation.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

/**
 * Представляет собой структуру ошибки, возвращаемую пользователю при возникновении исключений.
 * Используется в глобальном обработчике {@link GlobalExceptionHandler}.
 */
public record ApiError(
        @JsonIgnore
        HttpStatus httpStatus,
        String status,
        String message
) {
    public static ApiError badRequest(String message){
        return new ApiError(HttpStatus.BAD_REQUEST, "error", message);
    }
}