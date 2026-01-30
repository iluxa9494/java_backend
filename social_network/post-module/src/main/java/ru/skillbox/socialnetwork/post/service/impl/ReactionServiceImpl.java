package ru.skillbox.socialnetwork.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.post.entity.EntityType;
import ru.skillbox.socialnetwork.post.entity.ReactionCount;
import ru.skillbox.socialnetwork.post.entity.ReactionEntity;
import ru.skillbox.socialnetwork.post.entity.ReactionType;
import ru.skillbox.socialnetwork.post.events.ReactionEvent;
import ru.skillbox.socialnetwork.post.repository.ReactionCountRepository;
import ru.skillbox.socialnetwork.post.repository.ReactionRepository;
import ru.skillbox.socialnetwork.post.service.EventPublisher;
import ru.skillbox.socialnetwork.post.service.ReactionResult;
import ru.skillbox.socialnetwork.post.service.ReactionService;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional(transactionManager = "postTransactionManager")
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final ReactionCountRepository reactionCountRepository;
    private final EventPublisher eventPublisher;

    @Override
    public ReactionResult react(UUID userId, EntityType entityType, UUID entityId, ReactionType type) {
        // ВАЖНО: метод репозитория именно в таком порядке аргументов
        var existingOpt = reactionRepository.findByUserIdAndEntityTypeAndEntityId(userId, entityType, entityId);

        boolean active;
        String evtType;

        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();

            if (existing.getReactionType() == type) {
                // toggle OFF
                var prev = existing.getReactionType();
                reactionRepository.delete(existing);
                reactionRepository.flush();              // фиксируем удаление сразу
                decCount(entityType, entityId, prev);

                active = false;
                evtType = "REMOVED";
            } else {
                // смена типа A -> B
                var prev = existing.getReactionType();

                existing.setReactionType(type);
                existing.setCreatedAt(OffsetDateTime.now());
                reactionRepository.saveAndFlush(existing); // сразу фиксируем апдейт

                decCount(entityType, entityId, prev);
                incCount(entityType, entityId, type);

                active = true;
                evtType = "CHANGED";
            }
        } else {
            // новая реакция (ID НЕ задаём — сгенерируется @UuidGenerator’ом)
            var entity = new ReactionEntity();
            entity.setEntityType(entityType);
            entity.setEntityId(entityId);
            entity.setUserId(userId);
            entity.setReactionType(type);
            entity.setCreatedAt(OffsetDateTime.now());
            reactionRepository.saveAndFlush(entity);       // сразу фиксируем insert

            incCount(entityType, entityId, type);
            active = true;
            evtType = "ADDED";
        }

        long qty = currentCount(entityType, entityId, type);

        eventPublisher.publishReaction(new ReactionEvent(
                evtType, userId, entityType, entityId, type, qty, OffsetDateTime.now()
        ));

        return new ReactionResult(active, qty, type);
    }

    @Override
    public void remove(UUID userId, EntityType entityType, UUID entityId) {
        var existing = reactionRepository.findByUserIdAndEntityTypeAndEntityId(userId, entityType, entityId)
                .orElse(null);
        if (existing == null) return;

        var prev = existing.getReactionType();
        reactionRepository.delete(existing);
        reactionRepository.flush();
        decCount(entityType, entityId, prev);

        long qty = currentCount(entityType, entityId, prev);
        eventPublisher.publishReaction(new ReactionEvent(
                "REMOVED", userId, entityType, entityId, prev, qty, OffsetDateTime.now()
        ));
    }

    /* ===== counters ===== */

    private void incCount(EntityType et, UUID id, ReactionType rt) {
        int updated = reactionCountRepository.addDelta(et, id, rt, 1);
        if (updated == 0) {
            var rc = ReactionCount.builder()
                    .entityType(et)
                    .entityId(id)
                    .reactionType(rt)
                    .count(1)
                    .updatedAt(OffsetDateTime.now())
                    .build();
            try {
                reactionCountRepository.save(rc);
            } catch (Exception ignore) {
                // гонка вставки — повторим инкремент
                reactionCountRepository.addDelta(et, id, rt, 1);
            }
        }
    }

    private void decCount(EntityType et, UUID id, ReactionType rt) {
        reactionCountRepository.addDelta(et, id, rt, -1);
    }

    private long currentCount(EntityType et, UUID id, ReactionType rt) {
        return reactionCountRepository
                .findByEntityTypeAndEntityIdAndReactionType(et, id, rt)
                .map(ReactionCount::getCount)
                .orElse(0L);
    }
}