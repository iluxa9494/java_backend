package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "`index`")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Связь с таблицей `page` (страница).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    /**
     * Связь с таблицей `lemma` (лемма).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", nullable = false)
    private Lemma lemma;

    /**
     * Ранг леммы на данной странице.
     */
    @Column(name = "`rank`", nullable = false)
    private float rank;
}