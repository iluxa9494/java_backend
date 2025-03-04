package searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HtmlParser {

    /**
     * Загружает HTML-документ с указанного URL.
     *
     * @param url URL страницы.
     * @return HTML-документ.
     * @throws IOException Если произошла ошибка загрузки.
     */
    public Document loadDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .timeout(10000)
                .get();
    }

    /**
     * Извлекает текст из HTML-документа с дополнительной фильтрацией.
     *
     * @param document HTML-документ.
     * @return Очищенный текст страницы.
     */
    public String extractText(Document document) {
        if (document.body() == null) {
            return "";
        }

        Document cleanDocument = Jsoup.parse(document.body().html());
        cleanDocument.select("script, style, iframe, noscript, svg, form, footer, header, nav, aside").remove();

        return cleanDocument.body().text().replaceAll("\\s+", " ").trim();
    }

    /**
     * Извлекает заголовок страницы из HTML-документа.
     *
     * @param document HTML-документ.
     * @return Заголовок страницы.
     */
    public String extractTitle(Document document) {
        return document.title().trim();
    }

    /**
     * Извлекает все ссылки (URL) из HTML-документа.
     *
     * @param document HTML-документ.
     * @return Список ссылок.
     */
    public Elements extractLinks(Document document) {
        return document.select("a[href]");
    }

    /**
     * Очищает HTML-контент, удаляя все теги, кроме значимых.
     *
     * @param html HTML-контент в виде строки.
     * @return Очищенный текст.
     */
    public String sanitizeHtml(String html) {
        Document document = Jsoup.parse(html);
        document.select("script, style, iframe, noscript, svg, form, footer, header, nav, aside").remove();
        return document.body() != null ? document.body().text().replaceAll("\\s+", " ").trim() : "";
    }

    /**
     * Извлекает мета-описание из HTML-документа.
     *
     * @param document HTML-документ.
     * @return Содержимое мета-описания или пустая строка, если мета-описание отсутствует.
     */
    public String extractMetaDescription(Document document) {
        Element metaDescription = document.selectFirst("meta[name=description]");
        return metaDescription != null ? metaDescription.attr("content").trim() : "";
    }

    /**
     * Проверяет, является ли данный URL валидным.
     *
     * @param url URL для проверки.
     * @return true, если URL валиден, иначе false.
     */
    public boolean isValidUrl(String url) {
        try {
            Jsoup.connect(url).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
