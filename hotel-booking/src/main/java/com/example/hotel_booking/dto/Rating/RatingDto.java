package com.example.hotel_booking.dto.Rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long hotelId;

    @Min(1)
    @Max(5)
    private int score;

    private String review;

    private LocalDateTime createdAt;
}
