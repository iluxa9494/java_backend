package com.example.hotel_booking.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetails {
    private String bookingId;
    private String roomId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
}