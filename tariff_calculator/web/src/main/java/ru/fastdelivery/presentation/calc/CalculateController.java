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
import org.springframework.web.bind.annotation.RestController;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.domain.dimension.OuterDimensions;
import ru.fastdelivery.domain.geo.Coordinate;
import ru.fastdelivery.domain.repository.UserRequestRepository;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.presentation.config.GeoProperties;
import ru.fastdelivery.usecase.TariffCalculateUseCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Расчеты стоимости доставки")
public class CalculateController {
    private final GeoProperties geoProperties;
    private final TariffCalculateUseCase tariffCalculateUseCase;
    private final CurrencyFactory currencyFactory;
    private final ObjectMapper objectMapper;
    private final UserRequestRepository userRequestRepository;

    @PostMapping(path = {"/api/v1/calculate", "/api/tariff-calculator"})
    @Operation(summary = "Расчет стоимости по упаковкам груза")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный расчет"),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса")
    })
    public CalculatePackagesResponse calculate(
            @Valid @RequestBody CalculatePackagesRequest request,
            HttpServletRequest servletRequest
    ) {
        String requestJson = "";
        String ipAddress = extractClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        try {
            requestJson = objectMapper.writeValueAsString(request);
            log.info("Запрос на расчет: IP={}, UA={}, Payload={}",
                    ipAddress,
                    userAgent,
                    requestJson
            );
            validateCoordinates(request);
            List<Pack> packs = request.packages().stream()
                    .map(pkg -> new Pack(
                            new Weight(validateWeight(pkg.weight()).toBigIntegerExact()),
                            normalizeDimensions(pkg.length(), pkg.width(), pkg.height())
                    ))
                    .toList();
            if (log.isInfoEnabled()) {
                log.info("Нормализованные данные: packagesCount={}, totalWeightGrams={}, currencyCode={}, source={}, destination={}",
                        packs.size(),
                        packs.stream()
                                .map(pack -> pack.weight().weightGrams())
                                .reduce(java.math.BigInteger.ZERO, java.math.BigInteger::add),
                        request.currencyCode(),
                        request.source(),
                        request.destination()
                );
            }
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
            saveSuccessfulRequest(ipAddress, userAgent, requestJson, response, responseJson);
            log.info("Успешный ответ: {}", responseJson);
            return response;
        } catch (IllegalArgumentException e) {
            saveFailedRequest(ipAddress, userAgent, requestJson, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при расчете тарифа", e);
            saveFailedRequest(ipAddress, userAgent, requestJson, e.getMessage());
            throw new RuntimeException("Ошибка при расчете тарифа", e);
        }
    }

    private String extractClientIp(HttpServletRequest servletRequest) {
        String forwardedFor = servletRequest.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return servletRequest.getRemoteAddr();
    }

    private void saveSuccessfulRequest(
            String ipAddress,
            String userAgent,
            String requestJson,
            CalculatePackagesResponse response,
            String responseJson
    ) {
        try {
            userRequestRepository.saveSuccess(
                    ipAddress,
                    userAgent,
                    requestJson,
                    response.totalPrice(),
                    response.minimalPrice(),
                    response.currencyCode(),
                    responseJson
            );
        } catch (Exception saveException) {
            log.error("Не удалось сохранить успешный расчет в БД", saveException);
        }
    }

    private void saveFailedRequest(
            String ipAddress,
            String userAgent,
            String requestJson,
            String errorMessage
    ) {
        try {
            userRequestRepository.saveFailure(
                    ipAddress,
                    userAgent,
                    requestJson,
                    errorMessage
            );
        } catch (Exception saveException) {
            log.error("Не удалось сохранить ошибку расчета в БД", saveException);
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

    private BigDecimal validateWeight(BigDecimal weight) {
        BigDecimal maxAllowed = BigDecimal.valueOf(150000);
        if (weight.compareTo(maxAllowed) > 0) {
            throw new IllegalArgumentException("Вес превышает максимально допустимые 150000 г.");
        }
        return weight;
    }
}
