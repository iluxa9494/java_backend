package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    /**
     * Найти страницу по её пути (URL).
     *
     * @param path Путь страницы.
     * @return Страница, если она существует, иначе null.
     */
    Page findByPath(String path);

    /**
     * Найти все страницы, относящиеся к указанному сайту.
     *
     * @param siteId Идентификатор сайта.
     * @return Список страниц.
     */
    List<Page> findBySiteId(Integer siteId);

    List<Page> findBySite(Site site);

    /**
     * Удалить все страницы, относящиеся к указанному сайту.
     *
     * @param siteId Идентификатор сайта.
     */
    void deleteBySiteId(Integer siteId);

    /**
     * Найти страницы с указанным HTTP-кодом.
     *
     * @param code HTTP-код.
     * @return Список страниц.
     */
    List<Page> findByCode(Integer code);

    /**
     * Найти страницы с указанным HTTP-кодом для конкретного сайта.
     *
     * @param siteId Идентификатор сайта.
     * @param code   HTTP-код.
     * @return Список страниц.
     */
    List<Page> findBySiteIdAndCode(Integer siteId, Integer code);

    /**
     * Проверяет, существует ли страница с указанным путем.
     *
     * @param path Путь страницы.
     * @return true, если страница существует, иначе false.
     */
    boolean existsByPath(String path);

    @Query("SELECT MAX(i.rank) FROM Index i")
    float findMaxRank();

    @Query("SELECT COUNT(p) FROM Page p WHERE p.site = :site")
    long countBySite(@Param("site") Site site);

}