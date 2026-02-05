package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;

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