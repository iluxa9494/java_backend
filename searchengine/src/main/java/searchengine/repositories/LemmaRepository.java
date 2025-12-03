package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;
import java.util.Set;

/**
 * Репозиторий для работы с леммами — единицами текста, используемыми при поисковой индексации.
 */
@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query("SELECT l FROM Lemma l WHERE l.site = :site AND l.lemma IN :lemmas")
    List<Lemma> findBySiteAndLemmaIn(@Param("site") Site site, @Param("lemmas") Set<String> lemmas);

    @Query("SELECT l FROM Lemma l WHERE l.lemma = :lemma AND l.site.id = :siteId")
    Lemma findByLemmaAndSiteId(@Param("lemma") String lemma, @Param("siteId") int siteId);

    @Modifying
    @Query("DELETE FROM Lemma l WHERE l.site = :site")
    void deleteBySite(@Param("site") Site site);

    @Query("SELECT COUNT(l) FROM Lemma l WHERE l.site = :site")
    long countBySite(@Param("site") Site site);
}