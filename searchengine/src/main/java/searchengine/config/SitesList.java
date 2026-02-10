package searchengine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс для хранения списка сайтов из конфигурационного файла.
 */
@Data
@Component
@ConfigurationProperties(prefix = "indexing-settings")
public class SitesList {
    private List<SiteConfig> sites;
}