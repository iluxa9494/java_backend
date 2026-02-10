package ru.fastdelivery.domain.geo;

import java.math.BigDecimal;
import java.util.Objects;

public final class Coordinate {
    private final BigDecimal lat;
    private final BigDecimal lon;

    public Coordinate(BigDecimal lat, BigDecimal lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat() {
        return lat.doubleValue();
    }

    public double lon() {
        return lon.doubleValue();
    }

    public BigDecimal latRaw() {
        return lat;
    }

    public BigDecimal lonRaw() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return Objects.equals(lat, that.lat) && Objects.equals(lon, that.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}