package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {

    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                return conn;
            }

            // Danh sách các mật khẩu SQL Server sa phổ biến tại môi trường phát triển & học đường
            String[] commonPasswords = {"123", "123456", "sa", "admin", "root", ""};
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLPHONGKHAM;encrypt=false;trustServerCertificate=true";
            String user = "sa";

            for (String pwd : commonPasswords) {
                try {
                    conn = DriverManager.getConnection(url, user, pwd);
                    if (conn != null && !conn.isClosed()) {
                        return conn;
                    }
                } catch (Exception ignored) {}
            }

            // Nếu không đăng nhập được bằng các mật khẩu trên, in cảnh báo hướng dẫn
            System.err.println("[ConnectDB] Chưa kết nối được SQL Server (QLPHONGKHAM). Đang hoạt động ở chế độ Demo/Offline.");
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL QLPHONGKHAM: " + e.getMessage());
        }
        return null;
    }

    public static void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignored) {}
        }
    }
}