package ru.skillbox.socialnetwork.friend.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("outbox.cleanup")
@Data
@Component
public class OutboxCleanupProperties {
    private int retentionDays = 3;
}
