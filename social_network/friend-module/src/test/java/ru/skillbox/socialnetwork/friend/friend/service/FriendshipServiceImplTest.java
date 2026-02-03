package ru.skillbox.socialnetwork.friend.friend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.skillbox.socialnetwork.friend.common.client.AccountServiceClient;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendsSearchRequest;
import ru.skillbox.socialnetwork.friend.friend.mapper.FriendMapper;
import ru.skillbox.socialnetwork.friend.friend.mapper.UserRelationMapper;
import ru.skillbox.socialnetwork.friend.friend.kafka.service.KafkaEventService;
import ru.skillbox.socialnetwork.friend.friend.repository.UserRelationRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceImplTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private KafkaEventService kafkaEventService;

    @Mock
    private UserRelationRepository relationRepository;

    @Mock
    private UserRelationMapper relationMapper;

    @Mock
    private FriendMapper friendMapper;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    @Test
    void findPageFriends_returnsEmptyPageWithProvidedPageable() {
        UUID userId = UUID.randomUUID();
        FriendsSearchRequest request = new FriendsSearchRequest();
        Pageable pageable = PageRequest.of(0, 20);

        Page<?> result = friendshipService.findPageFriends(userId, request, pageable);

        assertThat(result.getPageable()).isEqualTo(pageable);
        assertThat(result.getContent()).isEmpty();
    }
}
