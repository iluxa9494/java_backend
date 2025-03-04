package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Page;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    /**
     * Найти все индексы, связанные с конкретной страницей.
     *
     * @param pageId Идентификатор страницы.
     * @return Список индексов.
     */
    List<Index> findByPageId(Integer pageId);

    /**
     * Найти все индексы, связанные с конкретной леммой.
     *
     * @param lemmaId Идентификатор леммы.
     * @return Список индексов.
     */
    List<Index> findByLemmaId(Integer lemmaId);

    /**
     * Найти все индексы, связанные с любым из указанных идентификаторов лемм.
     *
     * @param lemmaIds Список идентификаторов лемм.
     * @return Список индексов.
     */
    List<Index> findByLemmaIdIn(List<Integer> lemmaIds);

    /**
     * Удалить все индексы, связанные с конкретной страницей.
     *
     * @param pageId Идентификатор страницы.
     */
    void deleteByPageId(Integer pageId);

    /**
     * Удалить все индексы, связанные с конкретной леммой.
     *
     * @param lemmaId Идентификатор леммы.
     */
    void deleteByLemmaId(Integer lemmaId);

    @Query("SELECT MAX(i.rank) FROM Index i")
    Float findMaxRank();

    List<Index> findByPage(Page page);

    /**
     * Находит страницы и суммарный ранг для каждой страницы по заданным леммам.
     * Если параметр site равен null, поиск ведется по всем сайтам.
     *
     * @param lemmas Список нормальных форм слов (лемм).
     * @param site   URL сайта или null.
     * @return Список массивов, где row[0] – объект Page, а row[1] – сумма рангов (Double).
     */
    @Query("SELECT i.page, SUM(i.rank) as rankSum " +
            "FROM Index i " +
            "WHERE i.lemma.lemma IN :lemmas " +
            "AND (:site IS NULL OR i.page.site.url = :site) " +
            "GROUP BY i.page")
    List<Object[]> findPagesByLemmas(@Param("lemmas") List<String> lemmas,
                                     @Param("site") String site);
}
