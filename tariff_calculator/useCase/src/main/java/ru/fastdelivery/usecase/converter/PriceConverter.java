package ru.fastdelivery.usecase.converter;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.Price;

public interface PriceConverter {
    Price convert(Price price, Currency target);
}