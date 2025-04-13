package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.price.Price;

public interface WeightPriceProvider {
    Price costPerKg();

    Price minimalPrice();
}
