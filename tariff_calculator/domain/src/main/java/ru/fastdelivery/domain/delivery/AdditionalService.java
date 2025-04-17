package ru.fastdelivery.domain.delivery;

import lombok.Value;

import java.math.BigDecimal;

/**
 * Описывает дополнительную услугу, которую можно применить к доставке.
 * Содержит название, цену, тип цены и описание.
 */
@Value
public class AdditionalService {
    String name;
    BigDecimal price;
    String priceType;
    String description;
}