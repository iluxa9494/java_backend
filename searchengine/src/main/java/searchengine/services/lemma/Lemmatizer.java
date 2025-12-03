package searchengine.services.lemma;

import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для работы с леммами текста.
 * Предоставляет методы для извлечения лемм и фильтрации служебных слов.
 */
public interface Lemmatizer {
    Map<String, Integer> collectLemmas(String text);
    Set<String> getLemmas(String text);
    boolean isServiceWord(String word);
}