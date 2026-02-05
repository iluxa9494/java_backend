package ru.fastdelivery.domain.repository;

import ru.fastdelivery.domain.common.currency.Currency;

import java.util.Optional;

public interface CurrencyRepository {
    Optional<Currency> findByCode(String code);
}
