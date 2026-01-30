package ru.skillbox.socialnetwork.post.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.post.kafka.event.CommentEvent;
import ru.skillbox.socialnetwork.post.kafka.event.PostEvent;
import ru.skillbox.socialnetwork.post.kafka.event.ReactionEvent;

@Component
@RequiredArgsConstructor
public class PostEventsProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicNames topics;

    public void sendPost(PostEvent event) {
        kafkaTemplate.send(topics.post(), event.postId().toString(), event);
    }

    public void sendComment(CommentEvent event) {
        kafkaTemplate.send(topics.comment(), event.postId().toString(), event);
    }

    public void sendReaction(ReactionEvent event) {
        kafkaTemplate.send(topics.reaction(), event.entityId().toString(), event);
    }
}