package ru.skillbox.socialnetwork.friend.recommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.friend.recommendation.model.RecommendationFriends;

import java.util.UUID;

@Repository
public interface RecommendationFriendsRepository extends JpaRepository<RecommendationFriends, UUID> {
}
