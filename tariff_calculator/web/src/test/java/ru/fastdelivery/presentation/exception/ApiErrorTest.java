package ru.fastdelivery.presentation.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiErrorTest {
    @Test
    @DisplayName("Создание объекта ошибки 400 BAD_REQUEST")
    void shouldCreateBadRequestError() {
        String errorMessage = "Некорректный запрос";
        ApiError error = ApiError.badRequest(errorMessage);
        assertEquals(HttpStatus.BAD_REQUEST, error.httpStatus());
        assertEquals("error", error.status());
        assertEquals(errorMessage, error.message());
    }
}