package ru.skillbox.socialnetwork.post.service;

import ru.skillbox.socialnetwork.post.entity.EntityType;
import ru.skillbox.socialnetwork.post.entity.ReactionType;

import java.util.UUID;

public interface ReactionService {
    ReactionResult react(UUID userId, EntityType entityType, UUID entityId, ReactionType type);
    void remove(UUID userId, EntityType entityType, UUID entityId);
}