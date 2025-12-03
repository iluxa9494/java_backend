package ru.fastdelivery.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность валюты, отображающая таблицу currency в базе данных.
 * Хранит код, название, курс к рублю и дату обновления.
 */
@Entity
@Table(name = "currency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyEntity {
    @Id
    @Column(length = 3)
    private String code;

    @Column
    private String name;

    @Column(name = "rate_to_rub", precision = 12, scale = 6, nullable = false)
    private BigDecimal rateToRub;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}