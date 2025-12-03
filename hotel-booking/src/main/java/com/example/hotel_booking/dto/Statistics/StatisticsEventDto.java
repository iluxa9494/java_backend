package com.example.hotel_booking.dto.Statistics;

import com.example.hotel_booking.model.BookingDetails;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsEventDto {
    private String eventType;
    private String userId;
    private LocalDateTime timestamp;
    private BookingDetails details;
}
