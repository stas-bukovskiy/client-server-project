package edu.clientserver.inittilzer;

import edu.clientserver.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void createTables() throws SQLException {
        try (Connection connection = ConnectionFactory.getConnection()) {
            assert connection != null;
            try (Statement statement = connection.createStatement()) {
                String createGroupTableSql = """
                        CREATE TABLE IF NOT EXISTS good_group (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
                        )
                        """;
                statement.executeUpdate(createGroupTableSql);

                String createGoodTableSql = """
                        CREATE TABLE IF NOT EXISTS good (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        quantity INTEGER NOT NULL,
                        price NUMERIC(10, 2) NOT NULL,
                        group_id INTEGER REFERENCES good_group(id) ON DELETE CASCADE
                        )
                        """;
                statement.executeUpdate(createGoodTableSql);
            }
        }
    }

}
