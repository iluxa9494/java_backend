package searchengine.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для конфигурации информации о сайте.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Site {
    private String name;
    private String url;
}