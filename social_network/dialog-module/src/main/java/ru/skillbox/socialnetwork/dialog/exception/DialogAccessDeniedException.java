package ru.skillbox.socialnetwork.dialog.exception;

import java.util.UUID;

public class DialogAccessDeniedException extends RuntimeException {
    public DialogAccessDeniedException(UUID dialogId, UUID userId) {
        super("User " + userId + " attempted to access dialog " + dialogId + " without permission");
    }
}
