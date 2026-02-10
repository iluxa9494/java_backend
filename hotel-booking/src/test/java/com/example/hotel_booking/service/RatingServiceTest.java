package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Rating.RatingDto;
import com.example.hotel_booking.mapper.RatingMapper;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Rating;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.jpa.HotelRepository;
import com.example.hotel_booking.repository.jpa.RatingRepository;
import com.example.hotel_booking.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RatingService ratingService;

    private Rating rating;
    private RatingDto ratingDto;
    private User user;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        hotel = new Hotel();
        hotel.setId(1L);
        rating = new Rating();
        rating.setId(1L);
        rating.setScore(5);
        rating.setUser(user);
        rating.setHotel(hotel);
        ratingDto = new RatingDto();
        ratingDto.setId(1L);
        ratingDto.setScore(5);
        ratingDto.setUserId(1L);
        ratingDto.setHotelId(1L);
    }

    @Test
    void testGetRatingById_WhenRatingExists() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingMapper.toDto(rating)).thenReturn(ratingDto);
        RatingDto actualDto = ratingService.getRatingById(1L);
        assertNotNull(actualDto);
        assertEquals(5, actualDto.getScore());
        verify(ratingRepository).findById(1L);
    }

    @Test
    void testGetRatingById_WhenRatingNotFound() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ratingService.getRatingById(99L));
    }

    @Test
    void testGetAllRatings() {
        when(ratingRepository.findAll()).thenReturn(List.of(rating));
        when(ratingMapper.toDto(rating)).thenReturn(ratingDto);
        List<RatingDto> ratings = ratingService.getAllRatings();
        assertEquals(1, ratings.size());
        assertEquals(5, ratings.get(0).getScore());
    }

    @Test
    void testAddRating() {
        when(ratingMapper.toEntity(ratingDto)).thenReturn(rating);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toDto(rating)).thenReturn(ratingDto);
        RatingDto result = ratingService.addRating(ratingDto);
        assertNotNull(result);
        assertEquals(5, result.getScore());
    }

    @Test
    void testUpdateRating_WhenRatingExists() {
        RatingDto updatedDto = new RatingDto();
        updatedDto.setId(1L);
        updatedDto.setScore(4);
        updatedDto.setUserId(1L);
        updatedDto.setHotelId(1L);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toDto(rating)).thenReturn(updatedDto);
        RatingDto result = ratingService.updateRating(1L, updatedDto);
        assertNotNull(result);
        assertEquals(4, result.getScore());
    }

    @Test
    void testUpdateRating_WhenRatingNotFound() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ratingService.updateRating(99L, ratingDto));
    }

    @Test
    void testDeleteRating_WhenExists() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        ratingService.deleteRating(1L);
        verify(ratingRepository).delete(rating);
    }

    @Test
    void testDeleteRating_WhenNotFound() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ratingService.deleteRating(99L));
    }
}
