package ru.skillbox.socialnetwork.authentication.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillbox.socialnetwork.authentication.repositories.CaptchaRepository;
import ru.skillbox.socialnetwork.authentication.web.models.CaptchaDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaptchaServiceImplTest {

    @Mock
    private CaptchaRepository captchaRepository;

    @Test
    void generateCaptcha_fallsBackWhenRepositoryUnavailable() {
        when(captchaRepository.save(any())).thenThrow(new RuntimeException("mongo down"));
        CaptchaServiceImpl service = new CaptchaServiceImpl(captchaRepository);

        CaptchaDto dto = service.generateCaptcha();

        assertThat(dto.getSecret()).isNotBlank();
        assertThat(dto.getImage()).startsWith("data:image/png;base64,");
    }

    @Test
    void validateCaptcha_returnsFalseWhenRepositoryUnavailable() {
        when(captchaRepository.findById(any())).thenThrow(new RuntimeException("mongo down"));
        CaptchaServiceImpl service = new CaptchaServiceImpl(captchaRepository);

        boolean result = service.validateCaptcha("secret", "answer");

        assertThat(result).isFalse();
    }
}
