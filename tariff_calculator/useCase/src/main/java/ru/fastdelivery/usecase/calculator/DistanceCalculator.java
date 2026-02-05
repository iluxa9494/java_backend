package ru.fastdelivery.usecase.calculator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.fastdelivery.domain.geo.Coordinate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    public double calculate(Coordinate source, Coordinate destination) {
        log.info("Расчет расстояния между координатами: {} и {}", source, destination);
        double lat1 = Math.toRadians(source.lat());
        double lon1 = Math.toRadians(source.lon());
        double lat2 = Math.toRadians(destination.lat());
        double lon2 = Math.toRadians(destination.lon());
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = EARTH_RADIUS_KM * c;
        log.info("Итоговое расстояние: {} км", result);
        return result;
    }
}