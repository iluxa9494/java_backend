package ru.fastdelivery.persistence.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.tariff.TariffSettings;
import ru.fastdelivery.persistence.entity.TariffSettingsEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TariffSettingsJpaAdapterTest {

    @Test
    @DisplayName("Возвращает актуальные настройки тарифа, если они найдены в БД")
    void shouldReturnLatestTariffSettingsWhenPresent() {
        TariffSettingsEntity entity = new TariffSettingsEntity();
        entity.setWeightRate(new BigDecimal("0.04"));
        entity.setVolumeRate(new BigDecimal("1200.00"));
        entity.setMinimalPrice(new BigDecimal("500.00"));
        entity.setDistanceStepKm(450);
        entity.setCurrencyCode("RUB");
        TariffSettingsJpaRepository mockRepo = mock(TariffSettingsJpaRepository.class);
        when(mockRepo.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.of(entity));
        TariffSettingsJpaAdapter adapter = new TariffSettingsJpaAdapter(mockRepo);
        Optional<TariffSettings> result = adapter.getLatest();
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("0.04"), result.get().getWeightRate());
        assertEquals(new BigDecimal("1200.00"), result.get().getVolumeRate());
        assertEquals(new BigDecimal("500.00"), result.get().getMinimalPrice());
        assertEquals(450, result.get().getDistanceStepKm());
        assertEquals("RUB", result.get().getCurrencyCode());
    }

    @Test
    @DisplayName("Возвращает пустой Optional, если настройки тарифа не найдены")
    void shouldReturnEmptyWhenNoTariffSettingsFound() {
        TariffSettingsJpaRepository mockRepo = mock(TariffSettingsJpaRepository.class);
        when(mockRepo.findTopByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
        TariffSettingsJpaAdapter adapter = new TariffSettingsJpaAdapter(mockRepo);
        Optional<TariffSettings> result = adapter.getLatest();
        assertFalse(result.isPresent());
    }
}