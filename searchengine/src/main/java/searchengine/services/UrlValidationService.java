package searchengine.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UrlValidationService {

    private static final String URL_REGEX = "^(https?://)?([\\w\\-]+\\.)+[\\w\\-]+(:\\d+)?(/.*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }
}