package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Hotel.HotelCreateRequest;
import com.example.hotel_booking.dto.Hotel.HotelDto;
import com.example.hotel_booking.dto.Hotel.HotelUpdateRequest;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.repository.jpa.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllHotels() {
        List<Hotel> hotels = List.of(new Hotel(1L, "Hotel1", "Title1", "City1", "Address1", BigDecimal.valueOf(1.5), BigDecimal.valueOf(4.0), 10, null, null));
        when(hotelRepository.findAll()).thenReturn(hotels);
        List<HotelDto> result = hotelService.getAllHotels();
        assertEquals(1, result.size());
        assertEquals("Hotel1", result.get(0).getName());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    void testGetHotelById_WhenHotelExists() {
        Hotel hotel = new Hotel(1L, "Hotel1", "Title1", "City1", "Address1", BigDecimal.valueOf(1.5), BigDecimal.valueOf(4.0), 10, null, null);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        HotelDto result = hotelService.getHotelById(1L);
        assertNotNull(result);
        assertEquals("Hotel1", result.getName());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testGetHotelById_WhenHotelDoesNotExist() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> hotelService.getHotelById(1L));
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateHotel() {
        HotelCreateRequest request = new HotelCreateRequest("Hotel1", "Title1", "City1", "Address1", BigDecimal.valueOf(1.5));
        Hotel hotel = new Hotel(1L, request.getName(), request.getTitle(), request.getCity(), request.getAddress(), request.getDistanceFromCenter(), BigDecimal.ZERO, 0, null, null);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        HotelDto result = hotelService.createHotel(request);
        assertNotNull(result);
        assertEquals("Hotel1", result.getName());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testUpdateHotel_WhenHotelExists() {
        Hotel hotel = new Hotel(1L, "Hotel1", "Title1", "City1", "Address1", BigDecimal.valueOf(1.5), BigDecimal.valueOf(4.0), 10, null, null);
        HotelUpdateRequest request = new HotelUpdateRequest("Updated Hotel", "Updated Title", "Updated City", "Updated Address", BigDecimal.valueOf(2.0));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        HotelDto result = hotelService.updateHotel(1L, request);
        assertNotNull(result);
        assertEquals("Updated Hotel", result.getName());
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    void testUpdateHotel_WhenHotelDoesNotExist() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());
        HotelUpdateRequest request = new HotelUpdateRequest("Updated Hotel", "Updated Title", "Updated City", "Updated Address", BigDecimal.valueOf(2.0));
        assertThrows(EntityNotFoundException.class, () -> hotelService.updateHotel(1L, request));
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteHotel_WhenHotelExists() {
        when(hotelRepository.existsById(1L)).thenReturn(true);
        hotelService.deleteHotel(1L);
        verify(hotelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteHotel_WhenHotelDoesNotExist() {
        when(hotelRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> hotelService.deleteHotel(1L));
        verify(hotelRepository, times(1)).existsById(1L);
    }

    @Test
    void testGetHotels_WithPagination() {
        Page<Hotel> hotels = new PageImpl<>(List.of(new Hotel(1L, "Hotel1", "Title1", "City1", "Address1", BigDecimal.valueOf(1.5), BigDecimal.valueOf(4.0), 10, null, null)));
        when(hotelRepository.findAll(any(Pageable.class))).thenReturn(hotels);
        Page<HotelDto> result = hotelService.getHotels(null, 0, 10);
        assertEquals(1, result.getTotalElements());
        assertEquals("Hotel1", result.getContent().get(0).getName());
        verify(hotelRepository, times(1)).findAll(any(Pageable.class));
    }
}
