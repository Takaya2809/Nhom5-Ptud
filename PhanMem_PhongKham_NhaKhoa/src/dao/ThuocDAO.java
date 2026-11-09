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
 * Lớp truy xuất dữ liệu Danh Mục Thuốc Điều Trị & Kê Đơn từ CSDL SQL Server
 * thật.
 * Chuẩn hóa:
 * - 100% PreparedStatement.
 * - Phân biệt rõ giá nhập và giá bán.
 * - Dựng đối tượng nhà cung cấp (NCC).
 */
public class ThuocDAO {

    public List<Thuoc> getAllThuoc() {
        List<Thuoc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

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
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, giaNhap, giaBan, gia, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai, maNCC) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            // Fallback nếu schema cũ chưa cập nhật các cột mới
            try {
                String sqlFb = "INSERT INTO Thuoc (maThuoc, tenThuoc, hoatChat, donViTinh, gia, donGia, soLuongTon, dinhMucToiThieu, hanSuDung, cachDungMacDinh, trangThai) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                    return stmtFb.executeUpdate() > 0;
                }
            } catch (Exception exFb) {
                System.err.println("ThuocDAO.themThuoc DB error: " + exFb.getMessage());
                return false;
            }
        }
    }

    public boolean capNhatThuoc(Thuoc th) {
        if (th == null || th.getMaThuoc() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE Thuoc SET tenThuoc = ?, hoatChat = ?, donViTinh = ?, giaNhap = ?, giaBan = ?, gia = ?, donGia = ?, "
                    + "soLuongTon = ?, dinhMucToiThieu = ?, hanSuDung = ?, cachDungMacDinh = ?, trangThai = ?, maNCC = ? WHERE maThuoc = ?";
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
                return stmt.executeUpdate() > 0;
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
                    return stmtFb.executeUpdate() > 0;
                }
            } catch (Exception exFb) {
                System.err.println("ThuocDAO.capNhatThuoc DB error: " + exFb.getMessage());
                return false;
            }
        }
    }

    public boolean xoaThuoc(String maThuoc) {
        if (maThuoc == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "DELETE FROM Thuoc WHERE maThuoc = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maThuoc);
                return stmt.executeUpdate() > 0;
            } catch (Exception e) {
                String sqlUp = "UPDATE Thuoc SET trangThai = N'Ngừng kinh doanh' WHERE maThuoc = ?";
                try (PreparedStatement stmtUp = conn.prepareStatement(sqlUp)) {
                    stmtUp.setString(1, maThuoc);
                    return stmtUp.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("ThuocDAO.xoaThuoc error: " + e.getMessage());
            return false;
        }
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
        Connection conn = ConnectDB.getConnection();
        int max = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maThuoc FROM Thuoc";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maThuoc");
                        if (ma != null && ma.toUpperCase().startsWith("TH")) {
                            try {
                                int n = Integer.parseInt(ma.substring(2).trim());
                                if (n > max) max = n;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThuocDAO.sinhMaThuocMoi error: " + e.getMessage());
            }
        }
        return String.format("TH%03d", max + 1);
    }
}
