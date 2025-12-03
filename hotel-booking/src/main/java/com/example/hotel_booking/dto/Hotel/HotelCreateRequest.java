package com.example.hotel_booking.dto.Hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelCreateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String title;

    @NotBlank
    private String city;

    @NotBlank
    private String address;

    @NotNull
    @Positive
    private BigDecimal distanceFromCenter;
}
