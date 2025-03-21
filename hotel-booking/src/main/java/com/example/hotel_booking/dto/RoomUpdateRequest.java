package com.example.hotel_booking.dto;

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
public class RoomUpdateRequest {
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String number;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotNull
    @Positive
    private Integer maxGuests;
}
