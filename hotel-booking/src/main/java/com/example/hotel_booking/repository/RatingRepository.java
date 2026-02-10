package com.example.hotel_booking.repository;

import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Rating;
import com.example.hotel_booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByHotel(Hotel hotel);

    Optional<Rating> findByUserAndHotel(User user, Hotel hotel);

    void deleteByUserAndHotel(User user, Hotel hotel);
}
