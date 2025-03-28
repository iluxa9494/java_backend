package com.example.hotel_booking.dto.Booking;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
}
