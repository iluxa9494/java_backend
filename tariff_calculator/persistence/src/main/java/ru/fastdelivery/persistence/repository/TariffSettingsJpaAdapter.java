package ru.fastdelivery.persistence.repository;

import org.springframework.stereotype.Repository;
import ru.fastdelivery.domain.repository.TariffSettingsRepository;
import ru.fastdelivery.domain.tariff.TariffSettings;
import ru.fastdelivery.persistence.mapper.EntityToDomainMapper;

import java.util.Optional;

/**
 * Адаптер для получения актуальных тарифных настроек из базы данных.
 * Реализует интерфейс TariffSettingsRepository, используя JPA.
 */
@Repository
public class TariffSettingsJpaAdapter implements TariffSettingsRepository {
    private final TariffSettingsJpaRepository repository;

    public TariffSettingsJpaAdapter(TariffSettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TariffSettings> getLatest() {
        return repository.findTopByOrderByCreatedAtDesc()
                .map(EntityToDomainMapper::toDomain);
    }
}