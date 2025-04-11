package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Статистика по всем сайтам:
 * общее количество сайтов, страниц, лемм и флаг активности индексации.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalStatistics {
    private int sites;
    private long pages;
    private long lemmas;
    private boolean isIndexing;
}
