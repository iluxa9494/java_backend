package ru.skillbox.socialnetwork.app.config.liquibase;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FriendLiquibaseConfig {

    @Bean
    public SpringLiquibase friendLiquibase(
            @Qualifier("friendDataSource") DataSource friendDataSource,
            @Value("${social.friend.liquibase.change-log:classpath:db/changelog/changelog-master.yaml}")
            String changeLog,
            @Value("${social.friend.liquibase.enabled:true}") boolean enabled,
            @Value("${social.friend.datasource.schema:public}") String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(friendDataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(schema);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
