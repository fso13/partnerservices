package ru.drudenko.partnerservices;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class TestConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfiguration.class);

    @Bean(name = "testDataSource")
    public DataSource customDataSource() {
        final String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;INIT=create schema IF NOT EXISTS PUBLIC";

        LOGGER.info("Creating in-memory h2 database with jdbc url [{}]", url);
        return DataSourceBuilder
                .create()
                .driverClassName(EmbeddedDatabaseConnection.H2.getDriverClassName())
                .url(url)
                .username("sa")
                .password("")
                .build();

    }

    @Bean(name = "dbUnitConnectionTransport")
    public DatabaseDataSourceConnection customDatabaseDataSourceConnection(@Autowired DataSource testDataSource) throws SQLException {
        return new DatabaseDataSourceConnection(testDataSource);
    }
}
