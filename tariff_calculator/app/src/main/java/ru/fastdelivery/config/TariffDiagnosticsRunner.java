package ru.fastdelivery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.fastdelivery.persistence.entity.TariffSettingsEntity;
import ru.fastdelivery.persistence.repository.TariffSettingsJpaRepository;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class TariffDiagnosticsRunner {
    private final TariffSettingsJpaRepository tariffSettingsRepository;

    @Bean
    public ApplicationRunner tariffDiagnostics() {
        return args -> {
            long count = tariffSettingsRepository.count();
            log.info("Tariff settings count in DB: {}", count);
            tariffSettingsRepository.findTopByOrderByCreatedAtDesc()
                    .ifPresentOrElse(
                            this::logLatestTariff,
                            () -> log.warn("Tariff settings table is empty.")
                    );
        };
    }

    private void logLatestTariff(TariffSettingsEntity tariff) {
        log.info("Latest tariff settings: id={}, weightRate={}, volumeRate={}, minimalPrice={}, distanceStepKm={}, currencyCode={}, createdAt={}",
                tariff.getId(),
                tariff.getWeightRate(),
                tariff.getVolumeRate(),
                tariff.getMinimalPrice(),
                tariff.getDistanceStepKm(),
                tariff.getCurrencyCode(),
                tariff.getCreatedAt()
        );
    }
}
