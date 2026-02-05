package com.example.hotel_booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String name;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String title;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String city;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String address;

    @Column(nullable = false, precision = 6, scale = 2)
    @NotNull
    @Positive
    private BigDecimal distanceFromCenter;

    @Column(nullable = false, precision = 2, scale = 1)
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer ratingsCount = 0;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    public void updateRating(BigDecimal newRating) {
        this.ratingsCount++;
        BigDecimal totalRating = this.rating
                .multiply(BigDecimal.valueOf(this.ratingsCount - 1))
                .add(newRating);
        this.rating = totalRating.divide(BigDecimal.valueOf(this.ratingsCount), 1, RoundingMode.HALF_UP);
    }
}
