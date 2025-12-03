package com.example.hotel_booking.dto.Room;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    private Long id;
    private Long hotelId;
    private String title;
    private String description;
    private String number;
    private BigDecimal price;
    private Integer maxGuests;
}
