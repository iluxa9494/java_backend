package com.example.hotel_booking.controller;

import com.example.hotel_booking.dto.Statistics.StatisticsDto;
import com.example.hotel_booking.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStatistics() {
        List<StatisticsDto> statisticsList = List.of(
                new StatisticsDto("1", "USER_REGISTRATION", "user1", null, LocalDateTime.now())
        );
        when(statisticsService.getAllStatistics()).thenReturn(statisticsList);

        List<StatisticsDto> result = statisticsController.getAllStatistics();

        assertEquals(1, result.size());
        assertEquals("USER_REGISTRATION", result.get(0).getEventType());
        verify(statisticsService, times(1)).getAllStatistics();
    }

    @Test
    void testGetStatisticsByType() {
        List<StatisticsDto> statisticsList = List.of(
                new StatisticsDto("1", "ROOM_BOOKING", "user1", null, LocalDateTime.now())
        );
        when(statisticsService.getStatisticsByType("ROOM_BOOKING")).thenReturn(statisticsList);

        List<StatisticsDto> result = statisticsController.getStatisticsByType("ROOM_BOOKING");

        assertEquals(1, result.size());
        assertEquals("ROOM_BOOKING", result.get(0).getEventType());
        verify(statisticsService, times(1)).getStatisticsByType("ROOM_BOOKING");
    }
}
