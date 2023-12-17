package currency_exchange_api.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection getConnection() {

        Connection connection = null;
        try {

            URL jdbcUrl = DatabaseUtil.class.getClassLoader().getResource("database.sqlite");
            Class.forName("org.sqlite.JDBC");
            String path = new File(jdbcUrl.toURI()).getAbsolutePath();
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();

        } catch (URISyntaxException e) {
            e.getMessage();
        }

        return connection;
    }
}