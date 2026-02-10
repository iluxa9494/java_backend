package ru.skillbox.socialnetwork.friend.friend.provider;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;

import java.util.UUID;

public interface FriendProviderService {

    Page<FriendDto> findFriendsOfFriendsForRecommendations(UUID currentUserId, Pageable pageable);

}
