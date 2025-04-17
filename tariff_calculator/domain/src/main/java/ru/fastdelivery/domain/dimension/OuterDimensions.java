package ru.fastdelivery.domain.dimension;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Габариты упаковки в миллиметрах.
 */
public record OuterDimensions(
        BigDecimal length,
        BigDecimal width,
        BigDecimal height
) {
    private static final BigDecimal ONE_BILLION = BigDecimal.valueOf(1_000_000_000);

    public OuterDimensions {
        if (isNonPositive(length) || isNonPositive(width) || isNonPositive(height)) {
            throw new IllegalArgumentException("All dimensions must be positive and non-zero");
        }
    }

    private static boolean isNonPositive(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public BigDecimal cubicMeters() {
        return volume().divide(ONE_BILLION, 6, RoundingMode.HALF_UP);
    }

    public BigDecimal volume() {
        return length.multiply(width).multiply(height);
    }
}
