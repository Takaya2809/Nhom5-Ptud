package dao;

import connectDB.ConnectDB;
import entity.DichVu;
import entity.LichSuGiaDichVu;
import entity.TrangThaiConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Danh Mục Dịch Vụ Nha Khoa & Lịch Sử Giá từ CSDL SQL
 * Server thật
 */
public class DichVuDAO {

    public List<DichVu> getAllDichVu() {
        List<DichVu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            // Kiểm tra xem bảng có cột nhomDichVu hay chưa
            String sql = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa, " +
                    "ISNULL(nhomDichVu, N'Khám & tư vấn') AS nhomDichVu FROM DichVu ORDER BY maDichVu";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DichVu dv = new DichVu(
                            rs.getString("maDichVu"),
                            rs.getString("tenDichVu"),
                            rs.getDouble("giaNiemYet"),
                            rs.getInt("thoiGianDuKien"),
                            rs.getString("trangThai"),
                            rs.getString("moTa"),
                            rs.getString("nhomDichVu"));
                    list.add(dv);
                }
            }
        } catch (Exception e) {
            // Fallback nếu chưa có cột nhomDichVu
            try {
                String sqlFallback = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa FROM DichVu ORDER BY maDichVu";
                try (PreparedStatement stmt = conn.prepareStatement(sqlFallback);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        DichVu dv = new DichVu(
                                rs.getString("maDichVu"),
                                rs.getString("tenDichVu"),
                                rs.getDouble("giaNiemYet"),
                                rs.getInt("thoiGianDuKien"),
                                rs.getString("trangThai"),
                                rs.getString("moTa"));
                        list.add(dv);
                    }
                }
            } catch (Exception ex2) {
                System.err.println("DichVuDAO.getAllDichVu error: " + ex2.getMessage());
            }
        }
        return list;
    }

    public List<DichVu> getDichVuHoatDong() {
        List<DichVu> all = getAllDichVu();
        List<DichVu> res = new ArrayList<>();
        for (DichVu dv : all) {
            if (TrangThaiConstants.DV_DANG_CUNG_CAP.equalsIgnoreCase(dv.getTrangThai())) {
                res.add(dv);
            }
        }
        return res;
    }

    public DichVu getDichVuByMa(String maDV) {
        if (maDV == null || maDV.trim().isEmpty())
            return null;
        maDV = maDV.trim();
        List<DichVu> all = getAllDichVu();
        for (DichVu dv : all) {
            if (dv.getMaDichVu().equalsIgnoreCase(maDV)) {
                return dv;
            }
        }
        return null;
    }

    public boolean themDichVu(DichVu dv) {
        if (dv == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa, nhomDichVu) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, dv.getMaDichVu());
                stmt.setString(2, dv.getTenDichVu());
                stmt.setDouble(3, dv.getGiaNiemYet());
                stmt.setDouble(4, dv.getGiaNiemYet());
                stmt.setInt(5, dv.getThoiGianDuKien());
                stmt.setString(6,
                        dv.getTrangThai() != null ? dv.getTrangThai() : TrangThaiConstants.DV_DANG_CUNG_CAP);
                stmt.setString(7, dv.getMoTa() != null ? dv.getMoTa() : "");
                stmt.setString(8, dv.getNhomDichVu() != null ? dv.getNhomDichVu() : "Khám & tư vấn");
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            // Thử insert không có cột nhomDichVu
            try {
                String sqlFallback = "INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                    stmt.setString(1, dv.getMaDichVu());
                    stmt.setString(2, dv.getTenDichVu());
                    stmt.setDouble(3, dv.getGiaNiemYet());
                    stmt.setDouble(4, dv.getGiaNiemYet());
                    stmt.setInt(5, dv.getThoiGianDuKien());
                    stmt.setString(6,
                            dv.getTrangThai() != null ? dv.getTrangThai() : TrangThaiConstants.DV_DANG_CUNG_CAP);
                    stmt.setString(7, dv.getMoTa() != null ? dv.getMoTa() : "");
                    return stmt.executeUpdate() > 0;
                }
            } catch (Exception ex2) {
                System.err.println("DichVuDAO.themDichVu DB error: " + ex2.getMessage());
                return false;
            }
        }
    }

    public boolean capNhatDichVu(DichVu dv) {
        if (dv == null || dv.getMaDichVu() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE DichVu SET tenDichVu = ?, giaDichVu = ?, giaNiemYet = ?, thoiGianDuKien = ?, " +
                    "trangThai = ?, moTa = ?, nhomDichVu = ? WHERE maDichVu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, dv.getTenDichVu());
                stmt.setDouble(2, dv.getGiaNiemYet());
                stmt.setDouble(3, dv.getGiaNiemYet());
                stmt.setInt(4, dv.getThoiGianDuKien());
                stmt.setString(5, dv.getTrangThai());
                stmt.setString(6, dv.getMoTa());
                stmt.setString(7, dv.getNhomDichVu() != null ? dv.getNhomDichVu() : "Khám & tư vấn");
                stmt.setString(8, dv.getMaDichVu());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            try {
                String sqlFallback = "UPDATE DichVu SET tenDichVu = ?, giaDichVu = ?, giaNiemYet = ?, thoiGianDuKien = ?, "
                        + "trangThai = ?, moTa = ? WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                    stmt.setString(1, dv.getTenDichVu());
                    stmt.setDouble(2, dv.getGiaNiemYet());
                    stmt.setDouble(3, dv.getGiaNiemYet());
                    stmt.setInt(4, dv.getThoiGianDuKien());
                    stmt.setString(5, dv.getTrangThai());
                    stmt.setString(6, dv.getMoTa());
                    stmt.setString(7, dv.getMaDichVu());
                    return stmt.executeUpdate() > 0;
                }
            } catch (Exception ex2) {
                System.err.println("DichVuDAO.capNhatDichVu DB error: " + ex2.getMessage());
                return false;
            }
        }
    }

    public boolean chuyenTrangThai(String maDichVu, String trangThaiMoi) {
        if (maDichVu == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE DichVu SET trangThai = ? WHERE maDichVu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, trangThaiMoi);
                stmt.setString(2, maDichVu);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("DichVuDAO.chuyenTrangThai DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean kiemTraTenDichVuTrung(String tenDV, String maDVBoQua) {
        if (tenDV == null || tenDV.trim().isEmpty())
            return false;
        String tenClean = tenDV.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "SELECT COUNT(*) FROM DichVu WHERE LOWER(LTRIM(RTRIM(tenDichVu))) = LOWER(?)";
            if (maDVBoQua != null && !maDVBoQua.trim().isEmpty()) {
                sql += " AND maDichVu <> ?";
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tenClean);
                if (maDVBoQua != null && !maDVBoQua.trim().isEmpty()) {
                    stmt.setString(2, maDVBoQua.trim());
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DichVuDAO.kiemTraTenDichVuTrung DB error: " + e.getMessage());
        }
        return false;
    }

    public boolean daTungSuDung(String maDV) {
        if (maDV == null || maDV.trim().isEmpty())
            return false;
        maDV = maDV.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                // Kiểm tra trong ChiTietHoaDon hoặc ChiTietHoaDonKham
                String sql = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0)
                            return true;
                    }
                }
            } catch (Exception e) {
                // Có thể bảng không tồn tại hoặc lỗi khác
            }
        }
        return false;
    }

    public boolean ghiLichSuGia(String maDV, double giaCu, double giaMoi, String nguoiDoi, String lyDo) {
        if (maDV == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        Date now = new Date();
        try {
            String sql = "INSERT INTO LichSuGiaDichVu (maDichVu, giaCu, giaMoi, ngayHieuLuc, nguoiDoi, lyDo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maDV);
                stmt.setDouble(2, giaCu);
                stmt.setDouble(3, giaMoi);
                stmt.setTimestamp(4, new Timestamp(now.getTime()));
                stmt.setString(5, nguoiDoi != null ? nguoiDoi : "Hệ thống");
                stmt.setString(6, lyDo != null ? lyDo : "Điều chỉnh giá");
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("DichVuDAO.ghiLichSuGia DB error: " + e.getMessage());
            return false;
        }
    }

    public List<LichSuGiaDichVu> getLichSuGia(String maDV) {
        List<LichSuGiaDichVu> list = new ArrayList<>();
        if (maDV == null || maDV.trim().isEmpty())
            return list;
        maDV = maDV.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maLichSu, maDichVu, giaCu, giaMoi, ngayHieuLuc, nguoiDoi, lyDo " +
                    "FROM LichSuGiaDichVu WHERE maDichVu = ? ORDER BY ngayHieuLuc DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maDV);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new LichSuGiaDichVu(
                                rs.getInt("maLichSu"),
                                rs.getString("maDichVu"),
                                rs.getDouble("giaCu"),
                                rs.getDouble("giaMoi"),
                                rs.getTimestamp("ngayHieuLuc"),
                                rs.getString("nguoiDoi"),
                                rs.getString("lyDo")));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DichVuDAO.getLichSuGia DB error: " + e.getMessage());
        }
        return list;
    }

    public int getSoLuotSuDung(String maDV) {
        if (maDV == null)
            return 0;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT ISNULL(SUM(ct.soLuong), 0) FROM ChiTietHoaDon ct " +
                        "JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon " +
                        "WHERE ct.maDichVu = ? AND hd.trangThai = N'Đã thanh toán'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    public double getDoanhThu30Ngay(String maDV) {
        if (maDV == null)
            return 0;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT ISNULL(SUM(ct.soLuong * ct.donGia), 0) FROM ChiTietHoaDon ct " +
                        "JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon " +
                        "WHERE ct.maDichVu = ? AND hd.trangThai = N'Đã thanh toán' " +
                        "AND hd.ngayLap >= DATEADD(day, -30, GETDATE())";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    public List<DichVu> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDichVu();
        }
        String cleanKeyword = keyword.trim();
        List<DichVu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa, " +
                    "ISNULL(nhomDichVu, N'Khám & tư vấn') AS nhomDichVu FROM DichVu " +
                    "WHERE maDichVu LIKE ? OR tenDichVu LIKE ? OR moTa LIKE ? OR trangThai LIKE ? " +
                    "OR nhomDichVu LIKE ? ORDER BY maDichVu";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String param = "%" + cleanKeyword + "%";
                for (int i = 1; i <= 5; i++) {
                    stmt.setString(i, param);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new DichVu(
                                rs.getString("maDichVu"),
                                rs.getString("tenDichVu"),
                                rs.getDouble("giaNiemYet"),
                                rs.getInt("thoiGianDuKien"),
                                rs.getString("trangThai"),
                                rs.getString("moTa"),
                                rs.getString("nhomDichVu")));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DichVuDAO.timKiem DB error: " + e.getMessage());
        }
        return list;
    }

    public boolean xoaDichVu(String maDV) {
        if (maDV == null || maDV.trim().isEmpty())
            return false;
        maDV = maDV.trim();

        // Nếu đã từng sử dụng, không xóa vật lý mà chuyển sang Tạm ngừng cung cấp
        if (daTungSuDung(maDV)) {
            return chuyenTrangThai(maDV, TrangThaiConstants.DV_TAM_NGUNG);
        }

        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "DELETE FROM DichVu WHERE maDichVu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maDV);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("DichVuDAO.xoaDichVu foreign key fallback to marking inactive: " + e.getMessage());
            return chuyenTrangThai(maDV, TrangThaiConstants.DV_TAM_NGUNG);
        }
    }

    public String sinhMaDVMoi() {
        Connection conn = ConnectDB.getConnection();
        int maxIndex = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maDichVu FROM DichVu";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maDichVu");
                        if (ma != null && ma.toUpperCase().startsWith("DV")) {
                            try {
                                int num = Integer.parseInt(ma.substring(2).trim());
                                if (num > maxIndex) maxIndex = num;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.sinhMaDVMoi error: " + e.getMessage());
            }
        }
        return String.format("DV%03d", maxIndex + 1);
    }
}
