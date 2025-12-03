package ru.fastdelivery.domain.common.currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyTest {
    @Test
    @DisplayName("Создание валюты с курсом")
    void createCurrencyWithRate() {
        Currency currency = new Currency("USD", BigDecimal.TEN);
        assertEquals("USD", currency.getCode());
        assertEquals(BigDecimal.TEN, currency.getRateToRub());
    }

    @Test
    @DisplayName("Создание валюты без указания курса")
    void createCurrencyWithoutRate() {
        Currency currency = new Currency("RUB");
        assertEquals("RUB", currency.getCode());
        assertEquals(BigDecimal.ONE, currency.getRateToRub());
    }

    @Test
    @DisplayName("Пустой код валюты — исключение")
    void createCurrencyWithBlankCode() {
        assertThrows(IllegalArgumentException.class, () -> new Currency("", BigDecimal.TEN));
    }

    @Test
    @DisplayName("Null-код валюты — исключение")
    void createCurrencyWithNullCode() {
        assertThrows(IllegalArgumentException.class, () -> new Currency(null, BigDecimal.ONE));
    }
}
