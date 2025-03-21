package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.BookingCreateRequest;
import com.example.hotel_booking.dto.BookingDto;
import com.example.hotel_booking.dto.BookingUpdateRequest;
import com.example.hotel_booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BookingControllerTest {

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

        BookingDto responseDto = new BookingDto(1L, 1L, 1L, request.getCheckIn(), request.getCheckOut());

        when(bookingService.createBooking(request)).thenReturn(responseDto);

        ResponseEntity<BookingDto> response = bookingController.createBooking(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookingService, times(1)).createBooking(request);
    }


    @Test
    void testUpdateBooking() {
        BookingUpdateRequest request = new BookingUpdateRequest();
        request.setCheckIn(LocalDate.now());
        request.setCheckOut(LocalDate.now().plusDays(3));

        BookingDto responseDto = new BookingDto(1L, 1L, 1L, request.getCheckIn(), request.getCheckOut());

        when(bookingService.updateBooking(1L, request)).thenReturn(responseDto);

        ResponseEntity<BookingDto> response = bookingController.updateBooking(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
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
        BookingDto responseDto = new BookingDto(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(3));

        when(bookingService.getBookingById(1L)).thenReturn(responseDto);

        ResponseEntity<BookingDto> response = bookingController.getBookingById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(bookingService, times(1)).getBookingById(1L);
    }

    @Test
    void testGetAllBookings() {
        Page<BookingDto> bookingsPage = new PageImpl<>(List.of(new BookingDto(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(3))));

        when(bookingService.getAllBookings(PageRequest.of(0, 10))).thenReturn(bookingsPage);

        ResponseEntity<Page<BookingDto>> response = bookingController.getAllBookings(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(bookingService, times(1)).getAllBookings(PageRequest.of(0, 10));
    }
}
