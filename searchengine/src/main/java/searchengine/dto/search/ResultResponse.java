package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO для возврата универсального ответа с результатом и возможным сообщением об ошибке.
 */
@Getter
@AllArgsConstructor
public class ResultResponse {
    private boolean result;
    private String error;
}