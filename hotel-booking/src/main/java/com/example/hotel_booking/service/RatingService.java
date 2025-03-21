package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.RatingDto;
import com.example.hotel_booking.mapper.RatingMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Rating;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.HotelRepository;
import com.example.hotel_booking.repository.RatingRepository;
import com.example.hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RatingMapper ratingMapper;

    public List<RatingDto> getAllRatings() {
        return ratingRepository.findAll().stream()
                .map(ratingMapper::toDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рейтинг не найден"));
        return ratingMapper.toDto(rating);
    }

    @Transactional
    public RatingDto addRating(RatingDto ratingDto) {
        User user = userRepository.findById(ratingDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Hotel hotel = hotelRepository.findById(ratingDto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Отель не найден"));

        Optional<Rating> existingRating = ratingRepository.findByUserAndHotel(user, hotel);
        if (existingRating.isPresent()) {
            throw new RuntimeException("Пользователь уже оставил оценку для этого отеля");
        }

        Rating rating = ratingMapper.toEntity(ratingDto);
        rating.setUser(user);
        rating.setHotel(hotel);

        rating = ratingRepository.save(rating);
        updateHotelRating(hotel);

        return ratingMapper.toDto(rating);
    }

    @Transactional
    public RatingDto updateRating(Long id, RatingDto ratingDto) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рейтинг не найден"));

        if (ratingDto.getScore() < 1 || ratingDto.getScore() > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }

        rating.setScore(ratingDto.getScore());
        rating.setReview(ratingDto.getReview());
        rating = ratingRepository.save(rating);

        updateHotelRating(rating.getHotel());

        return ratingMapper.toDto(rating);
    }

    @Transactional
    public void deleteRating(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рейтинг не найден"));

        ratingRepository.delete(rating);
        updateHotelRating(rating.getHotel());
    }

    @Transactional
    public void updateHotelRating(Hotel hotel) {
        List<Rating> ratings = ratingRepository.findByHotel(hotel);
        if (ratings.isEmpty()) {
            hotel.setRating(BigDecimal.ZERO);
            hotel.setRatingsCount(0);
        } else {
            BigDecimal totalRating = ratings.stream()
                    .map(r -> BigDecimal.valueOf(r.getScore()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int count = ratings.size();
            hotel.setRatingsCount(count);
            hotel.setRating(totalRating.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP));
        }
        hotelRepository.save(hotel);
    }
}
