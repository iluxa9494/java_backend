package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.services.lemma.LemmatizerImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LemmatizerServiceTest {
    private LemmatizerImpl lemmatizer;

    @BeforeEach
    public void setup() {
        LuceneMorphology mockMorphology = mock(LuceneMorphology.class);
        when(mockMorphology.getNormalForms("тест")).thenReturn(List.of("тест"));
        when(mockMorphology.getMorphInfo("тест")).thenReturn(List.of("СУЩ"));
        lemmatizer = new LemmatizerImpl(mockMorphology);
    }

    @Test
    public void testCollectLemmasBasicWord() {
        var result = lemmatizer.collectLemmas("тест");
        assertEquals(1, result.size());
        assertEquals(1, result.get("тест"));
    }

    @Test
    public void testIsServiceWordReturnFalse() {
        boolean isService = lemmatizer.isServiceWord("тест");
        assertFalse(isService);
    }
}