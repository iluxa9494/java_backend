package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.HotelCreateRequest;
import com.example.hotel_booking.dto.HotelDto;
import com.example.hotel_booking.dto.HotelUpdateRequest;
import com.example.hotel_booking.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        log.info("GET /api/v1/hotels | Получение списка всех отелей");
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        log.info("GET /api/v1/hotels/{} | Получение отеля по id", id);
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@Valid @RequestBody HotelCreateRequest request) {
        log.info("POST /api/v1/hotels | Создание отеля: {}", request.getName());
        return ResponseEntity.ok(hotelService.createHotel(request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id, @Valid @RequestBody HotelUpdateRequest request) {
        log.info("PUT /api/v1/hotels/{} | Обновление отеля", id);
        return ResponseEntity.ok(hotelService.updateHotel(id, request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        log.info("DELETE /api/v1/hotels/{} | Удаление отеля", id);
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<HotelDto>> getHotels(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(hotelService.getHotels(city, page, size));
    }
}
