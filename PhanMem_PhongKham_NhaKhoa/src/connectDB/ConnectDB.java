package connectDB;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {

    private static Connection conn = null;
    private static String lastError = null;
    private static long lastFailedTime = 0;
    private static final long RETRY_COOLDOWN_MS = 3000;
    private static boolean loggedOfflineOnce = false;

    public static boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static Connection getConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                return conn;
            }

            // Circuit breaker: nếu vừa thất bại cách đây chưa tới 3s thì trả về null tránh
            // chậm UI
            if (System.currentTimeMillis() - lastFailedTime < RETRY_COOLDOWN_MS) {
                return null;
            }

            // 1. Kiểm tra nhanh socket cổng 1433 (chỉ mất ~5-20ms nếu mở, hoặc 250ms nếu
            // tắt)
            boolean portOpen = false;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("127.0.0.1", 1433), 250);
                portOpen = true;
            } catch (Exception ignored) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("localhost", 1433), 250);
                    portOpen = true;
                } catch (Exception ex2) {
                    portOpen = false;
                }
            }

            if (!portOpen) {
                lastFailedTime = System.currentTimeMillis();
                lastError = "Kết nối SQL Server thất bại: Cổng 1433 chưa mở hoặc dịch vụ SQL Server chưa khởi chạy.";
                if (!loggedOfflineOnce) {
                    System.err.println("[ConnectDB] " + lastError);
                    loggedOfflineOnce = true;
                }
                return null;
            }

            // 2. Cổng 1433 mở -> Thử kết nối với các mật khẩu sa (ưu tiên 123 và 123456)
            DriverManager.setLoginTimeout(3);
            String[] commonPasswords = { "123", "123456", "sa", "admin", "root", "1234" };
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLPHONGKHAM;encrypt=false;trustServerCertificate=true;loginTimeout=3";
            String user = "sa";

            for (String pwd : commonPasswords) {
                try {
                    conn = DriverManager.getConnection(url, user, pwd);
                    if (conn != null && !conn.isClosed()) {
                        lastError = null;
                        lastFailedTime = 0;
                        loggedOfflineOnce = false;
                        System.out.println("Kết nối SQL Server thành công!");
                        return conn;
                    }
                } catch (Exception ignored) {
                }
            }

            // 3. Thử Windows Authentication (integratedSecurity) nếu SQL Auth không khớp
            try {
                String winAuthUrl = "jdbc:sqlserver://localhost:1433;databaseName=QLPHONGKHAM;integratedSecurity=true;encrypt=false;trustServerCertificate=true;loginTimeout=3";
                conn = DriverManager.getConnection(winAuthUrl);
                if (conn != null && !conn.isClosed()) {
                    lastError = null;
                    lastFailedTime = 0;
                    loggedOfflineOnce = false;
                    System.out.println(
                            "Kết nối SQL Server thành công qua Windows Authentication!");
                    return conn;
                }
            } catch (Exception ignored) {
            }

            lastFailedTime = System.currentTimeMillis();
            lastError = "Kết nối SQL Server thất bại: Không thể kết nối tới cơ sở dữ liệu QLPHONGKHAM.";
            if (!loggedOfflineOnce) {
                System.err.println("[ConnectDB] " + lastError);
                loggedOfflineOnce = true;
            }

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
            } catch (Exception ignored) {
            }
            conn = null;
        }
    }

    public static String getLastError() {
        return lastError != null ? lastError : "Lỗi kết nối!";
    }
}
