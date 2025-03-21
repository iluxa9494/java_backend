package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.BookingCreateRequest;
import com.example.hotel_booking.dto.BookingDto;
import com.example.hotel_booking.dto.BookingUpdateRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(BookingCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Комната не найдена"));

        Booking booking = bookingMapper.toEntity(request);
        booking.setUser(user);
        booking.setRoom(room);

        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto updateBooking(Long id, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));

        if (!isRoomAvailable(booking.getRoom().getId(), request.getCheckIn(), request.getCheckOut())) {
            throw new RoomNotAvailableException("Комната уже забронирована на эти даты.");
        }

        bookingMapper.updateEntity(request, booking);
        booking.validateDates();

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Бронирование не найдено");
        }
        bookingRepository.deleteById(id);
    }

    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено"));
        return bookingMapper.toDto(booking);
    }

    public Page<BookingDto> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    private boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, checkIn, checkOut).isEmpty();
    }
}
