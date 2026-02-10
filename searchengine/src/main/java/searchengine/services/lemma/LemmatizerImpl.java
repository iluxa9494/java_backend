package searchengine.services.lemma;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class LemmatizerImpl implements Lemmatizer {
    private static final List<String> EXCLUDED_PARTS = List.of(
            "МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ", "ВВОДН", "МС", "МС-П", "CONJ", "INTJ", "PRCL", "PREP"
    );
    private final LuceneMorphology morphology;

    @Autowired
    public LemmatizerImpl(LuceneMorphology morphology) {
        this.morphology = morphology;
    }

    @Override
    public Map<String, Integer> collectLemmas(String text) {
        Map<String, Integer> lemmaCount = new HashMap<>();
        if (text == null || text.isBlank()) return lemmaCount;
        String[] words = text.toLowerCase()
                .replaceAll("[^а-яА-ЯёЁ\\s]", " ")
                .trim()
                .split("\\s+");
        for (String word : words) {
            if (word.length() < 3 || isServiceWord(word)) continue;
            try {
                List<String> normalForms = morphology.getNormalForms(word);
                if (!normalForms.isEmpty()) {
                    String lemma = normalForms.get(0);
                    lemmaCount.put(lemma, lemmaCount.getOrDefault(lemma, 0) + 1);
                }
            } catch (Exception e) {
                log.debug("Пропущено слово (необрабатываемое): {}", word);
            }
        }
        return lemmaCount;
    }

    @Override
    public Set<String> getLemmas(String text) {
        return collectLemmas(text).keySet();
    }

    @Override
    public boolean isServiceWord(String word) {
        try {
            List<String> morphInfo = morphology.getMorphInfo(word);
            return morphInfo.stream().anyMatch(info ->
                    EXCLUDED_PARTS.stream().anyMatch(info::contains));
        } catch (Exception e) {
            return true;
        }
    }
}