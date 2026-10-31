package dao;

import connectDB.ConnectDB;
import entity.LichHen;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Lịch Hẹn Khám Bệnh
 */
public class LichHenDAO {

    private static final List<LichHen> MOCK_LICH_HEN = new ArrayList<>();

    static {
        LichHen lh1 = new LichHen("LH001", "BN001", "NV001", Date.valueOf("2026-10-24"), Time.valueOf("08:30:00"),
                "Cấy ghép Implant Straumann R46", "Bệnh nhân đã chụp CT ConeBeam", "Đang khám");
        lh1.setTenBenhNhan("Nguyễn Văn Tuấn");
        lh1.setSdtBenhNhan("0901234567");
        lh1.setTenNhaSi("BS. CKII Nguyễn Văn An");
        MOCK_LICH_HEN.add(lh1);

        LichHen lh2 = new LichHen("LH002", "BN002", "NV002", Date.valueOf("2026-10-24"), Time.valueOf("09:15:00"),
                "Tái khám siết dây cung niềng răng", "Thay thun màu xanh ngọc", "Đã xác nhận");
        lh2.setTenBenhNhan("Trần Mai Phương");
        lh2.setSdtBenhNhan("0909876543");
        lh2.setTenNhaSi("ThS. BS Lê Thị Thảo");
        MOCK_LICH_HEN.add(lh2);

        LichHen lh3 = new LichHen("LH003", "BN003", "NV003", Date.valueOf("2026-10-24"), Time.valueOf("10:00:00"),
                "Nhổ răng khôn mọc ngầm R38", "Chụp Panorex trước tiểu phẫu", "Đã xác nhận");
        lh3.setTenBenhNhan("Phạm Quốc Dũng");
        lh3.setSdtBenhNhan("0988776655");
        lh3.setTenNhaSi("BS. Trần Minh Trí");
        MOCK_LICH_HEN.add(lh3);

        LichHen lh4 = new LichHen("LH004", "BN004", "NV002", Date.valueOf("2026-10-24"), Time.valueOf("14:00:00"),
                "Thử sườn răng sứ Cercon 4 răng cửa", "Bảo hành 10 năm", "Đã lên lịch");
        lh4.setTenBenhNhan("Vũ Bích Ngọc");
        lh4.setSdtBenhNhan("0977665544");
        lh4.setTenNhaSi("ThS. BS Lê Thị Thảo");
        MOCK_LICH_HEN.add(lh4);
    }

