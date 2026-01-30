package ru.skillbox.socialnetwork.integration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("storage")
public class StorageProperties {

    private String accessKeyId;
    private String secretKey;
    private String region;
    private S3 s3;

    @Data
    public static class S3 {
        private String endpoint;
        private String bucketName;
    }
}
