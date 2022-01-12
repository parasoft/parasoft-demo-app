package com.parasoft.demoapp.config.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceConfigurationProperties.Global.class)
@EnableJpaRepositories(
        entityManagerFactoryRef = "globalEntityManager",
        transactionManagerRef = "globalTransactionManager",
        basePackages = {"com.parasoft.demoapp.repository.global"}
)
public class GlobalDataSourceConfig {
    @Autowired
    DataSourceConfigurationProperties.Global dataSourceConfigurationProperties;

    @Primary
    @Bean(name = "globalDataSource")
    public DataSource getGlobalDataSource() {
        return dataSourceConfigurationProperties.createTargetDataSources();
    }

    @Primary
    @Bean(name = "globalEntityManager")
    public LocalContainerEntityManagerFactoryBean globalEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(getGlobalDataSource())
                .packages("com.parasoft.demoapp.model.global")
                .persistenceUnit("global_PU")
                .build();
    }

    @Primary
    @Bean(name = "globalTransactionManager")
    public PlatformTransactionManager globalTransactionManager(
            @Qualifier("globalEntityManager") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
