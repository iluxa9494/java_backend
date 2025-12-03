package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.geo.Coordinate;

import java.util.List;

/**
 * Представляет доставку: упаковки, координаты и валюту.
 */
public record Shipment(
        List<Pack> packages,
        Coordinate source,
        Coordinate destination,
        Currency currency
) {

    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::weight)
                .reduce(Weight.zero(), Weight::add);
    }
}