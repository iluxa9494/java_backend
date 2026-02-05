package com.example.hotel_booking.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "2000");
        configs.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "3000");
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setFatalIfBrokerNotAvailable(true);
        return admin;
    }

    @Bean
    public NewTopic statisticsEventsTopic() {
        return new NewTopic("statistics-events", 1, (short) 1);
    }

    @PostConstruct
    public void logKafkaBootstrap() {
        System.out.println("Kafka bootstrap servers: " + bootstrapServers);
    }
}
