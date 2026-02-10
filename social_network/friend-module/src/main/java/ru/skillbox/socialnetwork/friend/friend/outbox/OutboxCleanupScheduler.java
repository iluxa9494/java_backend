package ru.skillbox.socialnetwork.friend.friend.outbox;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.friend.common.configuration.OutboxCleanupProperties;
import ru.skillbox.socialnetwork.friend.friend.outbox.repository.OutboxMessageRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupScheduler {

    private final OutboxMessageRepository outboxRepository;
    private final OutboxCleanupProperties cleanupProperties;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(transactionManager = "friendTransactionManager")
    public void cleanupOldProcessedMessages() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(cleanupProperties.getRetentionDays());
        int deleted = outboxRepository.deleteByProcessedAtBefore(cutoff);
        log.info("Deleted {} outbox messages older than {} days", deleted, cutoff);
    }
}
