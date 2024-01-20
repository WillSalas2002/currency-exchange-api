package currency_exchange_api.util;

import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {

    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static final String DATABASE_URL = "jdbc:sqlite:C:/Program Files/JetBrains/java-projects/pet-projects/currency-exchange-api/src/main/resources/database.sqlite";
    private static final SQLiteDataSource dataSource = new SQLiteDataSource();

    static {
        dataSource.setUrl(DATABASE_URL);
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    private DatabaseUtil() {
    }
}