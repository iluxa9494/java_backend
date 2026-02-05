package com.skillbox.cryptobot.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DataIntegrityViolationException.class, EntityNotFoundException.class})
    public void handleDatabaseException(Exception ex) {
        log.error("Ошибка базы данных: {}", ex.getMessage());
    }

    @ExceptionHandler(TelegramApiException.class)
    public void handleTelegramApiException(TelegramApiException ex) {
        log.warn("Ошибка Telegram API: {}", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException ex) {
        log.error("Критическая ошибка в коде: {}", ex.getMessage(), ex);
    }

    @ExceptionHandler(Exception.class)
    public void handleGenericException(Exception ex) {
        log.error("Произошла неизвестная ошибка: {}", ex.getMessage(), ex);
    }
}