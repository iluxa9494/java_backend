package ru.skillbox.socialnetwork.post.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

    @Bean
    public KafkaAdmin kafkaAdmin(Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        // Эти таймауты можно не дублировать — ты их уже задал в yml через spring.kafka.properties.*
        return new KafkaAdmin(props);
    }
}