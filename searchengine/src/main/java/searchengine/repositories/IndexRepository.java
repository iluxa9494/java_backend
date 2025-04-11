package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;

/**
 * Репозиторий для доступа к сущностям, представляющим связи между страницами и леммами.
 */
@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {
    @Modifying
    @Query("DELETE FROM Index i WHERE i.page IN :pages")
    void deleteByPages(@Param("pages") List<Page> pages);

    List<Index> findByPage(Page page);

    @Query("SELECT i FROM Index i WHERE i.page = :page AND i.lemma = :lemma")
    Index findByPageAndLemma(@Param("page") Page page, @Param("lemma") Lemma lemma);
}