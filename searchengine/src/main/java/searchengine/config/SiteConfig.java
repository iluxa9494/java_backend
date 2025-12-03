package searchengine.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация одного сайта для индексации.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfig {
    private String url;
    private String name;
}