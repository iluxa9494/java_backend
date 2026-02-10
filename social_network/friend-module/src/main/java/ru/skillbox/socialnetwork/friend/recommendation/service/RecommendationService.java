package ru.skillbox.socialnetwork.friend.recommendation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.skillbox.socialnetwork.friend.recommendation.dto.RecommendationFriendsDto;

import java.util.UUID;

public interface RecommendationService {

    Page<RecommendationFriendsDto> recommendedFriends(UUID currentUserId, Pageable pageable);
}
