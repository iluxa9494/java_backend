package ru.skillbox.socialnetwork.post.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReactionEvent(
        String eventType,     // REACTION_ADDED, REACTION_REMOVED, REACTION_CHANGED
        String entityType,    // POST or COMMENT
        UUID entityId,
        UUID userId,
        String reactionType,  // LIKE, DISLIKE, HEART...
        OffsetDateTime time
) {}