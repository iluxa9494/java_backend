package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Финальный ответ со статистикой.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalStatisticsResponse {
    private boolean result;
    private StatisticsData statistics;
}
