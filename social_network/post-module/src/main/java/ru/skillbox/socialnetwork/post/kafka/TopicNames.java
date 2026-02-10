package ru.skillbox.socialnetwork.post.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TopicNames {

    private final String postTopic;
    private final String commentTopic;
    private final String reactionTopic;

    public TopicNames(
            @Value("${app.kafka.topic.post}") String postTopic,
            @Value("${app.kafka.topic.comment}") String commentTopic,
            @Value("${app.kafka.topic.reaction}") String reactionTopic
    ) {
        this.postTopic = postTopic;
        this.commentTopic = commentTopic;
        this.reactionTopic = reactionTopic;
    }

    public String post() {
        return postTopic;
    }

    public String comment() {
        return commentTopic;
    }

    public String reaction() {
        return reactionTopic;
    }
}