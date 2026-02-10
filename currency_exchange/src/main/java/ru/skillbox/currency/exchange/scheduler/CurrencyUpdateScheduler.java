package ru.skillbox.currency.exchange.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skillbox.currency.exchange.service.CurrencyUpdateService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyUpdateScheduler {

    private final CurrencyUpdateService currencyUpdateService;

    @Scheduled(
            initialDelayString = "${scheduler.initial-delay-ms:120000}",
            fixedDelayString = "${scheduler.fixed-delay-ms:21600000}"
    )
    public void scheduledCurrencyUpdate() {
        log.info("Запуск планового обновления курсов валют...");
        try {
            currencyUpdateService.updateCurrencies();
            log.info("Обновление курсов валют завершено.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении курсов валют: {}", e.getMessage(), e);
        }
    }
}
