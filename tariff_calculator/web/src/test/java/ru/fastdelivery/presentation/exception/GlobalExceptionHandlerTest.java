package ru.fastdelivery.presentation.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Обработка IllegalArgumentException возвращает 400")
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Некорректный параметр");
        ResponseEntity<ApiError> response = handler.handleIllegalArgumentException(ex);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Некорректный параметр", response.getBody().message());
    }

    @Test
    @DisplayName("Обработка ошибок валидации возвращает 400 и список ошибок")
    void shouldHandleValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "field1", "должно быть заполнено"),
                new FieldError("request", "field2", "не может быть null")
        ));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException((MethodParameter) null, bindingResult);
        ResponseEntity<ApiError> response = handler.handleValidationErrors(ex);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().message().contains("field1"));
        assertTrue(response.getBody().message().contains("field2"));
    }
}