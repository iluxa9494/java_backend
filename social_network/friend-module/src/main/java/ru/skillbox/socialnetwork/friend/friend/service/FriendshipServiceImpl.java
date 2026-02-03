package ru.skillbox.socialnetwork.friend.friend.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.friend.common.client.AccountServiceClient;
import ru.skillbox.socialnetwork.friend.friend.dto.account.*;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.*;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsNotFoundException;
import ru.skillbox.socialnetwork.friend.friend.exception.friendship.NotSelfRequestException;
import ru.skillbox.socialnetwork.friend.friend.exception.friendship.UserRelationNotFoundException;
import ru.skillbox.socialnetwork.friend.friend.kafka.service.KafkaEventService;
import ru.skillbox.socialnetwork.friend.friend.mapper.FriendMapper;
import ru.skillbox.socialnetwork.friend.friend.mapper.UserRelationMapper;
import ru.skillbox.socialnetwork.friend.friend.model.FriendshipPair;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelation;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelationId;
import ru.skillbox.socialnetwork.friend.friend.kafka.enums.EventType;
import ru.skillbox.socialnetwork.friend.friend.policy.relations.FriendStatusPolicy;
import ru.skillbox.socialnetwork.friend.friend.repository.UserRelationRepository;
import ru.skillbox.socialnetwork.friend.friend.service.utils.AccountStatusValidator;
import ru.skillbox.socialnetwork.friend.friend.service.utils.FriendStatusValidator;

import java.util.*;
import java.util.stream.Collectors;

