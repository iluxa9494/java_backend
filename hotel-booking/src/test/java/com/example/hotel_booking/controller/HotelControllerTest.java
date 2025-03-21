package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.HotelCreateRequest;
import com.example.hotel_booking.dto.HotelDto;
import com.example.hotel_booking.dto.HotelUpdateRequest;
import com.example.hotel_booking.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class HotelControllerTest {

    @Mock
    private HotelService hotelService;

    @InjectMocks
    private HotelController hotelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllHotels() {
        List<HotelDto> hotels = List.of(new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St", BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10));
        when(hotelService.getAllHotels()).thenReturn(hotels);
        ResponseEntity<List<HotelDto>> response = hotelController.getAllHotels();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Hotel 1", response.getBody().get(0).getName());
        verify(hotelService, times(1)).getAllHotels();
    }

    @Test
    void testGetHotelById() {
        HotelDto hotel = new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St", BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10);
        when(hotelService.getHotelById(1L)).thenReturn(hotel);
        ResponseEntity<HotelDto> response = hotelController.getHotelById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hotel 1", response.getBody().getName());
        verify(hotelService, times(1)).getHotelById(1L);
    }

    @Test
    void testCreateHotel() {
        HotelCreateRequest request = new HotelCreateRequest("Hotel 1", "Luxury Hotel", "New York", "Main St", BigDecimal.valueOf(2.5));
        HotelDto hotelDto = new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St", BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10);
        when(hotelService.createHotel(request)).thenReturn(hotelDto);
        ResponseEntity<HotelDto> response = hotelController.createHotel(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hotel 1", response.getBody().getName());
        verify(hotelService, times(1)).createHotel(request);
    }

    @Test
    void testUpdateHotel() {
        HotelUpdateRequest request = new HotelUpdateRequest("Hotel Updated", "Updated Luxury", "Los Angeles", "Broadway", BigDecimal.valueOf(3.0));
        HotelDto hotelDto = new HotelDto(1L, "Hotel Updated", "Updated Luxury", "Los Angeles", "Broadway", BigDecimal.valueOf(3.0), BigDecimal.valueOf(4.5), 10);
        when(hotelService.updateHotel(1L, request)).thenReturn(hotelDto);
        ResponseEntity<HotelDto> response = hotelController.updateHotel(1L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hotel Updated", response.getBody().getName());
        verify(hotelService, times(1)).updateHotel(1L, request);
    }

    @Test
    void testDeleteHotel() {
        doNothing().when(hotelService).deleteHotel(1L);
        ResponseEntity<Void> response = hotelController.deleteHotel(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(hotelService, times(1)).deleteHotel(1L);
    }

    @Test
    void testGetHotels() {
        Page<HotelDto> hotelsPage = new PageImpl<>(List.of(new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St", BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10)));
        when(hotelService.getHotels("New York", 0, 10)).thenReturn(hotelsPage);
        ResponseEntity<Page<HotelDto>> response = hotelController.getHotels("New York", 0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(hotelService, times(1)).getHotels("New York", 0, 10);
    }
}