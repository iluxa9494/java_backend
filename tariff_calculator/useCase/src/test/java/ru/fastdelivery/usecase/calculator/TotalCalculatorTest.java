package ru.fastdelivery.usecase.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.domain.dimension.OuterDimensions;
import ru.fastdelivery.domain.geo.Coordinate;
import ru.fastdelivery.domain.repository.TariffSettingsRepository;
import ru.fastdelivery.domain.tariff.TariffSettings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TotalCalculatorTest {
    private VolumeCalculator volumeCalculator;
    private DistanceCalculator distanceCalculator;
    private TariffSettingsRepository repository;
    private TotalCalculator calculator;

    @BeforeEach
    void setUp() {
        volumeCalculator = mock(VolumeCalculator.class);
        distanceCalculator = mock(DistanceCalculator.class);
        repository = mock(TariffSettingsRepository.class);
        calculator = new TotalCalculator(
                volumeCalculator,
                distanceCalculator,
                repository,
                new BigDecimal("0.04"),
                new BigDecimal("1200.00"),
                new BigDecimal("500.00"),
                450
        );
    }

    @Test
    @DisplayName("Расчет стоимости с использованием fallback-тарифа")
    void whenTariffNotFound_thenUseFallback() {
        Currency currency = new Currency("RUB", BigDecimal.ONE);
        Shipment shipment = new Shipment(
                List.of(new Pack(
                        new Weight(BigInteger.valueOf(2000)),
                        new OuterDimensions(BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(40))
                )),
                new Coordinate(BigDecimal.valueOf(55.75), BigDecimal.valueOf(37.61)),
                new Coordinate(BigDecimal.valueOf(59.93), BigDecimal.valueOf(30.33)),
                currency
        );
        when(repository.getLatest()).thenReturn(Optional.empty());
        when(volumeCalculator.calculate(any())).thenReturn(new BigDecimal("0.2"));
        when(distanceCalculator.calculate(any(), any())).thenReturn(900.0);
        Price price = calculator.calculateTotal(shipment);
        assertThat(price).isNotNull();
        assertThat(price.getCurrencyCode()).isEqualTo("RUB");
        assertThat(price.getAmount()).isGreaterThanOrEqualTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Получение минимальной цены — fallback")
    void whenNoTariff_thenGetFallbackMinimal() {
        when(repository.getLatest()).thenReturn(Optional.empty());
        Price price = calculator.getMinimalPrice();
        assertThat(price.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(price.getCurrencyCode()).isEqualTo("RUB");
    }

    @Test
    @DisplayName("Большие значения веса/габаритов дают цену больше минимальной")
    void whenLargeValues_thenTotalGreaterThanMinimal() {
        TariffSettings tariff = new TariffSettings(
                new BigDecimal("0.04"),
                new BigDecimal("1200.00"),
                new BigDecimal("500.00"),
                450,
                "RUB"
        );
        when(repository.getLatest()).thenReturn(Optional.of(tariff));
        VolumeCalculator realVolume = new VolumeCalculator();
        DistanceCalculator realDistance = new DistanceCalculator();
        TotalCalculator realCalculator = new TotalCalculator(
                realVolume,
                realDistance,
                repository,
                new BigDecimal("0.04"),
                new BigDecimal("1200.00"),
                new BigDecimal("500.00"),
                450
        );

        Currency currency = new Currency("RUB", BigDecimal.ONE);
        Shipment shipment = new Shipment(
                List.of(new Pack(
                        new Weight(BigInteger.valueOf(150000)),
                        new OuterDimensions(BigDecimal.valueOf(1500), BigDecimal.valueOf(1500), BigDecimal.valueOf(1500))
                )),
                new Coordinate(BigDecimal.ZERO, BigDecimal.ZERO),
                new Coordinate(BigDecimal.ZERO, BigDecimal.valueOf(50)),
                currency
        );

        Price price = realCalculator.calculateTotal(shipment);
        assertThat(price.getAmount()).isGreaterThan(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Объемный расчет использует стоимость за м3")
    void whenVolumeDominates_thenUsesVolumeRate() {
        TariffSettings tariff = new TariffSettings(
                new BigDecimal("0.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("0.00"),
                450,
                "RUB"
        );
        TariffSettingsRepository repo = mock(TariffSettingsRepository.class);
        when(repo.getLatest()).thenReturn(Optional.of(tariff));

        VolumeCalculator realVolume = new VolumeCalculator();
        DistanceCalculator zeroDistance = mock(DistanceCalculator.class);
        when(zeroDistance.calculate(any(), any())).thenReturn(0.0);

        TotalCalculator volumeCalculator = new TotalCalculator(
                realVolume,
                zeroDistance,
                repo,
                new BigDecimal("0.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("0.00"),
                450
        );

        Currency currency = new Currency("RUB", BigDecimal.ONE);
        Shipment shipment = new Shipment(
                List.of(new Pack(
                        new Weight(BigInteger.valueOf(1000)),
                        new OuterDimensions(BigDecimal.valueOf(350), BigDecimal.valueOf(600), BigDecimal.valueOf(250))
                )),
                new Coordinate(BigDecimal.valueOf(55.75), BigDecimal.valueOf(37.61)),
                new Coordinate(BigDecimal.valueOf(55.75), BigDecimal.valueOf(37.61)),
                currency
        );

        Price price = volumeCalculator.calculateTotal(shipment);
        assertThat(price.getAmount()).isEqualByComparingTo(new BigDecimal("52.50"));
    }
}
