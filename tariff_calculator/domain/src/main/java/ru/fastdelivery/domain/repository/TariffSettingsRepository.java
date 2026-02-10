package ru.fastdelivery.domain.repository;

import ru.fastdelivery.domain.tariff.TariffSettings;

import java.util.Optional;

public interface TariffSettingsRepository {
    Optional<TariffSettings> getLatest();
}