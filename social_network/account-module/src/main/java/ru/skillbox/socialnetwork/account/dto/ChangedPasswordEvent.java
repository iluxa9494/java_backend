package ru.skillbox.socialnetwork.account.dto;

import java.util.UUID;

public class ChangedPasswordEvent {
    private String newPassword; // новый пароль
    private String email;       // почта
    private UUID id;            // айди

    // Конструктор, геттеры, сеттеры
    public ChangedPasswordEvent(String newPassword, String email, UUID id) {
        this.newPassword = newPassword;
        this.email = email;
        this.id = id;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}