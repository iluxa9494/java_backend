package ru.skillbox.socialnetwork.friend.friend.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.friend.common.client.AccountServiceClient;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.StatusCode;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.UserRelationDto;
import ru.skillbox.socialnetwork.friend.friend.mapper.FriendMapper;
import ru.skillbox.socialnetwork.friend.friend.mapper.UserRelationMapper;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelation;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelationId;
import ru.skillbox.socialnetwork.friend.friend.policy.provider.RecommendationPolicy;
import ru.skillbox.socialnetwork.friend.friend.repository.UserRelationRepository;
import ru.skillbox.socialnetwork.friend.common.security.SecurityUtils;
import ru.skillbox.socialnetwork.friend.friend.service.FriendshipService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendProviderServiceImpl implements FriendProviderService {

    private final UserRelationRepository relationRepository;
    private final AccountServiceClient accountServiceClient;
    private final FriendshipService friendService;

    private final UserRelationMapper userRelationMapper;
    private final FriendMapper friendMapper;

    @Override
    public Page<FriendDto> findFriendsOfFriendsForRecommendations(UUID currentUserId, Pageable pageable) {

        List<UUID> friendList = friendService.findFriendsId(currentUserId);

        Set<UUID> friendOfFriendIds = friendList.stream()
                .flatMap(friendId -> findFriendsIdsByUserId(friendId).stream())
                .filter(friendId -> !friendId.equals(currentUserId))
                .collect(Collectors.toSet());

        return findRecommendedFriendsByFriendIds(friendOfFriendIds,  pageable);
    }

    private List<UUID> findFriendsIdsByUserId(UUID userId) {
        log.info("Попытка найти друзей у пользователя с UserId: {}", userId);

        return relationRepository.findByIdUserId(userId)
                .stream()
                .map(relation -> relation.getId().getFriendId())
                .collect(Collectors.toList());
    }

    private Page<FriendDto> findRecommendedFriendsByFriendIds(Set<UUID> ids, Pageable pageable) {
        log.info("Попытка найти рекомендуемых друзей из списка друзей. FriendIds: {}", ids);

        UUID userId = SecurityUtils.getCurrentUserId();

        List<UserRelationId> userToFriendsRelationIds = ids.stream()
                .map(friendId -> new UserRelationId(userId, friendId))
                .toList();

        List<UserRelationId> friendsToUserRelationIds = ids.stream()
                .map(friendId -> new UserRelationId(friendId, userId))
                .toList();

        List<UserRelationDto> userToFriendRelations = findByIdIn(userToFriendsRelationIds);
        List<UserRelationDto> friendToUserRelations = findByIdIn(friendsToUserRelationIds);

        var statuses = RecommendationPolicy.FRIEND_STATUSES_FOR_FINDING_FRIENDS_OF_FRIENDS_RECOMMENDATIONS;

        Set<UUID> notRecommendedFromUserToFriend = userToFriendRelations.stream()
                .filter(rel -> !statuses.contains(rel.getStatusCode()))
                .map(UserRelationDto::getFriendId)
                .collect(Collectors.toSet());

        Set<UUID> notRecommendedFromFriendToUser = friendToUserRelations.stream()
                .filter(rel -> !statuses.contains(rel.getStatusCode()))
                .map(UserRelationDto::getUserId)
                .collect(Collectors.toSet());

        Set<UUID> notRecommendedFriendIds = new HashSet<>();

        notRecommendedFriendIds.addAll(notRecommendedFromUserToFriend);
        notRecommendedFriendIds.addAll(notRecommendedFromFriendToUser);

        log.info("Список не рекомендуемых друзей: {} ", notRecommendedFriendIds.size());
        List<UUID> recommendedFriendIds = userToFriendRelations.stream()
                .map(UserRelationDto::getFriendId)
                .filter(friendId -> !notRecommendedFriendIds.contains(friendId))
                .toList();
        log.info("Список рекомендуемых друзей: {} ", recommendedFriendIds.size());

        Page<AccountDto> pageAccountDto = accountServiceClient.findAccountsByIds(recommendedFriendIds, pageable);
        return friendMapper.accountDtoPageToFriendDtoPageWithoutStatus(pageAccountDto);
    }

    private List<UserRelationDto> findByIdIn(List<UserRelationId> userToFriendRelations) {
        List<UserRelation> userRelations = relationRepository.findByIdIn(userToFriendRelations);
        List<UserRelationId> userRelationIds = userRelations.stream()
                .map(UserRelation::getId)
                .toList();

        List<UserRelationDto> userRelationDtoList = userRelationMapper.entityListToDtoList(userRelations);

        for (UserRelationId userRelationId : userToFriendRelations) {

            if (!userRelationIds.contains(userRelationId)) {
                UserRelationDto userRelationDto = new UserRelationDto();

                userRelationDto.setFriendId(userRelationId.getFriendId());
                userRelationDto.setUserId(userRelationId.getUserId());
                userRelationDto.setStatusCode(StatusCode.NONE);

                userRelationDtoList.add(userRelationDto);
            }
        }

        return userRelationDtoList;
    }

}
