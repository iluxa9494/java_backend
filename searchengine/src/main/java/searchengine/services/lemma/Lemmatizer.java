package searchengine.services.lemma;

import java.util.Map;
import java.util.Set;

public interface Lemmatizer {
    Map<String, Integer> collectLemmas(String text);
    Set<String> getLemmas(String text);
    boolean isServiceWord(String word);
}