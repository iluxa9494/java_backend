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
        basePackages = "ru.skillbox.socialnetwork.post",
        entityManagerFactoryRef = "postEntityManagerFactory",
        transactionManagerRef = "postTransactionManager"
)
public class PostDataSourceConfig {

    @Bean
    @ConfigurationProperties("social.post.datasource")
    public DataSourceProperties postDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource postDataSource(
            @Qualifier("postDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean postEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("postDataSource") DataSource dataSource,
            @Value("${social.post.datasource.schema:public}") String schema) {
        return builder
                .dataSource(dataSource)
                .packages("ru.skillbox.socialnetwork.post")
                .persistenceUnit("post")
                .properties(java.util.Map.of("hibernate.default_schema", schema))
                .build();
    }

    @Bean
    public JpaTransactionManager postTransactionManager(
            @Qualifier("postEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
