package ru.skillbox.socialnetwork.post.events;

import ru.skillbox.socialnetwork.post.entity.EntityType;
import ru.skillbox.socialnetwork.post.entity.ReactionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReactionEvent(
        String eventType,          // ADDED | REMOVED | CHANGED
        UUID userId,
        EntityType entityType,
        UUID entityId,
        ReactionType reactionType,
        long totalForThisType,     // кол-во таких реакций на сущность
        OffsetDateTime at
) {}