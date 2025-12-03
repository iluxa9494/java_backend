package ru.fastdelivery.usecase;

import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.domain.dimension.OuterDimensions;
import ru.fastdelivery.domain.geo.Coordinate;
import ru.fastdelivery.usecase.calculator.TotalCalculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TariffCalculateUseCaseTest {
    final TotalCalculator totalCalculator = mock(TotalCalculator.class);
    final TariffCalculateUseCase useCase = new TariffCalculateUseCase(totalCalculator);
    final Currency currency = new Currency("RUB", BigDecimal.ONE);

    @Test
    @DisplayName("Расчет стоимости доставки -> успешно")
    void whenCalculatePrice_thenSuccess() {
        var shipment = new Shipment(
                List.of(new Pack(
                        new Weight(BigInteger.valueOf(1200)),
                        new OuterDimensions(
                                BigDecimal.valueOf(200),
                                BigDecimal.valueOf(200),
                                BigDecimal.valueOf(200)
                        ))),
                new Coordinate(BigDecimal.valueOf(55.75), BigDecimal.valueOf(37.61)),
                new Coordinate(BigDecimal.valueOf(59.93), BigDecimal.valueOf(30.33)),
                currency
        );
        Price expected = Price.of(BigDecimal.valueOf(1200), "RUB");
        when(totalCalculator.calculateTotal(shipment)).thenReturn(expected);
        var actual = useCase.calc(shipment);
        assertThat(actual).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение минимальной стоимости -> успешно")
    void whenMinimalPrice_thenSuccess() {
        Price minimal = Price.of(BigDecimal.valueOf(500), "RUB");
        when(totalCalculator.getMinimalPrice()).thenReturn(minimal);
        var actual = useCase.minimalPrice();
        assertThat(actual).isEqualTo(minimal);
    }
}