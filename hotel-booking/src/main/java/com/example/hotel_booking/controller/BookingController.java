package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Booking.BookingCreateRequest;
import com.example.hotel_booking.dto.Booking.BookingDto;
import com.example.hotel_booking.dto.Booking.BookingUpdateRequest;
import com.example.hotel_booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@Valid @RequestBody BookingCreateRequest request) {
        log.info("POST /api/v1/bookings | Создание бронирования: userId={}, roomId={}", request.getUserId(), request.getRoomId());
        return bookingService.createBooking(request);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public BookingDto updateBooking(@PathVariable Long id, @Valid @RequestBody BookingUpdateRequest request) {
        log.info("PUT /api/v1/bookings/{} | Обновление бронирования", id);
        return bookingService.updateBooking(id, request);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        log.info("DELETE /api/v1/bookings/{} | Удаление бронирования", id);
        bookingService.deleteBooking(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        log.info("GET /api/v1/bookings/{} | Получение бронирования по id", id);
        return bookingService.getBookingById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public Page<BookingDto> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/bookings | Получение списка бронирований");
        return bookingService.getAllBookings(PageRequest.of(page, size));
    }
}