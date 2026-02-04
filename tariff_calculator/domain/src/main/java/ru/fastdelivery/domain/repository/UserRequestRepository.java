package ru.fastdelivery.domain.repository;

import java.math.BigDecimal;

/**
 * Репозиторий для сохранения пользовательских запросов на расчет тарифа.
 */
public interface UserRequestRepository {
    void saveSuccess(
            String ipAddress,
            String userAgent,
            String requestPayload,
            BigDecimal totalPrice,
            BigDecimal minimalPrice,
            String currencyCode,
            String responsePayload
    );

    void saveFailure(
            String ipAddress,
            String userAgent,
            String requestPayload,
            String errorMessage
    );
}
