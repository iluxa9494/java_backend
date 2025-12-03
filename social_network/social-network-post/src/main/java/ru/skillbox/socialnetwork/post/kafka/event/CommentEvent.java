package ru.skillbox.socialnetwork.post.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentEvent(
        String eventType,  // COMMENT_CREATED, COMMENT_UPDATED, COMMENT_DELETED
        UUID commentId,
        UUID postId,
        UUID parentId,
        String text,
        OffsetDateTime time
) {}