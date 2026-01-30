package ru.skillbox.socialnetwork.friend.friend.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.friend.friend.outbox.model.OutboxMessage;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

    @Query(value = """
        SELECT * FROM outbox_kafka_message
        WHERE processed_at IS NULL
          AND attempt_count < :maxAttempts
        ORDER BY created_at
        LIMIT 1
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    OutboxMessage findNextUnprocessedMessage(@Param("maxAttempts") int maxAttempts);

    int deleteByProcessedAtBefore(OffsetDateTime cutoff);
}
