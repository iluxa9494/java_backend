package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Booking.BookingCreateRequest;
import com.example.hotel_booking.dto.Booking.BookingDto;
import com.example.hotel_booking.dto.Booking.BookingUpdateRequest;
import com.example.hotel_booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setUserId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now());
        request.setCheckOut(LocalDate.now().plusDays(3));

        BookingDto expectedDto = new BookingDto(1L, 1L, 1L, request.getCheckIn(), request.getCheckOut());
        when(bookingService.createBooking(request)).thenReturn(expectedDto);

        BookingDto result = bookingController.createBooking(request);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        verify(bookingService, times(1)).createBooking(request);
    }

    @Test
    void testUpdateBooking() {
        BookingUpdateRequest request = new BookingUpdateRequest();
        request.setCheckIn(LocalDate.now());
        request.setCheckOut(LocalDate.now().plusDays(3));

        BookingDto expectedDto = new BookingDto(1L, 1L, 1L, request.getCheckIn(), request.getCheckOut());
        when(bookingService.updateBooking(1L, request)).thenReturn(expectedDto);

        BookingDto result = bookingController.updateBooking(1L, request);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        verify(bookingService, times(1)).updateBooking(1L, request);
    }

    @Test
    void testDeleteBooking() {
        doNothing().when(bookingService).deleteBooking(1L);
        bookingController.deleteBooking(1L);
        verify(bookingService, times(1)).deleteBooking(1L);
    }

    @Test
    void testGetBookingById() {
        BookingDto expectedDto = new BookingDto(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(3));
        when(bookingService.getBookingById(1L)).thenReturn(expectedDto);

        BookingDto result = bookingController.getBookingById(1L);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        verify(bookingService, times(1)).getBookingById(1L);
    }

    @Test
    void testGetAllBookings() {
        Page<BookingDto> expectedPage = new PageImpl<>(
                List.of(new BookingDto(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(3)))
        );
        when(bookingService.getAllBookings(PageRequest.of(0, 10))).thenReturn(expectedPage);

        Page<BookingDto> result = bookingController.getAllBookings(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookingService, times(1)).getAllBookings(PageRequest.of(0, 10));
    }
}
