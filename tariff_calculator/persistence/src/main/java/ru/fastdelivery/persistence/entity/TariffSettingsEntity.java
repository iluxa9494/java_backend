package ru.fastdelivery.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая настройки тарифа для расчета стоимости доставки.
 * Отражает таблицу tariff_settings в базе данных.
 */
@Entity
@Table(name = "tariff_settings")
@Getter
@Setter
@NoArgsConstructor
public class TariffSettingsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal weightRate;

    @Column(nullable = false)
    private BigDecimal volumeRate;

    @Column(nullable = false)
    private BigDecimal minimalPrice;

    @Column(nullable = false)
    private int distanceStepKm;

    @Column(nullable = false)
    private String currencyCode;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}