package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBContext {

    private static final String USER;
    private static final String PASS;
    private static final String URL;

    static {
        // Priority: Environment Variables > DBconfig.properties > Exception
        String user = null;
        String pass = null;
        String host = null;
        String port = null;
        String dbName = null;
        
        try {
            // Step 1: Try environment variables (highest priority)
            user = System.getenv("DB_USER");
            pass = System.getenv("DB_PASSWORD");
            host = System.getenv("DB_SERVER_HOST");
            port = System.getenv("DB_SERVER_PORT");
            dbName = System.getenv("DB_NAME");
            
            // Step 2: Try DBconfig.properties from several portable locations
            Properties props = new Properties();

            // 2a: Classpath resource (e.g. WEB-INF/classes/DBconfig.properties)
            if (user == null || pass == null || host == null) {
                try (InputStream is = DBContext.class.getClassLoader().getResourceAsStream("DBconfig.properties")) {
                    if (is != null) {
                        System.out.println("DEBUG: Found DBconfig.properties on classpath");
                        props.load(is);
                    }
                } catch (Exception ignored) {
                    // Fallbacks below will handle missing config
                }
            }

            // 2b: File next to the deployed classes directory (portable across context paths)
            if ((user == null || pass == null || host == null) && props.isEmpty()) {
                try {
                    File codeSourceDir = new File(DBContext.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());
                    // Typically: .../WEB-INF/classes/ -> go up twice to webapp root
                    File webAppRoot = codeSourceDir.getParentFile() != null
                            ? codeSourceDir.getParentFile().getParentFile()
                            : null;
                    if (webAppRoot != null) {
                        File configFile = new File(webAppRoot, "DBconfig.properties");
                        System.out.println("DEBUG: Looking for config near webapp root at: " + configFile.getAbsolutePath());
                        if (configFile.exists()) {
                            try (FileInputStream fis = new FileInputStream(configFile)) {
                                props.load(fis);
                            }
                        }
                    }
                } catch (Exception ignored) {
                    // Fallbacks below will handle missing config
                }
            }

            // 2c: Fallback to JVM working directory (original behaviour)
            if ((user == null || pass == null || host == null) && props.isEmpty()) {
                String configPath = System.getProperty("user.dir") + File.separator + "DBconfig.properties";
                File configFile = new File(configPath);

                System.out.println("DEBUG: Looking for config at: " + configPath);
                System.out.println("DEBUG: File exists: " + configFile.exists());

                if (configFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        props.load(fis);
                    }
                }
            }

            // Apply properties if any were loaded
            if (!props.isEmpty()) {
                if (user == null) user = props.getProperty("db.user");
                if (pass == null) pass = props.getProperty("db.password");
                if (host == null) host = props.getProperty("db.host");
                if (port == null) port = props.getProperty("db.port");
                if (dbName == null) dbName = props.getProperty("db.name");
                System.out.println("DEBUG: Loaded database config from properties file");
            }
            
            // Step 3: Apply defaults for localhost development (if still null)
            if (host == null) host = "localhost";
            if (port == null) port = "1433";
            if (dbName == null) dbName = "devquery";
            
            // Step 4: Validate required credentials are present
            if (user == null || pass == null) {
                throw new IllegalStateException(
                    "Database credentials not found. Please set DB_USER and DB_PASSWORD " +
                    "environment variables or create DBconfig.properties with db.user and db.password properties."
                );
            }
            
            System.out.println("DEBUG: DBContext - Using DB server: " + host + ":" + port + "/" + dbName);
            System.out.println("DEBUG: DBContext - Using DB user: " + user);
            
        } catch (Exception e) {
            System.err.println("ERROR: Database configuration failed. Error: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
        
        USER = user;
        PASS = pass;
        URL = String.format(
            "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;",
            host, port, dbName
        );
        
        System.out.println("DEBUG: DBContext initialized - URL: " + URL + ", USER: " + USER);
    }

    public Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        System.out.println("DEBUG: Attempting connection with user: " + USER);
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

