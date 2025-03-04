package searchengine.services;

import org.springframework.transaction.annotation.Transactional;

public interface IndexingService {
    /**
     * Запускает процесс индексации всех сайтов.
     *
     * @return true, если индексация успешно запущена, false, если уже выполняется.
     */
    boolean startIndexing();

    /**
     * Останавливает процесс индексации.
     *
     * @return true, если индексация успешно остановлена, false, если индексация не была запущена.
     */
    boolean stopIndexing();

    /**
     * Запускает индексацию для отдельной страницы.
     *
     * @param url URL страницы, которую нужно проиндексировать.
     * @return true, если индексация страницы успешна, false в случае ошибки.
     */
    boolean indexPage(String url);

    /**
     * Проверяет, находится ли индексация в процессе выполнения.
     *
     * @return true, если индексация выполняется, иначе false.
     */
    boolean isIndexing();

    /**
     * Проверяет, существует ли уже такой URL в базе данных.
     *
     * @param url URL для проверки.
     * @return true, если URL уже существует, иначе false.
     */
    boolean isDuplicateUrl(String url);

    @Transactional
    boolean addSite(String url, String name);

    boolean siteExists(String url);
}