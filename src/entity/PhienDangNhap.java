package entity;

/**
 * Lớp quản lý phiên đăng nhập hiện tại của hệ thống.
 * Lưu trữ đối tượng NhanVien đang đăng nhập để các form và DAO truy xuất:
 * - Xác định quyền hạn truy cập (VaiTro).
 * - Tự động điền nhân viên lập hóa đơn trong PanelThanhToan_LeTan.
 * - Lọc danh sách lịch hẹn khám của bác sĩ hiện tại trong PanelKhamBenh_BacSi.
 */
public class PhienDangNhap {

    private static NhanVien nhanVienHienTai;

    public static NhanVien getNhanVien() {
        return nhanVienHienTai;
    }

    public static void setNhanVien(NhanVien nv) {
        nhanVienHienTai = nv;
    }

    public static VaiTro getVaiTro() {
        return nhanVienHienTai != null ? nhanVienHienTai.getVaiTro() : null;
    }

    public static String getMaNV() {
        return nhanVienHienTai != null ? nhanVienHienTai.getMaNV() : null;
    }

    public static String getHoTen() {
        return nhanVienHienTai != null ? nhanVienHienTai.getHoTen() : null;
    }

    public static boolean daDangNhap() {
        return nhanVienHienTai != null;
    }

    public static void dangXuat() {
        nhanVienHienTai = null;
    }
}

