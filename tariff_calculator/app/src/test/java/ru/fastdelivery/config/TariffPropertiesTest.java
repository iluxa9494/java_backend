package ru.fastdelivery.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class TariffPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TariffPropertiesConfig.class)
            .withPropertyValues(
                    "cost.rub.perKg=400.00",
                    "cost.rub.perCubicMeter=1200.00",
                    "cost.rub.minimal=500.00",
                    "cost.rub.distanceStepKm=450"
            );

    @Test
    void bindTariffPropertiesCorrectly() {
        contextRunner.run(context -> {
            TariffProperties properties = context.getBean(TariffProperties.class);
            assertThat(properties.getPerKg()).isEqualByComparingTo(BigDecimal.valueOf(400));
            assertThat(properties.getPerCubicMeter()).isEqualByComparingTo(BigDecimal.valueOf(1200));
            assertThat(properties.getMinimal()).isEqualByComparingTo(BigDecimal.valueOf(500));
            assertThat(properties.getDistanceStepKm()).isEqualTo(450);
        });
    }

    @EnableConfigurationProperties(TariffProperties.class)
    static class TariffPropertiesConfig {
    }
}