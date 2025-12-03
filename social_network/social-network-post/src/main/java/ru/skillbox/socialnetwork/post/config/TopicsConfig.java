package ru.skillbox.socialnetwork.post.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class TopicsConfig {

    private final KafkaTopicsProperties topics;

    public TopicsConfig(KafkaTopicsProperties topics) {
        this.topics = topics;
    }

    @Bean
    public NewTopic postTopic() {
        return TopicBuilder.name(topics.post())
                .partitions(topics.partitions())
                .replicas(topics.replicas())
                .build();
    }

    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder.name(topics.comment())
                .partitions(topics.partitions())
                .replicas(topics.replicas())
                .build();
    }

    @Bean
    public NewTopic reactionTopic() {
        return TopicBuilder.name(topics.reaction())
                .partitions(topics.partitions())
                .replicas(topics.replicas())
                .build();
    }
}