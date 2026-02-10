package ru.skillbox.socialnetwork.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnetwork.post.entity.EntityType;
import ru.skillbox.socialnetwork.post.entity.ReactionEntity;

import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<ReactionEntity, UUID> {

    Optional<ReactionEntity> findByUserIdAndEntityTypeAndEntityId(
            UUID userId, EntityType entityType, UUID entityId
    );
}