package ru.skillbox.socialnetwork.account.config;

// Запрос к MC-AUTH
class ValidateRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
