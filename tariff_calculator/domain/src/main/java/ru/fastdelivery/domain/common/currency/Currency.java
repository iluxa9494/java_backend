package ru.fastdelivery.domain.common.currency;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Валюта для стоимости
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Currency {
    String code;
}
