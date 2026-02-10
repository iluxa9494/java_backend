package ru.skillbox.socialnetwork.post.it;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.skillbox.socialnetwork.post.entity.PostEntity;
import ru.skillbox.socialnetwork.post.repository.PostRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Testcontainers
@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.liquibase.enabled=true"
})
class PostRepositoryIT {

    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("social_network")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        pg.start();
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired PostRepository postRepository;

    @Test
    void saveAndFind() {
        var p = new PostEntity();
        p.setId(UUID.randomUUID());
        p.setAuthorId(UUID.randomUUID());
        p.setTitle("t");
        p.setPostText("x");
        p.setTime(OffsetDateTime.now());
        p.setTimeChanged(p.getTime());
        p.setDeleted(false);
        p.setBlocked(false);
        p.setLikeAmount(0L);
        p.setCommentsCount(0L);

        postRepository.save(p);

        var found = postRepository.findById(p.getId()).orElseThrow();
        Assertions.assertEquals("t", found.getTitle());
    }
}