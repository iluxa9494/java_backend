package ru.skillbox.socialnetwork.integration.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(StorageProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getS3().getEndpoint())
                .credentials(properties.getAccessKeyId(), properties.getSecretKey())
                .build();
    }
}
