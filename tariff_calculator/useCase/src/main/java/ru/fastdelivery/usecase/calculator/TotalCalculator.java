package ru.fastdelivery.usecase.calculator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.domain.repository.TariffSettingsRepository;
import ru.fastdelivery.domain.tariff.TariffSettings;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Выполняет расчет полной стоимости доставки на основе веса, объема и расстояния.
 * Использует настройки тарифа из базы или значения по умолчанию.
 */
@RequiredArgsConstructor
@Slf4j
public class TotalCalculator {
    private final VolumeCalculator volumeCalculator;
    private final DistanceCalculator distanceCalculator;
    private final TariffSettingsRepository tariffSettingsRepository;
    private final BigDecimal fallbackWeightRate;
    private final BigDecimal fallbackVolumeRate;
    private final BigDecimal fallbackMinimalPrice;
    private final int fallbackDistanceStepKm;

    public Price calculateTotal(Shipment shipment) {
        log.debug("Начат расчет полной стоимости доставки");
        TariffSettings tariff = tariffSettingsRepository.getLatest().orElse(null);
        Currency currency = shipment.currency();
        BigDecimal weightRate;
        BigDecimal volumeRate;
        BigDecimal minimal;
        int distanceStepKm;
        if (tariff == null) {
            log.warn("Настройки тарифа не найдены в БД. Используются значения по умолчанию.");
            weightRate = fallbackWeightRate;
            volumeRate = fallbackVolumeRate;
            minimal = fallbackMinimalPrice;
            distanceStepKm = fallbackDistanceStepKm;
        } else {
            weightRate = tariff.getWeightRate();
            volumeRate = tariff.getVolumeRate();
            minimal = tariff.getMinimalPrice();
            distanceStepKm = tariff.getDistanceStepKm();
        }
        BigDecimal weight = shipment.weightAllPackages().kilograms();
        BigDecimal volume = volumeCalculator.calculate(shipment);
        double distance = distanceCalculator.calculate(shipment.source(), shipment.destination());
        BigDecimal weightCost = weightRate.multiply(weight);
        BigDecimal volumeCost = volumeRate.multiply(volume);
        BigDecimal distanceCoefficient = BigDecimal.valueOf(distance / distanceStepKm).max(BigDecimal.ONE);
        BigDecimal maxCost = weightCost.max(volumeCost);
        BigDecimal costWithDistance = maxCost.multiply(distanceCoefficient);
        BigDecimal finalAmount = costWithDistance.max(minimal)
                .setScale(2, RoundingMode.CEILING);
        log.debug("Рассчитанная стоимость: {}, Минимальная: {}, Итоговая (округленная): {}",
                costWithDistance, minimal, finalAmount);
        return Price.of(finalAmount, currency.getCode());
    }

    public Price getMinimalPrice() {
        TariffSettings tariff = tariffSettingsRepository.getLatest().orElse(null);
        if (tariff == null) {
            log.warn("Минимальная цена не найдена в БД, используется fallback: {}", fallbackMinimalPrice);
            return Price.of(fallbackMinimalPrice, "RUB");
        }
        return Price.of(tariff.getMinimalPrice(), tariff.getCurrencyCode());
    }
}