package dao;

import connectDB.ConnectDB;
import entity.TaiKhoan;
import entity.VaiTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Tài Khoản & Phân Quyền
 */
public class TaiKhoanDAO {

    /**
     * Danh sách tài khoản mặc định của hệ thống theo từng vị trí (hỗ trợ chế độ offline / seed data)
     */
    private static final List<TaiKhoan> DEFAULT_ACCOUNTS = new ArrayList<>();

    static {
        // 1. Quản lý
        TaiKhoan admin = new TaiKhoan("admin", "123456", null, "Hoạt động", 0, "QL001", "VT_QL");
        admin.setHoTenNV("Trần Thị Hoàng Lan");
        admin.setTenVaiTro("Quản lý");
        DEFAULT_ACCOUNTS.add(admin);

        TaiKhoan quanly = new TaiKhoan("quanly", "123456", null, "Hoạt động", 0, "QL001", "VT_QL");
        quanly.setHoTenNV("Trần Thị Hoàng Lan");
        quanly.setTenVaiTro("Quản lý");
        DEFAULT_ACCOUNTS.add(quanly);

        // 2. Nha sĩ / Bác sĩ
        TaiKhoan bs1 = new TaiKhoan("bacsi_tuan", "123456", null, "Hoạt động", 0, "BS001", "VT_BS");
        bs1.setHoTenNV("BS.CKI Nguyễn Minh Tuấn");
        bs1.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(bs1);

        TaiKhoan bs2 = new TaiKhoan("bacsi_hang", "123456", null, "Hoạt động", 0, "BS002", "VT_BS");
        bs2.setHoTenNV("ThS.BS Lê Thu Hằng");
        bs2.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(bs2);

        TaiKhoan bs3 = new TaiKhoan("bacsi_dat", "123456", null, "Hoạt động", 0, "BS003", "VT_BS");
        bs3.setHoTenNV("BS Phạm Quốc Đạt");
        bs3.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(bs3);

        TaiKhoan bsAn = new TaiKhoan("bs.an", "123456", null, "Hoạt động", 0, "BS001", "VT_BS");
        bsAn.setHoTenNV("BS. CKII Nguyễn Văn An");
        bsAn.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(bsAn);

        TaiKhoan bsThao = new TaiKhoan("bs.thao", "123456", null, "Hoạt động", 0, "BS002", "VT_BS");
        bsThao.setHoTenNV("ThS. BS Lê Thị Thảo");
        bsThao.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(bsThao);

        TaiKhoan bsTri = new TaiKhoan("bs.tri", "123456", null, "Hoạt động", 0, "BS003", "VT_BS");
        bsTri.setHoTenNV("BS. Trần Minh Trí");
        bsTri.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(bsTri);

        TaiKhoan nhasi = new TaiKhoan("nhasi", "123456", null, "Hoạt động", 0, "BS001", "VT_BS");
        nhasi.setHoTenNV("BS.CKI Nguyễn Minh Tuấn");
        nhasi.setTenVaiTro("Nha sĩ");
        DEFAULT_ACCOUNTS.add(nhasi);

        // 3. Lễ tân
        TaiKhoan lt1 = new TaiKhoan("letan_tram", "123456", null, "Hoạt động", 0, "LT001", "VT_LT");
        lt1.setHoTenNV("Phạm Ngọc Trâm");
        lt1.setTenVaiTro("Lễ tân");
        DEFAULT_ACCOUNTS.add(lt1);

        TaiKhoan lt2 = new TaiKhoan("letan01", "123456", null, "Hoạt động", 0, "LT001", "VT_LT");
        lt2.setHoTenNV("Đỗ Thùy Trang");
        lt2.setTenVaiTro("Lễ tân");
        DEFAULT_ACCOUNTS.add(lt2);

        TaiKhoan letan = new TaiKhoan("letan", "123456", null, "Hoạt động", 0, "LT001", "VT_LT");
        letan.setHoTenNV("Phạm Ngọc Trâm");
        letan.setTenVaiTro("Lễ tân");
        DEFAULT_ACCOUNTS.add(letan);

        // 4. Thu ngân
        TaiKhoan tn1 = new TaiKhoan("thungan_phuong", "123456", null, "Hoạt động", 0, "TN001", "VT_TN");
        tn1.setHoTenNV("Đỗ Hoài Phương");
        tn1.setTenVaiTro("Thu ngân");
        DEFAULT_ACCOUNTS.add(tn1);

        TaiKhoan thungan = new TaiKhoan("thungan", "123456", null, "Hoạt động", 0, "TN001", "VT_TN");
        thungan.setHoTenNV("Đỗ Hoài Phương");
        thungan.setTenVaiTro("Thu ngân");
        DEFAULT_ACCOUNTS.add(thungan);

        // 5. Y tá / Phụ tá
        TaiKhoan yt1 = new TaiKhoan("yta_oanh", "123456", null, "Hoạt động", 0, "YT001", "VT_YT");
        yt1.setHoTenNV("Nguyễn Thị Kim Oanh");
        yt1.setTenVaiTro("Y tá");
        DEFAULT_ACCOUNTS.add(yt1);

        TaiKhoan yt2 = new TaiKhoan("yta_duyen", "123456", null, "Hoạt động", 0, "YT002", "VT_YT");
        yt2.setHoTenNV("Võ Thị Mỹ Duyên");
        yt2.setTenVaiTro("Y tá");
        DEFAULT_ACCOUNTS.add(yt2);

        TaiKhoan yta = new TaiKhoan("yta", "123456", null, "Hoạt động", 0, "YT001", "VT_YT");
        yta.setHoTenNV("Nguyễn Thị Kim Oanh");
        yta.setTenVaiTro("Y tá");
        DEFAULT_ACCOUNTS.add(yta);
    }

