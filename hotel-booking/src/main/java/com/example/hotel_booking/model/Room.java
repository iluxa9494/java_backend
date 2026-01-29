package com.example.hotel_booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "room_number"})
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

    @Column(name = "name", nullable = false, length = 255)
    @NotBlank
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "room_number", nullable = false, length = 50)
    @NotBlank
    private String roomNumber;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    @NotNull
    @Positive
    private BigDecimal price;

    @Column(name = "max_guests", nullable = false)
    @NotNull
    @Positive
    private Integer maxGuests;

    @Getter
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
}
