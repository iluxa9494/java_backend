package com.example.hotel_booking.dto.Statistics;

import com.example.hotel_booking.model.BookingDetails;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDto {
    private String id;
    private String eventType;
    private String userId;
    private BookingDetails details;
    private LocalDateTime createdAt;
}
