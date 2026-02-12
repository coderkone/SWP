  //package config;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//
//public class DBContext {
//
//    // sửa đúng máy bạn
//    private static final String URL =
//        "jdbc:sqlserver://localhost:1433;databaseName=devquery;encrypt=true;trustServerCertificate=true;";
//    private static final String USER = "sa";
//    private static final String PASS = "123";
//
//    public static Connection getConnection() throws Exception {
//        return DriverManager.getConnection(URL, USER, PASS);
//    }
//}

 package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {

    // SỬA user/pass theo SQL Server của bạn
     private static final String USER = "huylq";
     private static final String PASS = "123";
 
    // Nếu bạn dùng instance MSSQLSERVER + port 1433 như ảnh
    private static final String URL =
             "jdbc:sqlserver://localhost:1433;databaseName=devquery;encrypt=true;trustServerCertificate=true;";
 
    public Connection getConnection() throws Exception {
        // Quan trọng để tránh lỗi: No suitable driver found
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
