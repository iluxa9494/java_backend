package com.skillbox.cryptobot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoBotApplication {
    private static final Logger log = LoggerFactory.getLogger(CryptoBotApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CryptoBotApplication.class, args);
    }

    @Bean
    public ApplicationRunner logDatasource(Environment environment) {
        return args -> {
            String jdbcUrl = environment.getProperty("spring.datasource.url");
            if (jdbcUrl == null || jdbcUrl.isBlank()) {
                log.warn("Datasource JDBC URL is not set");
                return;
            }
            log.info("Datasource JDBC host/port: {}", extractHostPort(jdbcUrl));
            log.info("Datasource JDBC URL: {}", jdbcUrl);
        };
    }

    private static String extractHostPort(String jdbcUrl) {
        String prefix = "jdbc:postgresql://";
        String normalized = jdbcUrl.startsWith(prefix) ? jdbcUrl.substring(prefix.length()) : jdbcUrl;
        int slashIndex = normalized.indexOf('/');
        String hostPort = slashIndex >= 0 ? normalized.substring(0, slashIndex) : normalized;
        int queryIndex = hostPort.indexOf('?');
        return queryIndex >= 0 ? hostPort.substring(0, queryIndex) : hostPort;
    }
}
