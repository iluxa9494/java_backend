package ru.fastdelivery.persistence.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.delivery.AdditionalService;
import ru.fastdelivery.domain.tariff.TariffSettings;
import ru.fastdelivery.persistence.entity.AdditionalServiceEntity;
import ru.fastdelivery.persistence.entity.CurrencyEntity;
import ru.fastdelivery.persistence.entity.TariffSettingsEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityToDomainMapperTest {
    @Test
    @DisplayName("Маппинг CurrencyEntity в Currency")
    void shouldMapCurrencyEntityToCurrency() {
        CurrencyEntity entity = CurrencyEntity.builder().code("USD").rateToRub(new BigDecimal("92.5")).build();
        Currency result = EntityToDomainMapper.toDomain(entity);
        assertEquals("USD", result.getCode());
        assertEquals(new BigDecimal("92.5"), result.getRateToRub());
    }

    @Test
    @DisplayName("Маппинг AdditionalServiceEntity в AdditionalService")
    void shouldMapAdditionalServiceEntityToDomain() {
        AdditionalServiceEntity entity = AdditionalServiceEntity.builder().name("Упаковка").price(
                new BigDecimal("200")).priceType("fixed").description("Дополнительная упаковка груза").build();
        AdditionalService result = EntityToDomainMapper.toDomain(entity);
        assertEquals("Упаковка", result.getName());
        assertEquals(new BigDecimal("200"), result.getPrice());
        assertEquals("fixed", result.getPriceType());
        assertEquals("Дополнительная упаковка груза", result.getDescription());
    }

    @Test
    @DisplayName("Маппинг TariffSettingsEntity в TariffSettings")
    void shouldMapTariffSettingsEntityToTariffSettings() {
        TariffSettingsEntity entity = new TariffSettingsEntity();
        entity.setWeightRate(new BigDecimal("0.04"));
        entity.setVolumeRate(new BigDecimal("1200.00"));
        entity.setMinimalPrice(new BigDecimal("500.00"));
        entity.setDistanceStepKm(450);
        entity.setCurrencyCode("RUB");
        TariffSettings result = EntityToDomainMapper.toDomain(entity);
        assertEquals(new BigDecimal("0.04"), result.getWeightRate());
        assertEquals(new BigDecimal("1200.00"), result.getVolumeRate());
        assertEquals(new BigDecimal("500.00"), result.getMinimalPrice());
        assertEquals(450, result.getDistanceStepKm());
        assertEquals("RUB", result.getCurrencyCode());
    }
}
