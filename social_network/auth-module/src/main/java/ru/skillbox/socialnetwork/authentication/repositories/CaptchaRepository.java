package ru.skillbox.socialnetwork.authentication.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.authentication.entities.Captcha;

@Repository
public interface CaptchaRepository extends MongoRepository<Captcha, String> {
}