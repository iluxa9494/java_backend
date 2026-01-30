package ru.skillbox.socialnetwork.friend.friend.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.friend.common.kafka.topics.KafkaTopicConfig;
import ru.skillbox.socialnetwork.friend.friend.kafka.enums.EventType;
import ru.skillbox.socialnetwork.friend.friend.policy.message.NotificationMessagePolicy;

@Service
@RequiredArgsConstructor
public class KafkaTopicService {
    private final KafkaTopicConfig topicConfig;

    public String getTopicFor(EventType event) {
        return switch (event) {
            case FRIEND_REQUEST_CREATED -> topicConfig.getFriendRequest();
        };
    }

    public String getContentFor(EventType event) {
        return switch (event) {
            case FRIEND_REQUEST_CREATED -> NotificationMessagePolicy.FRIEND_REQUEST_MESSAGE;
        };
    }
}