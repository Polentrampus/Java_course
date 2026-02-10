package hotel.util;

import hotel.config.PropertiesConfiguration;
import hotel.exception.ConnectException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.user";
    private static final String PASSWORD_KEY = "db.password";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    PropertiesConfiguration.getProperty(URL_KEY),
                    PropertiesConfiguration.getProperty(USER_KEY),
                    PropertiesConfiguration.getProperty(PASSWORD_KEY)
            );
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new ConnectException(e);
        }
    }
}
