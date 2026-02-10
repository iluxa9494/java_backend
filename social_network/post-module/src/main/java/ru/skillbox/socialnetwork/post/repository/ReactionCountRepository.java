package ru.skillbox.socialnetwork.post.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.post.entity.EntityType;
import ru.skillbox.socialnetwork.post.entity.ReactionCount;
import ru.skillbox.socialnetwork.post.entity.ReactionType;

import java.util.*;

@Repository
public interface ReactionCountRepository extends JpaRepository<ReactionCount, UUID> {

    Optional<ReactionCount> findByEntityTypeAndEntityIdAndReactionType(
            EntityType entityType, UUID entityId, ReactionType reactionType);

    List<ReactionCount> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    @Modifying
    @Query("""
        update ReactionCount rc
           set rc.count = rc.count + :delta,
               rc.updatedAt = CURRENT_TIMESTAMP
         where rc.entityType   = :entityType
           and rc.entityId     = :entityId
           and rc.reactionType = :reactionType
        """)
    int addDelta(@Param("entityType") EntityType entityType,
                 @Param("entityId") UUID entityId,
                 @Param("reactionType") ReactionType reactionType,
                 @Param("delta") long delta);
}