//package searchengine;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import searchengine.model.Lemma;
//import searchengine.model.Page;
//import searchengine.model.Site;
//import searchengine.repositories.LemmaRepository;
//import searchengine.repositories.PageRepository;
//import searchengine.repositories.SiteRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@Transactional
//class DatabaseIntegrationTest {
//
//    @Autowired
//    private SiteRepository siteRepository;
//
//    @Autowired
//    private PageRepository pageRepository;
//
//    @Autowired
//    private LemmaRepository lemmaRepository;
//
//    @Test
//    void testSaveAndRetrieveSite() {
//        Site site = new Site(0, "Test Site", "https://testsite.com", "INDEXED", LocalDateTime.now(), null);
//        siteRepository.save(site);
//        List<Site> sites = siteRepository.findAll();
//        assertEquals(1, sites.size());
//        assertEquals("Test Site", sites.get(0).getName());
//    }
//
//    @Test
//    void testSaveAndRetrievePagesAndLemmas() {
//        Site site = new Site(0, "Test Site", "https://testsite.com", "INDEXED", LocalDateTime.now(), null);
//        site = siteRepository.save(site);
//        Page page1 = new Page(0, "/page1", "Title 1", "<html>Content 1</html>", 200, site);
//        Page page2 = new Page(0, "/page2", "Title 2", "<html>Content 2</html>", 200, site);
//        pageRepository.saveAll(List.of(page1, page2));
//        Lemma lemma1 = new Lemma(0, "lemma1", 10, site);
//        Lemma lemma2 = new Lemma(0, "lemma2", 20, site);
//        lemmaRepository.saveAll(List.of(lemma1, lemma2));
//        assertEquals(2, pageRepository.findAll().size());
//        assertEquals(2, lemmaRepository.findAll().size());
//    }
//
//    @Test
//    void testDeleteSiteAndCascade() {
//        Site site = new Site(0, "Test Site", "https://testsite.com", "INDEXED", LocalDateTime.now(), null);
//        site = siteRepository.save(site);
//        Page page = new Page(0, "/page", "Title", "<html>Content</html>", 200, site);
//        Lemma lemma = new Lemma(0, "lemma", 10, site);
//        pageRepository.save(page);
//        lemmaRepository.save(lemma);
//        siteRepository.delete(site);
//        assertTrue(siteRepository.findAll().isEmpty());
//        assertTrue(pageRepository.findAll().isEmpty());
//        assertTrue(lemmaRepository.findAll().isEmpty());
//    }
//}