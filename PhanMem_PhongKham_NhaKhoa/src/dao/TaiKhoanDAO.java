package dao;

import connectDB.ConnectDB;
import connectDB.PasswordUtils;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.VaiTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Tài Khoản & Phân Quyền từ CSDL SQL Server thật.
 * Cải tiến chuẩn hóa:
 * - Định danh người dùng trực tiếp bằng Mã Nhân Viên (maNV).
 * - Mật khẩu được mã hóa băm SHA-256 an toàn, không hiển thị plain-text.
 * - Loại bỏ hoàn toàn chức năng xóa tài khoản để bảo vệ toàn vẹn dữ liệu.
 */
public class TaiKhoanDAO {

    /**
     * Xác thực thông tin đăng nhập theo Mã nhân viên và Mật khẩu.
     */
    public TaiKhoan checkLogin(String maNV, String password) {
        if (maNV == null || password == null)
            return null;

        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return null;
        }

        try {
            String sql = "SELECT tk.maNV, tk.matKhau, tk.trangThai, tk.soLanSaiMatKhau, " +
                    "tk.maVaiTro, nv.hoTen, nv.chucDanh, vt.tenVaiTro " +
                    "FROM TaiKhoan tk " +
                    "LEFT JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                    "LEFT JOIN VaiTro vt ON tk.maVaiTro = vt.maVaiTro " +
                    "WHERE tk.maNV = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maNV.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPass = rs.getString("matKhau");
                        if (!PasswordUtils.checkPassword(password, storedPass)) {
                            return null; // Mật khẩu không đúng
                        }
                        TaiKhoan tk = new TaiKhoan();
                        tk.setTenDangNhap(rs.getString("maNV"));
                        tk.setMatKhau(storedPass);
                        tk.setTrangThai(rs.getString("trangThai"));
                        tk.setSoLanSaiMatKhau(rs.getInt("soLanSaiMatKhau"));
                        String mNV = rs.getString("maNV");
                        String hoTen = rs.getString("hoTen");
                        tk.setMaNV(mNV);
                        tk.setMaVaiTro(rs.getString("maVaiTro"));
                        tk.setHoTenNV(hoTen);
                        tk.setTenVaiTro(rs.getString("tenVaiTro"));
                        if (mNV != null) {
                            tk.setNhanVien(new NhanVien(mNV, hoTen));
                        }
                        return tk;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("TaiKhoanDAO.checkLogin DB error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Lấy toàn bộ danh sách tài khoản người dùng từ CSDL thật.
     */
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT tk.maNV, tk.matKhau, tk.trangThai, tk.soLanSaiMatKhau, " +
                    "tk.maVaiTro, nv.hoTen, nv.chucDanh, vt.tenVaiTro " +
                    "FROM TaiKhoan tk " +
                    "LEFT JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                    "LEFT JOIN VaiTro vt ON tk.maVaiTro = vt.maVaiTro " +
                    "ORDER BY tk.maNV";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaiKhoan tk = new TaiKhoan();
                    String maNV = rs.getString("maNV");
                    tk.setTenDangNhap(maNV);
                    tk.setMaNV(maNV);
                    tk.setMatKhau(rs.getString("matKhau"));
                    tk.setTrangThai(rs.getString("trangThai"));
                    tk.setSoLanSaiMatKhau(rs.getInt("soLanSaiMatKhau"));
                    String hoTen = rs.getString("hoTen");
                    tk.setHoTenNV(hoTen);
                    tk.setMaVaiTro(rs.getString("maVaiTro"));
                    tk.setTenVaiTro(rs.getString("tenVaiTro"));
                    if (maNV != null) {
                        tk.setNhanVien(new NhanVien(maNV, hoTen));
                    }
                    list.add(tk);
                }
            }
        } catch (Exception e) {
            System.err.println("TaiKhoanDAO.getAllTaiKhoan DB error: " + e.getMessage());
        }

        return list;
    }

    /**
     * Cấp tài khoản mới cho nhân viên.
     * Mật khẩu được tự động băm SHA-256 an toàn.
     */
    public boolean themTaiKhoan(TaiKhoan tk) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || tk == null || tk.getMaNV() == null) {
            return false;
        }

        try {
            // Kiểm tra cột tenDangNhap nếu bảng cũ còn yêu cầu
            String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, trangThai, soLanSaiMatKhau, maNV, maVaiTro, vaiTro) "
                    + "VALUES (?, ?, ?, 0, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String passHashed = PasswordUtils.hashPassword(tk.getMatKhau() != null ? tk.getMatKhau() : "123");
                stmt.setString(1, tk.getMaNV());
                stmt.setString(2, passHashed);
                stmt.setString(3, tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động");
                stmt.setString(4, tk.getMaNV());
                stmt.setString(5, tk.getMaVaiTro());
                stmt.setString(6, tk.getTenVaiTro());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            // Thử câu lệnh không có tenDangNhap nếu bảng đã cập nhật
            try {
                String sql2 = "INSERT INTO TaiKhoan (maNV, matKhau, trangThai, soLanSaiMatKhau, maVaiTro) "
                        + "VALUES (?, ?, ?, 0, ?)";
                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    String passHashed = PasswordUtils.hashPassword(tk.getMatKhau() != null ? tk.getMatKhau() : "123");
                    stmt2.setString(1, tk.getMaNV());
                    stmt2.setString(2, passHashed);
                    stmt2.setString(3, tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động");
                    stmt2.setString(4, tk.getMaVaiTro());
                    return stmt2.executeUpdate() > 0;
                }
            } catch (Exception ex2) {
                System.err.println("TaiKhoanDAO.themTaiKhoan DB error: " + ex2.getMessage());
                return false;
            }
        }
    }

    /**
     * Đổi mật khẩu cho nhân viên (Mật khẩu mới được băm SHA-256).
     */
    public boolean doiMatKhau(String maNV, String matKhauMoi) {
        if (maNV == null || matKhauMoi == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String passHashed = PasswordUtils.hashPassword(matKhauMoi);
            String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE maNV = ? OR tenDangNhap = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, passHashed);
                stmt.setString(2, maNV.trim());
                stmt.setString(3, maNV.trim());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("TaiKhoanDAO.doiMatKhau DB error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Quản lý đặt lại mật khẩu về mặc định "123" (được lưu dạng SHA-256).
     */
    public boolean datLaiMatKhau(String maNV) {
        return doiMatKhau(maNV, "123");
    }

    /**
     * Bật / Tắt trạng thái tài khoản: "Hoạt động" hoặc "Bị khóa".
     */
    public boolean doiTrangThai(String maNV, String trangThaiMoi) {
        if (maNV == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE TaiKhoan SET trangThai = ? WHERE maNV = ? OR tenDangNhap = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, trangThaiMoi != null ? trangThaiMoi : "Hoạt động");
                stmt.setString(2, maNV.trim());
                stmt.setString(3, maNV.trim());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("TaiKhoanDAO.doiTrangThai DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean khoaTaiKhoan(String maNV) {
        return doiTrangThai(maNV, "Bị khóa");
    }

    public boolean moKhoaTaiKhoan(String maNV) {
        return doiTrangThai(maNV, "Hoạt động");
    }

    /**
     * Kiểm tra nhân viên đã có tài khoản hay chưa.
     */
    public boolean daCoTaiKhoan(String maNV) {
        if (maNV == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null)
            return false;
        try {
            String sql = "SELECT 1 FROM TaiKhoan WHERE maNV = ? OR tenDangNhap = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maNV.trim());
                stmt.setString(2, maNV.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    public List<VaiTro> getAllVaiTro() {
        return java.util.Arrays.asList(VaiTro.values());
    }
}
