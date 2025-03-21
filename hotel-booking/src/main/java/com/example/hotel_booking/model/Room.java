package com.example.hotel_booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false, length = 10)
    @NotBlank
    private String number;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Double price;

    @Column(name = "max_guests", nullable = false)
    @NotNull
    @Positive
    private Integer maxGuests;

    @Getter
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
}
