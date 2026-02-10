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
        log.info("Начат расчет полной стоимости доставки: packages={}, currency={}, source={}, destination={}",
                shipment.packages().size(),
                shipment.currency().getCode(),
                shipment.source(),
                shipment.destination());
        if (log.isInfoEnabled()) {
            shipment.packages().forEach(pack -> log.info(
                    "Package: weightGrams={}, dimensions(mm)={}x{}x{}",
                    pack.weight().weightGrams(),
                    pack.outerDimensions().length(),
                    pack.outerDimensions().width(),
                    pack.outerDimensions().height()
            ));
        }
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
            log.info("Fallback тариф: weightRate={}, volumeRate={}, minimalPrice={}, distanceStepKm={}",
                    weightRate, volumeRate, minimal, distanceStepKm);
        } else {
            weightRate = tariff.getWeightRate();
            volumeRate = tariff.getVolumeRate();
            minimal = tariff.getMinimalPrice();
            distanceStepKm = tariff.getDistanceStepKm();
            log.info("Тариф из БД: weightRate={}, volumeRate={}, minimalPrice={}, distanceStepKm={}, currencyCode={}",
                    weightRate, volumeRate, minimal, distanceStepKm, tariff.getCurrencyCode());
        }
        BigDecimal weightGrams = new BigDecimal(shipment.weightAllPackages().weightGrams());
        BigDecimal weightKg = weightGrams.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
        BigDecimal volume = volumeCalculator.calculate(shipment);
        double distance = distanceCalculator.calculate(shipment.source(), shipment.destination());
        int distanceSteps = (int) Math.ceil(distance / distanceStepKm);
        if (distanceSteps < 1) {
            distanceSteps = 1;
        }
        BigDecimal weightCost = weightRate.multiply(weightGrams);
        BigDecimal volumeCost = volumeRate.multiply(volume);
        BigDecimal distanceCoefficient = BigDecimal.valueOf(distanceSteps);
        BigDecimal maxCost = weightCost.max(volumeCost);
        BigDecimal costWithDistance = maxCost.multiply(distanceCoefficient);
        BigDecimal finalAmount = costWithDistance.max(minimal)
                .setScale(2, RoundingMode.CEILING);
        log.info("Детали расчета: weightGrams={}, weightKg={}, volumeM3={}, distanceKm={}, distanceStepKm={}, distanceSteps={}, distanceCoef={}",
                weightGrams, weightKg, volume, distance, distanceStepKm, distanceSteps, distanceCoefficient);
        log.info("Стоимость до минималки: weightCost={}, volumeCost={}, maxCost={}, costWithDistance={}",
                weightCost, volumeCost, maxCost, costWithDistance);
        log.info("Минималка={} => финальная={}", minimal, finalAmount);
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
