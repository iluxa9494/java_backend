package com.example.hotel_booking.dto.Room;

import jakarta.validation.constraints.Min;
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
public class RoomCreateRequest {
    @NotNull
    private Long hotelId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String number;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer maxGuests;
}
