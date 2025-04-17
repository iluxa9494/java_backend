package ru.fastdelivery.domain.delivery.shipment;

import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.dimension.OuterDimensions;
import ru.fastdelivery.domain.geo.Coordinate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ShipmentTest {
    @Test
    void whenSummarizingWeightOfAllPackages_thenReturnSum() {
        var weight1 = new Weight(BigInteger.TEN);
        var weight2 = new Weight(BigInteger.ONE);
        var dimensions = new OuterDimensions(
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(400)
        );
        var packages = List.of(
                new Pack(weight1, dimensions),
                new Pack(weight2, dimensions)
        );
        var shipment = new Shipment(
                packages,
                new Coordinate(BigDecimal.valueOf(55.75), BigDecimal.valueOf(37.61)),
                new Coordinate(BigDecimal.valueOf(59.93), BigDecimal.valueOf(30.33)),
                new Currency("RUB", BigDecimal.ONE)
        );
        var massOfShipment = shipment.weightAllPackages();
        assertThat(massOfShipment.weightGrams()).isEqualByComparingTo(BigInteger.valueOf(11));
    }
}