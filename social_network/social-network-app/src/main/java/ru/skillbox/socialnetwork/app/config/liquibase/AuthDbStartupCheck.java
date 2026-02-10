package ru.skillbox.socialnetwork.app.config.liquibase;

import java.util.Map;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AuthDbStartupCheck {
    private static final Logger log = LoggerFactory.getLogger(AuthDbStartupCheck.class);

    @Bean
    public ApplicationRunner authDbInfoRunner(@Qualifier("authDataSource") DataSource authDataSource) {
        return args -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(authDataSource);
            Map<String, Object> row = jdbcTemplate.queryForMap("select current_database() as db, current_schema() as schema");
            log.info("[auth-db] connected db={}, schema={}", row.get("db"), row.get("schema"));
        };
    }
}
