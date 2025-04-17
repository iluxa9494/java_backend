package ru.fastdelivery.domain.common.price;

import ru.fastdelivery.domain.common.currency.Currency;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Представляет цену в определённой валюте
 */
public record Price(
        BigDecimal amount,
        Currency currency) {

    public Price {
        if (isLessThanZero(amount)) {
            throw new IllegalArgumentException("Price Amount cannot be below Zero!");
        }
        Objects.requireNonNull(currency, "Currency must not be null!");
    }

    private static boolean isLessThanZero(BigDecimal price) {
        return BigDecimal.ZERO.compareTo(price) > 0;
    }

    public static Price of(BigDecimal amount, String currencyCode) {
        return new Price(amount, new Currency(currencyCode));
    }

    public String getCurrencyCode() {
        return currency.getCode();
    }

    public BigDecimal getAmount() {
        return amount;
    }
}