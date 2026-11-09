package connectDB;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Tiện ích mã hóa băm mật khẩu bằng thuật toán SHA-256 tiêu chuẩn.
 * Đảm bảo mật khẩu không được lưu trữ ở dạng văn bản thô (plain-text).
 */
public class PasswordUtils {

    /**
     * Mã hóa băm một chuỗi mật khẩu sang chuỗi Hex SHA-256 (64 ký tự).
     */
    public static String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("PasswordUtils.hashPassword error: " + e.getMessage());
            return password;
        }
    }

    /**
     * Kiểm tra mật khẩu người dùng nhập có khớp với mật khẩu lưu trong CSDL hay không.
     * Hỗ trợ cả trường hợp lưu dạng SHA-256 và trường hợp văn bản thô cũ.
     */
    public static boolean checkPassword(String rawPassword, String storedPasswordOrHash) {
        if (rawPassword == null || storedPasswordOrHash == null) return false;
        String trimmedStored = storedPasswordOrHash.trim();
        // 1. So khớp trực tiếp (nếu CSDL cũ lưu thô)
        if (rawPassword.equals(trimmedStored)) {
            return true;
        }
        // 2. So khớp chuỗi băm SHA-256
        String rawHashed = hashPassword(rawPassword);
        return rawHashed.equalsIgnoreCase(trimmedStored);
    }

    /**
     * Kiểm tra xem mật khẩu có phải là mật khẩu mặc định khởi tạo "123" hay không.
     */
    public static boolean isDefaultPassword(String password) {
        if (password == null) return false;
        String clean = password.trim();
        if ("123".equals(clean) || "123456".equals(clean)) {
            return true;
        }
        String hash123 = hashPassword("123");
        String hash123456 = hashPassword("123456");
        return hash123.equalsIgnoreCase(clean) || hash123456.equalsIgnoreCase(clean);
    }
}

