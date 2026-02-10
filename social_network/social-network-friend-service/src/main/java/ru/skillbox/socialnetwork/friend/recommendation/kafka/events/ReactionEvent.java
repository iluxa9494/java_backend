package ru.skillbox.socialnetwork.friend.recommendation.kafka.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReactionEvent(
        String eventType,          // ADDED | REMOVED | CHANGED
        UUID userId,
        EntityType entityType,
        UUID entityId,
        ReactionType reactionType,
        long totalForThisType,     // кол-во таких реакций на сущность
        OffsetDateTime at
) {}
