package ru.skillbox.socialnetwork.authentication.services;

import ru.skillbox.socialnetwork.authentication.web.models.CaptchaDto;

public interface CaptchaService {

    CaptchaDto generateCaptcha();

    boolean validateCaptcha(String secret, String answer);
}