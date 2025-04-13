package ru.fastdelivery.domain.common.currency;

/**
 * Проверка доступности использования валюты
 */
public interface CurrencyPropertiesProvider {
    /**
     * @param code трехбуквенный код валюты
     * @return доступно ли использование валюты в приложении
     */
    boolean isAvailable(String code);
}
