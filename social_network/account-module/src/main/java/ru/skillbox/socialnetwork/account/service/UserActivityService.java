package ru.skillbox.socialnetwork.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.account.repository.AccountRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityService {

    private final AccountRepository accountRepository;
    private final AccountCacheService accountCacheService;

    private final Map<UUID, LocalDateTime> lastUpdateCache = new ConcurrentHashMap<>();
    private final Duration updateThreshold = Duration.ofSeconds(30);

    @Async
    public void recordUserActivity(UUID userId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastUpdate = lastUpdateCache.get(userId);

            if (lastUpdate == null || Duration.between(lastUpdate, now).compareTo(updateThreshold) > 0) {
                int updated = accountRepository.updateLastOnlineTime(userId, now);

                if (updated > 0) {
                    lastUpdateCache.put(userId, now);
                    accountCacheService.evictAccountCache(userId);
                    log.debug("🕐 Обновлено lastOnlineTime для пользователя: {}", userId);
                } else {
                    log.debug("⏸️ Аккаунт не найден или удален: {}", userId);
                }
            }
        } catch (Exception e) {
            log.error("❌ Ошибка при обновлении lastOnlineTime для пользователя {}: {}", userId, e.getMessage());
        }
    }
}