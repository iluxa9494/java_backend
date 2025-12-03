package ru.fastdelivery.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Сущность дополнительной услуги, хранимой в базе данных.
 * Отражает таблицу additional_services.
 */
@Entity
@Table(name = "additional_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdditionalServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "price_type", nullable = false, length = 10)
    private String priceType;

    @Column(columnDefinition = "TEXT")
    private String description;
}