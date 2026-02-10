package ru.fastdelivery.presentation.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CargoPackage(
        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal weight,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal length,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal width,

        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal height
) {
}