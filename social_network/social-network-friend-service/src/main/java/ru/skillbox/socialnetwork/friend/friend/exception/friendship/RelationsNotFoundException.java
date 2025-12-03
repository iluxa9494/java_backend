package ru.skillbox.socialnetwork.friend.friend.exception.friendship;

import jakarta.persistence.EntityNotFoundException;

public class RelationsNotFoundException extends EntityNotFoundException {
    public RelationsNotFoundException(String message) {
        super(message);
    }
}
