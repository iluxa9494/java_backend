package ru.skillbox.socialnetwork.authentication.services.impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.authentication.entities.Captcha;
import ru.skillbox.socialnetwork.authentication.repositories.CaptchaRepository;
import ru.skillbox.socialnetwork.authentication.services.CaptchaService;
import ru.skillbox.socialnetwork.authentication.web.models.CaptchaDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaRepository captchaRepository;

    private final Cage cage = new GCage();
    private final Map<String, Captcha> inMemoryCaptchas = new ConcurrentHashMap<>();

    @Override
    public CaptchaDto generateCaptcha() {
        String code = generateCode();
        String secret = UUID.randomUUID().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            cage.draw(code, baos);
        } catch (IOException e) {
            log.error("Failed to draw captcha image", e);
            throw new RuntimeException("Captcha generation failed", e);
        }

        String imageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        Captcha captcha = new Captcha(secret, code, Instant.now().plusSeconds(300));
        try {
            captchaRepository.save(captcha);
        } catch (Exception ex) {
            log.warn("Captcha repository unavailable, falling back to in-memory store", ex);
            storeInMemory(captcha);
        }
        return CaptchaDto.builder()
                .secret(secret)
                .image("data:image/png;base64," + imageBase64)
                .build();
    }

    @Override
    public boolean validateCaptcha(String secret, String answer) {
        if (secret == null || answer == null) {
            return false;
        }

        try {
            return captchaRepository.findById(secret)
                    .filter(c -> c.getExpiresAt().isAfter(Instant.now()))
                    .filter(c -> c.getCode().equalsIgnoreCase(answer))
                    .map(c -> {
                        captchaRepository.delete(c);
                        return true;
                    })
                    .orElse(false);
        } catch (Exception ex) {
            log.warn("Captcha repository unavailable, using in-memory validation", ex);
            return validateInMemory(secret, answer);
        }
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 5);
    }

    private void storeInMemory(Captcha captcha) {
        purgeExpired();
        inMemoryCaptchas.put(captcha.getId(), captcha);
    }

    private boolean validateInMemory(String secret, String answer) {
        purgeExpired();
        Optional<Captcha> captchaOpt = Optional.ofNullable(inMemoryCaptchas.get(secret));
        if (captchaOpt.isEmpty()) {
            return false;
        }
        Captcha captcha = captchaOpt.get();
        if (!captcha.getExpiresAt().isAfter(Instant.now())) {
            inMemoryCaptchas.remove(secret);
            return false;
        }
        if (!captcha.getCode().equalsIgnoreCase(answer)) {
            return false;
        }
        inMemoryCaptchas.remove(secret);
        return true;
    }

    private void purgeExpired() {
        if (inMemoryCaptchas.isEmpty()) {
            return;
        }
        Instant now = Instant.now();
        Iterator<Map.Entry<String, Captcha>> iterator = inMemoryCaptchas.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Captcha> entry = iterator.next();
            Captcha captcha = entry.getValue();
            if (captcha == null || !captcha.getExpiresAt().isAfter(now)) {
                iterator.remove();
            }
        }
    }
}
