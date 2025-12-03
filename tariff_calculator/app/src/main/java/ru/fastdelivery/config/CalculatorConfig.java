package ru.fastdelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.fastdelivery.domain.repository.TariffSettingsRepository;
import ru.fastdelivery.usecase.calculator.DistanceCalculator;
import ru.fastdelivery.usecase.calculator.TotalCalculator;
import ru.fastdelivery.usecase.calculator.VolumeCalculator;

/**
 * Конфигурация калькуляторов, используемых для расчета стоимости доставки.
 * Создает бин TotalCalculator с необходимыми зависимостями.
 */
@Configuration
public class CalculatorConfig {

    @Bean
    public TotalCalculator totalCalculator(
            VolumeCalculator volumeCalculator,
            DistanceCalculator distanceCalculator,
            TariffSettingsRepository tariffSettingsRepository,
            TariffProperties tariffProperties
    ) {
        return new TotalCalculator(
                volumeCalculator,
                distanceCalculator,
                tariffSettingsRepository,
                tariffProperties.getPerKg(),
                tariffProperties.getPerCubicMeter(),
                tariffProperties.getMinimal(),
                tariffProperties.getDistanceStepKm()
        );
    }
}