package ru.fastdelivery.persistence.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.persistence.entity.CurrencyEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyJpaAdapterTest {

    @Test
    @DisplayName("Если валюта найдена по коду, возвращается доменная модель")
    void whenCurrencyExists_thenReturnsDomainCurrency() {
        CurrencyJpaRepository repository = mock(CurrencyJpaRepository.class);
        CurrencyEntity entity = CurrencyEntity.builder()
                .code("USD")
                .name("Доллар США")
                .rateToRub(BigDecimal.valueOf(90.55))
                .updatedAt(LocalDateTime.now())
                .build();
        when(repository.findByCode("USD")).thenReturn(Optional.of(entity));
        CurrencyJpaAdapter adapter = new CurrencyJpaAdapter(repository);
        Optional<Currency> result = adapter.findByCode("USD");
        assertTrue(result.isPresent());
        assertEquals("USD", result.get().getCode());
        assertEquals(0, result.get().getRateToRub().compareTo(BigDecimal.valueOf(90.55)));
        verify(repository, times(1)).findByCode("USD");
    }

    @Test
    @DisplayName("Если валюта не найдена по коду, возвращается Optional.empty")
    void whenCurrencyNotExists_thenReturnsEmpty() {
        CurrencyJpaRepository repository = mock(CurrencyJpaRepository.class);
        when(repository.findByCode("EUR")).thenReturn(Optional.empty());
        CurrencyJpaAdapter adapter = new CurrencyJpaAdapter(repository);
        Optional<Currency> result = adapter.findByCode("EUR");
        assertFalse(result.isPresent());
        verify(repository, times(1)).findByCode("EUR");
    }
}