    public List<LichHen> getAllLichHen() {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                        +
                        "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, nv.hoTen AS tenNhaSi " +
                        "FROM LichHen lh " +
                        "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN " +
                        "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV " +
                        "ORDER BY lh.ngayHen DESC, lh.gioHen ASC";
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        LichHen lh = new LichHen(
                                rs.getString("maLichHen"),
                                rs.getString("maBN"),
                                rs.getString("maNhaSi"),
                                rs.getDate("ngayHen"),
                                rs.getTime("gioHen"),
                                rs.getString("lyDoKham"),
                                rs.getString("ghiChu"),
                                rs.getString("trangThai"));
                        lh.setTenBenhNhan(rs.getString("tenBenhNhan"));
                        lh.setSdtBenhNhan(rs.getString("sdtBenhNhan"));
                        lh.setTenNhaSi(rs.getString("tenNhaSi"));
                        list.add(lh);
                    }
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.getAllLichHen DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_LICH_HEN);
        }
        return list;
    }

    public List<LichHen> getLichHenHomNay() {
        return getLichHenTheoNgay(new Date(System.currentTimeMillis()));
    }

    public List<LichHen> getLichHenTheoNgay(Date ngay) {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null && ngay != null) {
            try {
                String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, " +
                             "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, nv.hoTen AS tenNhaSi " +
                             "FROM LichHen lh " +
                             "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN " +
                             "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV " +
                             "WHERE lh.ngayHen = ? " +
                             "ORDER BY lh.gioHen ASC";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDate(1, ngay);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            LichHen lh = new LichHen(
                                    rs.getString("maLichHen"),
                                    rs.getString("maBN"),
                                    rs.getString("maNhaSi"),
                                    rs.getDate("ngayHen"),
                                    rs.getTime("gioHen"),
                                    rs.getString("lyDoKham"),
                                    rs.getString("ghiChu"),
                                    rs.getString("trangThai"));
                            lh.setTenBenhNhan(rs.getString("tenBenhNhan"));
                            lh.setSdtBenhNhan(rs.getString("sdtBenhNhan"));
                            lh.setTenNhaSi(rs.getString("tenNhaSi"));
                            list.add(lh);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.getLichHenTheoNgay error: " + e.getMessage());
            }
        }
        if (list.isEmpty() && conn == null) {
            String ngayStr = ngay != null ? ngay.toString() : "";
            for (LichHen lh : MOCK_LICH_HEN) {
                if (lh.getNgayHen() != null && lh.getNgayHen().toString().equals(ngayStr)) {
                    list.add(lh);
                }
            }
            if (list.isEmpty()) {
                list.addAll(MOCK_LICH_HEN);
            }
        }
        return list;
    }

    public boolean kiemTraTrungLichBacSi(String maNhaSi, Date ngayHen, Time gioHen, String maLichHenBoQua) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null && maNhaSi != null && ngayHen != null && gioHen != null) {
            try {
                String sql = "SELECT COUNT(*) FROM LichHen " +
                             "WHERE maNhaSi = ? AND ngayHen = ? AND gioHen = ? " +
                             "  AND trangThai NOT IN (?, ?) ";
                if (maLichHenBoQua != null && !maLichHenBoQua.trim().isEmpty()) {
                    sql += " AND maLichHen != ? ";
                }
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maNhaSi);
                    stmt.setDate(2, ngayHen);
                    stmt.setTime(3, gioHen);
                    stmt.setString(4, entity.TrangThaiConstants.LH_DA_HUY);
                    stmt.setString(5, entity.TrangThaiConstants.LH_VANG_MAT);
                    if (maLichHenBoQua != null && !maLichHenBoQua.trim().isEmpty()) {
                        stmt.setString(6, maLichHenBoQua);
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) return rs.getInt(1) > 0;
                    }
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.kiemTraTrungLichBacSi error: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean themLichHen(LichHen lh) {
        if (lh == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO LichHen (maLichHen, maBN, maNhaSi, ngayHen, gioHen, lyDoKham, ghiChu, trangThai) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, lh.getMaLichHen());
                    stmt.setString(2, lh.getMaBN());
                    stmt.setString(3, lh.getMaNhaSi());
                    stmt.setDate(4, lh.getNgayHen());
                    stmt.setTime(5, lh.getGioHen());
                    stmt.setString(6, lh.getLyDoKham());
                    stmt.setString(7, lh.getGhiChu());
                    stmt.setString(8, lh.getTrangThai() != null ? lh.getTrangThai() : "Đã lên lịch");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.themLichHen DB error: " + e.getMessage());
            }
        }
        MOCK_LICH_HEN.add(0, lh);
        return dbOk || conn == null;
    }

    public boolean capNhatTrangThai(String maLichHen, String trangThai) {
        if (maLichHen == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE LichHen SET trangThai = ? WHERE maLichHen = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, trangThai);
                    stmt.setString(2, maLichHen);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.capNhatTrangThai DB error: " + e.getMessage());
            }
        }
        for (LichHen lh : MOCK_LICH_HEN) {
            if (lh.getMaLichHen().equalsIgnoreCase(maLichHen)) {
                lh.setTrangThai(trangThai);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean doiLichHen(String maLichHen, Date ngayMoi, Time gioMoi) {
        if (maLichHen == null || ngayMoi == null || gioMoi == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE LichHen SET ngayHen = ?, gioHen = ? WHERE maLichHen = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDate(1, ngayMoi);
                    stmt.setTime(2, gioMoi);
                    stmt.setString(3, maLichHen);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.doiLichHen DB error: " + e.getMessage());
            }
        }
        for (LichHen lh : MOCK_LICH_HEN) {
            if (lh.getMaLichHen().equalsIgnoreCase(maLichHen)) {
                lh.setNgayHen(ngayMoi);
                lh.setGioHen(gioMoi);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean xoaLichHen(String maLichHen) {
        if (maLichHen == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM LichHen WHERE maLichHen = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maLichHen);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.xoaLichHen DB error: " + e.getMessage());
            }
        }
        final String fMa = maLichHen;
        MOCK_LICH_HEN.removeIf(l -> l.getMaLichHen().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
    }

    public List<LichHen> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllLichHen();
        }
        String clean = keyword.trim().toLowerCase();
        List<LichHen> all = getAllLichHen();
        List<LichHen> res = new ArrayList<>();
        for (LichHen lh : all) {
            boolean match = (lh.getMaLichHen() != null && lh.getMaLichHen().toLowerCase().contains(clean)) ||
                            (lh.getTenBenhNhan() != null && lh.getTenBenhNhan().toLowerCase().contains(clean)) ||
                            (lh.getTenNhaSi() != null && lh.getTenNhaSi().toLowerCase().contains(clean)) ||
                            (lh.getLyDoKham() != null && lh.getLyDoKham().toLowerCase().contains(clean)) ||
                            (lh.getTrangThai() != null && lh.getTrangThai().toLowerCase().contains(clean));
            if (match) res.add(lh);
        }
        return res;
    }

    public String sinhMaLichHenMoi() {
        int max = 0;
        List<LichHen> all = getAllLichHen();
        for (LichHen lh : all) {
            String ma = lh.getMaLichHen();
            if (ma != null && ma.toUpperCase().startsWith("LH")) {
                try {
                    int n = Integer.parseInt(ma.substring(2).trim());
                    if (n > max) max = n;
                } catch (Exception ignored) {}
            }
        }
        return String.format("LH%03d", max + 1);
    }
}
