package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Hotel.HotelCreateRequest;
import com.example.hotel_booking.dto.Hotel.HotelDto;
import com.example.hotel_booking.dto.Hotel.HotelUpdateRequest;
import com.example.hotel_booking.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class HotelControllerTest {

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
        List<HotelDto> hotels = List.of(new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St",
                BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10));
        when(hotelService.getAllHotels()).thenReturn(hotels);

        List<HotelDto> result = hotelController.getAllHotels();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hotel 1", result.get(0).getName());
        verify(hotelService, times(1)).getAllHotels();
    }

    @Test
    void testGetHotelById() {
        HotelDto hotel = new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St",
                BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10);
        when(hotelService.getHotelById(1L)).thenReturn(hotel);

        HotelDto result = hotelController.getHotelById(1L);

        assertNotNull(result);
        assertEquals("Hotel 1", result.getName());
        verify(hotelService, times(1)).getHotelById(1L);
    }

    @Test
    void testCreateHotel() {
        HotelCreateRequest request = new HotelCreateRequest("Hotel 1", "Luxury Hotel", "New York", "Main St",
                BigDecimal.valueOf(2.5));
        HotelDto hotelDto = new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St",
                BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10);
        when(hotelService.createHotel(request)).thenReturn(hotelDto);

        HotelDto result = hotelController.createHotel(request);

        assertNotNull(result);
        assertEquals("Hotel 1", result.getName());
        verify(hotelService, times(1)).createHotel(request);
    }

    @Test
    void testUpdateHotel() {
        HotelUpdateRequest request = new HotelUpdateRequest("Hotel Updated", "Updated Luxury", "Los Angeles", "Broadway",
                BigDecimal.valueOf(3.0));
        HotelDto hotelDto = new HotelDto(1L, "Hotel Updated", "Updated Luxury", "Los Angeles", "Broadway",
                BigDecimal.valueOf(3.0), BigDecimal.valueOf(4.5), 10);
        when(hotelService.updateHotel(1L, request)).thenReturn(hotelDto);

        HotelDto result = hotelController.updateHotel(1L, request);

        assertNotNull(result);
        assertEquals("Hotel Updated", result.getName());
        verify(hotelService, times(1)).updateHotel(1L, request);
    }

    @Test
    void testDeleteHotel() {
        doNothing().when(hotelService).deleteHotel(1L);
        hotelController.deleteHotel(1L);
        verify(hotelService, times(1)).deleteHotel(1L);
    }

    @Test
    void testGetHotels() {
        Page<HotelDto> hotelsPage = new PageImpl<>(List.of(
                new HotelDto(1L, "Hotel 1", "Luxury Hotel", "New York", "Main St",
                        BigDecimal.valueOf(2.5), BigDecimal.valueOf(4.5), 10)
        ));
        when(hotelService.getHotels("New York", 0, 10)).thenReturn(hotelsPage);

        Page<HotelDto> result = hotelController.getHotels("New York", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(hotelService, times(1)).getHotels("New York", 0, 10);
    }
}