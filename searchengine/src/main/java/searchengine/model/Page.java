package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Index;

/**
 * Сущность страницы сайта, содержащая путь, код ответа, заголовок и HTML-контент.
 */
@Entity
@Table(
        name = "page",
        uniqueConstraints = @UniqueConstraint(columnNames = {"site_id", "path"}),
        indexes = @Index(name = "path_index", columnList = "path")
)
@org.hibernate.annotations.Table(appliesTo = "page",
        comment = "Page table",
        indexes = {
                @org.hibernate.annotations.Index(name = "path_index", columnNames = {"path"})
        }
)

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "path", nullable = false, length = 191)
    private String path;

    @Column(name = "code", nullable = false)
    private int code;

    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;
}