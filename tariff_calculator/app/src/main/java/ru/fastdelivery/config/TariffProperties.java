package ru.fastdelivery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Конфигурационные свойства тарифов на доставку.
 * Загружаются из блока cost.rub в application.yml.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cost.rub")
public class TariffProperties {
    private BigDecimal perKg;
    private BigDecimal perCubicMeter;
    private BigDecimal minimal;
    private int distanceStepKm;
}