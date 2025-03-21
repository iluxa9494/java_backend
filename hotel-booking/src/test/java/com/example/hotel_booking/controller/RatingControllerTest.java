package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.RatingDto;
import com.example.hotel_booking.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class RatingControllerTest {

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
        ResponseEntity<List<RatingDto>> response = ratingController.getAllRatings();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(5, response.getBody().get(0).getScore());
        verify(ratingService, times(1)).getAllRatings();
    }

    @Test
    void testGetRatingById() {
        RatingDto ratingDto = new RatingDto(1L, 1L, 1L, 4, "Хороший сервис", LocalDateTime.now());
        when(ratingService.getRatingById(1L)).thenReturn(ratingDto);
        ResponseEntity<RatingDto> response = ratingController.getRatingById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4, response.getBody().getScore());
        verify(ratingService, times(1)).getRatingById(1L);
    }

    @Test
    void testAddRating() {
        RatingDto requestDto = new RatingDto(null, 1L, 1L, 5, "Супер!", LocalDateTime.now());
        RatingDto responseDto = new RatingDto(1L, 1L, 1L, 5, "Супер!", LocalDateTime.now());
        when(ratingService.addRating(requestDto)).thenReturn(responseDto);
        ResponseEntity<RatingDto> response = ratingController.addRating(requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getScore());
        verify(ratingService, times(1)).addRating(requestDto);
    }

    @Test
    void testUpdateRating() {
        RatingDto requestDto = new RatingDto(1L, 1L, 1L, 3, "Средний", LocalDateTime.now());
        RatingDto responseDto = new RatingDto(1L, 1L, 1L, 3, "Средний", LocalDateTime.now());
        when(ratingService.updateRating(1L, requestDto)).thenReturn(responseDto);
        ResponseEntity<RatingDto> response = ratingController.updateRating(1L, requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getScore());
        verify(ratingService, times(1)).updateRating(1L, requestDto);
    }

    @Test
    void testDeleteRating() {
        doNothing().when(ratingService).deleteRating(1L);
        ResponseEntity<Void> response = ratingController.deleteRating(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ratingService, times(1)).deleteRating(1L);
    }
}
