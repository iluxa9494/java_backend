package ru.fastdelivery.presentation.api.response;

import lombok.extern.slf4j.Slf4j;
import ru.fastdelivery.domain.common.price.Price;

import java.math.BigDecimal;

/**
 * Ответ API на расчет стоимости доставки.
 */
@Slf4j
public record CalculatePackagesResponse(
        BigDecimal totalPrice,
        BigDecimal minimalPrice,
        String currencyCode
) {
    public CalculatePackagesResponse(Price totalPrice, Price minimalPrice) {
        this(
                totalPrice.amount(),
                minimalPrice.amount(),
                totalPrice.currency().getCode()
        );
        if (!totalPrice.currency().equals(minimalPrice.currency())) {
            log.error("Несовпадение валют: {} != {}", totalPrice.currency(), minimalPrice.currency());
            throw new IllegalArgumentException("Currencies do not match");
        }
        log.info("Создан ответ: totalPrice={} {}, minimalPrice={} {}",
                totalPrice.amount(), totalPrice.currency().getCode(),
                minimalPrice.amount(), minimalPrice.currency().getCode()
        );
    }
}