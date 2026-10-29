package dao;

import connectDB.ConnectDB;
import entity.Thuoc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Danh Mục Thuốc Điều Trị & Kê Đơn
 */
public class ThuocDAO {

    private static final List<Thuoc> MOCK_THUOC = new ArrayList<>();

    static {
        MOCK_THUOC.add(new Thuoc("TH001", "Amoxicillin 500mg", "Amoxicillin Trihydrate", "Viên", 3500, 450, 50, Date.valueOf("2027-12-31"), "Uống 1 viên x 2 lần/ngày sau ăn no", "Đang kinh doanh"));
        MOCK_THUOC.add(new Thuoc("TH002", "Paracetamol 500mg", "Paracetamol", "Viên", 1500, 800, 100, Date.valueOf("2028-06-30"), "Uống 1 viên khi đau hoặc sốt >= 38.5C", "Đang kinh doanh"));
        MOCK_THUOC.add(new Thuoc("TH003", "Ibuprofen 400mg", "Ibuprofen", "Viên", 4000, 250, 40, Date.valueOf("2027-08-15"), "Uống 1 viên x 2 lần/ngày sau khi ăn no", "Đang kinh doanh"));
        MOCK_THUOC.add(new Thuoc("TH004", "Nước súc miệng Chlorhexidine 0.12%", "Chlorhexidine Gluconate", "Chai", 65000, 15, 20, Date.valueOf("2026-11-30"), "Súc miệng 15ml x 2-3 lần/ngày", "Cảnh báo hết hàng"));
        MOCK_THUOC.add(new Thuoc("TH005", "Gel bôi giảm đau nướu Kamistad-Gel N", "Lidocaine HCl + Cúc La Mã", "Tuýp", 85000, 8, 15, Date.valueOf("2027-05-20"), "Bôi một lượng nhỏ lên nướu 3 lần/ngày", "Cảnh báo hết hàng"));
    }

    public List<Thuoc> getAllThuoc() {
        List<Thuoc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maThuoc, tenThuoc, hoatChat, donViTinh, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai FROM Thuoc ORDER BY maThuoc";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        list.add(new Thuoc(
                            rs.getString("maThuoc"),
                            rs.getString("tenThuoc"),
                            rs.getString("hoatChat"),
                            rs.getString("donViTinh"),
                            rs.getDouble("donGia"),
                            rs.getInt("soLuongTon"),
                            rs.getInt("dinhMucToiThieu"),
                            rs.getDate("hanSuDung"),
                            rs.getString("cachDungMacDinh"),
                            rs.getString("trangThai")
                        ));
                    }
                }
            } catch (Exception e) {
                System.err.println("ThuocDAO.getAllThuoc DB error: " + e.getMessage());
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
            if (th.getSoLuongTon() <= th.getDinhMucToiThieu() || "Cảnh báo hết hàng".equalsIgnoreCase(th.getTrangThai()) || "Hết hàng".equalsIgnoreCase(th.getTrangThai())) {
                res.add(th);
            }
        }
        return res;
    }

    public boolean themThuoc(Thuoc th) {
        if (th == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, gia, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, th.getMaThuoc());
                    stmt.setString(2, th.getTenThuoc());
                    stmt.setString(3, th.getHoatChat());
                    stmt.setString(4, th.getDonViTinh());
                    stmt.setDouble(5, th.getDonGia());
                    stmt.setDouble(6, th.getDonGia());
                    stmt.setInt(7, th.getSoLuongTon());
                    stmt.setInt(8, th.getDinhMucToiThieu());
                    stmt.setDate(9, th.getHanSuDung());
                    stmt.setString(10, th.getCachDungMacDinh());
                    stmt.setString(11, th.getTrangThai() != null ? th.getTrangThai() : "Đang kinh doanh");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("ThuocDAO.themThuoc DB error: " + e.getMessage());
            }
        }
        MOCK_THUOC.add(th);
        return dbOk || conn == null;
    }

    public boolean capNhatThuoc(Thuoc th) {
        if (th == null || th.getMaThuoc() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE Thuoc SET tenThuoc = ?, hoatChat = ?, donViTinh = ?, gia = ?, donGia = ?, " +
                             "soLuongTon = ?, dinhMucToiThieu = ?, hanSuDung = ?, cachDungMacDinh = ?, trangThai = ? WHERE maThuoc = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, th.getTenThuoc());
                    stmt.setString(2, th.getHoatChat());
                    stmt.setString(3, th.getDonViTinh());
                    stmt.setDouble(4, th.getDonGia());
                    stmt.setDouble(5, th.getDonGia());
                    stmt.setInt(6, th.getSoLuongTon());
                    stmt.setInt(7, th.getDinhMucToiThieu());
                    stmt.setDate(8, th.getHanSuDung());
                    stmt.setString(9, th.getCachDungMacDinh());
                    stmt.setString(10, th.getTrangThai());
                    stmt.setString(11, th.getMaThuoc());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("ThuocDAO.capNhatThuoc DB error: " + e.getMessage());
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
        if (maThuoc == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM Thuoc WHERE maThuoc = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maThuoc);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("ThuocDAO.xoaThuoc DB error (foreign key dependency, fallback to marking inactive): " + e.getMessage());
                try {
                    String sqlUp = "UPDATE Thuoc SET trangThai = N'Ngừng kinh doanh' WHERE maThuoc = ?";
                    try (PreparedStatement stmtUp = conn.prepareStatement(sqlUp)) {
                        stmtUp.setString(1, maThuoc);
                        dbOk = stmtUp.executeUpdate() > 0;
                    }
                } catch (Exception ignored) {}
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
        if (maThuoc == null) return false;
        List<Thuoc> all = getAllThuoc();
        for (Thuoc th : all) {
            if (th.getMaThuoc().equalsIgnoreCase(maThuoc)) {
                int slMoi = Math.max(0, th.getSoLuongTon() + soLuongThayDoi);
                th.setSoLuongTon(slMoi);
                th.setTrangThai(slMoi <= 0 ? "Hết hàng" : (slMoi <= th.getDinhMucToiThieu() ? "Cảnh báo hết hàng" : "Đang kinh doanh"));
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
                    if (n > max) max = n;
                } catch (Exception ignored) {}
            }
        }
        return String.format("TH%03d", max + 1);
    }
}
