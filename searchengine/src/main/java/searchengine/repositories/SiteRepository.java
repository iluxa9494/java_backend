package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;
import searchengine.model.SiteStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    Optional<Site> findByUrl(String url);

    List<Site> findByStatus(SiteStatus status);

    @Query("SELECT s FROM Site s WHERE REPLACE(s.url, 'www.', '') = REPLACE(:url, 'www.', '')")
    Optional<Site> findByUrlIgnoreWww(@Param("url") String url);
}