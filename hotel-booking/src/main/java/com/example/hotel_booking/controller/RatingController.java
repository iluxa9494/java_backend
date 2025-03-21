package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RatingDto;
import com.example.hotel_booking.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<RatingDto>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDto> getRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @PostMapping
    public ResponseEntity<RatingDto> addRating(@RequestBody RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.addRating(ratingDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @RequestBody RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.updateRating(id, ratingDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
