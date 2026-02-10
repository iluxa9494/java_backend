package ru.skillbox.socialnetwork.friend.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.recommendation.dto.RecommendationFriendsDto;
import ru.skillbox.socialnetwork.friend.recommendation.mapper.RecommendationMapper;
import ru.skillbox.socialnetwork.friend.friend.provider.FriendProviderService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.recommendation-service.enabled", havingValue = "true", matchIfMissing = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final FriendProviderService friendProviderService;
    private final RecommendationMapper recommendationMapper;

    @Override
    public Page<RecommendationFriendsDto> recommendedFriends(UUID currentUserId, Pageable pageable) {

        Page<FriendDto> pageFriendDto = friendProviderService.findFriendsOfFriendsForRecommendations(currentUserId, pageable);

        return recommendationMapper.friendDtoPageToRecommendationFriendsDtoPage(pageFriendDto);
    }

}
