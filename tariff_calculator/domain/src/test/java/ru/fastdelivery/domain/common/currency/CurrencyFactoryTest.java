package ru.fastdelivery.domain.common.currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyFactoryTest {

    CurrencyPropertiesProvider mockProvider = mock(CurrencyPropertiesProvider.class);
    CurrencyFactory factory = new CurrencyFactory(mockProvider);

    @Test
    @DisplayName("Код валюты NULL -> исключение")
    void whenCodeIsNull_thenThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(null));
    }

    @Test
    @DisplayName("Не доступно создание из указанного кода валюты -> исключение")
    void whenCodeIsNotAvailable_thenThrowException() {
        when(mockProvider.isAvailable("USD")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> factory.create("USD"));
    }

    @Test
    @DisplayName("Код валюты разрешен для создания -> новый объект")
    void whenCodeIsAvailable_thenObjectCreated() {
        when(mockProvider.isAvailable("RUB")).thenReturn(true);

        assertThat(factory.create("RUB")).isNotNull();
    }
}