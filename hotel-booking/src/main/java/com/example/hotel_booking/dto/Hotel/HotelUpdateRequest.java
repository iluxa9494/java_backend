package com.example.hotel_booking.dto.Hotel;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelUpdateRequest {
    private String name;
    private String title;
    private String city;
    private String address;
    private BigDecimal distanceFromCenter;
}
