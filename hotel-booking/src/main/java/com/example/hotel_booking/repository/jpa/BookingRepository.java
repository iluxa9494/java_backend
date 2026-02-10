package com.example.hotel_booking.repository.jpa;

import com.example.hotel_booking.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomIdAndCheckOutAfterAndCheckInBefore(Long roomId, LocalDate checkIn, LocalDate checkOut);

    Page<Booking> findAll(Pageable pageable);
}
