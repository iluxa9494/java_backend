package ru.skillbox.socialnetwork.account.dto.jwt;

import lombok.Data;

@Data
public class JwtValidationResponse {
    private boolean valid;
    private String userId;

    // Конструктор по умолчанию (для Jackson)
    public JwtValidationResponse() {}

    // Конструктор с параметрами
    public JwtValidationResponse(boolean valid, String userId) {
        this.valid = valid;
        this.userId = userId;
    }
}