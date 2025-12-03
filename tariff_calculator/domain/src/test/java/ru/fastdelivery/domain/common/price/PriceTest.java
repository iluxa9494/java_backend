package ru.fastdelivery.domain.common.price;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriceTest {
    @Test
    @DisplayName("Создание цены с корректными данными")
    void shouldCreatePriceCorrectly() {
        Price price = new Price(BigDecimal.valueOf(100), new Currency("RUB"));

        assertEquals(BigDecimal.valueOf(100), price.getAmount());
        assertEquals("RUB", price.getCurrencyCode());
    }

    @Test
    @DisplayName("Исключение при отрицательной сумме")
    void shouldThrowExceptionIfAmountIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Price(BigDecimal.valueOf(-1), new Currency("RUB"))
        );
        assertEquals("Price Amount cannot be below Zero!", exception.getMessage());
    }

    @Test
    @DisplayName("Исключение при null валюте")
    void shouldThrowExceptionIfCurrencyIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Price(BigDecimal.valueOf(100), null)
        );
    }
}
