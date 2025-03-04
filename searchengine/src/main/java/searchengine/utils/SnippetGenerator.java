package searchengine.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetGenerator {
    private static final int SNIPPET_LENGTH = 200;

    /**
     * Генерирует сниппет для заданного HTML-текста и поискового запроса.
     * Сниппет очищается от HTML-тегов, затем выделяются совпадения запроса тегом <b>.
     *
     * @param text  исходный HTML-контент страницы
     * @param query поисковый запрос (одна или несколько фраз)
     * @return сформированный сниппет с выделенными совпадениями
     */
    public String generateSnippet(String text, String query) {
        if (text == null || text.isEmpty() || query == null || query.isEmpty()) {
            return "";
        }
        String plainText = text.replaceAll("<[^>]+>", " ");
        plainText = plainText.replaceAll("\\s+", " ").trim();
        String[] queryWords = query.split("\\s+");
        int firstOccurrence = -1;
        String lowerText = plainText.toLowerCase();
        for (String word : queryWords) {
            if (word.isEmpty()) continue;
            int index = lowerText.indexOf(word.toLowerCase());
            if (index != -1 && (firstOccurrence == -1 || index < firstOccurrence)) {
                firstOccurrence = index;
            }
        }
        if (firstOccurrence == -1) {
            firstOccurrence = 0;
        }
        int start = Math.max(0, firstOccurrence - SNIPPET_LENGTH / 2);
        int end = Math.min(plainText.length(), start + SNIPPET_LENGTH);
        String snippet = plainText.substring(start, end);
        for (String word : queryWords) {
            if (word.isEmpty()) continue;
            Pattern pattern = Pattern.compile("(?i)(" + Pattern.quote(word) + ")");
            Matcher matcher = pattern.matcher(snippet);
            snippet = matcher.replaceAll("<b>$1</b>");
        }
        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < plainText.length()) {
            snippet = snippet + "...";
        }
        return snippet;
    }
}