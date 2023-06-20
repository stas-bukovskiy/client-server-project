package edu.clientserver.pr5.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimeZone;

public final class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres?connectionTimeZone=UTC";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";


    private ConnectionFactory() {
    }


    public static Connection getConnection() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
