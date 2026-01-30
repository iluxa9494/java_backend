package ru.skillbox.socialnetwork.account.config;

// Ответ от MC-AUTH
class JwtValidationResponse {
    private boolean valid;
    private String userId;

    public JwtValidationResponse() {
    }

    public JwtValidationResponse(boolean valid, String userId) {
        this.valid = valid;
        this.userId = userId;
    }

    public boolean isValid() {
        return valid;
    }

    public String getUserId() {
        return userId;
    }
}
