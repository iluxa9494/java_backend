package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RatingDto;
import com.example.hotel_booking.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<RatingDto>> getAllRatings() {
        log.info("GET /api/v1/ratings | Получение всех рейтингов");
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDto> getRatingById(@PathVariable Long id) {
        log.info("GET /api/v1/ratings/{} | Получение рейтинга", id);
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @PostMapping
    public ResponseEntity<RatingDto> addRating(@RequestBody RatingDto ratingDto) {
        log.info("POST /api/v1/ratings | Добавление рейтинга от пользователя {} для отеля {}", ratingDto.getUserId(), ratingDto.getHotelId());
        return ResponseEntity.ok(ratingService.addRating(ratingDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        log.info("PUT /api/v1/ratings/{} | Обновление рейтинга", id);
        return ResponseEntity.ok(ratingService.updateRating(id, ratingDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        log.info("DELETE /api/v1/ratings/{} | Удаление рейтинга", id);
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
