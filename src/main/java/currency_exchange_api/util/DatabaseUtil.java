package currency_exchange_api.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:sqlite:C:\\Program Files\\JetBrains\\java-projects\\pet-projects\\currency-exchange-api\\src\\main\\resources\\database.sqlite";
    public static Connection getConnection() {

        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(JDBC_URL);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return connection;
    }
}