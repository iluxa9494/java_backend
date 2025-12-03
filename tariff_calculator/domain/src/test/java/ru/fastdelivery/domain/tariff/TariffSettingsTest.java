package ru.fastdelivery.domain.tariff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TariffSettingsTest {
    @Test
    @DisplayName("Создание TariffSettings и проверка полей")
    void createTariffSettings_shouldReturnCorrectValues() {
        BigDecimal weightRate = BigDecimal.valueOf(5.0);
        BigDecimal volumeRate = BigDecimal.valueOf(10.0);
        BigDecimal minimalPrice = BigDecimal.valueOf(100.0);
        int distanceStepKm = 450;
        String currencyCode = "RUB";
        TariffSettings settings = new TariffSettings(
                weightRate,
                volumeRate,
                minimalPrice,
                distanceStepKm,
                currencyCode
        );
        assertEquals(weightRate, settings.getWeightRate());
        assertEquals(volumeRate, settings.getVolumeRate());
        assertEquals(minimalPrice, settings.getMinimalPrice());
        assertEquals(distanceStepKm, settings.getDistanceStepKm());
        assertEquals(currencyCode, settings.getCurrencyCode());
    }
}