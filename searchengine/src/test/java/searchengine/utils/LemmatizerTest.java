//package searchengine.utils;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class LemmatizerTest {
//
//    private Lemmatizer lemmatizer;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        lemmatizer = new Lemmatizer();
//    }
//
//    @Test
//    void testLemmatizeTextWithValidText() {
//        String text = "Тестовый текст для лемматизации.";
//        List<String> result = lemmatizer.lemmatizeText(text);
//        assertNotNull(result);
//        assertEquals(3, result.size());
//        assertTrue(result.contains("тест"));
//        assertTrue(result.contains("текст"));
//        assertTrue(result.contains("лемматизация"));
//    }
//
//    @Test
//    void testLemmatizeTextWithEmptyText() {
//        String text = "";
//        List<String> result = lemmatizer.lemmatizeText(text);
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void testLemmatizeTextWithNoise() {
//        String text = "Тест123! текст@ для# лемматизации.";
//        List<String> result = lemmatizer.lemmatizeText(text);
//        assertNotNull(result);
//        assertEquals(3, result.size());
//        assertTrue(result.contains("тест"));
//        assertTrue(result.contains("текст"));
//        assertTrue(result.contains("лемматизация"));
//    }
//
//    @Test
//    void testExtractMeaningfulWordsWithValidText() {
//        String text = "Тестовый текст для лемматизации.";
//        List<String> result = lemmatizer.extractMeaningfulWords(text);
//        assertNotNull(result);
//        assertTrue(result.contains("тест"));
//        assertTrue(result.contains("текст"));
//        assertTrue(result.contains("лемматизация"));
//    }
//
//    @Test
//    void testExtractMeaningfulWordsWithEmptyText() {
//        String text = "";
//        List<String> result = lemmatizer.extractMeaningfulWords(text);
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//}
