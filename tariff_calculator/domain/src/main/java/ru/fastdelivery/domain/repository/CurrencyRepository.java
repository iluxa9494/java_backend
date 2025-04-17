package ru.fastdelivery.domain.repository;

import ru.fastdelivery.domain.common.currency.Currency;

import java.util.Optional;

/**
 * Репозиторий для получения валют из источника данных по коду.
 */
public interface CurrencyRepository {
    Optional<Currency> findByCode(String code);
}
