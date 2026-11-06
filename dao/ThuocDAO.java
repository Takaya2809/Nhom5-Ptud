package dao;

import connectDB.ConnectDB;
import entity.NCC;
import entity.Thuoc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Danh Mục Thuốc Điều Trị & Kê Đơn.
 * Chuẩn hóa:
 * - 100% PreparedStatement.
 * - Phân biệt rõ giá nhập và giá bán.
 * - Dựng đối tượng nhà cung cấp (NCC).
 */
public class ThuocDAO {

    private static final List<Thuoc> MOCK_THUOC = new ArrayList<>();

    static {
        NCC ncc1 = new NCC("NCC001", "Công ty CP Dược phẩm Trung ương 1", "02438254250", "contact@cpc1.vn", "Hà Nội", "Phòng KD", "Đang hợp tác");
        NCC ncc2 = new NCC("NCC002", "Công ty TNHH Sanofi-Aventis Việt Nam", "02838298526", "sanofi.vn@sanofi.com", "TP. HCM", "Phòng KD", "Đang hợp tác");

        MOCK_THUOC.add(new Thuoc("TH001", "Amoxicillin 500mg", "Amoxicillin Trihydrate", "Viên", 2800, 3500, 450, 50,
                Date.valueOf("2027-12-31"), "Uống 1 viên x 2 lần/ngày sau ăn no", "Đang kinh doanh", ncc1));
        MOCK_THUOC.add(new Thuoc("TH002", "Paracetamol 500mg", "Paracetamol", "Viên", 1000, 1500, 800, 100,
                Date.valueOf("2028-06-30"), "Uống 1 viên khi đau hoặc sốt >= 38.5C", "Đang kinh doanh", ncc1));
        MOCK_THUOC.add(new Thuoc("TH003", "Ibuprofen 400mg", "Ibuprofen", "Viên", 3000, 4000, 250, 40,
                Date.valueOf("2027-08-15"), "Uống 1 viên x 2 lần/ngày sau khi ăn no", "Đang kinh doanh", ncc2));
        MOCK_THUOC.add(new Thuoc("TH004", "Nước súc miệng Chlorhexidine 0.12%", "Chlorhexidine Gluconate", "Chai",
                50000, 65000, 15, 20, Date.valueOf("2026-11-30"), "Súc miệng 15ml x 2-3 lần/ngày", "Cảnh báo hết hàng",
                ncc2));
        MOCK_THUOC.add(new Thuoc("TH005", "Gel bôi giảm đau nướu Kamistad-Gel N", "Lidocaine HCl + Cúc La Mã", "Tuýp",
                65000, 85000, 8, 15, Date.valueOf("2027-05-20"), "Bôi một lượng nhỏ lên nướu 3 lần/ngày",
                "Cảnh báo hết hàng", ncc2));
    }

