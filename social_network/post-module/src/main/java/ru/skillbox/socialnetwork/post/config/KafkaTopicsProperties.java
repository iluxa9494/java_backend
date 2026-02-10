package ru.skillbox.socialnetwork.post.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topic")
public record KafkaTopicsProperties(
        String post,
        String comment,
        String reaction,
        int partitions,
        short replicas
) {}