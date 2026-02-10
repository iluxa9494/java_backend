package com.example.hotel_booking.service;

import com.example.hotel_booking.dto.Statistics.StatisticsDto;
import com.example.hotel_booking.dto.Statistics.StatisticsEventDto;
import com.example.hotel_booking.kafka.KafkaProducerService;
import com.example.hotel_booking.model.BookingDetails;
import com.example.hotel_booking.model.Statistics;
import com.example.hotel_booking.repository.mongo.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class StatisticsServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStatistics() {
        List<Statistics> statisticsList = List.of(new Statistics("1", "USER_REGISTRATION", "user1", null, LocalDateTime.now()));
        when(statisticsRepository.findAll()).thenReturn(statisticsList);
        List<StatisticsDto> result = statisticsService.getAllStatistics();
        assertEquals(1, result.size());
        assertEquals("USER_REGISTRATION", result.get(0).getEventType());
        verify(statisticsRepository, times(1)).findAll();
    }

    @Test
    void testGetStatisticsByType() {
        List<Statistics> statisticsList = List.of(new Statistics("1", "ROOM_BOOKING", "user1", null, LocalDateTime.now()));
        when(statisticsRepository.findByEventType("ROOM_BOOKING")).thenReturn(statisticsList);
        List<StatisticsDto> result = statisticsService.getStatisticsByType("ROOM_BOOKING");
        assertEquals(1, result.size());
        assertEquals("ROOM_BOOKING", result.get(0).getEventType());
        verify(statisticsRepository, times(1)).findByEventType("ROOM_BOOKING");
    }

    @Test
    void testSaveUserRegistration() {
        statisticsService.saveUserRegistration("user1");
        verify(kafkaProducerService, times(1)).sendEvent(eq("statistics-events"), any(StatisticsEventDto.class));
    }

    @Test
    void testSaveRoomBooking() {
        statisticsService.saveRoomBooking("user1", "booking1", "room1", LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        verify(kafkaProducerService, times(1)).sendEvent(eq("statistics-events"), any(StatisticsEventDto.class));
    }

    @Test
    void testExportStatisticsToCSV() {
        List<Statistics> statisticsList = List.of(new Statistics("1", "ROOM_BOOKING", "user1",
                new BookingDetails("booking1", "room1", LocalDateTime.now(), LocalDateTime.now().plusDays(2)), LocalDateTime.now()));
        when(statisticsRepository.findAll()).thenReturn(statisticsList);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        statisticsService.exportStatisticsToCSV(writer);
        writer.flush();
        String csvContent = stringWriter.toString();
        assertTrue(csvContent.contains("ROOM_BOOKING"));
        assertTrue(csvContent.contains("user1"));
        assertTrue(csvContent.contains("booking1"));
        verify(statisticsRepository, times(1)).findAll();
    }
}
