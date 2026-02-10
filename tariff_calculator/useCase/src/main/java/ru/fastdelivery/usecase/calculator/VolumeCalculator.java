package ru.fastdelivery.usecase.calculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.fastdelivery.domain.delivery.shipment.Shipment;

import java.math.BigDecimal;

/**
 * Сервис для расчета суммарного объема всех упаковок в отправлении.
 */
@Slf4j
@Component
public class VolumeCalculator {
    public BigDecimal calculate(Shipment shipment) {
        log.debug("Начат расчет общего объема упаковок");
        BigDecimal totalVolume = shipment.packages().stream()
                .map(pack -> pack.outerDimensions().cubicMeters())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug("Общий объем: {} м³", totalVolume);
        return totalVolume;
    }
}