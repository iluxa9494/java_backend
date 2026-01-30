package ru.skillbox.socialnetwork.post.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostEvent(
        String eventType,          // CREATED | UPDATED | DELETED
        UUID id,
        UUID authorId,
        String title,
        String text,
        long likeAmount,
        long commentsCount,
        OffsetDateTime time,
        OffsetDateTime timeChanged
) {}