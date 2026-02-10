package ru.skillbox.socialnetwork.app.config.liquibase;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthLiquibaseConfig {

    @Bean
    public SpringLiquibase authLiquibase(
            @Qualifier("authDataSource") DataSource authDataSource,
            @Value("${social.auth.liquibase.change-log:classpath:db/changelog/auth/db.changelog-master.xml}")
            String changeLog,
            @Value("${social.auth.liquibase.enabled:true}") boolean enabled,
            @Value("${social.auth.datasource.schema:public}") String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(authDataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(schema);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
