//package searchengine.mapper;
//
//import org.junit.jupiter.api.Test;
//import searchengine.dto.statistics.DetailedStatisticsItem;
//import searchengine.dto.statistics.StatisticsData;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class StatisticsMapperTest {
//
//    @Test
//    void testMapToDetailedStatisticsItem() {
//        StatisticsData data = new StatisticsData(
//                "Test Site",
//                "http://test.com",
//                "INDEXED",
//                LocalDateTime.of(2025, 1, 25, 12, 0),
//                null,
//                100L,
//                200L
//        );
//        DetailedStatisticsItem item = StatisticsMapper.mapToDetailedStatisticsItem(data);
//        assertEquals("Test Site", item.getName());
//        assertEquals("http://test.com", item.getUrl());
//        assertEquals("INDEXED", item.getStatus());
//        assertEquals("2025-01-25T12:00", item.getStatusTime());
//        assertNull(item.getError());
//        assertEquals(100, item.getPages());
//        assertEquals(200, item.getLemmas());
//    }
//
//    @Test
//    void testMapToDetailedStatisticsItemWithError() {
//        StatisticsData data = new StatisticsData(
//                "Test Site",
//                "http://test.com",
//                "FAILED",
//                LocalDateTime.of(2025, 1, 25, 12, 0),
//                "Connection error",
//                null,
//                null
//        );
//        DetailedStatisticsItem item = StatisticsMapper.mapToDetailedStatisticsItem(data);
//        assertEquals("Test Site", item.getName());
//        assertEquals("http://test.com", item.getUrl());
//        assertEquals("FAILED", item.getStatus());
//        assertEquals("2025-01-25T12:00", item.getStatusTime());
//        assertEquals("Connection error", item.getError());
//        assertEquals(0, item.getPages());
//        assertEquals(0, item.getLemmas());
//    }
//
//    @Test
//    void testMapToDetailedStatisticsItemWithNullData() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//                StatisticsMapper.mapToDetailedStatisticsItem(null)
//        );
//
//        assertEquals("StatisticsData cannot be null", exception.getMessage());
//    }
//}