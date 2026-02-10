package ru.fastdelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.repository.AdditionalServiceRepository;
import ru.fastdelivery.domain.repository.CurrencyRepository;
import ru.fastdelivery.persistence.repository.AdditionalServiceJpaAdapter;
import ru.fastdelivery.persistence.repository.AdditionalServiceJpaRepository;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

/**
 * Конфигурационный класс для регистрации вручную создаваемых бинов.
 * Используется для явной привязки интерфейсов к их реализациям.
 */
@Configuration
public class Beans {

    @Bean
    public CurrencyFactory currencyFactory(CurrencyRepository currencyRepository) {
        return new CurrencyFactory(currencyRepository);
    }

    @Bean
    public TariffCalculateUseCase tariffCalculateUseCase(
            ru.fastdelivery.usecase.calculator.TotalCalculator totalCalculator
    ) {
        return new TariffCalculateUseCase(totalCalculator);
    }

    @Bean
    public AdditionalServiceRepository additionalServiceRepository(
            AdditionalServiceJpaRepository repository) {
        return new AdditionalServiceJpaAdapter(repository);
    }
}