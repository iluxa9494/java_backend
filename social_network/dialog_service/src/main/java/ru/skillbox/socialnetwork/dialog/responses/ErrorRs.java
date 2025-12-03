package ru.skillbox.socialnetwork.dialog.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorRs {
    private String error;
    private String errorDescription;
    private Integer timestamp;

    public ErrorRs(String error, Integer timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }
}
