package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.persistence.Index;

@Entity
@Table(
        name = "page",
        indexes = {@Index(name = "path_index", columnList = "path", unique = true)}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    /**
     * Уникальный идентификатор страницы.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Связь с таблицей `site` (сайт).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    /**
     * Путь страницы относительно корня сайта.
     */
    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    /**
     * HTTP-код ответа при запросе страницы.
     */
    @Column(name = "code", nullable = false)
    private int code;

    /**
     * Содержимое страницы (HTML).
     */
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    /**
     * Заголовок страницы.
     */
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    /**
     * Конструктор для тестов.
     */
    public Page(int id, String path, String title, String content, int code, Site site) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.content = content;
        this.code = code;
        this.site = site;
    }
}
