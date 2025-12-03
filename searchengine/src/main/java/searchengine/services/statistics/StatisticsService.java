package searchengine.services.statistics;

import searchengine.dto.statistics.FinalStatisticsResponse;

/**
 * Интерфейс сервиса для получения статистики по индексированным сайтам.
 */
public interface StatisticsService {
    FinalStatisticsResponse getStatistics();
}