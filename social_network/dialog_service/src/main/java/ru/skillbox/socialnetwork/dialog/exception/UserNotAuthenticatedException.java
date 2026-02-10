package ru.skillbox.socialnetwork.dialog.exception;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException() {
        super("User is not authenticated");
    }

    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
