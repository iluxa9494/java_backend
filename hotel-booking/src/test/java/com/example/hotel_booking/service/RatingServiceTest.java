package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.RatingDto;
import com.example.hotel_booking.model.Hotel;
import com.example.hotel_booking.model.Rating;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.HotelRepository;
import com.example.hotel_booking.repository.RatingRepository;
import com.example.hotel_booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRatings() {
        List<Rating> ratings = List.of(new Rating(1L, new User(), new Hotel(), 5, "Отлично", null));
        when(ratingRepository.findAll()).thenReturn(ratings);

        List<RatingDto> result = ratingService.getAllRatings();

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getScore());
        verify(ratingRepository, times(1)).findAll();
    }

    @Test
    void testGetRatingById_WhenRatingExists() {
        Rating rating = new Rating(1L, new User(), new Hotel(), 4, "Хороший отель", null);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        RatingDto result = ratingService.getRatingById(1L);

        assertNotNull(result);
        assertEquals(4, result.getScore());
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRatingById_WhenRatingDoesNotExist() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ratingService.getRatingById(1L));
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void testAddRating() {
        User user = new User();
        user.setId(1L);
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setRating(BigDecimal.ZERO);
        hotel.setRatingsCount(0);

        RatingDto ratingDto = new RatingDto(null, 1L, 1L, 5, "Отличный сервис", null);
        Rating rating = new Rating(1L, user, hotel, 5, "Отличный сервис", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(ratingRepository.findByUserAndHotel(user, hotel)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        RatingDto result = ratingService.addRating(ratingDto);

        assertNotNull(result);
        assertEquals(5, result.getScore());
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    void testUpdateRating_WhenRatingExists() {
        Rating rating = new Rating(1L, new User(), new Hotel(), 4, "Хорошо", null);
        RatingDto ratingDto = new RatingDto(1L, 1L, 1L, 5, "Отлично", null);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        RatingDto result = ratingService.updateRating(1L, ratingDto);

        assertNotNull(result);
        assertEquals(5, result.getScore());
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    void testUpdateRating_WhenRatingDoesNotExist() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        RatingDto ratingDto = new RatingDto(1L, 1L, 1L, 5, "Отлично", null);

        assertThrows(RuntimeException.class, () -> ratingService.updateRating(1L, ratingDto));
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteRating_WhenRatingExists() {
        Rating rating = new Rating(1L, new User(), new Hotel(), 5, "Отлично", null);
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L);

        verify(ratingRepository, times(1)).delete(rating);
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testDeleteRating_WhenRatingDoesNotExist() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ratingService.deleteRating(1L));
        verify(ratingRepository, times(1)).findById(1L);
    }
}