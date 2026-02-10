package ru.skillbox.socialnetwork.post.infra;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = LiquibaseIT.Init.class)
class LiquibaseIT {

    static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15-alpine")
    ).withDatabaseName("social")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        PG.start();
    }

    public static class Init implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + PG.getJdbcUrl(),
                    "spring.datasource.username=" + PG.getUsername(),
                    "spring.datasource.password=" + PG.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=none"
            ).applyTo(ctx.getEnvironment());
        }
    }

    @Resource
    JdbcTemplate jdbc;

    @Test
    void liquibase_shouldCreateTables() {
        jdbc.queryForObject("select 1 from post limit 1", Integer.class);
        jdbc.queryForObject("select 1 from comment limit 1", Integer.class);
        jdbc.queryForObject("select 1 from reaction limit 1", Integer.class);
    }
}