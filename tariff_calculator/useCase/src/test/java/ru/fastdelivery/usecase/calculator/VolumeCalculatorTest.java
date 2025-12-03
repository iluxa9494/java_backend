package ru.fastdelivery.usecase.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.domain.dimension.OuterDimensions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VolumeCalculatorTest {
    private final VolumeCalculator calculator = new VolumeCalculator();

    @Test
    @DisplayName("Рассчитывает суммарный объем всех упаковок")
    void shouldCalculateTotalVolumeCorrectly() {
        OuterDimensions dims1 = new OuterDimensions(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000)
        );
        OuterDimensions dims2 = new OuterDimensions(
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000)
        );
        Pack pack1 = new Pack(new Weight(BigInteger.valueOf(1000)), dims1);
        Pack pack2 = new Pack(new Weight(BigInteger.valueOf(2000)), dims2);
        Shipment shipment = new Shipment(
                List.of(pack1, pack2),
                null,
                null,
                new Currency("RUB", BigDecimal.ONE)
        );
        BigDecimal totalVolume = calculator.calculate(shipment);
        assertEquals(new BigDecimal("1.5"), totalVolume.stripTrailingZeros());
    }
}