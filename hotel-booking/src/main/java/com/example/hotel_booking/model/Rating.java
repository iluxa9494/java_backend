package com.example.hotel_booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "hotel_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @NotNull
    private Hotel hotel;

    @Column(nullable = false)
    @Min(value = 1, message = "Рейтинг должен быть не меньше 1")
    @Max(value = 5, message = "Рейтинг должен быть не больше 5")
    private int score;

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void validateRating() {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Рейтинг должен быть от 1 до 5.");
        }
    }
}