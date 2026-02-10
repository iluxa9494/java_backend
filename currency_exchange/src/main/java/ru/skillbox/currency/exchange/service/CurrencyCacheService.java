package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyCacheService {

    private final CurrencyRepository currencyRepository;
    private final Map<Long, Currency> currencyCache = new ConcurrentHashMap<>();

    public void initializeCache() {
        log.info("Инициализация кеша валют...");
        currencyRepository.findAll().forEach(currency ->
                currencyCache.put(Long.valueOf(currency.getIsoNumCode()), currency)
        );
        log.info("Кеш валют успешно загружен. Количество записей: {}", currencyCache.size());
    }

    public Currency getCurrencyFromCache(Long isoNumericCode) {
        return currencyCache.get(isoNumericCode);
    }

    public void updateCache(Currency updatedCurrency) {
        currencyCache.put(Long.valueOf(updatedCurrency.getIsoNumCode()), updatedCurrency);
        log.info("Кеш обновлен для валюты: {} с новым курсом: {}",
                updatedCurrency.getIsoCharCode(), updatedCurrency.getExchangeRate());
    }

    public void clearCache() {
        currencyCache.clear();
        log.info("Кеш валют очищен.");
    }
}
