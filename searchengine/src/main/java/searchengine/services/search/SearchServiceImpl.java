package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.dto.search.ErrorResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.search.SearchResult;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.lemma.Lemmatizer;

import java.util.*;

/**
 * Реализация сервиса полнотекстового поиска по проиндексированным сайтам.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final Lemmatizer lemmatizer;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;

    @Override
    public ResponseEntity<?> search(String query, String site, int offset, int limit) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, "Задан пустой поисковый запрос"));
        }
        List<Site> sitesToSearch;
        if (site != null && !site.isBlank()) {
            Optional<Site> optionalSite = siteRepository.findByUrl(site);
            if (optionalSite.isEmpty() || optionalSite.get().getStatus() != SiteStatus.INDEXED) {
                return ResponseEntity.badRequest().body(new ErrorResponse(false, "Указанный сайт не проиндексирован"));
            }
            sitesToSearch = List.of(optionalSite.get());
        } else {
            sitesToSearch = siteRepository.findAll().stream()
                    .filter(s -> s.getStatus() == SiteStatus.INDEXED)
                    .toList();
            if (sitesToSearch.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse(false, "Индексация ещё не завершена"));
            }
        }
        Map<String, Integer> lemmas = lemmatizer.collectLemmas(query);
        if (lemmas.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(false, "По запросу ничего не найдено"));
        }
        List<SearchResult> allResults = new ArrayList<>();
        for (Site s : sitesToSearch) {
            List<Lemma> siteLemmas = lemmaRepository.findBySiteAndLemmaIn(s, lemmas.keySet());
            if (siteLemmas.isEmpty()) continue;
            List<Page> matchingPages = pageRepository.findPagesWithAllLemmas(
                    s.getId(), siteLemmas, siteLemmas.size()
            );
            for (Page page : matchingPages) {
                String content = page.getContent();
                String title = page.getTitle();
                String snippet = makeSnippet(content, lemmas.keySet());
                float relevance = calculateRelevance(page, siteLemmas);
                allResults.add(new SearchResult(
                        s.getUrl(),
                        s.getName(),
                        page.getPath(),
                        title,
                        snippet,
                        relevance
                ));
            }
        }
        allResults.sort(Comparator.comparing(SearchResult::getRelevance).reversed());
        int count = allResults.size();
        int toIndex = Math.min(offset + limit, count);
        List<SearchResult> paginated = allResults.subList(Math.min(offset, count), toIndex);
        return ResponseEntity.ok(new SearchResponse(true, count, paginated));
    }

    private String makeSnippet(String htmlContent, Set<String> lemmas) {
        String noScripts = htmlContent.replaceAll("(?s)<script.*?>.*?</script>", " ");
        String text = noScripts.replaceAll("<[^>]+>", " ");
        text = text.replaceAll("\\s+", " ").trim();
        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            String clean = words[i].toLowerCase().replaceAll("[^а-яёa-z0-9]", "");
            Set<String> wordLemmas = lemmatizer.getLemmas(clean);
            if (wordLemmas.stream().anyMatch(lemmas::contains)) {
                int start = Math.max(0, i - 5);
                int end = Math.min(words.length, i + 5);
                StringBuilder snippet = new StringBuilder("... ");
                for (int j = start; j < end; j++) {
                    String word = words[j];
                    String wordBase = word.toLowerCase().replaceAll("[^а-яёa-z0-9]", "");
                    Set<String> currentLemmas = lemmatizer.getLemmas(wordBase);
                    if (currentLemmas.stream().anyMatch(lemmas::contains)) {
                        snippet.append("<b>").append(word).append("</b>").append(" ");
                    } else {
                        snippet.append(word).append(" ");
                    }
                }
                snippet.append("...");
                return snippet.toString().trim();
            }
        }
        return "...";
    }

    private float calculateRelevance(Page page, List<Lemma> lemmas) {
        return lemmas.stream()
                .map(lemma -> indexRepository.findByPageAndLemma(page, lemma))
                .filter(Objects::nonNull)
                .map(Index::getRank)
                .reduce(0F, Float::sum);
    }
}