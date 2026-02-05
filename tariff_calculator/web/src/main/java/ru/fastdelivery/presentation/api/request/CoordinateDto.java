package ru.fastdelivery.presentation.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CoordinateDto(
        @NotNull @Min(-90) @Max(90)
        BigDecimal lat,

        @NotNull @Min(-180) @Max(180)
        BigDecimal lon
) {
}