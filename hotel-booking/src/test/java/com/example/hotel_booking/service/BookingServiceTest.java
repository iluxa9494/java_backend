package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.BookingCreateRequest;
import com.example.hotel_booking.dto.BookingDto;
import com.example.hotel_booking.exception.ResourceNotFoundException;
import com.example.hotel_booking.mapper.BookingMapper;
import com.example.hotel_booking.model.Booking;
import com.example.hotel_booking.model.Room;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.BookingRepository;
import com.example.hotel_booking.repository.RoomRepository;
import com.example.hotel_booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRoomAvailabilityWhenRoomIsAvailable() {
        Long roomId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 4, 10);
        LocalDate checkOut = LocalDate.of(2025, 4, 15);

        when(bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, checkIn, checkOut))
                .thenReturn(List.of());

        BookingCreateRequest request = new BookingCreateRequest();
        request.setUserId(1L);
        request.setRoomId(roomId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);

        BookingDto bookingDto = bookingService.createBooking(request);
        assertNotNull(bookingDto, "Бронирование должно быть создано");
    }

    @Test
    void testRoomAvailabilityWhenRoomIsNotAvailable() {
        Long roomId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 4, 10);
        LocalDate checkOut = LocalDate.of(2025, 4, 15);
        Booking existingBooking = new Booking();
        existingBooking.setRoom(new Room());
        existingBooking.setCheckIn(LocalDate.of(2025, 4, 12));
        existingBooking.setCheckOut(LocalDate.of(2025, 4, 14));

        when(bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, checkIn, checkOut))
                .thenReturn(List.of(existingBooking));

        BookingCreateRequest request = new BookingCreateRequest();
        request.setUserId(1L);
        request.setRoomId(roomId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void testSuccessfulBookingCreation() {
        Long userId = 1L;
        Long roomId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 4, 10);
        LocalDate checkOut = LocalDate.of(2025, 4, 15);

        User user = new User();
        user.setId(userId);
        Room room = new Room();
        room.setId(roomId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, checkIn, checkOut))
                .thenReturn(List.of());

        BookingCreateRequest request = new BookingCreateRequest();
        request.setUserId(userId);
        request.setRoomId(roomId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);

        BookingDto bookingDto = bookingService.createBooking(request);

        assertNotNull(bookingDto);
        assertEquals(userId, bookingDto.getUserId());
        assertEquals(roomId, bookingDto.getRoomId());
        assertEquals(checkIn, bookingDto.getCheckIn());
        assertEquals(checkOut, bookingDto.getCheckOut());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testBookingFailsIfRoomIsNotAvailable() {
        Long userId = 1L;
        Long roomId = 1L;
        LocalDate checkIn = LocalDate.of(2025, 4, 10);
        LocalDate checkOut = LocalDate.of(2025, 4, 15);

        Booking existingBooking = new Booking();
        existingBooking.setRoom(new Room());
        existingBooking.setCheckIn(LocalDate.of(2025, 4, 12));
        existingBooking.setCheckOut(LocalDate.of(2025, 4, 14));

        when(bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(roomId, checkIn, checkOut))
                .thenReturn(List.of(existingBooking));

        BookingCreateRequest request = new BookingCreateRequest();
        request.setUserId(userId);
        request.setRoomId(roomId);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
