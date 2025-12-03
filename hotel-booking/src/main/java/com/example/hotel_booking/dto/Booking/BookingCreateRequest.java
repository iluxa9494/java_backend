package com.example.hotel_booking.dto.Booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingCreateRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long roomId;

    @NotNull
    @FutureOrPresent(message = "Дата заезда должна быть текущей или будущей")
    private LocalDate checkIn;

    @NotNull
    @Future(message = "Дата выезда должна быть в будущем")
    private LocalDate checkOut;
}
