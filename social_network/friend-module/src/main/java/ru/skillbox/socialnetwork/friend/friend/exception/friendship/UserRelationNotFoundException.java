package ru.skillbox.socialnetwork.friend.friend.exception.friendship;

import jakarta.persistence.EntityNotFoundException;

public class UserRelationNotFoundException extends EntityNotFoundException {
    public UserRelationNotFoundException(String message) {
        super(message);
    }
}
