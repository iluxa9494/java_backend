package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Booking.BookingCreateRequest;
import com.example.hotel_booking.dto.Booking.BookingDto;
import com.example.hotel_booking.dto.Booking.BookingUpdateRequest;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.exception.RoomNotAvailableException;
import com.example.hotel_booking.mapper.BookingMapper;
import com.example.hotel_booking.model.Booking;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.BookingRepository;
import com.example.hotel_booking.repository.RoomRepository;
import com.example.hotel_booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(BookingCreateRequest request) {
        log.info("Создание нового бронирования: {}", request);
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            log.warn("Пользователь с ID {} не найден", request.getUserId());
            return new ResourceNotFoundException("Пользователь не найден");
        });
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> {
            log.warn("Комната с ID {} не найдена", request.getRoomId());
            return new ResourceNotFoundException("Комната не найдена");
        });
        Booking booking = bookingMapper.toEntity(request);
        booking.setUser(user);
        booking.setRoom(room);
        booking = bookingRepository.save(booking);
        log.info("Бронирование успешно создано с ID {}", booking.getId());
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto updateBooking(Long id, BookingUpdateRequest request) {
        log.info("Обновление бронирования ID: {}", id);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            log.warn("Бронирование с ID {} не найдено", id);
            return new ResourceNotFoundException("Бронирование не найдено");
        });
        if (!isRoomAvailable(booking.getRoom().getId(), request.getCheckIn(), request.getCheckOut())) {
            throw new RoomNotAvailableException("Комната уже забронирована на эти даты.");
        }
        bookingMapper.updateEntity(request, booking);
        booking.validateDates();
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Бронирование с ID {} обновлено", id);
        return bookingMapper.toDto(updatedBooking);
    }

    public void deleteBooking(Long id) {
        log.info("Удаление бронирования с ID {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));
        bookingRepository.delete(booking);
        log.info("Бронирование с ID {} удалено", id);
    }

    public BookingDto getBookingById(Long id) {
        log.info("Поиск бронирования по ID: {}", id);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            log.warn("Бронирование с ID {} не найдено", id);
            return new ResourceNotFoundException("Бронирование не найдено");
        });
        return bookingMapper.toDto(booking);
    }

    public Page<BookingDto> getAllBookings(Pageable pageable) {
        log.info("Получение всех бронирований (страница: {})", pageable.getPageNumber());
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    private boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, checkIn, checkOut).isEmpty();
    }
}
