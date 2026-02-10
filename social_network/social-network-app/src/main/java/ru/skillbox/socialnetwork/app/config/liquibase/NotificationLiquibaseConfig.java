package ru.skillbox.socialnetwork.app.config.liquibase;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationLiquibaseConfig {

    @Bean
    public SpringLiquibase notificationLiquibase(
            @Qualifier("notificationDataSource") DataSource notificationDataSource,
            @Value("${social.notification.liquibase.change-log:classpath:db/changelog/notification/db.changelog-master.yaml}")
            String changeLog,
            @Value("${social.notification.liquibase.enabled:true}") boolean enabled,
            @Value("${social.notification.datasource.schema:public}") String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(notificationDataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(schema);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
