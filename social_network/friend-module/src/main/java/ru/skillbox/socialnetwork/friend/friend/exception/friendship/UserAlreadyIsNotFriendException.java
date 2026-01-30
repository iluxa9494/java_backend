package ru.skillbox.socialnetwork.friend.friend.exception.friendship;

public class UserAlreadyIsNotFriendException extends RuntimeException {
    public UserAlreadyIsNotFriendException(String message) {
        super(message);
    }
}
