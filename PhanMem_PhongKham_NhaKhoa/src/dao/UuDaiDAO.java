package dao;

import connectDB.ConnectDB;
import entity.UuDai;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Chính Sách Khuyến Mãi & Ưu Đãi Khách Hàng từ CSDL SQL
 * Server thật
 */
public class UuDaiDAO {

    public List<UuDai> getAllUuDai() {
        List<UuDai> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maUuDai, tenUuDai, phanTramGiam, dieuKien, ngayBD, ngayKT, trangThai FROM UuDai ORDER BY maUuDai";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UuDai ud = new UuDai(
                            rs.getString("maUuDai"),
                            rs.getString("tenUuDai"),
                            rs.getDouble("phanTramGiam"),
                            rs.getString("dieuKien"),
                            rs.getDate("ngayBD"),
                            rs.getDate("ngayKT"),
                            rs.getString("trangThai"));
                    list.add(ud);
                }
            }
        } catch (Exception e) {
            System.err.println("UuDaiDAO.getAllUuDai DB error: " + e.getMessage());
        }
        return list;
    }

    public List<UuDai> getUuDaiDangApDung() {
        List<UuDai> all = getAllUuDai();
        List<UuDai> res = new ArrayList<>();
        for (UuDai ud : all) {
            String tt = ud.getTrangThai();
            if (tt == null || tt.trim().isEmpty() || "Đang áp dụng".equalsIgnoreCase(tt)
                    || "Hoạt động".equalsIgnoreCase(tt) || "Áp dụng".equalsIgnoreCase(tt)) {
                res.add(ud);
            }
        }
        return res;
    }

    public UuDai getUuDaiByMa(String maUuDai) {
        if (maUuDai == null)
            return null;
        for (UuDai ud : getAllUuDai()) {
            if (maUuDai.equalsIgnoreCase(ud.getMaUuDai())) {
                return ud;
            }
        }
        return null;
    }

    public boolean themUuDai(UuDai ud) {
        if (ud == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO UuDai (maUuDai, tenUuDai, loaiGiamGia, phanTramGiam, dieuKien, thoiGianBatDau, thoiGianKetThuc, ngayBD, ngayKT, trangThai) "
                    + "VALUES (?, ?, N'Phần trăm', ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ud.getMaUuDai());
                stmt.setString(2, ud.getTenUuDai());
                stmt.setDouble(3, ud.getPhanTramGiam());
                stmt.setString(4, ud.getDieuKien() != null ? ud.getDieuKien() : "");
                Timestamp tsBD = ud.getNgayBD() != null ? new Timestamp(ud.getNgayBD().getTime())
                        : new Timestamp(System.currentTimeMillis());
                Timestamp tsKT = ud.getNgayKT() != null ? new Timestamp(ud.getNgayKT().getTime())
                        : new Timestamp(System.currentTimeMillis() + 30L * 86400000);
                stmt.setTimestamp(5, tsBD);
                stmt.setTimestamp(6, tsKT);
                stmt.setDate(7, ud.getNgayBD() != null ? ud.getNgayBD() : new Date(System.currentTimeMillis()));
                stmt.setDate(8, ud.getNgayKT() != null ? ud.getNgayKT()
                        : new Date(System.currentTimeMillis() + 30L * 86400000));
                stmt.setString(9, ud.getTrangThai() != null ? ud.getTrangThai() : "Đang áp dụng");
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("UuDaiDAO.themUuDai DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatUuDai(UuDai ud) {
        if (ud == null || ud.getMaUuDai() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE UuDai SET tenUuDai = ?, phanTramGiam = ?, dieuKien = ?, ngayBD = ?, ngayKT = ?, " +
                    "thoiGianBatDau = ?, thoiGianKetThuc = ?, trangThai = ? WHERE maUuDai = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ud.getTenUuDai());
                stmt.setDouble(2, ud.getPhanTramGiam());
                stmt.setString(3, ud.getDieuKien());
                stmt.setDate(4, ud.getNgayBD());
                stmt.setDate(5, ud.getNgayKT());
                Timestamp tsBD = ud.getNgayBD() != null ? new Timestamp(ud.getNgayBD().getTime())
                        : new Timestamp(System.currentTimeMillis());
                Timestamp tsKT = ud.getNgayKT() != null ? new Timestamp(ud.getNgayKT().getTime())
                        : new Timestamp(System.currentTimeMillis() + 30L * 86400000);
                stmt.setTimestamp(6, tsBD);
                stmt.setTimestamp(7, tsKT);
                stmt.setString(8, ud.getTrangThai());
                stmt.setString(9, ud.getMaUuDai());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("UuDaiDAO.capNhatUuDai DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean doiTrangThai(String maUuDai, String trangThaiMoi) {
        if (maUuDai == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE UuDai SET trangThai = ? WHERE maUuDai = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, trangThaiMoi);
                stmt.setString(2, maUuDai);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("UuDaiDAO.doiTrangThai DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaUuDai(String maUuDai) {
        if (maUuDai == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "DELETE FROM UuDai WHERE maUuDai = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maUuDai);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("UuDaiDAO.xoaUuDai DB error: " + e.getMessage());
            return false;
        }
    }

    public List<UuDai> timKiemUuDai(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUuDai();
        }
        String cleanKeyword = keyword.trim();
        List<UuDai> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maUuDai, tenUuDai, phanTramGiam, dieuKien, ngayBD, ngayKT, trangThai FROM UuDai " +
                    "WHERE maUuDai LIKE ? OR tenUuDai LIKE ? OR dieuKien LIKE ? OR trangThai LIKE ? " +
                    "ORDER BY maUuDai";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String param = "%" + cleanKeyword + "%";
                for (int i = 1; i <= 4; i++) {
                    stmt.setString(i, param);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new UuDai(
                                rs.getString("maUuDai"),
                                rs.getString("tenUuDai"),
                                rs.getDouble("phanTramGiam"),
                                rs.getString("dieuKien"),
                                rs.getDate("ngayBD"),
                                rs.getDate("ngayKT"),
                                rs.getString("trangThai")));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("UuDaiDAO.timKiemUuDai DB error: " + e.getMessage());
        }
        return list;
    }

    public String sinhMaUDMoi() {
        return sinhMaUuDaiMoi();
    }

    public String sinhMaUuDaiMoi() {
        Connection conn = ConnectDB.getConnection();
        int maxIndex = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maUuDai FROM UuDai";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maUuDai");
                        if (ma != null && ma.toUpperCase().startsWith("UD")) {
                            try {
                                int num = Integer.parseInt(ma.substring(2).trim());
                                if (num > maxIndex) maxIndex = num;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.sinhMaUuDaiMoi error: " + e.getMessage());
            }
        }
        return String.format("UD%03d", maxIndex + 1);
    }
}
