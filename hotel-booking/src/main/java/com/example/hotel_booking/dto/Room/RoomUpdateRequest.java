package com.example.hotel_booking.dto.Room;

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
    private String name;
    private String description;
    @NotBlank
    private String roomNumber;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotNull
    @Positive
    private Integer maxGuests;
}
