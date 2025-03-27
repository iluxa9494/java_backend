package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchRequest;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.search.SearchResult;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.utils.Lemmatizer;
import searchengine.utils.SnippetGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;
    private final Lemmatizer lemmatizer;
    private final SnippetGenerator snippetGenerator;

    public SearchServiceImpl(PageRepository pageRepository,
                             IndexRepository indexRepository,
                             LemmaRepository lemmaRepository,
                             Lemmatizer lemmatizer,
                             SnippetGenerator snippetGenerator) {
        this.pageRepository = pageRepository;
        this.indexRepository = indexRepository;
        this.lemmaRepository = lemmaRepository;
        this.lemmatizer = lemmatizer;
        this.snippetGenerator = snippetGenerator;
    }

    @Override
    public SearchResponse search(SearchRequest request) {
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return new SearchResponse(0, new ArrayList<>());
        }
        String query = request.getQuery().trim().toLowerCase();
        String[] words = query.split("\\s+");
        List<String> queryLemmas = new ArrayList<>();
        for (String word : words) {
            List<String> normalForms = lemmatizer.getNormalForms(word);
            if (normalForms != null && !normalForms.isEmpty()) {
                queryLemmas.add(normalForms.get(0));
            }
        }

        List<String> filteredLemmas = new ArrayList<>();
        for (String lemmaStr : queryLemmas) {
            Lemma lemmaObj = lemmaRepository.findByLemma(lemmaStr);
            int frequency = (lemmaObj != null) ? lemmaObj.getFrequency() : 0;
            if (frequency < 100) {
                filteredLemmas.add(lemmaStr);
            }
        }
        if (filteredLemmas.isEmpty()) {
            return new SearchResponse(0, new ArrayList<>());
        }
        List<Object[]> pageRankData = indexRepository.findPagesByLemmas(filteredLemmas, request.getSite());
        if (pageRankData == null || pageRankData.isEmpty()) {
            return new SearchResponse(0, new ArrayList<>());
        }

        double maxRank = pageRankData.stream()
                .mapToDouble(row -> (Double) row[1])
                .max()
                .orElse(1.0);

        List<SearchResult> results = new ArrayList<>();
        for (Object[] row : pageRankData) {
            Page page = (Page) row[0];
            double rankSum = (Double) row[1];
            float relativeRelevance = (float) (rankSum / maxRank);
            String snippet = snippetGenerator.generateSnippet(page.getContent(), query);
            SearchResult result = new SearchResult(
                    page.getPath(),
                    page.getTitle(),
                    snippet,
                    relativeRelevance
            );
            results.add(result);
        }
        results.sort(Comparator.comparingDouble(SearchResult::getRelevance).reversed());
        int offset = 0;
        int limit = 20;
        int endIndex = Math.min(results.size(), offset + limit);
        List<SearchResult> paginatedResults = results.subList(offset, endIndex);
        return new SearchResponse(results.size(), paginatedResults);
    }
}