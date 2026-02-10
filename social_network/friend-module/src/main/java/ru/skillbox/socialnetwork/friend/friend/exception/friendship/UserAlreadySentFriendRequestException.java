package ru.skillbox.socialnetwork.friend.friend.exception.friendship;

public class UserAlreadySentFriendRequestException extends RuntimeException {
    public UserAlreadySentFriendRequestException(String message) {
        super(message);
    }
}
