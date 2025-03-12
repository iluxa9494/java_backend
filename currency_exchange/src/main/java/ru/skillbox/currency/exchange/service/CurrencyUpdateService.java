package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyUpdateService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyLoader currencyLoader;

    @Transactional
    public void updateCurrencies() {
        log.info("Обновление курсов валют...");
        try {
            currencyLoader.updateCurrencies();
            log.info("Курсы валют успешно обновлены.");
        } catch (Exception e) {
            log.error("Ошибка при обновлении валют: {}", e.getMessage(), e);
        }
    }
}
