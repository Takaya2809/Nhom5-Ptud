package dao;

import connectDB.ConnectDB;
import entity.BenhNhan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Bệnh Nhân từ CSDL SQL Server thật
 */
public class BenhNhanDAO {

    public List<BenhNhan> getAllBenhNhan() {
        List<BenhNhan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai FROM BenhNhan ORDER BY maBN";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BenhNhan bn = new BenhNhan(
                            rs.getString("maBN"),
                            rs.getString("hoTen"),
                            rs.getString("soDienThoai"),
                            rs.getString("diaChi"),
                            rs.getString("gioiTinh"),
                            rs.getDate("ngaySinh"),
                            rs.getString("tienSuBenh"),
                            rs.getString("loaiKhach"),
                            rs.getInt("diemTichLuy"),
                            rs.getString("trangThai"));
                    list.add(bn);
                }
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.getAllBenhNhan DB error: " + e.getMessage());
        }
        return list;
    }

    public BenhNhan getBenhNhanByMa(String maBN) {
        if (maBN == null || maBN.trim().isEmpty())
            return null;
        maBN = maBN.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return null;
        }

        try {
            String sql = "SELECT maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai FROM BenhNhan WHERE maBN = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maBN);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new BenhNhan(
                                rs.getString("maBN"),
                                rs.getString("hoTen"),
                                rs.getString("soDienThoai"),
                                rs.getString("diaChi"),
                                rs.getString("gioiTinh"),
                                rs.getDate("ngaySinh"),
                                rs.getString("tienSuBenh"),
                                rs.getString("loaiKhach"),
                                rs.getInt("diemTichLuy"),
                                rs.getString("trangThai"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.getBenhNhanByMa DB error: " + e.getMessage());
        }
        return null;
    }

    /**
     * 1. Tìm kiếm bệnh nhân CHÍNH XÁC theo mã bệnh nhân.
     * 
     * @param maBN Mã bệnh nhân (ví dụ: BN001)
     * @return Đối tượng BenhNhan hoặc null nếu không tìm thấy
     */
    public BenhNhan timTheoMa(String maBN) {
        return getBenhNhanByMa(maBN);
    }

    /**
     * 2. Tìm kiếm bệnh nhân theo HỌ TÊN VÀ / HOẶC SỐ ĐIỆN THOẠI.
     * Cho phép tìm theo một trong hai hoặc kết hợp cả hai điều kiện.
     * 
     * @param hoTen Họ tên bệnh nhân (tìm gần đúng)
     * @param sdt   Số điện thoại bệnh nhân (tìm gần đúng)
     * @return Danh sách bệnh nhân khớp với điều kiện tìm kiếm
     */
    public List<BenhNhan> timTheoHoTenVaSdt(String hoTen, String sdt) {
        boolean hasTen = hoTen != null && !hoTen.trim().isEmpty();
        boolean hasSdt = sdt != null && !sdt.trim().isEmpty();

        if (!hasTen && !hasSdt) {
            return getAllBenhNhan();
        }

        String tenClean = hasTen ? hoTen.trim().toLowerCase() : "";
        String sdtClean = hasSdt ? sdt.trim() : "";

        List<BenhNhan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai FROM BenhNhan WHERE 1=1 ");
        if (hasTen) {
            sql.append("AND LOWER(hoTen) LIKE ? ");
        }
        if (hasSdt) {
            sql.append("AND soDienThoai LIKE ? ");
        }
        sql.append("ORDER BY maBN");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (hasTen) {
                stmt.setString(paramIdx++, "%" + tenClean + "%");
            }
            if (hasSdt) {
                stmt.setString(paramIdx++, "%" + sdtClean + "%");
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BenhNhan(
                            rs.getString("maBN"),
                            rs.getString("hoTen"),
                            rs.getString("soDienThoai"),
                            rs.getString("diaChi"),
                            rs.getString("gioiTinh"),
                            rs.getDate("ngaySinh"),
                            rs.getString("tienSuBenh"),
                            rs.getString("loaiKhach"),
                            rs.getInt("diemTichLuy"),
                            rs.getString("trangThai")));
                }
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.timTheoHoTenVaSdt DB error: " + e.getMessage());
        }
        return list;
    }

    public boolean themBenhNhan(BenhNhan bn) {
        if (bn == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO BenhNhan (maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bn.getMaBN());
                stmt.setString(2, bn.getHoTen());
                stmt.setString(3, bn.getSoDienThoai());
                stmt.setString(4, bn.getDiaChi() != null ? bn.getDiaChi() : "");
                stmt.setString(5, bn.getGioiTinh() != null ? bn.getGioiTinh() : "Nam");
                stmt.setDate(6, bn.getNgaySinh() != null ? bn.getNgaySinh() : Date.valueOf("1995-01-01"));
                stmt.setString(7, bn.getTienSuBenh() != null ? bn.getTienSuBenh() : "");
                stmt.setString(8, bn.getLoaiKhach() != null ? bn.getLoaiKhach() : "Khách mới");
                stmt.setInt(9, bn.getDiemTichLuy());
                stmt.setString(10, bn.getTrangThai() != null ? bn.getTrangThai() : "Đang điều trị");
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.themBenhNhan DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatBenhNhan(BenhNhan bn) {
        if (bn == null || bn.getMaBN() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE BenhNhan SET hoTen = ?, soDienThoai = ?, diaChi = ?, gioiTinh = ?, " +
                    "ngaySinh = ?, tienSuBenh = ?, loaiKhach = ?, diemTichLuy = ?, trangThai = ? WHERE maBN = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bn.getHoTen());
                stmt.setString(2, bn.getSoDienThoai());
                stmt.setString(3, bn.getDiaChi() != null ? bn.getDiaChi() : "");
                stmt.setString(4, bn.getGioiTinh() != null ? bn.getGioiTinh() : "Nam");
                stmt.setDate(5, bn.getNgaySinh() != null ? bn.getNgaySinh() : Date.valueOf("1995-01-01"));
                stmt.setString(6, bn.getTienSuBenh() != null ? bn.getTienSuBenh() : "");
                stmt.setString(7, bn.getLoaiKhach() != null ? bn.getLoaiKhach() : "Khách mới");
                stmt.setInt(8, bn.getDiemTichLuy());
                stmt.setString(9, bn.getTrangThai() != null ? bn.getTrangThai() : "Đang điều trị");
                stmt.setString(10, bn.getMaBN());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.capNhatBenhNhan DB error: " + e.getMessage());
            return false;
        }
    }

    public List<BenhNhan> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBenhNhan();
        }
        String cleanKeyword = keyword.trim();
        List<BenhNhan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai FROM BenhNhan "
                    + "WHERE maBN LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? OR diaChi LIKE ? OR tienSuBenh LIKE ? OR loaiKhach LIKE ? "
                    + "ORDER BY maBN";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String param = "%" + cleanKeyword + "%";
                for (int i = 1; i <= 6; i++) {
                    stmt.setString(i, param);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new BenhNhan(
                                rs.getString("maBN"),
                                rs.getString("hoTen"),
                                rs.getString("soDienThoai"),
                                rs.getString("diaChi"),
                                rs.getString("gioiTinh"),
                                rs.getDate("ngaySinh"),
                                rs.getString("tienSuBenh"),
                                rs.getString("loaiKhach"),
                                rs.getInt("diemTichLuy"),
                                rs.getString("trangThai")));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.timKiem DB error: " + e.getMessage());
        }
        return list;
    }

    public String kiemTraTrungSoDienThoai(String sdt, String maBNBoQua) {
        if (sdt == null || sdt.trim().isEmpty())
            return null;
        String cleanSDT = sdt.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return null;
        }

        try {
            String sql = "SELECT maBN, hoTen FROM BenhNhan WHERE soDienThoai = ? ";
            if (maBNBoQua != null && !maBNBoQua.trim().isEmpty()) {
                sql += " AND maBN != ? ";
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cleanSDT);
                if (maBNBoQua != null && !maBNBoQua.trim().isEmpty()) {
                    stmt.setString(2, maBNBoQua.trim());
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("maBN") + " - " + rs.getString("hoTen");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.kiemTraTrungSoDienThoai error: " + e.getMessage());
        }
        return null;
    }

    public boolean coDuLieuLienQuan(String maBN) {
        if (maBN == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT " +
                        "(SELECT COUNT(*) FROM HoaDonKham WHERE maBN = ?) + " +
                        "(SELECT COUNT(*) FROM HoSoBenhAn WHERE maBN = ?) + " +
                        "(SELECT COUNT(*) FROM LichHen WHERE maBN = ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maBN);
                    stmt.setString(2, maBN);
                    stmt.setString(3, maBN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1) > 0;
                    }
                }
            } catch (Exception e) {
                System.err.println("BenhNhanDAO.coDuLieuLienQuan error: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean xoaBenhNhan(String maBN) {
        if (maBN == null || maBN.trim().isEmpty())
            return false;
        maBN = maBN.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        boolean dbOk = false;
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            if (coDuLieuLienQuan(maBN)) {
                // Soft delete: Cập nhật trạng thái bệnh nhân sang "Ngừng theo dõi"
                String sqlUpdate = "UPDATE BenhNhan SET trangThai = N'Ngừng theo dõi' WHERE maBN = ?";
                try (PreparedStatement stmt2 = conn.prepareStatement(sqlUpdate)) {
                    stmt2.setString(1, maBN);
                    dbOk = stmt2.executeUpdate() > 0;
                }

                // Tự động hủy các lịch hẹn chưa diễn ra để tránh dữ liệu mồ côi
                String sqlHuyLH = "UPDATE LichHen SET trangThai = N'Đã hủy', " +
                        "ghiChu = ISNULL(ghiChu, '') + N' [Hệ thống: Tự động hủy do bệnh nhân ngừng theo dõi]' " +
                        "WHERE maBN = ? AND trangThai IN (N'Đã lên lịch', N'Chờ khám', N'Đã xác nhận', N'Đang khám')";
                try (PreparedStatement stmtLH = conn.prepareStatement(sqlHuyLH)) {
                    stmtLH.setString(1, maBN);
                    stmtLH.executeUpdate();
                }
            } else {
                // Hard delete nếu chưa từng phát sinh dữ liệu bệnh án/hóa đơn
                String sqlDelLH = "DELETE FROM LichHen WHERE maBN = ?";
                try (PreparedStatement stmtDelLH = conn.prepareStatement(sqlDelLH)) {
                    stmtDelLH.setString(1, maBN);
                    stmtDelLH.executeUpdate();
                }
                String sql = "DELETE FROM BenhNhan WHERE maBN = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maBN);
                    dbOk = stmt.executeUpdate() > 0;
                }
            }
            conn.commit();
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.xoaBenhNhan error: " + e.getMessage());
            try {
                conn.rollback();
            } catch (Exception ignored) {
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(oldAutoCommit);
            } catch (Exception ignored) {
            }
        }

        return dbOk;
    }

    public boolean congDiemTichLuy(String maBN, int diemCong) {
        if (maBN == null || diemCong <= 0)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE BenhNhan SET diemTichLuy = diemTichLuy + ?, " +
                    "loaiKhach = CASE WHEN diemTichLuy + ? >= 1000 THEN N'VIP' " +
                    "WHEN diemTichLuy + ? >= 500 THEN N'Thân thiết' ELSE loaiKhach END " +
                    "WHERE maBN = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, diemCong);
                stmt.setInt(2, diemCong);
                stmt.setInt(3, diemCong);
                stmt.setString(4, maBN);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("BenhNhanDAO.congDiemTichLuy error: " + e.getMessage());
            return false;
        }
    }

    public String sinhMaBNMoi() {
        Connection conn = ConnectDB.getConnection();
        int maxIndex = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maBN FROM BenhNhan";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maBN");
                        if (ma != null && ma.toUpperCase().startsWith("BN")) {
                            try {
                                int num = Integer.parseInt(ma.substring(2).trim());
                                if (num > maxIndex) maxIndex = num;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("BenhNhanDAO.sinhMaBNMoi error: " + e.getMessage());
            }
        }
        return String.format("BN%03d", maxIndex + 1);
    }
}
