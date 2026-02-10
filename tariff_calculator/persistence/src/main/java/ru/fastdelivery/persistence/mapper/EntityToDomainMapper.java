package ru.fastdelivery.persistence.mapper;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.delivery.AdditionalService;
import ru.fastdelivery.domain.tariff.TariffSettings;
import ru.fastdelivery.persistence.entity.AdditionalServiceEntity;
import ru.fastdelivery.persistence.entity.CurrencyEntity;
import ru.fastdelivery.persistence.entity.TariffSettingsEntity;

/**
 * Маппер для преобразования сущностей JPA в доменные модели и обратно.
 */
public class EntityToDomainMapper {
    public static Currency toDomain(CurrencyEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("CurrencyEntity is null");
        }
        return new Currency(entity.getCode(), entity.getRateToRub());
    }

    public static AdditionalService toDomain(AdditionalServiceEntity entity) {
        return new AdditionalService(
                entity.getName(),
                entity.getPrice(),
                entity.getPriceType(),
                entity.getDescription()
        );
    }

    public static TariffSettings toDomain(TariffSettingsEntity entity) {
        return new TariffSettings(
                entity.getWeightRate(),
                entity.getVolumeRate(),
                entity.getMinimalPrice(),
                entity.getDistanceStepKm(),
                entity.getCurrencyCode()
        );
    }
}