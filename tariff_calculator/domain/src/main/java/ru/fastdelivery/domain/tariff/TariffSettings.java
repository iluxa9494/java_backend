package ru.fastdelivery.domain.tariff;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TariffSettings {
    BigDecimal weightRate;
    BigDecimal volumeRate;
    BigDecimal minimalPrice;
    int distanceStepKm;
    String currencyCode;
}