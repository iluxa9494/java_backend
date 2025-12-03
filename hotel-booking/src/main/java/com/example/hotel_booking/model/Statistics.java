package com.example.hotel_booking.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {

    @Id
    private String id;

    @NotBlank
    private String eventType;

    @NotBlank
    private String userId;

    private BookingDetails details;

    private LocalDateTime createdAt = LocalDateTime.now();
}
