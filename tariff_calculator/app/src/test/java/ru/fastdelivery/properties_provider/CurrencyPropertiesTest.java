package ru.fastdelivery.properties_provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.properties.provider.CurrencyProperties;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyPropertiesTest {

    @Test
    @DisplayName("Если код валюты в списке -> true")
    void whenCodeInList_thanIsAvailableCurrencyCodeReturnTrue() {
        CurrencyProperties properties = new CurrencyProperties();
        properties.setAvailable(List.of("USD", "EUR"));

        assertTrue(properties.isAvailable("USD"));
        assertTrue(properties.isAvailable("EUR"));
        assertFalse(properties.isAvailable("RUB"));
    }

    @Test
    @DisplayName("Если код валюты НЕ в списке -> false")
    void whenCodeIsNotInList_thanIsAvailableCurrencyCodeReturnFalse() {
        CurrencyProperties properties = new CurrencyProperties();
        properties.setAvailable(List.of("USD", "EUR"));

        assertFalse(properties.isAvailable("RUB"));
        assertFalse(properties.isAvailable("BYN"));
    }
}