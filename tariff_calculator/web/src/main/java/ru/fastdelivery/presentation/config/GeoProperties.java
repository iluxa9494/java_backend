package ru.fastdelivery.presentation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Конфигурационные свойства для географических ограничений координат.
 * Используются для проверки допустимого диапазона широты и долготы.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "geo")
public class GeoProperties {
    private BigDecimal minLatitude;
    private BigDecimal maxLatitude;
    private BigDecimal minLongitude;
    private BigDecimal maxLongitude;
}