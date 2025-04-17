package ru.fastdelivery.domain.tariff;

import lombok.Value;

import java.math.BigDecimal;

/**
 * Настройки тарифа, используемые при расчете стоимости доставки.
 * Содержит ставки по весу, объему, минимальную стоимость, шаг расстояния и валюту.
 */
@Value
public class TariffSettings {
    BigDecimal weightRate;
    BigDecimal volumeRate;
    BigDecimal minimalPrice;
    int distanceStepKm;
    String currencyCode;
}