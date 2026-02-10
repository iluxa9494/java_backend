package ru.skillbox.socialnetwork.authentication.configurations;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class KafkaTopicsConfiguration {

    @Value("${app.kafka.kafkaUserTopic}")
    private String userTopic;

    @Value("${app.kafka.kafkaUserEmailChanged}")
    private String userEmailChangedTopic;

    @Value("${app.kafka.kafkaUserPasswordChanged}")
    private String userPasswordChangedTopic;

    @Value("${app.kafka.topic.partitions:1}")
    private int partitions;

    @Value("${app.kafka.topic.replicas:1}")
    private short replicas;

    @Bean
    public NewTopic userTopic() {
        return TopicBuilder.name(userTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic userEmailChangedTopic() {
        return TopicBuilder.name(userEmailChangedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic userPasswordChangedTopic() {
        return TopicBuilder.name(userPasswordChangedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public ApplicationRunner kafkaTopicsLogger() {
        return args -> log.info(
                "[KAFKA] expecting topics: userTopic={}, userEmailChanged={}, userPasswordChanged={} (partitions={}, replicas={})",
                userTopic, userEmailChangedTopic, userPasswordChangedTopic, partitions, replicas
        );
    }
}
