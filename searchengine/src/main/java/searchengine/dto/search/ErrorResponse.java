package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для передачи сообщения об ошибке в формате JSON.
 * Используется во всех ошибочных ответах API.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private boolean result;
    private String error;

    public ErrorResponse(String errorMessage) {
        this.result = false;
        this.error = errorMessage;
    }
}