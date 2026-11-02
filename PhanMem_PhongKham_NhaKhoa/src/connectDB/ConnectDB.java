package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {

    private static Connection conn = null;
    private static String lastError = null;
    private static long lastFailedTime = 0;
    private static final long RETRY_COOLDOWN_MS = 10000; // 10s cooldown before retrying if server is offline

    public static Connection getConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                return conn;
            }

            // Circuit breaker: nếu vừa thất bại cách đây chưa tới 10s thì dùng ngay chế độ offline không đợi timeout lại
            if (System.currentTimeMillis() - lastFailedTime < RETRY_COOLDOWN_MS) {
                return null;
            }

            DriverManager.setLoginTimeout(1);

            // Danh sách các mật khẩu SQL Server sa phổ biến tại môi trường phát triển & học đường
            String[] commonPasswords = {"123", "123456", "sa", "admin", "root", ""};
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLPHONGKHAM;encrypt=false;trustServerCertificate=true;loginTimeout=1";
            String user = "sa";

            for (String pwd : commonPasswords) {
                try {
                    conn = DriverManager.getConnection(url, user, pwd);
                    if (conn != null && !conn.isClosed()) {
                        lastError = null;
                        lastFailedTime = 0;
                        return conn;
                    }
                } catch (Exception ignored) {}
            }

            // Nếu không đăng nhập được bằng các mật khẩu trên, ghi nhận thời điểm thất bại để tránh chậm giao diện
            lastFailedTime = System.currentTimeMillis();
            lastError = "Chưa kết nối được SQL Server (QLPHONGKHAM). Đang hoạt động ở chế độ Demo/Offline.";
            System.err.println("[ConnectDB] " + lastError);
        } catch (Exception e) {
            lastFailedTime = System.currentTimeMillis();
            lastError = e.getMessage();
            System.err.println("Lỗi kết nối CSDL QLPHONGKHAM: " + e.getMessage());
        }
        return null;
    }

    public static void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignored) {}
            conn = null;
        }
    }
    
    public static String getLastError() {
        return lastError != null ? lastError : "Lỗi kết nối hoặc thao tác CSDL";
    }
}
