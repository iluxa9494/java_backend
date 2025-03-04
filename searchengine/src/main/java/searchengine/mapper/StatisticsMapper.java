package searchengine.mapper;

import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;

public class StatisticsMapper {

    /**
     * Преобразует объект StatisticsData в DetailedStatisticsItem.
     *
     * @param data объект StatisticsData
     * @return объект DetailedStatisticsItem
     */
    public static DetailedStatisticsItem mapToDetailedStatisticsItem(StatisticsData data) {
        if (data == null) {
            throw new IllegalArgumentException("StatisticsData cannot be null");
        }

        return new DetailedStatisticsItem(
                data.getName(),
                data.getUrl(),
                data.getStatus(),
                data.getStatusTime() != null ? data.getStatusTime().toString() : null,
                data.getLastError(),
                data.getPages() != null ? data.getPages().intValue() : 0,
                data.getLemmas() != null ? data.getLemmas().intValue() : 0
        );
    }
}