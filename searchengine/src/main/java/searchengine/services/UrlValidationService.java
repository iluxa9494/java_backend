package searchengine.services;

import org.springframework.stereotype.Service;

@Service
public class UrlValidationService {
    public boolean isValidUrl(String url) {
        return url != null && url.matches("^(http|https)://[^\\s]+$");
    }
}