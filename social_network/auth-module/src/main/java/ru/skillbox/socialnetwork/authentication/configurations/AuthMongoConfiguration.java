package ru.skillbox.socialnetwork.authentication.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ru.skillbox.socialnetwork.authentication.repositories.CaptchaRepository;

@Configuration
@EnableMongoRepositories(basePackageClasses = CaptchaRepository.class)
public class AuthMongoConfiguration {
}
