package ru.skillbox.socialnetwork.post.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostEvent(
        String eventType,     // POST_CREATED, POST_UPDATED, POST_DELETED, POST_RESTORED
        UUID postId,
        UUID authorId,
        String title,
        OffsetDateTime time
) {}