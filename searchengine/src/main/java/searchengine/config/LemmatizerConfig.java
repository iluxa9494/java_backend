package searchengine.config;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для создания и настройки экземпляра {@link LuceneMorphology} для русской морфологии.
 * Использует {@link RussianLuceneMorphology} для работы с русскими словами.
 */
@Configuration
public class LemmatizerConfig {
    @Bean
    public LuceneMorphology luceneMorphology() throws Exception {
        return new RussianLuceneMorphology();
    }
}