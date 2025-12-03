package ru.fastdelivery.domain.repository;

import ru.fastdelivery.domain.tariff.TariffSettings;

import java.util.Optional;

/**
 * Репозиторий для получения актуальных настроек тарифа.
 */
public interface TariffSettingsRepository {
    Optional<TariffSettings> getLatest();
}