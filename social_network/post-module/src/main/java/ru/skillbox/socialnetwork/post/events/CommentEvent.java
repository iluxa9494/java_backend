package ru.skillbox.socialnetwork.post.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentEvent(
        String eventType,          // CREATED | UPDATED | DELETED
        UUID id,
        UUID postId,
        UUID parentId,
        String commentText,
        long likeAmount,
        boolean deleted,
        OffsetDateTime time,
        OffsetDateTime timeChanged
) {}