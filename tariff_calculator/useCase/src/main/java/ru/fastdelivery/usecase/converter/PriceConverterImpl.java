package ru.fastdelivery.usecase.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Реализация конвертации цен между валютами на основе курса к рублю.
 * Использует {@link CurrencyRepository} для получения актуальных курсов валют.
 */
@RequiredArgsConstructor
@Slf4j
public class PriceConverterImpl implements PriceConverter {
    private final CurrencyRepository currencyRepository;

    @Override
    public Price convert(Price price, Currency target) {
        if (price.getCurrencyCode().equalsIgnoreCase(target.getCode())) {
            return price;
        }
        BigDecimal fromRate = currencyRepository.findByCode(price.getCurrencyCode())
                .orElseThrow(() -> new IllegalArgumentException("Не найден курс валюты: " + price.getCurrencyCode()))
                .getRateToRub();
        BigDecimal toRate = target.getRateToRub();
        BigDecimal inRub = price.getAmount().multiply(fromRate);
        BigDecimal converted = inRub.divide(toRate, 18, RoundingMode.HALF_UP);
        log.debug("Конвертация {} {} → {} {}",
                price.getAmount(), price.getCurrencyCode(),
                converted, target.getCode());
        return Price.of(converted, target.getCode());
    }
}