package ru.skillbox.socialnetwork.app.config.liquibase;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountLiquibaseConfig {

    @Bean
    public SpringLiquibase accountLiquibase(
            @Qualifier("accountDataSource") DataSource accountDataSource,
            @Value("${social.account.liquibase.change-log:classpath:db/changelog/account/db.changelog-master.yaml}")
            String changeLog,
            @Value("${social.account.liquibase.enabled:true}") boolean enabled,
            @Value("${social.account.datasource.schema:public}") String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(accountDataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(schema);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
