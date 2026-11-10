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
 * Lớp truy xuất dữ liệu Ca Trực & Phân Công Ca từ CSDL SQL Server thật
 */
public class CaTrucDAO {

    public List<CaTruc> getAllCaTruc() {
        List<CaTruc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soLuongBacSiToiThieu, soLuongLeTanToiThieu, soLuongKhamToiDa, trangThai FROM CaTruc ORDER BY ngayTruc DESC, gioBatDau ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
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
                            rs.getString("trangThai")));
                }
            }
            if (list.isEmpty()) {
                dongBoCaTrucTuanMacDinh(java.time.LocalDate.now().with(java.time.DayOfWeek.MONDAY));
                try (PreparedStatement stmt2 = conn.prepareStatement(sql);
                        ResultSet rs2 = stmt2.executeQuery()) {
                    while (rs2.next()) {
                        list.add(new CaTruc(
                                rs2.getString("maCa"),
                                rs2.getString("tenCa"),
                                rs2.getDate("ngayTruc"),
                                rs2.getTime("gioBatDau"),
                                rs2.getTime("gioKetThuc"),
                                rs2.getInt("soLuongBacSiToiThieu"),
                                rs2.getInt("soLuongLeTanToiThieu"),
                                rs2.getInt("soLuongKhamToiDa"),
                                rs2.getString("trangThai")));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.getAllCaTruc DB error: " + e.getMessage());
        }
        return list;
    }

    public void dongBoCaTrucTuanMacDinh(java.time.LocalDate startOfWeek) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || startOfWeek == null)
            return;
        try {
            String sqlCheck = "IF NOT EXISTS (SELECT 1 FROM CaTruc WHERE maCa = ?) " +
                    "INSERT INTO CaTruc (maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soLuongBacSiToiThieu, soLuongLeTanToiThieu, soLuongKhamToiDa, trangThai) "
                    +
                    "VALUES (?, ?, ?, ?, ?, 1, 1, 20, N'Đã lên lịch')";
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
            try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
                for (int d = 0; d < 7; d++) {
                    java.time.LocalDate day = startOfWeek.plusDays(d);
                    Date sqlDate = Date.valueOf(day);
                    String dayStr = day.format(dtf);

                    // Ca Sáng: 08:00 - 12:00
                    ps.setString(1, "CA_" + dayStr + "_SANG");
                    ps.setString(2, "CA_" + dayStr + "_SANG");
                    ps.setString(3, "Ca Sáng");
                    ps.setDate(4, sqlDate);
                    ps.setTime(5, Time.valueOf("08:00:00"));
                    ps.setTime(6, Time.valueOf("12:00:00"));
                    ps.executeUpdate();

                    // Ca Chiều: 13:30 - 17:30
                    ps.setString(1, "CA_" + dayStr + "_CHIEU");
                    ps.setString(2, "CA_" + dayStr + "_CHIEU");
                    ps.setString(3, "Ca Chiều");
                    ps.setDate(4, sqlDate);
                    ps.setTime(5, Time.valueOf("13:30:00"));
                    ps.setTime(6, Time.valueOf("17:30:00"));
                    ps.executeUpdate();

                    // Ca Tối: 18:00 - 21:00
                    ps.setString(1, "CA_" + dayStr + "_TOI");
                    ps.setString(2, "CA_" + dayStr + "_TOI");
                    ps.setString(3, "Ca Tối");
                    ps.setDate(4, sqlDate);
                    ps.setTime(5, Time.valueOf("18:00:00"));
                    ps.setTime(6, Time.valueOf("21:00:00"));
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.dongBoCaTrucTuanMacDinh error: " + e.getMessage());
        }
    }

    public boolean themCaTruc(CaTruc ct) {
        if (ct == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO CaTruc (maCa, tenCa, ngayTruc, gioBatDau, gioKetThuc, soLuongBacSiToiThieu, soLuongLeTanToiThieu, soLuongKhamToiDa, trangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.themCaTruc DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatCaTruc(CaTruc ct) {
        if (ct == null || ct.getMaCa() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

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
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.capNhatCaTruc DB error: " + e.getMessage());
            return false;
        }
    }

    public List<PhanCongCaTruc> getAllPhanCong() {
        List<PhanCongCaTruc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT pc.maPhanCong, pc.maNV, pc.maCa, pc.vaiTroTrongCa, pc.trangThaiDiemDanh, pc.ghiChu, "
                    + "nv.hoTen, ct.tenCa, ct.gioBatDau, ct.gioKetThuc, ct.ngayTruc "
                    + "FROM PhanCongCaTruc pc "
                    + "LEFT JOIN NhanVien nv ON pc.maNV = nv.maNV "
                    + "LEFT JOIN CaTruc ct ON pc.maCa = ct.maCa "
                    + "ORDER BY pc.maPhanCong DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    String hoTen = rs.getString("hoTen");
                    String maCa = rs.getString("maCa");
                    String tenCa = rs.getString("tenCa");
                    entity.NhanVien nv = maNV != null ? new entity.NhanVien(maNV, hoTen) : null;
                    entity.CaTruc ct = maCa != null ? new entity.CaTruc(maCa, tenCa) : null;
                    Time gb = rs.getTime("gioBatDau");
                    Time gk = rs.getTime("gioKetThuc");
                    Date nt = rs.getDate("ngayTruc");
                    if (ct != null) {
                        ct.setNgayTruc(nt);
                        ct.setGioBatDau(gb);
                        ct.setGioKetThuc(gk);
                    }

                    PhanCongCaTruc pc = new PhanCongCaTruc(
                            rs.getInt("maPhanCong"),
                            nv,
                            ct,
                            rs.getString("vaiTroTrongCa"),
                            rs.getString("trangThaiDiemDanh"),
                            rs.getString("ghiChu"));
                    pc.setHoTenNV(hoTen);
                    if (tenCa != null) {
                        pc.setTenCa(tenCa + (gb != null && gk != null
                                ? " (" + gb.toString().substring(0, 5) + " - " + gk.toString().substring(0, 5) + ")"
                                : ""));
                    }
                    list.add(pc);
                }
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.getAllPhanCong DB error: " + e.getMessage());
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
        if (maNV == null || maNV.trim().isEmpty())
            return all;
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
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT pc.maPhanCong, pc.maNV, pc.maCa, pc.vaiTroTrongCa, pc.trangThaiDiemDanh, pc.ghiChu, "
                    + "nv.hoTen, ct.tenCa, ct.gioBatDau, ct.gioKetThuc, "
                    + "CASE WHEN CAST(GETDATE() AS TIME) BETWEEN ct.gioBatDau AND ct.gioKetThuc THEN 1 ELSE 0 END AS DangDienRa "
                    + "FROM PhanCongCaTruc pc "
                    + "JOIN CaTruc ct ON pc.maCa = ct.maCa "
                    + "LEFT JOIN NhanVien nv ON pc.maNV = nv.maNV "
                    + "WHERE ct.ngayTruc = CAST(GETDATE() AS DATE) "
                    + "ORDER BY DangDienRa DESC, ct.gioBatDau ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    String hoTen = rs.getString("hoTen");
                    String maCa = rs.getString("maCa");
                    String tenCa = rs.getString("tenCa");
                    entity.NhanVien nv = maNV != null ? new entity.NhanVien(maNV, hoTen) : null;
                    entity.CaTruc ct = maCa != null ? new entity.CaTruc(maCa, tenCa) : null;

                    PhanCongCaTruc pc = new PhanCongCaTruc(
                            rs.getInt("maPhanCong"),
                            nv,
                            ct,
                            rs.getString("vaiTroTrongCa"),
                            rs.getString("trangThaiDiemDanh"),
                            rs.getString("ghiChu"));
                    pc.setHoTenNV(rs.getString("hoTen"));
                    Time gb = rs.getTime("gioBatDau");
                    Time gk = rs.getTime("gioKetThuc");
                    if (tenCa != null) {
                        pc.setTenCa(tenCa + (gb != null && gk != null
                                ? " (" + gb.toString().substring(0, 5) + " - " + gk.toString().substring(0, 5) + ")"
                                : ""));
                    }
                    list.add(pc);
                }
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.getPhanCongHomNay error: " + e.getMessage());
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
                        if (rs.next())
                            return rs.getInt(1) > 0;
                    }
                }
            } catch (Exception e) {
                System.err.println("CaTrucDAO.kiemTraTrungCaNhanVien error: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean themPhanCong(PhanCongCaTruc pc) {
        if (pc == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO PhanCongCaTruc (maNV, maCa, vaiTroTrongCa, trangThaiDiemDanh, ghiChu) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, pc.getMaNV());
                stmt.setString(2, pc.getMaCa());
                stmt.setString(3, pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "Bác sĩ chính");
                stmt.setString(4, pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : "Chưa đến");
                stmt.setString(5, pc.getGhiChu() != null ? pc.getGhiChu() : "");
                boolean ok = stmt.executeUpdate() > 0;
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pc.setMaPhanCong(rs.getInt(1));
                    }
                }
                return ok;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.themPhanCong DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaPhanCong(int maPhanCong) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "DELETE FROM PhanCongCaTruc WHERE maPhanCong = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, maPhanCong);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.xoaPhanCong DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean diemDanh(int maPhanCong, String trangThaiDiemDanh) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE PhanCongCaTruc SET trangThaiDiemDanh = ?, thoiGianDiemDanh = GETDATE() WHERE maPhanCong = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, trangThaiDiemDanh);
                stmt.setInt(2, maPhanCong);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.diemDanh DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean doiCa(int maPhanCong, String maNVMoi, String ghiChu) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE PhanCongCaTruc SET maNV = ?, ghiChu = ? WHERE maPhanCong = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maNVMoi);
                stmt.setString(2, ghiChu);
                stmt.setInt(3, maPhanCong);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.doiCa DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean kiemTraDinhBien(String maCa) {
        List<PhanCongCaTruc> list = getPhanCongByCa(maCa);
        return list != null && !list.isEmpty();
    }

    public boolean xoaCaTruc(String maCa) {
        if (maCa == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "DELETE FROM CaTruc WHERE maCa = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maCa);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("CaTrucDAO.xoaCaTruc DB error: " + e.getMessage());
            return false;
        }
    }
}
