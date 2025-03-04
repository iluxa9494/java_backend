package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Класс для хранения статистики по сайтам.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsData {

    /**
     * Имя сайта.
     */
    private String name;

    /**
     * URL сайта.
     */
    private String url;

    /**
     * Статус индексации сайта.
     */
    private String status;

    /**
     * Время последнего изменения статуса.
     */
    private LocalDateTime statusTime;

    /**
     * Сообщение об ошибке, если статус индексации `FAILED`.
     */
    private String lastError;

    /**
     * Общее количество страниц сайта.
     */
    private Long pages;

    /**
     * Общее количество лемм сайта.
     */
    private Long lemmas;

    /**
     * Конструктор без страниц и лемм.
     */
    public StatisticsData(String name, String url, String status, LocalDateTime statusTime, String lastError) {
        this.name = name;
        this.url = url;
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
    }
}