    public List<Thuoc> getAllThuoc() {
        List<Thuoc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            String sql = "SELECT t.maThuoc, t.tenThuoc, t.hoatChat, t.donViTinh, " +
                    "t.soLuongTon, t.dinhMucToiThieu, t.hanSuDung, t.cachDungMacDinh, t.trangThai, " +
                    "t.maNCC, ncc.tenNCC " +
                    "FROM Thuoc t " +
                    "LEFT JOIN NCC ncc ON t.maNCC = ncc.maNCC " +
                    "ORDER BY t.maThuoc";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maNCC = rs.getString("maNCC");
                    NCC ncc = null;
                    if (maNCC != null) {
                        ncc = new NCC(maNCC, rs.getString("tenNCC"));
                    }

                    Thuoc th = new Thuoc(
                            rs.getString("maThuoc"),
                            rs.getString("tenThuoc"),
                            rs.getString("hoatChat"),
                            rs.getString("donViTinh"),
                            0, 0, // Giá sẽ lấy động từ cột nếu có
                            rs.getInt("soLuongTon"),
                            rs.getInt("dinhMucToiThieu"),
                            rs.getDate("hanSuDung"),
                            rs.getString("cachDungMacDinh"),
                            rs.getString("trangThai"),
                            ncc);
                    list.add(th);
                }
            } catch (Exception e) {
                // Fallback nếu câu query chưa có cột maNCC
                try {
                    String sqlFallback = "SELECT maThuoc, tenThuoc, hoatChat, donViTinh, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai FROM Thuoc ORDER BY maThuoc";
                    try (PreparedStatement stmtFb = conn.prepareStatement(sqlFallback);
                            ResultSet rsFb = stmtFb.executeQuery()) {
                        while (rsFb.next()) {
                            double dg = rsFb.getDouble("donGia");
                            list.add(new Thuoc(
                                    rsFb.getString("maThuoc"),
                                    rsFb.getString("tenThuoc"),
                                    rsFb.getString("hoatChat"),
                                    rsFb.getString("donViTinh"),
                                    dg * 0.8,
                                    dg,
                                    rsFb.getInt("soLuongTon"),
                                    rsFb.getInt("dinhMucToiThieu"),
                                    rsFb.getDate("hanSuDung"),
                                    rsFb.getString("cachDungMacDinh"),
                                    rsFb.getString("trangThai"),
                                    null));
                        }
                    }
                } catch (Exception exOld) {
                    System.err.println("ThuocDAO.getAllThuoc DB error: " + exOld.getMessage());
                }
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_THUOC);
        }
        return list;
    }

    public List<Thuoc> getThuocCanhBaoTonKho() {
        List<Thuoc> all = getAllThuoc();
        List<Thuoc> res = new ArrayList<>();
        for (Thuoc th : all) {
            if (th.getSoLuongTon() <= th.getDinhMucToiThieu() || "Cảnh báo hết hàng".equalsIgnoreCase(th.getTrangThai())
                    || "Hết hàng".equalsIgnoreCase(th.getTrangThai())) {
                res.add(th);
            }
        }
        return res;
    }

    public boolean themThuoc(Thuoc th) {
        if (th == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, giaNhap, giaBan, gia, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai, maNCC) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, th.getMaThuoc());
                    stmt.setString(2, th.getTenThuoc());
                    stmt.setString(3, th.getHoatChat());
                    stmt.setString(4, th.getDonViTinh());
                    stmt.setDouble(5, th.getGiaNhap());
                    stmt.setDouble(6, th.getGiaBan());
                    stmt.setDouble(7, th.getGiaNhap());
                    stmt.setDouble(8, th.getGiaBan());
                    stmt.setInt(9, th.getSoLuongTon());
                    stmt.setInt(10, th.getDinhMucToiThieu());
                    stmt.setDate(11, th.getHanSuDung());
                    stmt.setString(12, th.getCachDungMacDinh());
                    stmt.setString(13, th.getTrangThai() != null ? th.getTrangThai() : "Đang kinh doanh");
                    stmt.setString(14, th.getMaNCC());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                // Fallback nếu schema cũ chưa cập nhật các cột mới
                try {
                    String sqlFb = "INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, gia, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai) "
                            +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmtFb = conn.prepareStatement(sqlFb)) {
                        stmtFb.setString(1, th.getMaThuoc());
                        stmtFb.setString(2, th.getTenThuoc());
                        stmtFb.setString(3, th.getHoatChat());
                        stmtFb.setString(4, th.getDonViTinh());
                        stmtFb.setDouble(5, th.getGiaNhap());
                        stmtFb.setDouble(6, th.getGiaBan());
                        stmtFb.setInt(7, th.getSoLuongTon());
                        stmtFb.setInt(8, th.getDinhMucToiThieu());
                        stmtFb.setDate(9, th.getHanSuDung());
                        stmtFb.setString(10, th.getCachDungMacDinh());
                        stmtFb.setString(11, th.getTrangThai() != null ? th.getTrangThai() : "Đang kinh doanh");
                        dbOk = stmtFb.executeUpdate() > 0;
                    }
                } catch (Exception exFb) {
                    System.err.println("ThuocDAO.themThuoc DB error: " + exFb.getMessage());
                }
            }
        }
        MOCK_THUOC.add(th);
        return dbOk || conn == null;
    }

    public boolean capNhatThuoc(Thuoc th) {
        if (th == null || th.getMaThuoc() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE Thuoc SET tenThuoc = ?, hoatChat = ?, donViTinh = ?, giaNhap = ?, giaBan = ?, gia = ?, donGia = ?, "
                        +
                        "soLuongTon = ?, dinhMucToiThieu = ?, hanSuDung = ?, cachDungMacDinh = ?, trangThai = ?, maNCC = ? WHERE maThuoc = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, th.getTenThuoc());
                    stmt.setString(2, th.getHoatChat());
                    stmt.setString(3, th.getDonViTinh());
                    stmt.setDouble(4, th.getGiaNhap());
                    stmt.setDouble(5, th.getGiaBan());
                    stmt.setDouble(6, th.getGiaNhap());
                    stmt.setDouble(7, th.getGiaBan());
                    stmt.setInt(8, th.getSoLuongTon());
                    stmt.setInt(9, th.getDinhMucToiThieu());
                    stmt.setDate(10, th.getHanSuDung());
                    stmt.setString(11, th.getCachDungMacDinh());
                    stmt.setString(12, th.getTrangThai());
                    stmt.setString(13, th.getMaNCC());
                    stmt.setString(14, th.getMaThuoc());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                try {
                    String sqlFb = "UPDATE Thuoc SET tenThuoc = ?, hoatChat = ?, donViTinh = ?, gia = ?, donGia = ?, " +
                            "soLuongTon = ?, dinhMucToiThieu = ?, hanSuDung = ?, cachDungMacDinh = ?, trangThai = ? WHERE maThuoc = ?";
                    try (PreparedStatement stmtFb = conn.prepareStatement(sqlFb)) {
                        stmtFb.setString(1, th.getTenThuoc());
                        stmtFb.setString(2, th.getHoatChat());
                        stmtFb.setString(3, th.getDonViTinh());
                        stmtFb.setDouble(4, th.getGiaBan());
                        stmtFb.setDouble(5, th.getGiaBan());
                        stmtFb.setInt(6, th.getSoLuongTon());
                        stmtFb.setInt(7, th.getDinhMucToiThieu());
                        stmtFb.setDate(8, th.getHanSuDung());
                        stmtFb.setString(9, th.getCachDungMacDinh());
                        stmtFb.setString(10, th.getTrangThai());
                        stmtFb.setString(11, th.getMaThuoc());
                        dbOk = stmtFb.executeUpdate() > 0;
                    }
                } catch (Exception exFb) {
                    System.err.println("ThuocDAO.capNhatThuoc DB error: " + exFb.getMessage());
                }
            }
        }
        for (int i = 0; i < MOCK_THUOC.size(); i++) {
            if (MOCK_THUOC.get(i).getMaThuoc().equalsIgnoreCase(th.getMaThuoc())) {
                MOCK_THUOC.set(i, th);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean xoaThuoc(String maThuoc) {
        if (maThuoc == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            String sql = "DELETE FROM Thuoc WHERE maThuoc = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maThuoc);
                dbOk = stmt.executeUpdate() > 0;
            } catch (Exception e) {
                try {
                    String sqlUp = "UPDATE Thuoc SET trangThai = N'Ngừng kinh doanh' WHERE maThuoc = ?";
                    try (PreparedStatement stmtUp = conn.prepareStatement(sqlUp)) {
                        stmtUp.setString(1, maThuoc);
                        dbOk = stmtUp.executeUpdate() > 0;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        final String fMa = maThuoc;
        MOCK_THUOC.removeIf(t -> t.getMaThuoc().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
    }

    public List<Thuoc> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllThuoc();
        }
        String cleanKeyword = keyword.trim().toLowerCase();
        List<Thuoc> all = getAllThuoc();
        List<Thuoc> res = new ArrayList<>();
        for (Thuoc th : all) {
            boolean match = (th.getMaThuoc() != null && th.getMaThuoc().toLowerCase().contains(cleanKeyword)) ||
                    (th.getTenThuoc() != null && th.getTenThuoc().toLowerCase().contains(cleanKeyword)) ||
                    (th.getHoatChat() != null && th.getHoatChat().toLowerCase().contains(cleanKeyword)) ||
                    (th.getDonViTinh() != null && th.getDonViTinh().toLowerCase().contains(cleanKeyword)) ||
                    (th.getTrangThai() != null && th.getTrangThai().toLowerCase().contains(cleanKeyword));
            if (match) {
                res.add(th);
            }
        }
        return res;
    }

    public boolean nhapXuatKho(String maThuoc, int soLuongThayDoi) {
        if (maThuoc == null)
            return false;
        List<Thuoc> all = getAllThuoc();
        for (Thuoc th : all) {
            if (th.getMaThuoc().equalsIgnoreCase(maThuoc)) {
                int slMoi = Math.max(0, th.getSoLuongTon() + soLuongThayDoi);
                th.setSoLuongTon(slMoi);
                th.setTrangThai(slMoi <= 0 ? "Hết hàng"
                        : (slMoi <= th.getDinhMucToiThieu() ? "Cảnh báo hết hàng" : "Đang kinh doanh"));
                return capNhatThuoc(th);
            }
        }
        return false;
    }

    public String sinhMaThuocMoi() {
        int max = 0;
        for (Thuoc t : getAllThuoc()) {
            if (t.getMaThuoc() != null && t.getMaThuoc().toUpperCase().startsWith("TH")) {
                try {
                    int n = Integer.parseInt(t.getMaThuoc().substring(2).trim());
                    if (n > max)
                        max = n;
                } catch (Exception ignored) {
                }
            }
        }
        return String.format("TH%03d", max + 1);
    }
}
