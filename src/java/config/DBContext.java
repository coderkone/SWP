package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBContext {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "1433";
    private static final String DEFAULT_DB = "devquery2";
    private static final String DEFAULT_USER = "huylq";
    private static final String DEFAULT_PASS = "123";

    private final Properties props = loadDbProperties();

    private Properties loadDbProperties() {
        Properties p = new Properties();
        try (InputStream in = DBContext.class.getClassLoader().getResourceAsStream("DBconfig.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception ignored) {
            // Use fallback defaults when file is not found or unreadable.
        }
        return p;
    }

    private String getProp(String key, String defaultValue) {
        String value = props.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    public Connection getConnection() throws Exception {
        String host = getProp("db.host", DEFAULT_HOST);
        String port = getProp("db.port", DEFAULT_PORT);
        String dbName = getProp("db.name", DEFAULT_DB);
        String user = getProp("db.user", DEFAULT_USER);
        String pass = getProp("db.password", DEFAULT_PASS);

        String url = "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=" + dbName
                + ";encrypt=true;trustServerCertificate=true;loginTimeout=10;";

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, user, pass);
    }
}