// todo необходимо реализовать проверки на существование account в методах
// todo на текущий момент запросы работают с любыми friendId uuid, без проверки на существование аккаунта
// todo из-за этого может возникнуть баг, что появятся друзья без аккаунта
@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {

    private final AccountServiceClient accountServiceClient;
    private final KafkaEventService kafkaEventService;

    private final UserRelationRepository relationRepository;
    private final UserRelationMapper relationMapper;
    private final FriendMapper friendMapper;

    @Override
    public void approve(UUID userId, UUID friendId) {
        validateNotSelfRequest(userId, friendId);
        log.debug("Попытка принять запрос в друзья. UserId: {}, FriendId: {}", userId, friendId);

        AccountStatus friendStatus = accountServiceClient.getStatus(friendId);
        AccountStatusValidator.checkBeforeApprovingFriendRequest(friendStatus);

        FriendshipPair pair = findMutualRelations(userId, friendId);
        FriendStatusValidator.checkBeforeApprovingFriendRequest(pair);

        pair.userToFriend().setStatusCode(FriendStatusPolicy.STATUS_AFTER_FRIEND_APPROVE);
        pair.friendToUser().setStatusCode(FriendStatusPolicy.STATUS_FRIEND_AFTER_FRIEND_APPROVE);

        relationRepository.saveAll(List.of(pair.userToFriend(), pair.friendToUser()));
    }

    @Override
    public void unblock(UUID userId, UUID friendId) {
        validateNotSelfRequest(userId, friendId);
        log.debug("Попытка разблокировать друга. UserId: {}, FriendId: {}", userId, friendId);

        AccountStatus friendStatus = accountServiceClient.getStatus(friendId);
        AccountStatusValidator.checkBeforeUnblockingRequest(friendStatus);

        FriendshipPairDto pairDto = findMutualRelationsDto(userId, friendId);
        FriendStatusValidator.checkBeforeUnblockingFriend(pairDto);

        pairDto.userToFriend().setStatusCode(FriendStatusPolicy.STATUS_AFTER_UNBLOCKING);

        if (pairDto.friendToUser().getStatusCode() != FriendStatusPolicy.DEFAULT_STATUS) {
            relationRepository.deleteById(new UserRelationId(pairDto.friendToUser().getUserId(), pairDto.userToFriend().getFriendId()));
        }

        relationRepository.save(relationMapper.dtoToEntity(pairDto.userToFriend()));
    }

    @Override
    public void block(UUID userId, UUID friendId) {
        validateNotSelfRequest(userId, friendId);
        log.debug("Попытка заблокировать друга. UserId: {}, FriendId: {}", userId, friendId);

        AccountStatus friendStatus = accountServiceClient.getStatus(friendId);
        AccountStatusValidator.checkBeforeBlockingRequest(friendStatus);

        FriendshipPairDto pairDto = findMutualRelationsDto(userId, friendId);
        FriendStatusValidator.checkBeforeBlockingFriend(pairDto);

        pairDto.userToFriend().setStatusCode(FriendStatusPolicy.STATUS_AFTER_BLOCKING);

        if (pairDto.friendToUser().getStatusCode() != FriendStatusPolicy.DEFAULT_STATUS) {
            pairDto.friendToUser().setStatusCode(FriendStatusPolicy.STATUS_FRIEND_AFTER_BLOCKING);
            FriendshipPair pair = relationMapper.dtoPairToEntityPair(pairDto);
            relationRepository.saveAll(List.of(pair.userToFriend(), pair.friendToUser())); // сохраняем обе связи
        }

        relationRepository.save(relationMapper.dtoToEntity(pairDto.userToFriend()));
    }

    @Override
    @Transactional(transactionManager = "friendTransactionManager")
    public void request(UUID userId, UUID friendId) {
        validateNotSelfRequest(userId, friendId);
        log.debug("Попытка отправить запрос на добавление в друзья. UserId: {}, FriendId: {}", userId, friendId);

        AccountStatus friendStatus;
        try {
            friendStatus = accountServiceClient.getStatus(friendId);
        } catch (Exception e) {
            log.warn("Микросервис аккаунтов не нашел аккаунт или недоступен. UserId {}, FriendId {}", userId, friendId);
            throw new AccountStatusIsNotFoundException("Микросервис аккаунтов не нашел аккаунт или недоступен.");
        }
        AccountStatusValidator.checkBeforeFriendRequestSending(friendStatus);

        FriendshipPairDto pairDto = findMutualRelationsDto(userId, friendId);
        FriendStatusValidator.checkBeforeSendingFriendRequest(pairDto);

        pairDto.userToFriend().setStatusCode(FriendStatusPolicy.STATUS_AFTER_FRIEND_REQUEST);
        pairDto.friendToUser().setStatusCode(FriendStatusPolicy.STATUS_FRIEND_AFTER_FRIEND_REQUEST);

        FriendshipPair pair = relationMapper.dtoPairToEntityPair(pairDto);
        relationRepository.saveAll(List.of(pair.userToFriend(), pair.friendToUser()));

        kafkaEventService.publishEvent(userId, friendId, EventType.FRIEND_REQUEST_CREATED);
    }

    private void requestAndSave() {

    }
    @Override
    public void subscribe(UUID userId, UUID friendId) {
        validateNotSelfRequest(userId, friendId);
        log.debug("Попытка подписаться на пользователя. UserId: {}, FriendId: {}", userId, friendId);

        AccountStatus friendStatus = accountServiceClient.getStatus(friendId);
        AccountStatusValidator.checkBeforeSubscribeRequest(friendStatus);

        FriendshipPairDto pairDto = findMutualRelationsDto(userId, friendId);
        FriendStatusValidator.checkBeforeSubscribe(pairDto);

        pairDto.userToFriend().setStatusCode(FriendStatusPolicy.STATUS_AFTER_SUBSCRIBE);
        pairDto.friendToUser().setStatusCode(FriendStatusPolicy.STATUS_FRIEND_AFTER_SUBSCRIBE);

        relationRepository.save(relationMapper.dtoToEntity(pairDto.userToFriend()));
    }

    @Override
    public AccountDto findAccountByFriendId(UUID friendId) {
        return accountServiceClient.getAccountById(friendId);
    }

    @Override
    public List<UUID> findFriendsId(UUID userId) {
        log.debug("Попытка найти id всех друзей. UserId: {}", userId);

        return relationRepository.findByIdUserIdAndStatusCode(userId, FriendStatusPolicy.STATUS_FOR_FINDING_IDS_FRIENDS)
                .stream()
                .map(relation -> relation.getId().getFriendId())
                .collect(Collectors.toList());
    }

    @Override
    public Integer findCountFriends(UUID userId) {
        log.debug("Попытка найти количество друзей. UserId: {}", userId);

        return relationRepository.countByIdUserIdAndStatusCode(userId, FriendStatusPolicy.STATUS_FOR_FINDING_COUNT_FRIENDS);
    }

    @Override
    public List<UUID> getBlockFriendIds(UUID userId) {
        log.debug("Попытка найти заблокированных пользователей. UserId: {}", userId);

        return relationRepository.findByIdUserIdAndStatusCode(userId, FriendStatusPolicy.STATUS_FOR_FINDING_BLOCKED_FRIENDS).stream()
                .map(relation -> relation.getId().getFriendId())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID userId, UUID friendId) {
        validateNotSelfRequest(userId, friendId);
        log.debug("Попытка удалить друга. UserId: {}, FriendId: {}", userId, friendId);

        AccountStatus friendStatus = accountServiceClient.getStatus(friendId);
        AccountStatusValidator.checkBeforeDeletingFriend(friendStatus);


        FriendshipPairDto pairDto = findMutualRelationsDto(userId, friendId);
        FriendStatusValidator.checkBeforeDelete(pairDto);

        peekStatusesAfterDelete(pairDto);

        FriendshipPair pair = relationMapper.dtoPairToEntityPair(pairDto);
        relationRepository.saveAll(List.of(pair.userToFriend(), pair.friendToUser()));
    }

    private FriendshipPairDto peekStatusesAfterDelete(FriendshipPairDto pairDto) {
        if (pairDto.userToFriend().getStatusCode() == StatusCode.WATCHING) {
            pairDto.userToFriend().setStatusCode(StatusCode.NONE);
            pairDto.friendToUser().setStatusCode(StatusCode.NONE);
            return pairDto;
        }
        if (pairDto.userToFriend().getStatusCode() == StatusCode.SUBSCRIBED) {
            pairDto.userToFriend().setStatusCode(StatusCode.NONE);
            pairDto.friendToUser().setStatusCode(StatusCode.NONE);
            return pairDto;
        }
        if (pairDto.friendToUser().getStatusCode() == StatusCode.WATCHING) {
            pairDto.userToFriend().setStatusCode(StatusCode.NONE);
            pairDto.friendToUser().setStatusCode(StatusCode.NONE);
            return pairDto;
        }
        if (pairDto.friendToUser().getStatusCode() == StatusCode.SUBSCRIBED) {
            pairDto.userToFriend().setStatusCode(StatusCode.NONE);
            pairDto.friendToUser().setStatusCode(StatusCode.NONE);
            return pairDto;
        }
        if (pairDto.userToFriend().getStatusCode() == StatusCode.REQUEST_FROM) {
            pairDto.userToFriend().setStatusCode(StatusCode.DECLINED);
            pairDto.friendToUser().setStatusCode(StatusCode.WATCHING);
            return pairDto;
        }
        if (pairDto.userToFriend().getStatusCode() == StatusCode.REQUEST_TO) { // удаление отправленного запроса в друзья
            pairDto.userToFriend().setStatusCode(StatusCode.NONE);
            pairDto.friendToUser().setStatusCode(StatusCode.NONE);
            return pairDto;
        }
        if (pairDto.friendToUser().getStatusCode() == StatusCode.REQUEST_FROM) {
            pairDto.userToFriend().setStatusCode(StatusCode.NONE);
            pairDto.friendToUser().setStatusCode(StatusCode.NONE);
            return pairDto;
        }
        if (pairDto.friendToUser().getStatusCode() == StatusCode.REQUEST_TO) {
            pairDto.userToFriend().setStatusCode(StatusCode.DECLINED);
            pairDto.friendToUser().setStatusCode(StatusCode.WATCHING);
            return pairDto;
        }
        if (pairDto.friendToUser().getStatusCode() == StatusCode.FRIEND) {
            pairDto.userToFriend().setStatusCode(StatusCode.SUBSCRIBED);
            pairDto.friendToUser().setStatusCode(StatusCode.WATCHING);
            return pairDto;
        }

        return pairDto;
    }

    @Override
    public Page<FriendDto> findPageFriends(UUID userId, FriendsSearchRequest request, Pageable pageable) {

        StatusCode statusCode = validateAndConvertStatusCode(request.getStatusCode());

        List<UUID> targetIds = findTargetIds(userId, request.getIds(), statusCode);

        if (targetIds == null || targetIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<AccountDto> accountDtoPage;
        try {
            AccountByFilterDto accountByFilterDto = createAccountFilter(targetIds, request, pageable);
            log.info("По statusCode {}, попытка найти количество аккаунтов: {}", statusCode ,accountByFilterDto.getAccountSearchDto().getIds());
            accountDtoPage = accountServiceClient.getAccountsByFilter(accountByFilterDto, pageable);
            log.info("По списку userId размером {},  найдено {} аккаунтов", targetIds.size(), accountDtoPage.getContent().size());
        } catch (Exception e) {
            log.info("Возникла ошибка при попытке получить список аккаунтов из Account Service");
            throw new RuntimeException(e.getMessage());
        }
        List<AccountDto> accountList = accountDtoPage.getContent();
        log.info("Получен список аккаунтов: count {}", accountList.size());

        Page<FriendDto> pageFriendDtoWithoutStatuses = friendMapper.accountDtoPageToFriendDtoPageWithoutStatus(accountDtoPage);

        if (statusCode == null) {
            enrichFriendDtoList(userId, pageFriendDtoWithoutStatuses.getContent());
        } else {
            pageFriendDtoWithoutStatuses.getContent().forEach(friendDto -> friendDto.setStatusCode(statusCode));
        }

        return pageFriendDtoWithoutStatuses;
    }

    @Transactional(transactionManager = "friendTransactionManager")
    private void enrichFriendDtoList(UUID userId, List<FriendDto> friendDtoList) {

        List<UUID> friendIds = friendDtoList.stream()
                .map(FriendDto::getFriendId)
                .collect(Collectors.toList());

        Map<UUID, StatusCode> statusMap = relationRepository
                .findByIdUserIdAndIdFriendIdIn(userId, friendIds)
                .stream()
                .collect(Collectors.toMap(
                        relation -> relation.getId().getFriendId(),
                        UserRelation::getStatusCode
                ));

        friendDtoList.forEach(friendDto -> {
            StatusCode status = statusMap.get(friendDto.getFriendId());
            friendDto.setStatusCode(status != null ? status : StatusCode.NONE);
        });

    }

    private StatusCode validateAndConvertStatusCode(String statusCodeStr) {
        if (statusCodeStr == null || statusCodeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return StatusCode.valueOf(statusCodeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status code provided: {}", statusCodeStr);
            throw new InvalidRequestException("Invalid status code. Allowed values: " +
                    Arrays.toString(StatusCode.values()));
        }
    }

    private List<UUID> findTargetIds(UUID userId, List<UUID> friendIds, StatusCode statusCode) {
        List<UUID> targetIds = new ArrayList<>();

        if (friendIds != null && statusCode != null) {
            targetIds = relationRepository.findFriendIdsByIdUserIdAndIdFriendIdInAndStatusCode(userId, friendIds, statusCode);
            log.info("Пользователь {} передал список ID друзей размером {}, для поиска по статусу {}", userId, friendIds.size(), statusCode);
            return targetIds;
        }

        if (friendIds == null && statusCode != null) {
            targetIds = relationRepository.findFriendIdsByUserIdAndStatus(userId, statusCode);
            log.info("Пользователь {} передал статус {} для поиска друзей", userId, statusCode);
            return targetIds;
        }

        if (friendIds != null && statusCode == null) {
            log.info("Пользователь {} передал список ID друзей {} для поиска друзей", userId, friendIds);
            return friendIds;
        }

        log.warn("Пользователь {} не передал список друзей и статус для поиска друзей", userId);
        return targetIds;
    }

    private AccountByFilterDto createAccountFilter(List<UUID> targetIds, FriendsSearchRequest request, Pageable pageable) {
        AccountSearchDto accountSearch = friendMapper.toAccountSearchDto(request);
        accountSearch.setIds(targetIds);

        AccountByFilterDto filter = new AccountByFilterDto();
        filter.setAccountSearchDto(accountSearch);
        filter.setPageNumber(pageable.getPageNumber());
        filter.setPageSize(pageable.getPageSize());

        return filter;
    }

    private UserRelationDto getDefaultRelation(UUID userId, UUID friendId) {
        return new UserRelationDto(userId, friendId, FriendStatusPolicy.DEFAULT_STATUS);
    }

    private FriendshipPair findMutualRelations(UUID userId, UUID friendId) {
        UserRelation userToFriend = relationRepository
                .findById(new UserRelationId(userId, friendId))
                .orElseThrow(() -> new UserRelationNotFoundException("Связь от пользователя к другу не найдена"));

        UserRelation friendToUser = relationRepository
                .findById(new UserRelationId(friendId, userId))
                .orElseThrow(() -> new UserRelationNotFoundException("Связь от друга к пользователю не найдена"));

        return new FriendshipPair(userToFriend, friendToUser);
    }

    private FriendshipPairDto findMutualRelationsDto(UUID userId, UUID friendId) {
        UserRelationDto userToFriend = relationRepository
                .findById(new UserRelationId(userId, friendId))
                .map(relationMapper::entityToDto)
                .orElse(getDefaultRelation(userId, friendId));

        UserRelationDto friendToUser = relationRepository
                .findById(new UserRelationId(friendId, userId))
                .map(relationMapper::entityToDto)
                .orElse(getDefaultRelation(friendId, userId));

        return new FriendshipPairDto(userToFriend, friendToUser);
    }

    public void validateNotSelfRequest(UUID senderId, UUID recipientId) {
        if (senderId.equals(recipientId)) {
            throw new NotSelfRequestException("Cannot send request to yourself");
        }
    }

}
