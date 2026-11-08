package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectDB {

    private static Connection conn;
    private static String lastError;
    private static long lastFailedTime;
    private static final long RETRY_COOLDOWN_MS = 10000;

    private ConnectDB() {
    }

    public static synchronized Connection getConnection() {
        try {
            if (conn != null && !conn.isClosed()) return conn;
        } catch (SQLException ex) {
            conn = null;
        }

        if (System.currentTimeMillis() - lastFailedTime < RETRY_COOLDOWN_MS) return null;

        String server = env("SQL_SERVER", "localhost");
        String port = env("SQL_PORT", "1433");
        String database = env("SQL_DATABASE", "QLPHONGKHAM");
        String user = System.getenv("SQL_USER");
        String password = System.getenv("SQL_PASSWORD");

        if (user == null || user.trim().isEmpty() || password == null) {
            lastError = "Thiếu thông tin đăng nhập SQL Server. Hãy cấu hình SQL_USER và SQL_PASSWORD "
                    + "trong biến môi trường Windows rồi khởi động lại ứng dụng.";
            lastFailedTime = System.currentTimeMillis();
            return null;
        }

        String url = "jdbc:sqlserver://" + server + ":" + port
                + ";databaseName=" + database
                + ";encrypt=false;trustServerCertificate=true;loginTimeout=5";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Properties credentials = new Properties();
            credentials.setProperty("user", user);
            credentials.setProperty("password", password);
            DriverManager.setLoginTimeout(5);
            conn = DriverManager.getConnection(url, credentials);
            lastError = null;
            lastFailedTime = 0;
            return conn;
        } catch (ClassNotFoundException ex) {
            lastError = "Không tìm thấy Microsoft SQL Server JDBC Driver trong classpath.";
        } catch (SQLException ex) {
            lastError = "Kết nối SQL Server thất bại tại " + server + ":" + port + "/" + database
                    + ". " + ex.getMessage();
        }

        conn = null;
        lastFailedTime = System.currentTimeMillis();
        System.err.println("[ConnectDB] " + lastError);
        return null;
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    public static synchronized void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
            conn = null;
        }
    }

    public static String getLastError() {
        return lastError != null ? lastError : "Lỗi kết nối hoặc thao tác cơ sở dữ liệu.";
    }
}
