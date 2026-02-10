package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    Page findBySiteAndPath(Site site, String path);

    List<Page> findBySite(Site site);

    @Query("SELECT COUNT(p) FROM Page p WHERE p.site = :site")
    long countBySite(@Param("site") Site site);

    @Query("SELECT p FROM Page p " +
            "JOIN SearchIndex i ON i.page = p " +
            "WHERE p.site.id = :siteId AND i.lemma IN :lemmas " +
            "GROUP BY p.id " +
            "HAVING COUNT(DISTINCT i.lemma) = :lemmaCount")
    List<Page> findPagesWithAllLemmas(@Param("siteId") int siteId,
                                      @Param("lemmas") List<Lemma> lemmas,
                                      @Param("lemmaCount") long lemmaCount);
}