package com.pisethjavaschool.userservice.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class LiquibaseJdbcConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties liquibaseDataSourceProperties() {
    	System.out.print("Liquibase DataSource bean created");
        return new DataSourceProperties();
    }

    @Bean
    DataSource liquibaseDataSource(DataSourceProperties liquibaseDataSourceProperties) {
        return liquibaseDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
