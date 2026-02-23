package config;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBContext {

    private static final String USER;
    private static final String PASS;
    private static final String URL;

    static {
        String user = "huylq";
        String pass = "123";
        
        try {
            // Cố gắng đọc từ DBconfig.properties
            String configPath = System.getProperty("user.dir") + File.separator + "DBconfig.properties";
            File configFile = new File(configPath);
            
            System.out.println("DEBUG: Looking for config at: " + configPath);
            System.out.println("DEBUG: File exists: " + configFile.exists());
            
            // Nếu không tìm thấy, thử cách khác
            if (!configFile.exists()) {
                // Thử đường dẫn tuyệt đối Windows
                configPath = "C:\\Users\\VIVOBOOK\\Downloads\\code-rabbit\\DBconfig.properties";
                configFile = new File(configPath);
                System.out.println("DEBUG: Trying absolute path: " + configPath);
                System.out.println("DEBUG: File exists: " + configFile.exists());
            }
            
            if (configFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    user = props.getProperty("db.user", "huylq").trim();
                    pass = props.getProperty("db.password", "123").trim();
                    System.out.println("DEBUG: Loaded from properties - user: " + user);
                }
            } else {
                System.out.println("DEBUG: Config file not found, using hardcoded defaults - user: huylq");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load DBconfig.properties, using defaults. Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        USER = user;
        PASS = pass;
        URL = "jdbc:sqlserver://localhost:1433;databaseName=devquery;encrypt=true;trustServerCertificate=true;";
        
        System.out.println("DEBUG: DBContext initialized - URL: " + URL + ", USER: " + USER);
    }

    public Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        System.out.println("DEBUG: Attempting connection with user: " + USER);
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

