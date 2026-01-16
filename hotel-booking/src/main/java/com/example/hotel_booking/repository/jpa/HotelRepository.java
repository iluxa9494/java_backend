package com.example.hotel_booking.repository.jpa;

import com.example.hotel_booking.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByCity(String city);

    Page<Hotel> findAll(Pageable pageable);

    Page<Hotel> findByCityContainingIgnoreCase(String city, Pageable pageable);

}
