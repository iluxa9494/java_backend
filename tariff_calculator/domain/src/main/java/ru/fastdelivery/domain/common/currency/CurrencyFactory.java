package ru.fastdelivery.domain.common.currency;

import ru.fastdelivery.domain.repository.CurrencyRepository;

/**
 * Фабрика для создания валютных объектов на основе данных из базы.
 */
public class CurrencyFactory {
    private final CurrencyRepository currencyRepository;

    public CurrencyFactory(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public Currency create(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found: " + code));
    }
}