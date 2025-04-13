package ru.fastdelivery.domain.common.currency;

import lombok.RequiredArgsConstructor;

/**
 * Создание валюты с проверками
 */
@RequiredArgsConstructor
public class CurrencyFactory {

    private final CurrencyPropertiesProvider propertiesProvider;

    public Currency create(String code) {
        if (code == null || !propertiesProvider.isAvailable(code)) {
            throw new IllegalArgumentException("Currency code contains not available value");
        }

        return new Currency(code);
    }
}
