package searchengine.utils;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.util.ArrayList;
import java.util.List;

public class Lemmatizer {

    private final LuceneMorphology luceneMorphology;

    public Lemmatizer() {
        try {
            this.luceneMorphology = new RussianLuceneMorphology();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации RussianLuceneMorphology", e);
        }
    }

    /**
     * Получает список нормальных форм (лемм) для заданного слова.
     * Сначала слово приводится к нижнему регистру и очищается от лишних символов.
     *
     * @param word входное слово
     * @return список нормальных форм слова
     */
    public List<String> getNormalForms(String word) {
        List<String> normalForms = new ArrayList<>();
        if (word == null || word.trim().isEmpty()) {
            return normalForms;
        }
        String cleanedWord = word.toLowerCase().replaceAll("[^a-zа-яё]", "");
        if (cleanedWord.isEmpty()) {
            return normalForms;
        }
        try {
            normalForms = luceneMorphology.getNormalForms(cleanedWord);
        } catch (Exception e) {
            System.err.println("Ошибка лемматизации слова: " + cleanedWord + " - " + e.getMessage());
        }
        return normalForms;
    }

    /**
     * Получает морфологическую информацию по слову.
     *
     * @param word входное слово
     * @return список строк с морфологической информацией
     */
    public List<String> getMorphInfo(String word) {
        List<String> morphInfo = new ArrayList<>();
        if (word == null || word.trim().isEmpty()) {
            return morphInfo;
        }
        String cleanedWord = word.toLowerCase().replaceAll("[^a-zа-яё]", "");
        if (cleanedWord.isEmpty()) {
            return morphInfo;
        }
        try {
            morphInfo = luceneMorphology.getMorphInfo(cleanedWord);
        } catch (Exception e) {
            System.err.println("Ошибка получения морфологии для слова: " + cleanedWord + " - " + e.getMessage());
        }
        return morphInfo;
    }
}