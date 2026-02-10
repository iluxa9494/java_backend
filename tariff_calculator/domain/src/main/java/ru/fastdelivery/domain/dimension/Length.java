package ru.fastdelivery.domain.dimension;

import java.math.BigDecimal;
import java.util.Objects;

public final class Length {

    private final BigDecimal value;

    public Length(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Длина должна быть положительной");
        }
        this.value = value;
    }

    public BigDecimal value() {
        return value;
    }

    public Length add(Length other) {
        return new Length(this.value.add(other.value));
    }

    @Override
    public String toString() {
        return value + " мм";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Length)) return false;
        Length length = (Length) o;
        return Objects.equals(value, length.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
