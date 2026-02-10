package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SearchIndex;

import java.util.List;

/**
 * Репозиторий для доступа к сущностям, представляющим связи между страницами и леммами.
 */
@Repository
public interface SearchIndexRepository extends JpaRepository<SearchIndex, Integer> {
    @Modifying
    @Query("DELETE FROM SearchIndex i WHERE i.page IN :pages")
    void deleteByPages(@Param("pages") List<Page> pages);

    List<SearchIndex> findByPage(Page page);

    @Query("SELECT i FROM SearchIndex i WHERE i.page = :page AND i.lemma = :lemma")
    SearchIndex findByPageAndLemma(@Param("page") Page page, @Param("lemma") Lemma lemma);
}