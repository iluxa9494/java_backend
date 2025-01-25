package mapper;

import org.junit.jupiter.api.Test;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsMapperTest {

    @Test
    void testMapToDetailedStatisticsItem() {
        // Данные для теста
        StatisticsData data = new StatisticsData(
                "Test Site",
                "http://test.com",
                "INDEXED",
                LocalDateTime.of(2025, 1, 25, 12, 0),
                null,
                100L,
                200L
        );

        // Преобразование
        DetailedStatisticsItem item = StatisticsMapper.mapToDetailedStatisticsItem(data);

        // Проверка результатов
        assertEquals("Test Site", item.getName());
        assertEquals("http://test.com", item.getUrl());
        assertEquals("INDEXED", item.getStatus());
        assertEquals("2025-01-25T12:00", item.getStatusTime());
        assertNull(item.getError());
        assertEquals(100, item.getPages());
        assertEquals(200, item.getLemmas());
    }

    @Test
    void testMapToDetailedStatisticsItemWithError() {
        // Данные для теста с ошибкой
        StatisticsData data = new StatisticsData(
                "Test Site",
                "http://test.com",
                "FAILED",
                LocalDateTime.of(2025, 1, 25, 12, 0),
                "Connection error",
                null,
                null
        );

        // Преобразование
        DetailedStatisticsItem item = StatisticsMapper.mapToDetailedStatisticsItem(data);

        // Проверка результатов
        assertEquals("Test Site", item.getName());
        assertEquals("http://test.com", item.getUrl());
        assertEquals("FAILED", item.getStatus());
        assertEquals("2025-01-25T12:00", item.getStatusTime());
        assertEquals("Connection error", item.getError());
        assertEquals(0, item.getPages());
        assertEquals(0, item.getLemmas());
    }

    @Test
    void testMapToDetailedStatisticsItemWithNullData() {
        // Ожидаем выброс исключения при null-значении
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                StatisticsMapper.mapToDetailedStatisticsItem(null)
        );

        assertEquals("StatisticsData cannot be null", exception.getMessage());
    }
}
