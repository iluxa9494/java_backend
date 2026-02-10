package ru.fastdelivery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tariff")
public class TariffProperties {
    private BigDecimal weightCostPerGram;
    private BigDecimal volumeCostPerM3;
    private BigDecimal minimalPrice;
    private int distanceStep;
}
