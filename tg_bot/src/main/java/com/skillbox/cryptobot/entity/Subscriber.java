package com.skillbox.cryptobot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "subscribers")
public class Subscriber {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private Long telegramId;

    private BigDecimal targetPrice;

    @Column(name = "last_notification")
    private LocalDateTime lastNotificationTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}