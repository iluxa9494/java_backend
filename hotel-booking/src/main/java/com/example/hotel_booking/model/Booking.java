package com.example.hotel_booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    private static final Logger log = LoggerFactory.getLogger(Booking.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @NotNull
    private Room room;

    @Column(name = "check_in", nullable = false)
    @NotNull
    @FutureOrPresent(message = "Дата заезда должна быть текущей или будущей")
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    @NotNull
    @Future(message = "Дата выезда должна быть в будущем")
    private LocalDate checkOut;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void validateDates() {
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            log.error("Ошибка бронирования: checkOut ({}) <= checkIn ({})", checkOut, checkIn);
            throw new IllegalArgumentException("Дата выезда должна быть позже даты заезда.");
        }
    }
}
