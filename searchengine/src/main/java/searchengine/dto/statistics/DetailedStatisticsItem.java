package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedStatisticsItem {
    private String name;
    private String url;
    private String status;
    private String statusTime;
    private String lastError;
    private long pages;
    private long lemmas;
}