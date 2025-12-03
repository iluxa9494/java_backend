package ru.skillbox.socialnetwork.friend.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.StatusCode;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelation;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelationId;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, UserRelationId> {

    @Query("SELECT ur.id.friendId FROM UserRelation ur WHERE ur.id.userId = :userId AND ur.statusCode = :status")
    List<UUID> findFriendIdsByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") StatusCode status);

    List<UserRelation> findByIdIn(List<UserRelationId> ids);

    List<UserRelation> findByIdUserId(UUID idUserId);

    List<UserRelation> findByIdUserIdAndStatusCode(UUID idUserId, StatusCode statusCode);

    Integer countByIdUserIdAndStatusCode(UUID userId, StatusCode statusCode);

    List<UUID> findFriendIdsByIdUserIdAndIdFriendIdInAndStatusCode(UUID userId, List<UUID> friendIds, StatusCode statucCode);

    List<UserRelation> findByIdUserIdAndIdFriendIdIn(UUID userId, List<UUID> friendIds);
}
