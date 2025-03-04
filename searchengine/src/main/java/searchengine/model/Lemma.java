package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "lemma")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lemma {

    /**
     * Уникальный идентификатор леммы.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Текст леммы.
     */
    @Column(nullable = false)
    private String lemma;

    /**
     * Частота использования леммы на сайте.
     */
    @Column(nullable = false)
    private int frequency;

    /**
     * Связь с сайтом, к которому относится лемма.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    public Lemma(String lemma, int frequency, Site site) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.site = site;
    }
}