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
    Optional<Room> findByHotelIdAndNumber(Long hotelId, String number);

    Page<Room> findByPriceBetweenAndMaxGuestsBetween(Double minPrice, Double maxPrice, Integer minGuests, Integer maxGuests, Pageable pageable);
}
