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
        basePackages = "ru.skillbox.socialnetwork.dialog",
        entityManagerFactoryRef = "dialogEntityManagerFactory",
        transactionManagerRef = "dialogTransactionManager"
)
public class DialogDataSourceConfig {

    @Bean
    @ConfigurationProperties("social.dialog.datasource")
    public DataSourceProperties dialogDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dialogDataSource(
            @Qualifier("dialogDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dialogEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dialogDataSource") DataSource dataSource,
            @Value("${social.dialog.datasource.schema:public}") String schema) {
        return builder
                .dataSource(dataSource)
                .packages("ru.skillbox.socialnetwork.dialog")
                .persistenceUnit("dialog")
                .properties(java.util.Map.of("hibernate.default_schema", schema))
                .build();
    }

    @Bean
    public JpaTransactionManager dialogTransactionManager(
            @Qualifier("dialogEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
