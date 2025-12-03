package ru.fastdelivery.domain.common.currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrencyFactoryTest {
    CurrencyRepository repository = mock(CurrencyRepository.class);
    CurrencyFactory factory = new CurrencyFactory(repository);

    @Test
    @DisplayName("Создание валюты — OK")
    void whenCurrencyExists_thenReturnCurrency() {
        Currency expected = new Currency("USD", BigDecimal.valueOf(90.5));
        when(repository.findByCode("USD")).thenReturn(Optional.of(expected));
        Currency currency = factory.create("USD");
        assertThat(currency.getCode()).isEqualTo("USD");
        assertThat(currency.getRateToRub()).isEqualTo(BigDecimal.valueOf(90.5));
    }

    @Test
    @DisplayName("Валюта не найдена — ошибка")
    void whenCurrencyNotFound_thenThrowException() {
        when(repository.findByCode("EUR")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> factory.create("EUR"));
    }
}