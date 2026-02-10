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

    @Scheduled(cron = "${scheduler.cron.expression:0 0 * * * *}")
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