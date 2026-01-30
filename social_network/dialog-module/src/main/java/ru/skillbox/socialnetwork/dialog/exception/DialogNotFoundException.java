package ru.skillbox.socialnetwork.dialog.exception;

import java.util.UUID;

public class DialogNotFoundException extends RuntimeException {
    public DialogNotFoundException(UUID dialogId) {
        super("Dialog not found: " + dialogId);
    }

    public DialogNotFoundException(UUID currentUserId, UUID otherUserId) {
        super("Dialog not found between users: " + currentUserId + " and " + otherUserId);
    }
}
