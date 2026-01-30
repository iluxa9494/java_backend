package ru.skillbox.socialnetwork.account.exception;

public class ValidationException extends RuntimeException {
    private final String field;
    private final String errorCode;

    public ValidationException(String message, String field, String errorCode) {
        super(message);
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}