package ru.fastdelivery.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class TariffPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TariffPropertiesConfig.class)
            .withPropertyValues(
                    "spring.config.location=classpath:application-test.yml"
            );

    @Test
    void bindTariffPropertiesCorrectly() {
        contextRunner.run(context -> {
            TariffProperties properties = context.getBean(TariffProperties.class);
            assertThat(properties.getWeightCostPerGram()).isEqualByComparingTo(BigDecimal.valueOf(0.04));
            assertThat(properties.getVolumeCostPerM3()).isEqualByComparingTo(BigDecimal.valueOf(1200));
            assertThat(properties.getMinimalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
            assertThat(properties.getDistanceStep()).isEqualTo(450);
        });
    }

    @EnableConfigurationProperties(TariffProperties.class)
    static class TariffPropertiesConfig {
    }
}
