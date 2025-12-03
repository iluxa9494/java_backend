package ru.skillbox.socialnetwork.account.dto;

import java.util.UUID;

public class ChangedEmailEvent {
    private String password;    // пароль
    private String newEmail;    // новая почта
    private UUID id;            // айди

    // Конструктор, геттеры, сеттеры
    public ChangedEmailEvent(String password, String newEmail, UUID id) {
        this.password = password;
        this.newEmail = newEmail;
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}