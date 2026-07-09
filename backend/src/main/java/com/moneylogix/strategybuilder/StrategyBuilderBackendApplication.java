package com.moneylogix.strategybuilder;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class StrategyBuilderBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(StrategyBuilderBackendApplication.class, args);
    }

    @Configuration
    static class FlywayConfig {
        @Value("${flyway.custom.locations:classpath:db/migration}")
        private String flywayLocations;

        @Value("${flyway.custom.baseline-version:5}")
        private String baselineVersion;

        @Bean(initMethod = "migrate")
        public Flyway flyway(DataSource dataSource) {
            return Flyway.configure()
                    .dataSource(dataSource)
                    .locations(flywayLocations)
                    .baselineOnMigrate(true)
                    .baselineVersion(baselineVersion)
                    .load();
        }
    }
}