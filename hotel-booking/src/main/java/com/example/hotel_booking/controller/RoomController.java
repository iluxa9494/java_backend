package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RoomCreateRequest;
import com.example.hotel_booking.dto.RoomDto;
import com.example.hotel_booking.dto.RoomUpdateRequest;
import com.example.hotel_booking.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        log.info("GET /api/v1/rooms | Получение списка всех комнат");
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        log.info("GET /api/v1/rooms/{} | Получение комнаты по id", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        log.info("POST /api/v1/rooms | Создание комнаты для отеля с id={}", request.getHotelId());
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomUpdateRequest request) {
        log.info("PUT /api/v1/rooms/{} | Обновление данных комнаты", id);
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.info("DELETE /api/v1/rooms/{} | Удаление комнаты", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RoomDto>> searchRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minGuests,
            @RequestParam(required = false) Integer maxGuests,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(roomService.getFilteredRooms(hotelId, minPrice, maxPrice, minGuests, maxGuests, checkIn, checkOut, page, size));
    }

    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return ResponseEntity.ok(roomService.isRoomAvailable(roomId, checkIn, checkOut));
    }
}
