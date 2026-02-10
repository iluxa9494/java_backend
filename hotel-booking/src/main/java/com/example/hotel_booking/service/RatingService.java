package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Rating.RatingDto;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.mapper.RatingMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Rating;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.HotelRepository;
import com.example.hotel_booking.repository.RatingRepository;
import com.example.hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RatingMapper ratingMapper;

    public List<RatingDto> getAllRatings() {
        log.info("Получение списка всех оценок");
        return ratingRepository.findAll().stream()
                .map(ratingMapper::toDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(Long id) {
        log.info("Получение оценки по ID: {}", id);
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Оценка с ID {} не найдена", id);
                    return new ResourceNotFoundException("Оценка не найдена");
                });
        return ratingMapper.toDto(rating);
    }

    @Transactional
    public RatingDto addRating(RatingDto ratingDto) {
        log.info("Добавление новой оценки: {} (userId={}, hotelId={})", ratingDto.getScore(), ratingDto.getUserId(), ratingDto.getHotelId());

        User user = userRepository.findById(ratingDto.getUserId())
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", ratingDto.getUserId());
                    return new ResourceNotFoundException("Пользователь не найден");
                });

        Hotel hotel = hotelRepository.findById(ratingDto.getHotelId())
                .orElseThrow(() -> {
                    log.warn("Отель с ID {} не найден", ratingDto.getHotelId());
                    return new ResourceNotFoundException("Отель не найден");
                });

        Optional<Rating> existingRating = ratingRepository.findByUserAndHotel(user, hotel);
        if (existingRating.isPresent()) {
            throw new RuntimeException("Пользователь уже оставил оценку для этого отеля");
        }

        Rating rating = ratingMapper.toEntity(ratingDto);
        rating.setUser(user);
        rating.setHotel(hotel);
        rating = ratingRepository.save(rating);

        updateHotelRating(hotel);

        log.info("Оценка успешно добавлена с ID {}", rating.getId());
        return ratingMapper.toDto(rating);
    }

    @Transactional
    public RatingDto updateRating(Long id, RatingDto ratingDto) {
        log.info("Обновление оценки с ID {}", id);

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Оценка с ID {} не найдена", id);
                    return new ResourceNotFoundException("Оценка не найдена");
                });

        if (ratingDto.getScore() < 1 || ratingDto.getScore() > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }

        rating.setScore(ratingDto.getScore());
        rating.setReview(ratingDto.getReview());
        rating = ratingRepository.save(rating);

        updateHotelRating(rating.getHotel());

        log.info("Оценка с ID {} успешно обновлена", rating.getId());
        return ratingMapper.toDto(rating);
    }

    @Transactional
    public void deleteRating(Long id) {
        log.info("Удаление оценки с ID {}", id);

        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Оценка с ID {} не найдена", id);
                    return new ResourceNotFoundException("Оценка не найдена");
                });

        ratingRepository.delete(rating);
        updateHotelRating(rating.getHotel());

        log.info("Оценка с ID {} успешно удалена", id);
    }

    @Transactional
    public void updateHotelRating(Hotel hotel) {
        log.debug("Пересчёт рейтинга для отеля ID {}", hotel.getId());

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
        log.info("Обновлён рейтинг отеля");
    }
}
