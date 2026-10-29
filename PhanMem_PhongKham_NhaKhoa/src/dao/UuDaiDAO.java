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
 * Lớp truy xuất dữ liệu Chính Sách Khuyến Mãi & Ưu Đãi Khách Hàng
 * Hỗ trợ đồng thời CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class UuDaiDAO {

    private static final List<UuDai> MOCK_UU_DAI = new ArrayList<>();

    static {
        MOCK_UU_DAI.add(new UuDai("UD001", "Chào Hè Nụ Cười Xinh", 15, "Áp dụng niềng răng & bọc sứ", Date.valueOf("2026-06-01"), Date.valueOf("2026-08-31"), "Đang áp dụng"));
        MOCK_UU_DAI.add(new UuDai("UD002", "Tri Ân Khách Hàng Thân Thiết", 10, "Áp dụng khách có thẻ VIP", Date.valueOf("2026-01-01"), Date.valueOf("2026-12-31"), "Đang áp dụng"));
        MOCK_UU_DAI.add(new UuDai("UD003", "Trợ Giá Trồng Răng Implant", 20, "Cho bệnh nhân từ 50 tuổi", Date.valueOf("2026-09-01"), Date.valueOf("2026-10-31"), "Đang áp dụng"));
        MOCK_UU_DAI.add(new UuDai("UD004", "Tẩy Trắng Răng Đón Tết", 25, "Tất cả khách hàng", Date.valueOf("2026-01-01"), Date.valueOf("2026-02-15"), "Hết hạn"));
    }

    public List<UuDai> getAllUuDai() {
        List<UuDai> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maUuDai, tenUuDai, phanTramGiam, dieuKien, ngayBD, ngayKT, trangThai FROM UuDai ORDER BY maUuDai";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        UuDai ud = new UuDai(
                            rs.getString("maUuDai"),
                            rs.getString("tenUuDai"),
                            rs.getDouble("phanTramGiam"),
                            rs.getString("dieuKien"),
                            rs.getDate("ngayBD"),
                            rs.getDate("ngayKT"),
                            rs.getString("trangThai")
                        );
                        list.add(ud);
                    }
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.getAllUuDai DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_UU_DAI);
        }
        return list;
    }

    public List<UuDai> getUuDaiDangApDung() {
        List<UuDai> all = getAllUuDai();
        List<UuDai> res = new ArrayList<>();
        for (UuDai ud : all) {
            if ("Đang áp dụng".equalsIgnoreCase(ud.getTrangThai())) {
                res.add(ud);
            }
        }
        return res;
    }

    public boolean themUuDai(UuDai ud) {
        if (ud == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO UuDai (maUuDai, tenUuDai, loaiGiamGia, phanTramGiam, dieuKien, thoiGianBatDau, thoiGianKetThuc, ngayBD, ngayKT, trangThai) " +
                             "VALUES (?, ?, N'Phần trăm', ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, ud.getMaUuDai());
                    stmt.setString(2, ud.getTenUuDai());
                    stmt.setDouble(3, ud.getPhanTramGiam());
                    stmt.setString(4, ud.getDieuKien() != null ? ud.getDieuKien() : "");
                    Timestamp tsBD = ud.getNgayBD() != null ? new Timestamp(ud.getNgayBD().getTime()) : new Timestamp(System.currentTimeMillis());
                    Timestamp tsKT = ud.getNgayKT() != null ? new Timestamp(ud.getNgayKT().getTime()) : new Timestamp(System.currentTimeMillis() + 30L * 86400000);
                    stmt.setTimestamp(5, tsBD);
                    stmt.setTimestamp(6, tsKT);
                    stmt.setDate(7, ud.getNgayBD() != null ? ud.getNgayBD() : new Date(System.currentTimeMillis()));
                    stmt.setDate(8, ud.getNgayKT() != null ? ud.getNgayKT() : new Date(System.currentTimeMillis() + 30L * 86400000));
                    stmt.setString(9, ud.getTrangThai() != null ? ud.getTrangThai() : "Đang áp dụng");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.themUuDai DB error: " + e.getMessage());
            }
        }
        boolean exists = false;
        for (int i = 0; i < MOCK_UU_DAI.size(); i++) {
            if (MOCK_UU_DAI.get(i).getMaUuDai().equalsIgnoreCase(ud.getMaUuDai())) {
                MOCK_UU_DAI.set(i, ud);
                exists = true;
                break;
            }
        }
        if (!exists) {
            MOCK_UU_DAI.add(ud);
        }
        return dbOk || conn == null;
    }

    public boolean capNhatUuDai(UuDai ud) {
        if (ud == null || ud.getMaUuDai() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE UuDai SET tenUuDai = ?, phanTramGiam = ?, dieuKien = ?, ngayBD = ?, ngayKT = ?, " +
                             "thoiGianBatDau = ?, thoiGianKetThuc = ?, trangThai = ? WHERE maUuDai = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, ud.getTenUuDai());
                    stmt.setDouble(2, ud.getPhanTramGiam());
                    stmt.setString(3, ud.getDieuKien());
                    stmt.setDate(4, ud.getNgayBD());
                    stmt.setDate(5, ud.getNgayKT());
                    Timestamp tsBD = ud.getNgayBD() != null ? new Timestamp(ud.getNgayBD().getTime()) : new Timestamp(System.currentTimeMillis());
                    Timestamp tsKT = ud.getNgayKT() != null ? new Timestamp(ud.getNgayKT().getTime()) : new Timestamp(System.currentTimeMillis() + 30L * 86400000);
                    stmt.setTimestamp(6, tsBD);
                    stmt.setTimestamp(7, tsKT);
                    stmt.setString(8, ud.getTrangThai());
                    stmt.setString(9, ud.getMaUuDai());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.capNhatUuDai DB error: " + e.getMessage());
            }
        }
        for (int i = 0; i < MOCK_UU_DAI.size(); i++) {
            if (MOCK_UU_DAI.get(i).getMaUuDai().equalsIgnoreCase(ud.getMaUuDai())) {
                MOCK_UU_DAI.set(i, ud);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean doiTrangThai(String maUuDai, String trangThaiMoi) {
        if (maUuDai == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE UuDai SET trangThai = ? WHERE maUuDai = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, trangThaiMoi);
                    stmt.setString(2, maUuDai);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.doiTrangThai DB error: " + e.getMessage());
            }
        }
        for (UuDai ud : MOCK_UU_DAI) {
            if (ud.getMaUuDai().equalsIgnoreCase(maUuDai)) {
                ud.setTrangThai(trangThaiMoi);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean xoaUuDai(String maUuDai) {
        if (maUuDai == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM UuDai WHERE maUuDai = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maUuDai);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.xoaUuDai DB error: " + e.getMessage());
            }
        }
        MOCK_UU_DAI.removeIf(ud -> ud.getMaUuDai().equalsIgnoreCase(maUuDai));
        return dbOk || conn == null;
    }

    public List<UuDai> timKiemUuDai(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUuDai();
        }
        String cleanKeyword = keyword.trim();
        List<UuDai> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
                                rs.getString("trangThai")
                            ));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("UuDaiDAO.timKiemUuDai DB error: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            String lower = cleanKeyword.toLowerCase();
            for (UuDai ud : MOCK_UU_DAI) {
                boolean match = (ud.getMaUuDai() != null && ud.getMaUuDai().toLowerCase().contains(lower)) ||
                                (ud.getTenUuDai() != null && ud.getTenUuDai().toLowerCase().contains(lower)) ||
                                (ud.getDieuKien() != null && ud.getDieuKien().toLowerCase().contains(lower)) ||
                                (ud.getTrangThai() != null && ud.getTrangThai().toLowerCase().contains(lower));
                if (match) {
                    list.add(ud);
                }
            }
        }
        return list;
    }

    public String sinhMaUDMoi() {
        return sinhMaUuDaiMoi();
    }

    public String sinhMaUuDaiMoi() {
        int maxIndex = 0;
        List<UuDai> all = getAllUuDai();
        for (UuDai ud : all) {
            String ma = ud.getMaUuDai();
            if (ma != null && ma.toUpperCase().startsWith("UD")) {
                try {
                    int num = Integer.parseInt(ma.substring(2).trim());
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (Exception ignored) {}
            }
        }
        return String.format("UD%03d", maxIndex + 1);
    }
}
