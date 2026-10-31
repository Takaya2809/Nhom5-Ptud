package dao;

import connectDB.ConnectDB;
import entity.CaTruc;
import entity.PhanCongCaTruc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Ca Trực & Phân Công Ca
 * Hỗ trợ đồng thời CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class CaTrucDAO {

    private static final List<CaTruc> MOCK_CA_TRUC = new ArrayList<>();
    private static final List<PhanCongCaTruc> MOCK_PHAN_CONG = new ArrayList<>();

    static {
        MOCK_CA_TRUC.add(new CaTruc("CA_20261023_SANG", "Ca Sáng", Date.valueOf("2026-10-23"), Time.valueOf("08:00:00"), Time.valueOf("12:00:00"), 3, 2, 25, "Đang trực"));
        MOCK_CA_TRUC.add(new CaTruc("CA_20261023_CHIEU", "Ca Chiều", Date.valueOf("2026-10-23"), Time.valueOf("13:30:00"), Time.valueOf("17:30:00"), 3, 2, 30, "Đã lên lịch"));
        MOCK_CA_TRUC.add(new CaTruc("CA_20261023_TOI", "Ca Tối", Date.valueOf("2026-10-23"), Time.valueOf("18:00:00"), Time.valueOf("21:00:00"), 2, 1, 15, "Đã lên lịch"));
        MOCK_CA_TRUC.add(new CaTruc("CA_20261024_SANG", "Ca Sáng", Date.valueOf("2026-10-24"), Time.valueOf("08:00:00"), Time.valueOf("12:00:00"), 3, 2, 25, "Đã lên lịch"));

        PhanCongCaTruc pc1 = new PhanCongCaTruc(1, "NV001", "CA_20261023_SANG", "Bác sĩ chính", "Đã điểm danh", "Phụ trách phòng cấy ghép Implant - Ghế 01");
        pc1.setHoTenNV("BS. CKII Nguyễn Văn An");
        pc1.setTenCa("Ca Sáng (08:00 - 12:00)");
        MOCK_PHAN_CONG.add(pc1);

        PhanCongCaTruc pc2 = new PhanCongCaTruc(2, "NV002", "CA_20261023_SANG", "Bác sĩ chính", "Đã điểm danh", "Phụ trách chỉnh nha & thẩm mỹ - Ghế 02");
        pc2.setHoTenNV("ThS. BS Lê Thị Thảo");
        pc2.setTenCa("Ca Sáng (08:00 - 12:00)");
        MOCK_PHAN_CONG.add(pc2);

        PhanCongCaTruc pc3 = new PhanCongCaTruc(3, "NV003", "CA_20261023_SANG", "Bác sĩ chính", "Đã điểm danh", "Phụ trách tiểu phẫu răng khôn & nội nha - Ghế 03");
        pc3.setHoTenNV("BS. Trần Minh Trí");
        pc3.setTenCa("Ca Sáng (08:00 - 12:00)");
        MOCK_PHAN_CONG.add(pc3);

        PhanCongCaTruc pc4 = new PhanCongCaTruc(4, "LT001", "CA_20261023_SANG", "Lễ tân", "Đã điểm danh", "Tiếp đón & điều phối hàng đợi sảnh A");
        pc4.setHoTenNV("Phạm Ngọc Trâm");
        pc4.setTenCa("Ca Sáng (08:00 - 12:00)");
        MOCK_PHAN_CONG.add(pc4);

        PhanCongCaTruc pc5 = new PhanCongCaTruc(5, "YT001", "CA_20261023_SANG", "Phụ tá", "Đã điểm danh", "Hỗ trợ vô trùng & chuẩn bị dụng cụ mổ");
        pc5.setHoTenNV("Nguyễn Thị Kim Oanh");
        pc5.setTenCa("Ca Sáng (08:00 - 12:00)");
        MOCK_PHAN_CONG.add(pc5);
    }

    public List<CaTruc> getAllCaTruc() {
        List<CaTruc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soLuongBacSiToiThieu, soLuongLeTanToiThieu, soLuongKhamToiDa, trangThai FROM CaTruc ORDER BY ngayTruc DESC, gioBatDau ASC";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        list.add(new CaTruc(
                            rs.getString("maCa"),
                            rs.getString("tenCa"),
                            rs.getDate("ngayTruc"),
                            rs.getTime("gioBatDau"),
                            rs.getTime("gioKetThuc"),
                            rs.getInt("soLuongBacSiToiThieu"),
                            rs.getInt("soLuongLeTanToiThieu"),
                            rs.getInt("soLuongKhamToiDa"),
                            rs.getString("trangThai")
                        ));
                    }
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.getAllCaTruc DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty() && conn == null) {
            list.addAll(MOCK_CA_TRUC);
        }
        return list;
    }

    public boolean themCaTruc(CaTruc ct) {
        if (ct == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO CaTruc (maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soLuongBacSiToiThieu, soLuongLeTanToiThieu, soLuongKhamToiDa, trangThai) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, ct.getMaCa());
                    stmt.setString(2, ct.getTenCa());
                    stmt.setDate(3, ct.getNgayTruc());
                    stmt.setTime(4, ct.getGioBatDau());
                    stmt.setTime(5, ct.getGioKetThuc());
                    stmt.setInt(6, ct.getSoLuongBacSiToiThieu());
                    stmt.setInt(7, ct.getSoLuongLeTanToiThieu());
                    stmt.setInt(8, ct.getSoLuongKhamToiDa());
                    stmt.setString(9, ct.getTrangThai() != null ? ct.getTrangThai() : "Đã lên lịch");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.themCaTruc DB error: " + e.getMessage());
            }
        }
        MOCK_CA_TRUC.add(ct);
        return dbOk || conn == null;
    }

    public boolean capNhatCaTruc(CaTruc ct) {
        if (ct == null || ct.getMaCa() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE CaTruc SET tenCa = ?, ngayTruc = ?, gioBatDau = ?, gioKetThuc = ?, " +
                             "soLuongBacSiToiThieu = ?, soLuongLeTanToiThieu = ?, soLuongKhamToiDa = ?, trangThai = ? WHERE maCa = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, ct.getTenCa());
                    stmt.setDate(2, ct.getNgayTruc());
                    stmt.setTime(3, ct.getGioBatDau());
                    stmt.setTime(4, ct.getGioKetThuc());
                    stmt.setInt(5, ct.getSoLuongBacSiToiThieu());
                    stmt.setInt(6, ct.getSoLuongLeTanToiThieu());
                    stmt.setInt(7, ct.getSoLuongKhamToiDa());
                    stmt.setString(8, ct.getTrangThai());
                    stmt.setString(9, ct.getMaCa());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.capNhatCaTruc DB error: " + e.getMessage());
            }
        }
        for (int i = 0; i < MOCK_CA_TRUC.size(); i++) {
            if (MOCK_CA_TRUC.get(i).getMaCa().equalsIgnoreCase(ct.getMaCa())) {
                MOCK_CA_TRUC.set(i, ct);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public List<PhanCongCaTruc> getAllPhanCong() {
        List<PhanCongCaTruc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT pc.maPhanCong, pc.maNV, pc.maCa, pc.vaiTroTrongCa, pc.trangThaiDiemDanh, pc.ghiChu, " +
                             "nv.hoTen, ct.tenCa, ct.gioBatDau, ct.gioKetThuc " +
                             "FROM PhanCongCaTruc pc " +
                             "LEFT JOIN NhanVien nv ON pc.maNV = nv.maNV " +
                             "LEFT JOIN CaTruc ct ON pc.maCa = ct.maCa " +
                             "ORDER BY pc.maPhanCong DESC";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        PhanCongCaTruc pc = new PhanCongCaTruc(
                            rs.getInt("maPhanCong"),
                            rs.getString("maNV"),
                            rs.getString("maCa"),
                            rs.getString("vaiTroTrongCa"),
                            rs.getString("trangThaiDiemDanh"),
                            rs.getString("ghiChu")
                        );
                        pc.setHoTenNV(rs.getString("hoTen"));
                        String tenCa = rs.getString("tenCa");
                        Time gb = rs.getTime("gioBatDau");
                        Time gk = rs.getTime("gioKetThuc");
                        if (tenCa != null) {
                            pc.setTenCa(tenCa + (gb != null && gk != null ? " (" + gb.toString().substring(0, 5) + " - " + gk.toString().substring(0, 5) + ")" : ""));
                        }
                        list.add(pc);
                    }
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.getAllPhanCong DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty() && conn == null) {
            list.addAll(MOCK_PHAN_CONG);
        }
        return list;
    }

    public List<PhanCongCaTruc> getPhanCongByCa(String maCa) {
        List<PhanCongCaTruc> all = getAllPhanCong();
        List<PhanCongCaTruc> res = new ArrayList<>();
        for (PhanCongCaTruc pc : all) {
            if (pc.getMaCa() != null && pc.getMaCa().equalsIgnoreCase(maCa)) {
                res.add(pc);
            }
        }
        return res;
    }

    public List<PhanCongCaTruc> getPhanCongTheoNV(String maNV) {
        List<PhanCongCaTruc> all = getAllPhanCong();
        if (maNV == null || maNV.trim().isEmpty()) return all;
        List<PhanCongCaTruc> res = new ArrayList<>();
        for (PhanCongCaTruc pc : all) {
            if (pc.getMaNV() != null && pc.getMaNV().equalsIgnoreCase(maNV)) {
                res.add(pc);
            }
        }
        return res;
    }

    public List<PhanCongCaTruc> getPhanCongHomNay() {
        List<PhanCongCaTruc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT pc.maPhanCong, pc.maNV, pc.maCa, pc.vaiTroTrongCa, pc.trangThaiDiemDanh, pc.ghiChu, " +
                             "nv.hoTen, ct.tenCa, ct.gioBatDau, ct.gioKetThuc, " +
                             "CASE WHEN CAST(GETDATE() AS TIME) BETWEEN ct.gioBatDau AND ct.gioKetThuc THEN 1 ELSE 0 END AS DangDienRa " +
                             "FROM PhanCongCaTruc pc " +
                             "JOIN CaTruc ct ON pc.maCa = ct.maCa " +
                             "LEFT JOIN NhanVien nv ON pc.maNV = nv.maNV " +
                             "WHERE ct.ngayTruc = CAST(GETDATE() AS DATE) " +
                             "ORDER BY DangDienRa DESC, ct.gioBatDau ASC";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        PhanCongCaTruc pc = new PhanCongCaTruc(
                            rs.getInt("maPhanCong"),
                            rs.getString("maNV"),
                            rs.getString("maCa"),
                            rs.getString("vaiTroTrongCa"),
                            rs.getString("trangThaiDiemDanh"),
                            rs.getString("ghiChu")
                        );
                        pc.setHoTenNV(rs.getString("hoTen"));
                        String tenCa = rs.getString("tenCa");
                        Time gb = rs.getTime("gioBatDau");
                        Time gk = rs.getTime("gioKetThuc");
                        if (tenCa != null) {
                            pc.setTenCa(tenCa + (gb != null && gk != null ? " (" + gb.toString().substring(0, 5) + " - " + gk.toString().substring(0, 5) + ")" : ""));
                        }
                        list.add(pc);
                    }
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.getPhanCongHomNay error: " + e.getMessage());
            }
        }
        return list;
    }

    public boolean kiemTraTrungCaNhanVien(String maNV, String maCaMoi) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null && maNV != null && maCaMoi != null) {
            try {
                String sql = "SELECT COUNT(*) FROM PhanCongCaTruc pc " +
                             "JOIN CaTruc ct ON pc.maCa = ct.maCa " +
                             "JOIN CaTruc ctMoi ON ctMoi.maCa = ? " +
                             "WHERE pc.maNV = ? AND ct.ngayTruc = ctMoi.ngayTruc " +
                             "  AND ((ctMoi.gioBatDau < ct.gioKetThuc AND ctMoi.gioKetThuc > ct.gioBatDau))";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maCaMoi);
                    stmt.setString(2, maNV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) return rs.getInt(1) > 0;
                    }
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.kiemTraTrungCaNhanVien error: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean themPhanCong(PhanCongCaTruc pc) {
        if (pc == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO PhanCongCaTruc (maNV, maCa, vaiTroTrongCa, trangThaiDiemDanh, ghiChu) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, pc.getMaNV());
                    stmt.setString(2, pc.getMaCa());
                    stmt.setString(3, pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "Bác sĩ chính");
                    stmt.setString(4, pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : "Chưa đến");
                    stmt.setString(5, pc.getGhiChu() != null ? pc.getGhiChu() : "");
                    dbOk = stmt.executeUpdate() > 0;
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            pc.setMaPhanCong(rs.getInt(1));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.themPhanCong DB error: " + e.getMessage());
            }
        }
        if (pc.getMaPhanCong() <= 0) {
            pc.setMaPhanCong(MOCK_PHAN_CONG.size() + 1);
        }
        MOCK_PHAN_CONG.add(0, pc);
        return dbOk || conn == null;
    }

    public boolean xoaPhanCong(int maPhanCong) {
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM PhanCongCaTruc WHERE maPhanCong = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, maPhanCong);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.xoaPhanCong DB error: " + e.getMessage());
            }
        }
        MOCK_PHAN_CONG.removeIf(p -> p.getMaPhanCong() == maPhanCong);
        return dbOk || conn == null;
    }

    public boolean diemDanh(int maPhanCong, String trangThaiDiemDanh) {
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE PhanCongCaTruc SET trangThaiDiemDanh = ?, thoiGianDiemDanh = GETDATE() WHERE maPhanCong = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, trangThaiDiemDanh);
                    stmt.setInt(2, maPhanCong);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.diemDanh DB error: " + e.getMessage());
            }
        }
        for (PhanCongCaTruc p : MOCK_PHAN_CONG) {
            if (p.getMaPhanCong() == maPhanCong) {
                p.setTrangThaiDiemDanh(trangThaiDiemDanh);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean doiCa(int maPhanCong, String maNVMoi, String ghiChu) {
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE PhanCongCaTruc SET maNV = ?, ghiChu = ? WHERE maPhanCong = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maNVMoi);
                    stmt.setString(2, ghiChu);
                    stmt.setInt(3, maPhanCong);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.doiCa DB error: " + e.getMessage());
            }
        }
        for (PhanCongCaTruc p : MOCK_PHAN_CONG) {
            if (p.getMaPhanCong() == maPhanCong) {
                p.setMaNV(maNVMoi);
                p.setGhiChu(ghiChu);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean kiemTraDinhBien(String maCa) {
        List<PhanCongCaTruc> list = getPhanCongByCa(maCa);
        return list != null && !list.isEmpty();
    }

    public boolean xoaCaTruc(String maCa) {
        if (maCa == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM CaTruc WHERE maCa = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maCa);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.xoaCaTruc DB error: " + e.getMessage());
            }
        }
        final String fMa = maCa;
        MOCK_CA_TRUC.removeIf(c -> c.getMaCa().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
    }
}
