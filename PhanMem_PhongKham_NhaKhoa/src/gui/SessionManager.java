package gui;

import entity.TaiKhoan;

/**
 * Quản lý phiên làm việc của người dùng đang đăng nhập trong toàn hệ thống
 */
public class SessionManager {

    private static TaiKhoan currentUser = new TaiKhoan("admin", "123456", null, "Hoạt động", 0, "QL001", "VT_QL");

    static {
        currentUser.setHoTenNV("Trần Thị Hoàng Lan");
        currentUser.setTenVaiTro("Quản lý");
    }

    public static TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(TaiKhoan user) {
        if (user != null) {
            currentUser = user;
        }
    }

    public static String getMaNVHienTai() {
        if (currentUser != null && currentUser.getMaNV() != null && !currentUser.getMaNV().isEmpty()) {
            return currentUser.getMaNV();
        }
        return "QL001";
    }

    public static String getTenDangNhapHienTai() {
        if (currentUser != null && currentUser.getTenDangNhap() != null && !currentUser.getTenDangNhap().isEmpty()) {
            return currentUser.getTenDangNhap();
        }
        return "admin";
    }

    public static String getTenVaiTroHienTai() {
        if (currentUser != null && currentUser.getTenVaiTro() != null) {
            return currentUser.getTenVaiTro();
        }
        return "Quản lý";
    }

    public static boolean isQuanLy() {
        String role = getTenVaiTroHienTai().toLowerCase();
        return role.contains("quản lý") || role.contains("admin");
    }

    public static boolean isNhaSi() {
        String role = getTenVaiTroHienTai().toLowerCase();
        return role.contains("nha sĩ") || role.contains("bác sĩ");
    }

    public static boolean isLeTan() {
        String role = getTenVaiTroHienTai().toLowerCase();
        return role.contains("lễ tân") || role.contains("tiếp tân");
    }

    public static boolean isThuNgan() {
        String role = getTenVaiTroHienTai().toLowerCase();
        return role.contains("thu ngân");
    }

    public static boolean isYTa() {
        String role = getTenVaiTroHienTai().toLowerCase();
        return role.contains("y tá") || role.contains("phụ tá");
    }
}
