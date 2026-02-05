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

    Optional<Subscriber> findByTelegramId(Long telegramId);

    @Modifying
    @Query("UPDATE Subscriber s SET s.targetPrice = :price WHERE s.telegramId = :telegramId")
    void updateSubscription(@Param("telegramId") Long telegramId, @Param("price") BigDecimal price);

    @Modifying
    @Query("UPDATE Subscriber s SET s.targetPrice = NULL WHERE s.telegramId = :telegramId")
    void unsubscribe(@Param("telegramId") Long telegramId);

    @Query("SELECT s FROM Subscriber s WHERE s.targetPrice >= :currentPrice")
    List<Subscriber> findUsersForNotification(@Param("currentPrice") BigDecimal currentPrice);
}
