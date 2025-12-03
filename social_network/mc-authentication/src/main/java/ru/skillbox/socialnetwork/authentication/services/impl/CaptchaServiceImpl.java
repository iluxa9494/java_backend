package ru.skillbox.socialnetwork.authentication.services.impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.authentication.entities.Captcha;
import ru.skillbox.socialnetwork.authentication.repositories.CaptchaRepository;
import ru.skillbox.socialnetwork.authentication.services.CaptchaService;
import ru.skillbox.socialnetwork.authentication.web.models.CaptchaDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaRepository captchaRepository;

    private final Cage cage = new GCage();

    @Override
    public CaptchaDto generateCaptcha() {
        String code = generateCode();
        String secret = UUID.randomUUID().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            cage.draw(code, baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String imageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        captchaRepository.save(new Captcha(secret, code, Instant.now().plusSeconds(300)));
        return CaptchaDto.builder()
                .secret(secret)
                .image("data:image/png;base64," + imageBase64)
                .build();
    }

    @Override
    public boolean validateCaptcha(String secret, String answer) {
        return captchaRepository.findById(secret)
                .filter(c -> c.getExpiresAt().isAfter(Instant.now()))
                .filter(c -> c.getCode().equalsIgnoreCase(answer))
                .map(c -> {
                    captchaRepository.delete(c);
                    return true;
                })
                .orElse(false);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 5);
    }
}