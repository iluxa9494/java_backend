package ru.skillbox.socialnetwork.authentication.exceptions;

public class AlreadyExitsException extends RuntimeException {
    public AlreadyExitsException(String message) {
        super(message);
    }
}
