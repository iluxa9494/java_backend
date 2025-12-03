package ru.skillbox.socialnetwork.friend.friend.exception.security;

public class JwtTokenInvalidException extends RuntimeException {
    public JwtTokenInvalidException(String message) {
        super(message);
    }
}
