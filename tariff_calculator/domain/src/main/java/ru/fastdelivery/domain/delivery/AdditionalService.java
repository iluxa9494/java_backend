package ru.fastdelivery.domain.delivery;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class AdditionalService {
    String name;
    BigDecimal price;
    String priceType;
    String description;
}