package searchengine.services;

import org.springframework.stereotype.Service;

/**
 * Сервис для валидации URL-адресов.
 */
@Service
public class UrlValidationService {
    public boolean isValidUrl(String url) {
        return url != null && url.matches("^(http|https)://[^\\s]+$");
    }
}