package ru.fastdelivery.domain.delivery.pack;

import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.dimension.OuterDimensions;

import java.math.BigInteger;

/**
 * Упаковка груза
 */
public record Pack(Weight weight, OuterDimensions outerDimensions) {
    private static final Weight maxWeight = new Weight(BigInteger.valueOf(150_000));

    public Pack {
        if (weight.greaterThan(maxWeight)) {
            throw new IllegalArgumentException("Package can't be more than " + maxWeight);
        }
    }
}