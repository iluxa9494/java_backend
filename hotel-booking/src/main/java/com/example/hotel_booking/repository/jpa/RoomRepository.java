package com.example.hotel_booking.repository.jpa;

import com.example.hotel_booking.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    Optional<Room> findByHotelIdAndRoomNumber(Long hotelId, String roomNumber);

    Page<Room> findByPriceBetweenAndMaxGuestsBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Integer minGuests, Integer maxGuests, Pageable pageable);
}
