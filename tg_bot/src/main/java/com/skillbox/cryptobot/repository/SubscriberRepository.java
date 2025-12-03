package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {

    /**
     * Найти подписчика по Telegram ID
     */
    Optional<Subscriber> findByTelegramId(Long telegramId);

    /**
     * Обновить подписку пользователя (установить целевую цену)
     */
    @Modifying
    @Query("UPDATE Subscriber s SET s.targetPrice = :price WHERE s.telegramId = :telegramId")
    void updateSubscription(@Param("telegramId") Long telegramId, @Param("price") BigDecimal price);

    /**
     * Удалить подписку пользователя (сбросить targetPrice в NULL)
     */
    @Modifying
    @Query("UPDATE Subscriber s SET s.targetPrice = NULL WHERE s.telegramId = :telegramId")
    void unsubscribe(@Param("telegramId") Long telegramId);

    /**
     * Найти пользователей, которых нужно уведомить
     * (текущая цена BTC ниже или равна их targetPrice)
     */
    @Query("SELECT s FROM Subscriber s WHERE s.targetPrice >= :currentPrice")
    List<Subscriber> findUsersForNotification(@Param("currentPrice") BigDecimal currentPrice);
}
