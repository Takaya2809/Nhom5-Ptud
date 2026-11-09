package dao;

import connectDB.ConnectDB;
import connectDB.PasswordUtils;
import entity.NhanVien;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Nhân Viên, Bác Sĩ & Điều Dưỡng từ CSDL SQL Server thật
 */
public class NhanVienDAO {

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai "
                        + "FROM NhanVien ORDER BY maNV";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        NhanVien nv = new NhanVien(
                                rs.getString("maNV"),
                                rs.getString("hoTen"),
                                rs.getDate("ngaySinh"),
                                rs.getString("gioiTinh"),
                                rs.getString("soDienThoai"),
                                rs.getString("email"),
                                rs.getString("chucDanh"),
                                rs.getString("bangCapChungChi"),
                                rs.getDate("ngayVaoLam"),
                                rs.getString("trangThai"));
                        list.add(nv);
                    }
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.getAllNhanVien DB error: " + e.getMessage());
            }
        }
        return list;
    }

    public NhanVien getNhanVienByMa(String maNV) {
        if (maNV == null || maNV.trim().isEmpty())
            return null;
        maNV = maNV.trim();
        List<NhanVien> all = getAllNhanVien();
        for (NhanVien nv : all) {
            if (nv.getMaNV().equalsIgnoreCase(maNV)) {
                return nv;
            }
        }
        return null;
    }

    public boolean themNhanVien(NhanVien nv) {
        if (nv == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO NhanVien (maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nv.getMaNV());
                    stmt.setString(2, nv.getHoTen());
                    stmt.setDate(3, nv.getNgaySinh() != null ? nv.getNgaySinh() : Date.valueOf("1990-01-01"));
                    stmt.setString(4, nv.getGioiTinh() != null ? nv.getGioiTinh() : "Nam");
                    stmt.setString(5, nv.getSoDienThoai());
                    stmt.setString(6, nv.getEmail() != null ? nv.getEmail() : (nv.getMaNV() + "@nhakhoaabc.vn"));
                    stmt.setString(7, nv.getChucDanh() != null ? nv.getChucDanh() : "Bác sĩ Chuyên khoa RHM");
                    stmt.setString(8, nv.getBangCapChungChi() != null ? nv.getBangCapChungChi() : "");
                    stmt.setDate(9, nv.getNgayVaoLam() != null ? nv.getNgayVaoLam() : Date.valueOf("2024-01-01"));
                    stmt.setString(10, nv.getTrangThai() != null ? nv.getTrangThai() : "Đang làm việc");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.themNhanVien DB error: " + e.getMessage());
            }
        }
        return dbOk;
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        if (nv == null || nv.getMaNV() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE NhanVien SET hoTen = ?, ngaySinh = ?, gioiTinh = ?, soDienThoai = ?, " +
                        "email = ?, chucDanh = ?, bangCapChungChi = ?, ngayVaoLam = ?, trangThai = ? WHERE maNV = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nv.getHoTen());
                    stmt.setDate(2, nv.getNgaySinh());
                    stmt.setString(3, nv.getGioiTinh());
                    stmt.setString(4, nv.getSoDienThoai());
                    stmt.setString(5, nv.getEmail());
                    stmt.setString(6, nv.getChucDanh());
                    stmt.setString(7, nv.getBangCapChungChi());
                    stmt.setDate(8, nv.getNgayVaoLam());
                    stmt.setString(9, nv.getTrangThai());
                    stmt.setString(10, nv.getMaNV());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.capNhatNhanVien DB error: " + e.getMessage());
            }
        }
        return dbOk;
    }

    public boolean chuyenTrangThaiNghiViec(String maNV) {
        if (maNV == null)
            return false;
        List<NhanVien> all = getAllNhanVien();
        for (NhanVien nv : all) {
            if (nv.getMaNV().equalsIgnoreCase(maNV)) {
                nv.setTrangThai("Đã nghỉ việc");
                return capNhatNhanVien(nv);
            }
        }
        return false;
    }

    public List<NhanVien> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNhanVien();
        }
        String cleanKeyword = keyword.trim();
        List<NhanVien> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai FROM NhanVien "
                        + "WHERE maNV LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? OR email LIKE ? OR chucDanh LIKE ? OR bangCapChungChi LIKE ? OR trangThai LIKE ? "
                        + "ORDER BY maNV";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String param = "%" + cleanKeyword + "%";
                    for (int i = 1; i <= 7; i++) {
                        stmt.setString(i, param);
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new NhanVien(
                                    rs.getString("maNV"),
                                    rs.getString("hoTen"),
                                    rs.getDate("ngaySinh"),
                                    rs.getString("gioiTinh"),
                                    rs.getString("soDienThoai"),
                                    rs.getString("email"),
                                    rs.getString("chucDanh"),
                                    rs.getString("bangCapChungChi"),
                                    rs.getDate("ngayVaoLam"),
                                    rs.getString("trangThai")));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.timKiem DB error: " + e.getMessage());
            }
        }
        return list;
    }

    public boolean xoaNhanVien(String maNV) {
        if (maNV == null || maNV.trim().isEmpty())
            return false;
        maNV = maNV.trim();
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM NhanVien WHERE maNV = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maNV);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println(
                        "NhanVienDAO.xoaNhanVien DB error (foreign key dependency, fallback to marking inactive): "
                                + e.getMessage());
                return chuyenTrangThaiNghiViec(maNV);
            }
        }
        return dbOk;
    }

    public List<NhanVien> getDanhSachBacSi() {
        List<NhanVien> list = new ArrayList<>();
        for (NhanVien nv : getAllNhanVien()) {
            String cd = nv.getChucDanh() != null ? nv.getChucDanh().toLowerCase() : "";
            if (cd.contains("bác sĩ") || cd.contains("nha sĩ") || nv.getMaNV().startsWith("BS")) {
                list.add(nv);
            }
        }
        return list;
    }

    public String sinhMaNVMoi(String chucDanh) {
        String prefix = "NV";
        if (chucDanh != null) {
            String cdLower = chucDanh.toLowerCase();
            if (cdLower.contains("bác sĩ") || cdLower.contains("nha sĩ")) {
                prefix = "BS";
            } else if (cdLower.contains("lễ tân")) {
                prefix = "LT";
            } else if (cdLower.contains("thu ngân")) {
                prefix = "TN";
            } else if (cdLower.contains("y tá") || cdLower.contains("điều dưỡng") || cdLower.contains("phụ tá")
                    || cdLower.contains("kỹ thuật viên")) {
                prefix = "YT";
            } else if (cdLower.contains("quản lý")) {
                prefix = "QL";
            }
        }
        int maxIndex = 0;
        List<NhanVien> all = getAllNhanVien();
        for (NhanVien nv : all) {
            String ma = nv.getMaNV();
            if (ma != null && ma.toUpperCase().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(ma.substring(prefix.length()).trim());
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return String.format("%s%03d", prefix, maxIndex + 1);
    }

    /**
     * Nghiệp vụ Đăng nhập hệ thống bằng Mã nhân viên và Mật khẩu.
     * Xác thực thông tin trực tiếp từ CSDL thật và trả về đối tượng NhanVien kèm
     * vai trò.
     * 
     * @param maNV    Mã nhân viên (ví dụ: QL001, BS001, LT001, YT001)
     * @param matKhau Mật khẩu đăng nhập
     * @return NhanVien nếu đăng nhập thành công từ CSDL, null nếu sai hoặc không
     *         kết nối được
     */
    public NhanVien dangNhap(String maNV, String matKhau) {
        if (maNV == null || matKhau == null)
            return null;
        String cleanMa = maNV.trim();
        String cleanPass = matKhau.trim();

        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            String sql = "SELECT nv.maNV, nv.hoTen, nv.ngaySinh, nv.gioiTinh, nv.soDienThoai, nv.email, " +
                    "nv.chucDanh, nv.bangCapChungChi, nv.ngayVaoLam, nv.trangThai, " +
                    "tk.matKhau, tk.trangThai AS trangThaiTK, tk.maVaiTro " +
                    "FROM NhanVien nv " +
                    "JOIN TaiKhoan tk ON nv.maNV = tk.maNV " +
                    "WHERE nv.maNV = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cleanMa);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPass = rs.getString("matKhau");
                        if (!connectDB.PasswordUtils.checkPassword(cleanPass, storedPass)) {
                            return null; // Sai mật khẩu
                        }
                        String trangThaiTK = rs.getString("trangThaiTK");
                        String trangThaiNV = rs.getString("trangThai");
                        // Nếu tài khoản bị khóa trong bảng TaiKhoan thì phản ánh vào trạng thái
                        if ("Bị khóa".equalsIgnoreCase(trangThaiTK)) {
                            trangThaiNV = "Bị khóa";
                        }
                        NhanVien nv = new NhanVien(
                                rs.getString("maNV"),
                                rs.getString("hoTen"),
                                rs.getDate("ngaySinh"),
                                rs.getString("gioiTinh"),
                                rs.getString("soDienThoai"),
                                rs.getString("email"),
                                rs.getString("chucDanh"),
                                rs.getString("bangCapChungChi"),
                                rs.getDate("ngayVaoLam"),
                                trangThaiNV);
                        String maVT = rs.getString("maVaiTro");
                        if (maVT != null) {
                            nv.setVaiTro(entity.VaiTro.fromMa(maVT));
                        }
                        return nv;
                    }
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.dangNhap DB error: " + e.getMessage());
            }
        }

        return null;
    }
}
