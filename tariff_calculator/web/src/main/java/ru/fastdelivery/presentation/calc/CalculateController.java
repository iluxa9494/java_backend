package ru.fastdelivery.presentation.calc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.domain.dimension.OuterDimensions;
import ru.fastdelivery.domain.geo.Coordinate;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.presentation.config.GeoProperties;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Контроллер для расчета стоимости доставки.
 * Обрабатывает запросы на расчет по упаковкам и координатам, возвращает итоговую стоимость и минимальную цену.
 */
@RestController
@RequestMapping("/api/v1/calculate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Расчеты стоимости доставки")
public class CalculateController {
    private final GeoProperties geoProperties;
    private final TariffCalculateUseCase tariffCalculateUseCase;
    private final CurrencyFactory currencyFactory;
    private final ObjectMapper objectMapper;

    @PostMapping
    @Operation(summary = "Расчет стоимости по упаковкам груза")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный расчет"),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса")
    })
    public CalculatePackagesResponse calculate(
            @Valid @RequestBody CalculatePackagesRequest request,
            HttpServletRequest servletRequest
    ) {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            log.info("Запрос на расчет: IP={}, UA={}, Payload={}",
                    servletRequest.getRemoteAddr(),
                    servletRequest.getHeader("User-Agent"),
                    requestJson
            );
            validateCoordinates(request);
            List<Pack> packs = request.packages().stream()
                    .map(pkg -> new Pack(
                            new Weight(pkg.weight().toBigIntegerExact()),
                            normalizeDimensions(pkg.length(), pkg.width(), pkg.height())
                    ))
                    .toList();
            Shipment shipment = new Shipment(
                    packs,
                    new Coordinate(request.source().lat(), request.source().lon()),
                    new Coordinate(request.destination().lat(), request.destination().lon()),
                    currencyFactory.create(request.currencyCode())
            );
            CalculatePackagesResponse response = new CalculatePackagesResponse(
                    tariffCalculateUseCase.calc(shipment),
                    tariffCalculateUseCase.minimalPrice()
            );
            String responseJson = objectMapper.writeValueAsString(response);
            log.info("Успешный ответ: {}", responseJson);
            return response;
        } catch (Exception e) {
            log.error("Ошибка при расчете тарифа: {}", e.getMessage());
            throw new RuntimeException("Ошибка при расчете тарифа");
        }
    }

    private void validateCoordinates(CalculatePackagesRequest request) {
        double srcLat = request.source().lat().doubleValue();
        double srcLon = request.source().lon().doubleValue();
        double dstLat = request.destination().lat().doubleValue();
        double dstLon = request.destination().lon().doubleValue();
        if (srcLat < geoProperties.getMinLatitude().doubleValue() || srcLat > geoProperties.getMaxLatitude().doubleValue() ||
                dstLat < geoProperties.getMinLatitude().doubleValue() || dstLat > geoProperties.getMaxLatitude().doubleValue() ||
                srcLon < geoProperties.getMinLongitude().doubleValue() || srcLon > geoProperties.getMaxLongitude().doubleValue() ||
                dstLon < geoProperties.getMinLongitude().doubleValue() || dstLon > geoProperties.getMaxLongitude().doubleValue()) {
            throw new IllegalArgumentException("Координаты выходят за допустимые границы, указанные в конфигурации.");
        }
    }

    private OuterDimensions normalizeDimensions(BigDecimal length, BigDecimal width, BigDecimal height) {
        validateDimension(length, "length");
        validateDimension(width, "width");
        validateDimension(height, "height");
        return new OuterDimensions(
                roundToNearestFifty(length),
                roundToNearestFifty(width),
                roundToNearestFifty(height)
        );
    }

    private BigDecimal roundToNearestFifty(BigDecimal dimension) {
        return dimension.add(BigDecimal.valueOf(25))
                .divide(BigDecimal.valueOf(50), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(50));
    }

    private void validateDimension(BigDecimal dimension, String dimensionName) {
        BigDecimal maxAllowed = BigDecimal.valueOf(1500);
        if (dimension.compareTo(maxAllowed) > 0) {
            throw new IllegalArgumentException(
                    String.format("Размер %s превышает максимально допустимые 1500 мм.", dimensionName)
            );
        }
    }
}