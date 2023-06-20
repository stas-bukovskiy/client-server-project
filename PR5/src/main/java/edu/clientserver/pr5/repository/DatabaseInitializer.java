package edu.clientserver.pr5.repository;

import edu.clientserver.pr5.connection.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void createTables() throws SQLException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            assert connection != null;
            try (Statement statement = connection.createStatement()) {
                String createGoodTableSql = """
                        CREATE TABLE IF NOT EXISTS good (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        quantity INTEGER NOT NULL,
                        price NUMERIC(10, 2) NOT NULL
                        )
                        """;
                statement.executeUpdate(createGoodTableSql);
                log.info("table 'good' is created");

                String createUserTableSql = """
                        CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(1000) NOT NULL
                        )
                        """;
                statement.executeUpdate(createUserTableSql);
                log.info("table 'users' is created");
            }
        }
    }


    public static void dropTables() throws SQLException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            assert connection != null;
            try (Statement statement = connection.createStatement()) {
                String createGoodTableSql = """
                        DROP TABLE IF  EXISTS good;
                        """;
                statement.executeUpdate(createGoodTableSql);
                log.info("table 'good' is dropped");

                String createUserTableSql = """
                        DROP TABLE IF EXISTS users;
                        """;
                statement.executeUpdate(createUserTableSql);
                log.info("table 'good' is dropped");
            }
        }
    }

}
