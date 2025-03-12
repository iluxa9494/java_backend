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

    /**
     * Инициализация кеша валют при запуске.
     */
    public void initializeCache() {
        log.info("Инициализация кеша валют...");
        currencyRepository.findAll().forEach(currency ->
                currencyCache.put(Long.valueOf(currency.getIsoNumCode()), currency)
        );
        log.info("Кеш валют успешно загружен. Количество записей: {}", currencyCache.size());
    }

    /**
     * Получить валюту из кеша по числовому коду (ISO).
     *
     * @param isoNumericCode числовой код валюты.
     * @return объект Currency, если найден.
     */
    public Currency getCurrencyFromCache(Long isoNumericCode) {
        return currencyCache.get(isoNumericCode);
    }

    /**
     * Обновить кеш при изменении курса валют.
     *
     * @param updatedCurrency обновленный объект Currency.
     */
    public void updateCache(Currency updatedCurrency) {
        currencyCache.put(Long.valueOf(updatedCurrency.getIsoNumCode()), updatedCurrency);
        log.info("Кеш обновлен для валюты: {} с новым курсом: {}",
                updatedCurrency.getIsoCharCode(), updatedCurrency.getExchangeRate());
    }

    /**
     * Очистить кеш валют.
     */
    public void clearCache() {
        currencyCache.clear();
        log.info("Кеш валют очищен.");
    }
}
