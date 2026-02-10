package ru.skillbox.socialnetwork.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JwtClient {

    private final String authUrl;

    public JwtClient(@Value("${auth.url:http://localhost:8080}") String authUrl) {
        this.authUrl = authUrl;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public JwtValidationResponse validate(String token) {
        String url = authUrl + "/api/v1/auth/validate";
        ValidateRequest request = new ValidateRequest();
        request.setToken(token);

        try {
            return restTemplate.postForObject(url, request, JwtValidationResponse.class);
        } catch (Exception e) {
            // В случае ошибки — считаем токен невалидным
            return new JwtValidationResponse(false, null);
        }
    }
}

