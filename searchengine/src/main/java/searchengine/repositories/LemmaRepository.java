package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    /**
     * Найти лемму по тексту.
     *
     * @param lemma Текст леммы.
     * @return Лемма, если она существует, иначе null.
     */
    Lemma findByLemma(String lemma);

    /**
     * Найти все леммы, которые содержатся в списке текстов.
     *
     * @param lemmas Список текстов лемм.
     * @return Список лемм.
     */
    List<Lemma> findByLemmaIn(List<String> lemmas);

    /**
     * Найти лемму по тексту и идентификатору сайта.
     *
     * @param lemma  Текст леммы.
     * @param siteId Идентификатор сайта.
     * @return Лемма, если она существует, иначе null.
     */
    @Query("SELECT l FROM Lemma l WHERE l.lemma = :lemma AND l.site.id = :siteId")
    Lemma findByLemmaAndSiteId(@Param("lemma") String lemma, @Param("siteId") int siteId);

    /**
     * Увеличить частоту указанной леммы.
     *
     * @param lemmaId   Идентификатор леммы.
     * @param increment Значение, на которое нужно увеличить частоту.
     */
    default void incrementFrequency(Integer lemmaId, int increment) {
        Lemma lemma = findById(lemmaId).orElse(null);
        if (lemma != null) {
            lemma.setFrequency(lemma.getFrequency() + increment);
            save(lemma);
        }
    }

    /**
     * Уменьшить частоту указанной леммы.
     *
     * @param lemmaId   Идентификатор леммы.
     * @param decrement Значение, на которое нужно уменьшить частоту.
     */
    default void decrementFrequency(Integer lemmaId, int decrement) {
        Lemma lemma = findById(lemmaId).orElse(null);
        if (lemma != null) {
            lemma.setFrequency(lemma.getFrequency() - decrement);
            if (lemma.getFrequency() <= 0) {
                delete(lemma);
            } else {
                save(lemma);
            }
        }
    }

    @Query("SELECT COUNT(l) FROM Lemma l WHERE l.site = :site")
    long countBySite(@Param("site") Site site);

}