    /**
     * Kiểm tra thông tin đăng nhập từ CSDL hoặc danh sách hệ thống
     */
    public TaiKhoan checkLogin(String username, String password) {
        if (username == null || password == null) return null;

        // 1. Thử kiểm tra từ CSDL SQL Server nếu có kết nối
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT tk.tenDangNhap, tk.matKhau, tk.trangThai, tk.soLanSaiMatKhau, " +
                             "tk.maNV, tk.maVaiTro, nv.hoTen, vt.tenVaiTro " +
                             "FROM TaiKhoan tk " +
                             "LEFT JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                             "LEFT JOIN VaiTro vt ON tk.maVaiTro = vt.maVaiTro " +
                             "WHERE tk.tenDangNhap = ? AND tk.matKhau = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            TaiKhoan tk = new TaiKhoan();
                            tk.setTenDangNhap(rs.getString("tenDangNhap"));
                            tk.setMatKhau(rs.getString("matKhau"));
                            tk.setTrangThai(rs.getString("trangThai"));
                            tk.setSoLanSaiMatKhau(rs.getInt("soLanSaiMatKhau"));
                            tk.setMaNV(rs.getString("maNV"));
                            tk.setMaVaiTro(rs.getString("maVaiTro"));
                            tk.setHoTenNV(rs.getString("hoTen"));
                            tk.setTenVaiTro(rs.getString("tenVaiTro"));
                            return tk;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("TaiKhoanDAO.checkLogin DB error: " + e.getMessage());
            }
        }

        // 2. Kiểm tra danh sách tài khoản mặc định của hệ thống
        for (TaiKhoan tk : DEFAULT_ACCOUNTS) {
            if (tk.getTenDangNhap().equalsIgnoreCase(username)) {
                // Chỉ chấp nhận đúng mật khẩu của tài khoản mẫu.
                if (tk.getMatKhau() != null && tk.getMatKhau().equals(password)) {
                    return tk;
                }
            }
        }

        return null;
    }

    /**
     * Kiểm tra tài khoản có phù hợp với vị trí / vai trò đã chọn hay không
     */
    public boolean kiemTraVaiTro(TaiKhoan tk, String viTriDuocChon) {
        if (tk == null || viTriDuocChon == null) return false;

        String maVT = tk.getMaVaiTro() != null ? tk.getMaVaiTro().toUpperCase() : "";
        String tenVT = tk.getTenVaiTro() != null ? tk.getTenVaiTro().toLowerCase() : "";
        String target = viTriDuocChon.toLowerCase();

        if (target.contains("quản lý") || target.contains("admin")) {
            return maVT.contains("QL") || maVT.contains("ADMIN") || tenVT.contains("quản lý") || tenVT.contains("admin");
        }
        if (target.contains("nha sĩ") || target.contains("bác sĩ")) {
            return maVT.contains("BS") || tenVT.contains("nha sĩ") || tenVT.contains("bác sĩ");
        }
        if (target.contains("lễ tân") || target.contains("tiếp tân")) {
            return maVT.contains("LT") || tenVT.contains("lễ tân") || tenVT.contains("tiếp tân");
        }
        if (target.contains("thu ngân")) {
            return maVT.contains("TN") || tenVT.contains("thu ngân");
        }
        if (target.contains("y tá") || target.contains("phụ tá")) {
            return maVT.contains("YT") || tenVT.contains("y tá") || tenVT.contains("phụ tá") || tenVT.contains("điều dưỡng");
        }

        return false;
    }

    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT tk.tenDangNhap, tk.matKhau, tk.trangThai, tk.soLanSaiMatKhau, " +
                             "tk.maNV, tk.maVaiTro, nv.hoTen, vt.tenVaiTro " +
                             "FROM TaiKhoan tk " +
                             "LEFT JOIN NhanVien nv ON tk.maNV = nv.maNV " +
                             "LEFT JOIN VaiTro vt ON tk.maVaiTro = vt.maVaiTro";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        TaiKhoan tk = new TaiKhoan();
                        tk.setTenDangNhap(rs.getString("tenDangNhap"));
                        tk.setMatKhau(rs.getString("matKhau"));
                        tk.setTrangThai(rs.getString("trangThai"));
                        tk.setSoLanSaiMatKhau(rs.getInt("soLanSaiMatKhau"));
                        tk.setMaNV(rs.getString("maNV"));
                        tk.setMaVaiTro(rs.getString("maVaiTro"));
                        tk.setHoTenNV(rs.getString("hoTen"));
                        tk.setTenVaiTro(rs.getString("tenVaiTro"));
                        list.add(tk);
                    }
                }
            } catch (Exception e) {
                System.err.println("TaiKhoanDAO.getAllTaiKhoan DB error: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            list.addAll(DEFAULT_ACCOUNTS);
        }
        return list;
    }

    public boolean themTaiKhoan(TaiKhoan tk) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, trangThai, soLanSaiMatKhau, maNV, maVaiTro) " +
                             "VALUES (?, ?, ?, 0, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, tk.getTenDangNhap());
                    stmt.setString(2, tk.getMatKhau());
                    stmt.setString(3, tk.getTrangThai());
                    stmt.setString(4, tk.getMaNV());
                    stmt.setString(5, tk.getMaVaiTro());
                    return stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("TaiKhoanDAO.themTaiKhoan DB error: " + e.getMessage());
            }
        }
        // Keep offline demo data only when there is no database connection.
        if (tk == null || tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().isEmpty()) {
            return false;
        }
        for (TaiKhoan existing : DEFAULT_ACCOUNTS) {
            if (existing.getTenDangNhap().equalsIgnoreCase(tk.getTenDangNhap())) {
                return false;
            }
        }
        DEFAULT_ACCOUNTS.add(tk);
        return true;
    }

    public boolean doiMatKhau(String username, String matKhauMoi) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, matKhauMoi);
                    stmt.setString(2, username);
                    return stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("TaiKhoanDAO.doiMatKhau DB error: " + e.getMessage());
            }
        }
        for (TaiKhoan tk : DEFAULT_ACCOUNTS) {
            if (tk.getTenDangNhap().equalsIgnoreCase(username)) {
                tk.setMatKhau(matKhauMoi);
                return true;
            }
        }
        return false;
    }

    public boolean khoaTaiKhoan(String username) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "UPDATE TaiKhoan SET trangThai = N'Bị khóa' WHERE tenDangNhap = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    return stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("TaiKhoanDAO.khoaTaiKhoan DB error: " + e.getMessage());
            }
        }
        for (TaiKhoan tk : DEFAULT_ACCOUNTS) {
            if (tk.getTenDangNhap().equalsIgnoreCase(username)) {
                tk.setTrangThai("Bị khóa");
                return true;
            }
        }
        return false;
    }

    public boolean moKhoaTaiKhoan(String username) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "UPDATE TaiKhoan SET trangThai = N'Hoạt động' WHERE tenDangNhap = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    return stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("TaiKhoanDAO.moKhoaTaiKhoan DB error: " + e.getMessage());
            }
        }
        for (TaiKhoan tk : DEFAULT_ACCOUNTS) {
            if (tk.getTenDangNhap().equalsIgnoreCase(username)) {
                tk.setTrangThai("Hoạt động");
                return true;
            }
        }
        return false;
    }

    public boolean capLaiMatKhau(String username, String passMacDinh) {
        return doiMatKhau(username, passMacDinh);
    }

    public List<VaiTro> getAllVaiTro() {
        List<VaiTro> list = new ArrayList<>();
        list.add(new VaiTro("VT_QL", "Quản lý", "Toàn quyền quản trị hệ thống", "ALL"));
        list.add(new VaiTro("VT_BS", "Nha sĩ", "Khám và điều trị nha khoa", "KHAM,DIEUTRI,KEDON"));
        list.add(new VaiTro("VT_LT", "Lễ tân", "Tiếp đón và đặt lịch hẹn", "HOSO,LICHHEN,TIEPDON"));
        list.add(new VaiTro("VT_TN", "Thu ngân", "Lập hóa đơn và thanh toán", "HOADON,THANHTOAN"));
        list.add(new VaiTro("VT_YT", "Y tá", "Quản lý kho dược và vật tư y tế", "VATTU,KHUKHUAN,HOTRO"));
        return list;
    }
}
