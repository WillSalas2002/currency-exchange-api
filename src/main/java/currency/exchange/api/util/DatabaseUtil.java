package currency.exchange.api.util;

import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static final SQLiteDataSource DATA_SOURCE = new SQLiteDataSource();

    static {
        Properties properties = loadProperties("database.properties");
        String databaseUrl = properties.getProperty("database_url");
        DATA_SOURCE.setUrl(databaseUrl);
    }

    public static Connection getConnection() {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    private DatabaseUtil() {
    }

    private static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
            } else {
                LOGGER.log(Level.SEVERE, "File not found " + fileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error ", e);
        }
        return properties;
    }
}