package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalStatistics {

    /**
     * Общее количество сайтов.
     */
    private int totalSites;

    /**
     * Общее количество страниц.
     */
    private long totalPages;

    /**
     * Общее количество лемм.
     */
    private long totalLemmas;
}