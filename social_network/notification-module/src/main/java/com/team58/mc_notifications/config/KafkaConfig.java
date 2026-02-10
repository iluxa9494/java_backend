package com.team58.mc_notifications.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean(name = "notificationFriendRequestTopic")
    public NewTopic notificationFriendRequestTopic(
            @Value("${kafka.topic.friend-request:friend-request}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean(name = "notificationLikeTopic")
    public NewTopic notificationLikeTopic(@Value("${kafka.topic.like:like}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean(name = "notificationCommentTopic")
    public NewTopic notificationCommentTopic(@Value("${kafka.topic.comment:comment}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean(name = "notificationReplyTopic")
    public NewTopic notificationReplyTopic(@Value("${kafka.topic.reply:reply}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean(name = "notificationPostTopic")
    public NewTopic notificationPostTopic(@Value("${kafka.topic.post:post}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean(name = "notificationBirthdayTopic")
    public NewTopic notificationBirthdayTopic(@Value("${kafka.topic.birthday:birthday}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean(name = "notificationMessageTopic")
    public NewTopic notificationMessageTopic(@Value("${kafka.topic.message:message}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
