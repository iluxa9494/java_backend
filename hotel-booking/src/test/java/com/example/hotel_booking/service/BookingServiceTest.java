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
import com.example.hotel_booking.repository.jpa.BookingRepository;
import com.example.hotel_booking.repository.jpa.RoomRepository;
import com.example.hotel_booking.repository.jpa.UserRepository;
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

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Room room;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingCreateRequest createRequest;
    private BookingUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        room = new Room();
        room.setId(1L);
        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(LocalDate.of(2025, 3, 24));
        booking.setCheckOut(LocalDate.of(2025, 3, 26));
        bookingDto = BookingDto.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .checkIn(LocalDate.of(2025, 3, 24))
                .checkOut(LocalDate.of(2025, 3, 26))
                .build();
        createRequest = new BookingCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setRoomId(1L);
        createRequest.setCheckIn(LocalDate.of(2025, 3, 24));
        createRequest.setCheckOut(LocalDate.of(2025, 3, 26));
        updateRequest = new BookingUpdateRequest();
        updateRequest.setCheckIn(LocalDate.of(2025, 3, 25));
        updateRequest.setCheckOut(LocalDate.of(2025, 3, 27));
    }

    @Test
    void testCreateBooking_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(
                1L,
                createRequest.getCheckIn(),
                createRequest.getCheckOut()
        )).thenReturn(List.of());
        when(bookingMapper.toEntity(createRequest)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        BookingDto result = bookingService.createBooking(createRequest);
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getRoomId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_RoomNotAvailable() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndCheckOutAfterAndCheckInBefore(
                1L,
                createRequest.getCheckIn(),
                createRequest.getCheckOut()
        )).thenReturn(List.of(booking));

        assertThrows(RoomNotAvailableException.class, () -> bookingService.createBooking(createRequest));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(createRequest));
    }

    @Test
    void testGetBookingById_Found() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        BookingDto result = bookingService.getBookingById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1L));
    }

    @Test
    void testUpdateBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.findByRoomIdAndIdNotAndCheckOutAfterAndCheckInBefore(
                1L,
                1L,
                updateRequest.getCheckIn(),
                updateRequest.getCheckOut()
        )).thenReturn(List.of());
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        BookingDto result = bookingService.updateBooking(1L, updateRequest);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateBooking_NotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.updateBooking(1L, updateRequest));
    }

    @Test
    void testDeleteBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        bookingService.deleteBooking(1L);
        verify(bookingRepository).delete(booking);
    }

    @Test
    void testDeleteBooking_NotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.deleteBooking(1L));
    }
}
