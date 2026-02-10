package ru.skillbox.socialnetwork.friend.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.friend.common.client.AccountServiceClient;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.friend.mapper.FriendMapper;
import ru.skillbox.socialnetwork.friend.recommendation.dto.RecommendationFriendsDto;
import ru.skillbox.socialnetwork.friend.recommendation.mapper.RecommendationMapper;

import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.recommendation-service.enabled", havingValue = "false")
@RequiredArgsConstructor
@Slf4j
public class FakeRecommendationService implements RecommendationService {

    private final AccountServiceClient accountServiceClient;
    private final RecommendationMapper recommendationMapper;
    private final FriendMapper friendMapper;

    // todo no-correctly by swagger
    @Override
    public Page<RecommendationFriendsDto> recommendedFriends(UUID currentUserId, Pageable pageable) {
        log.info("Фейковый сервис рекомендаций: Попытка вернуть всех пользователей, которые есть в соц.сети для пользователя {}", currentUserId);

        Page<AccountDto> pageAccountDto;
        try {
            pageAccountDto = accountServiceClient.getAllAccounts(pageable);
        } catch (Exception e) {
            log.info("Возникла ошибка при попытке получить список аккаунтов из Account Service");
            throw new RuntimeException(e.getMessage());
        }

        Page<FriendDto> friendDtoPage = friendMapper.accountDtoPageToFriendDtoPageWithoutStatus(pageAccountDto);
        log.info("Фейковый сервис рекомендаций: возвращает список друзей размером {} для пользователя {}", friendDtoPage.getContent().size(), currentUserId);

        Page<RecommendationFriendsDto> pageRecommendationFriendsDto = recommendationMapper.friendDtoPageToRecommendationFriendsDtoPage(friendDtoPage);
        List<RecommendationFriendsDto> content = pageRecommendationFriendsDto.getContent().stream()
                .filter(recommendationFriendsDto -> !recommendationFriendsDto.getId().equals(currentUserId))
                .toList();
        return new PageImpl<>(content, pageRecommendationFriendsDto.getPageable(), pageRecommendationFriendsDto.getTotalElements());
    }
}
