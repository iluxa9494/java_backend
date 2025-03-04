package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    /**
     * Найти сайт по его точному URL.
     *
     * @param url URL сайта.
     * @return Optional с объектом Site, если сайт найден.
     */
    Optional<Site> findByUrl(String url);

    /**
     * Найти сайты с указанным статусом.
     *
     * @param status Статус сайта (например, INDEXING, INDEXED, FAILED).
     * @return Список сайтов.
     */
    List<Site> findByStatus(String status);

    /**
     * Удалить сайт по его URL.
     *
     * @param url URL сайта.
     */
    void deleteByUrl(String url);
}
