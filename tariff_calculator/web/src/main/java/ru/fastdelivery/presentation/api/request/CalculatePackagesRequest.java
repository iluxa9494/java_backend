package ru.fastdelivery.presentation.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalculatePackagesRequest(
        @NotNull @NotEmpty List<CargoPackage> packages,
        @NotNull String currencyCode,
        @NotNull CoordinateDto source,
        @NotNull CoordinateDto destination
) {
}