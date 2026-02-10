package ru.skillbox.socialnetwork.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.post.config.KafkaTopicsProperties;
import ru.skillbox.socialnetwork.post.events.CommentEvent;
import ru.skillbox.socialnetwork.post.events.PostEvent;
import ru.skillbox.socialnetwork.post.events.ReactionEvent;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafka;
    private final KafkaTopicsProperties topics;

    public void publishPost(PostEvent event) {
        kafka.send(topics.post(), event.id().toString(), event);
    }

    public void publishComment(CommentEvent event) {
        kafka.send(topics.comment(), event.postId().toString(), event);
    }

    public void publishReaction(ReactionEvent event) {
        String key = event.entityType() + ":" + event.entityId();
        kafka.send(topics.reaction(), key, event);
    }
}