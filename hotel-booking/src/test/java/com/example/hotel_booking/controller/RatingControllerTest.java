package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Rating.RatingDto;
import com.example.hotel_booking.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRatings() {
        List<RatingDto> ratings = List.of(
                new RatingDto(1L, 1L, 1L, 5, "Отличный отель", LocalDateTime.now())
        );
        when(ratingService.getAllRatings()).thenReturn(ratings);
        List<RatingDto> result = ratingController.getAllRatings();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getScore());
        verify(ratingService, times(1)).getAllRatings();
    }

    @Test
    void testGetRatingById() {
        RatingDto ratingDto = new RatingDto(1L, 1L, 1L, 4, "Хороший сервис", LocalDateTime.now());
        when(ratingService.getRatingById(1L)).thenReturn(ratingDto);
        RatingDto result = ratingController.getRatingById(1L);
        assertNotNull(result);
        assertEquals(4, result.getScore());
        verify(ratingService, times(1)).getRatingById(1L);
    }

    @Test
    void testAddRating() {
        RatingDto requestDto = new RatingDto(null, 1L, 1L, 5, "Супер!", LocalDateTime.now());
        RatingDto responseDto = new RatingDto(1L, 1L, 1L, 5, "Супер!", LocalDateTime.now());
        when(ratingService.addRating(requestDto)).thenReturn(responseDto);
        RatingDto result = ratingController.addRating(requestDto);
        assertNotNull(result);
        assertEquals(5, result.getScore());
        verify(ratingService, times(1)).addRating(requestDto);
    }

    @Test
    void testUpdateRating() {
        RatingDto requestDto = new RatingDto(1L, 1L, 1L, 3, "Средний", LocalDateTime.now());
        RatingDto responseDto = new RatingDto(1L, 1L, 1L, 3, "Средний", LocalDateTime.now());
        when(ratingService.updateRating(1L, requestDto)).thenReturn(responseDto);
        RatingDto result = ratingController.updateRating(1L, requestDto);
        assertNotNull(result);
        assertEquals(3, result.getScore());
        verify(ratingService, times(1)).updateRating(1L, requestDto);
    }

    @Test
    void testDeleteRating() {
        doNothing().when(ratingService).deleteRating(1L);
        ratingController.deleteRating(1L);
        verify(ratingService, times(1)).deleteRating(1L);
    }
}
