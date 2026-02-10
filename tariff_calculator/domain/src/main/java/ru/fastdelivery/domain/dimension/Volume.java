package ru.fastdelivery.domain.dimension;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Представляет объем в кубических метрах.
 * Обеспечивает проверку на неотрицательное значение и операции сложения.
 */
public final class Volume {
    private final BigDecimal cubicMeters;

    public Volume(BigDecimal cubicMeters) {
        if (cubicMeters.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Объём не может быть отрицательным");
        }
        this.cubicMeters = cubicMeters;
    }

    @Override
    public String toString() {
        return cubicMeters + " м³";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volume)) return false;
        Volume volume = (Volume) o;
        return Objects.equals(cubicMeters, volume.cubicMeters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cubicMeters);
    }
}