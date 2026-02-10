package ru.skillbox.socialnetwork.app.config.datasource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.skillbox.socialnetwork.friend",
        entityManagerFactoryRef = "friendEntityManagerFactory",
        transactionManagerRef = "friendTransactionManager"
)
public class FriendDataSourceConfig {

    @Bean
    @ConfigurationProperties("social.friend.datasource")
    public DataSourceProperties friendDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource friendDataSource(
            @Qualifier("friendDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean friendEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("friendDataSource") DataSource dataSource,
            @Value("${social.friend.datasource.schema:public}") String schema) {
        return builder
                .dataSource(dataSource)
                .packages("ru.skillbox.socialnetwork.friend")
                .persistenceUnit("friend")
                .properties(java.util.Map.of("hibernate.default_schema", schema))
                .build();
    }

    @Bean
    public JpaTransactionManager friendTransactionManager(
            @Qualifier("friendEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
