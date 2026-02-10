package ru.skillbox.socialnetwork.integration.exception;

public class GeoException extends RuntimeException {

    public GeoException() {
    }

    public GeoException(String message) {
        super(message);
    }

    public GeoException(String message, Throwable cause) {
        super(message, cause);
    }
}
