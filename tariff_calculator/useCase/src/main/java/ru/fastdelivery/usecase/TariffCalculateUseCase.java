package ru.fastdelivery.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.usecase.calculator.TotalCalculator;

import javax.inject.Named;

@Slf4j
@Named
@RequiredArgsConstructor
public class TariffCalculateUseCase {
    private final TotalCalculator totalCalculator;

    public Price calc(Shipment shipment) {
        return totalCalculator.calculateTotal(shipment);
    }

    public Price minimalPrice() {
        return totalCalculator.getMinimalPrice();
    }
}