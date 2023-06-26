package org.goodstorage.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

import static java.util.Collections.singletonMap;

@Testcontainers
public class AbstractContainerDatabaseTest {

    @Container
    static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("product_db")
                    .withUsername("user")
                    .withPassword("password")
                    .withInitScript("schema.sql")
                    .withTmpFs(singletonMap("/var/lib/postgresql/data", "rw"));
    static DataSource datasource;
    static Connection connection;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeAll
    static void init() throws SQLException {
        var config = new HikariConfig();
        var jdbcContainer = (JdbcDatabaseContainer<?>) container;
        config.setJdbcUrl(jdbcContainer.getJdbcUrl());
        config.setUsername(jdbcContainer.getUsername());
        config.setPassword(jdbcContainer.getPassword());
        config.setDriverClassName(jdbcContainer.getDriverClassName());
        datasource = new HikariDataSource(config);
        connection = datasource.getConnection();
    }

    @AfterAll
    static void close() throws SQLException {
        connection.close();
        container.close();
    }

}
