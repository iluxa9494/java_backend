package ru.fastdelivery.domain.common.currency;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Value
public class Currency {
    String code;
    BigDecimal rateToRub;

    public Currency(String code, BigDecimal rateToRub) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Currency code must not be null or blank");
        }
        this.code = code;
        this.rateToRub = rateToRub;
    }

    public Currency(String code) {
        this(code, BigDecimal.ONE);
    }
}