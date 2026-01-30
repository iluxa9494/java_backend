package ru.skillbox.socialnetwork.friend.friend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendsSearchRequest;

import java.util.List;
import java.util.UUID;

public interface FriendshipService {

    void approve(UUID userId, UUID friendId);

    void unblock(UUID userId, UUID friendId);

    void block(UUID userId, UUID friendId);

    void request(UUID userId, UUID friendId);

    void subscribe(UUID userId, UUID friendId);

    AccountDto findAccountByFriendId(UUID friendId);

    List<UUID> findFriendsId(UUID userId);

    Integer findCountFriends(UUID userId);

    List<UUID> getBlockFriendIds(UUID userId);

    void deleteById(UUID userId, UUID friendId);

    Page<FriendDto> findPageFriends(UUID userId, FriendsSearchRequest request, Pageable pageable);

}
