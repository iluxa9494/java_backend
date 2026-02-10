package ru.skillbox.socialnetwork.app.config.liquibase;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostLiquibaseConfig {

    @Bean
    public SpringLiquibase postLiquibase(
            @Qualifier("postDataSource") DataSource postDataSource,
            @Value("${social.post.liquibase.change-log:classpath:db/changelog/post/db.changelog-master.xml}")
            String changeLog,
            @Value("${social.post.liquibase.enabled:true}") boolean enabled,
            @Value("${social.post.datasource.schema:public}") String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(postDataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(schema);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
