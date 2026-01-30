package ru.skillbox.socialnetwork.post.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Configuration
public class KafkaHealthConfig {

    @Bean
    public HealthIndicator kafkaHealthIndicator(Environment env) {
        return () -> {
            String bootstrap = env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092");
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrap);
            props.put("request.timeout.ms", "3000");
            props.put("default.api.timeout.ms", "3000");

            try (AdminClient client = AdminClient.create(props)) {
                DescribeClusterResult r = client.describeCluster();
                String clusterId = r.clusterId().get(2, TimeUnit.SECONDS);
                int nodeCount = r.nodes().get(2, TimeUnit.SECONDS).size();
                return Health.up()
                        .withDetail("bootstrapServers", bootstrap)
                        .withDetail("clusterId", clusterId)
                        .withDetail("nodes", nodeCount)
                        .build();
            } catch (Exception e) {
                return Health.down(e).withDetail("bootstrapServers", bootstrap).build();
            }
        };
    }
}