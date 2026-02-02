package ru.skillbox.socialnetwork.app.config.liquibase;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DialogLiquibaseConfig {

    @Bean
    public SpringLiquibase dialogLiquibase(
            @Qualifier("dialogDataSource") DataSource dialogDataSource,
            @Value("${social.dialog.liquibase.change-log:classpath:db/changelog/dialog/db.changelog-master.yaml}")
            String changeLog,
            @Value("${social.dialog.liquibase.enabled:true}") boolean enabled,
            @Value("${social.dialog.datasource.schema:public}") String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dialogDataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(schema);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
