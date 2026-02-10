package com.team58.mc_notifications.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic friendRequestTopic(
            @Value("${kafka.topic.friend-request:friend-request}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic likeTopic(@Value("${kafka.topic.like:like}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic commentTopic(@Value("${kafka.topic.comment:comment}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic replyTopic(@Value("${kafka.topic.reply:reply}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic postTopic(@Value("${kafka.topic.post:post}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic birthdayTopic(@Value("${kafka.topic.birthday:birthday}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic messageTopic(@Value("${kafka.topic.message:message}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
