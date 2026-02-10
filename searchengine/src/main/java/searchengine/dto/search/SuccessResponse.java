package searchengine.dto.search;

import lombok.Getter;

/**
 * Ответ, сигнализирующий об успешном выполнении запроса.
 * Используется для возврата {"result": true} в JSON-ответе.
 */
@Getter
public class SuccessResponse {
    private final boolean result = true;
}