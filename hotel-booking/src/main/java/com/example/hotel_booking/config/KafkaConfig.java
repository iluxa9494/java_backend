package com.example.hotel_booking.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "PLAINTEXT://127.0.0.1:9092");
        System.out.println("Kafka Bootstrap Servers: " + configs.get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
        return new KafkaAdmin(configs);
    }

    @PostConstruct
    public void checkKafkaConnection() {
        System.out.println("KafkaAdmin инициализирован!");
    }
}