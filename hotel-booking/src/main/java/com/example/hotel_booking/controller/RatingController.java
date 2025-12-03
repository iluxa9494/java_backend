package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Rating.RatingDto;
import com.example.hotel_booking.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<RatingDto> getAllRatings() {
        log.info("GET /api/v1/ratings | Получение всех рейтингов");
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")
    public RatingDto getRatingById(@PathVariable Long id) {
        log.info("GET /api/v1/ratings/{} | Получение рейтинга", id);
        return ratingService.getRatingById(id);
    }

    @PostMapping
    public RatingDto addRating(@RequestBody RatingDto ratingDto) {
        log.info("POST /api/v1/ratings | Добавление рейтинга от пользователя {} для отеля {}", ratingDto.getUserId(), ratingDto.getHotelId());
        return ratingService.addRating(ratingDto);
    }

    @PutMapping("/{id}")
    public RatingDto updateRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        log.info("PUT /api/v1/ratings/{} | Обновление рейтинга", id);
        return ratingService.updateRating(id, ratingDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteRating(@PathVariable Long id) {
        log.info("DELETE /api/v1/ratings/{} | Удаление рейтинга", id);
        ratingService.deleteRating(id);
    }
}
