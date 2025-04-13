package ru.fastdelivery.domain.common.weight;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Общий класс веса
 *
 * @param weightGrams вес в граммах
 */
public record Weight(BigInteger weightGrams) implements Comparable<Weight> {

    public Weight {
        if (isLessThanZero(weightGrams)) {
            throw new IllegalArgumentException("Weight cannot be below Zero!");
        }
    }

    private static boolean isLessThanZero(BigInteger price) {
        return BigInteger.ZERO.compareTo(price) > 0;
    }

    public static Weight zero() {
        return new Weight(BigInteger.ZERO);
    }

    public BigDecimal kilograms() {
        return new BigDecimal(weightGrams)
                .divide(BigDecimal.valueOf(1000), 100, RoundingMode.HALF_UP);
    }

    public Weight add(Weight additionalWeight) {
        return new Weight(this.weightGrams.add(additionalWeight.weightGrams));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Weight weight = (Weight) o;
        return weightGrams.compareTo(weight.weightGrams) == 0;
    }


    @Override
    public int compareTo(Weight w) {
        return w.weightGrams().compareTo(weightGrams());
    }

    public boolean greaterThan(Weight w) {
        return weightGrams().compareTo(w.weightGrams()) > 0;
    }
}
