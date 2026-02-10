package ru.skillbox.currency.exchange.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.UpdateExchangeRateRequest;
import ru.skillbox.currency.exchange.service.CurrencyService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;
    @GetMapping
    public List<CurrencyDto> getAllCurrencies() {
        log.info("Запрос на получение списка всех валют");
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();
        log.info("Получено {} валют", currencies.size());
        return currencies;
    }

    @GetMapping("/short")
    public List<CurrencyDto> getAllShortCurrencies() {
        log.info("Запрос на получение сокращённого списка валют");
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();
        log.info("Получено {} валют (сокращённая информация)", currencies.size());
        return currencies;
    }

    @GetMapping("/{id}")
    public Optional<CurrencyDto> getCurrencyById(@PathVariable Long id) {
        log.info("Запрос на получение валюты по ID: {}", id);
        Optional<CurrencyDto> currency = currencyService.getCurrencyById(id);
        if (currency.isPresent()) {
            log.info("Валюта найдена: {}", currency.get());
        } else {
            log.warn("Валюта с ID {} не найдена", id);
        }
        return currency;
    }

    @GetMapping("/iso/{isoNumCode}")
    public Optional<CurrencyDto> getCurrencyByIsoCode(@PathVariable Integer isoNumCode) {
        log.info("Запрос на получение валюты по ISO-коду: {}", isoNumCode);
        Optional<CurrencyDto> currency = currencyService.getCurrencyByIsoCode(isoNumCode);
        if (currency.isPresent()) {
            log.info("Валюта найдена: {}", currency.get());
        } else {
            log.warn("Валюта с ISO-кодом {} не найдена", isoNumCode);
        }
        return currency;
    }

    @PostMapping
    public CurrencyDto createCurrency(@RequestBody CurrencyDto currencyDto) {
        log.info("Запрос на создание новой валюты: {}", currencyDto);
        CurrencyDto createdCurrency = currencyService.createCurrency(currencyDto);
        log.info("Валюта успешно создана: {}", createdCurrency);
        return createdCurrency;
    }

    @PutMapping("/{isoNumCode}/exchangeRate")
    public CurrencyDto updateCurrencyRate(@PathVariable Integer isoNumCode, @RequestBody UpdateExchangeRateRequest request) {
        log.info("Запрос на обновление курса валюты с ISO-кодом {}: новый курс {}", isoNumCode, request.getExchangeRate());
        CurrencyDto updatedCurrency = currencyService.updateCurrencyRate(isoNumCode, request.getExchangeRate());
        log.info("Курс валюты обновлён: {}", updatedCurrency);
        return updatedCurrency;
    }

    @DeleteMapping("/{id}")
    public void deleteCurrency(@PathVariable Long id) {
        log.info("Запрос на удаление валюты с ID: {}", id);
        currencyService.deleteCurrency(id);
        log.info("Валюта с ID {} успешно удалена", id);
    }

    @GetMapping("/convert")
    public BigDecimal convertCurrency(@RequestParam BigDecimal amount, @RequestParam Integer isoNumCode) {
        log.info("Запрос на конвертацию: сумма {} для валюты с ISO-кодом {}", amount, isoNumCode);
        BigDecimal result = currencyService.convertCurrency(amount, isoNumCode);
        log.info("Результат конвертации: {} -> {}", amount, result);
        return result;
    }
}