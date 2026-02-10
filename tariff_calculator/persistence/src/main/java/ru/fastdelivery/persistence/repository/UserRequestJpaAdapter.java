package ru.fastdelivery.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.fastdelivery.domain.repository.UserRequestRepository;
import ru.fastdelivery.persistence.entity.UserRequestEntity;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class UserRequestJpaAdapter implements UserRequestRepository {
    private final UserRequestJpaRepository repository;

    @Override
    public void saveSuccess(
            String ipAddress,
            String userAgent,
            String requestPayload,
            BigDecimal totalPrice,
            BigDecimal minimalPrice,
            String currencyCode,
            String responsePayload
    ) {
        UserRequestEntity entity = new UserRequestEntity();
        entity.setIpAddress(ipAddress);
        entity.setUserAgent(userAgent);
        entity.setRequestPayload(requestPayload);
        entity.setTotalPrice(totalPrice);
        entity.setMinimalPrice(minimalPrice);
        entity.setCurrencyCode(currencyCode);
        entity.setResponsePayload(responsePayload);
        entity.setStatus("SUCCESS");
        repository.save(entity);
    }

    @Override
    public void saveFailure(
            String ipAddress,
            String userAgent,
            String requestPayload,
            String errorMessage
    ) {
        UserRequestEntity entity = new UserRequestEntity();
        entity.setIpAddress(ipAddress);
        entity.setUserAgent(userAgent);
        entity.setRequestPayload(requestPayload);
        entity.setStatus("ERROR");
        entity.setErrorMessage(errorMessage);
        repository.save(entity);
    }
}
