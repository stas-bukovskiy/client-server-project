package org.goodstorage.initilizers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@RequiredArgsConstructor
public class TableInitializer {

    private static final String SCHEMA_FILE = "/schema.sql";

    private final Connection connection;


    public void initialize() throws IOException {
        log.info("Initializing tables from <{}> file...", SCHEMA_FILE);
        try (InputStream inputStream = getClass().getResourceAsStream(SCHEMA_FILE)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 Statement statement = connection.createStatement()) {

                StringBuilder sqlBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line);
                    if (line.trim().endsWith(";")) {
                        String sqlStatement = sqlBuilder.toString().trim();
                        statement.executeUpdate(sqlStatement);
                        sqlBuilder.setLength(0);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Tables are initialized");
    }
}
