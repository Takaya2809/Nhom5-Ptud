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
 * Lớp truy xuất dữ liệu Bệnh Nhân
 * Hỗ trợ đồng thời CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class BenhNhanDAO {

    private static final List<BenhNhan> MOCK_BENH_NHAN = new ArrayList<>();

    static {
        MOCK_BENH_NHAN
                .add(new BenhNhan("BN001", "Nguyễn Văn Tuấn", "0901234567", "123 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM",
                        "Nam", Date.valueOf("1992-06-14"), "Dị ứng Penicillin", "VIP", 1250));
        MOCK_BENH_NHAN
                .add(new BenhNhan("BN002", "Trần Mai Phương", "0909876543", "45 Hai Bà Trưng, P. Tân Định, Q.1, TP.HCM",
                        "Nữ", Date.valueOf("1996-03-22"), "Tiền sử cao huyết áp nhẹ", "Thân thiết", 850));
        MOCK_BENH_NHAN.add(new BenhNhan("BN003", "Phạm Quốc Dũng", "0988776655", "88 Lê Lợi, P. Bến Thành, Q.1, TP.HCM",
                "Nam", Date.valueOf("1984-10-09"), "Không có tiền sử bệnh lý", "Thường", 180));
        MOCK_BENH_NHAN.add(new BenhNhan("BN004", "Vũ Bích Ngọc", "0977665544", "234 Cách Mạng Tháng 8, Q.3, TP.HCM",
                "Nữ", Date.valueOf("2001-01-30"), "Răng nhạy cảm, dễ ê buốt", "Khách mới", 20));
        MOCK_BENH_NHAN
                .add(new BenhNhan("BN005", "Đặng Hoàng Nam", "0966554433", "56 Hoàng Hoa Thám, P.12, Tân Bình, TP.HCM",
                        "Nam", Date.valueOf("1999-12-05"), "Không có", "Thường", 120));
        MOCK_BENH_NHAN.add(new BenhNhan("BN006", "Lê Thanh Hương", "0933112233", "78 Nguyễn Thị Minh Khai, Q.1, TP.HCM",
                "Nữ", Date.valueOf("1995-04-18"), "Không có", "Thân thiết", 450));
        MOCK_BENH_NHAN
                .add(new BenhNhan("BN007", "Hoàng Kim Liên", "0938445566", "102 Pasteur, P. Võ Thị Sáu, Q.3, TP.HCM",
                        "Nữ", Date.valueOf("1980-08-27"), "Đái tháo đường Type 2 kiểm soát tốt", "VIP", 1100));
        MOCK_BENH_NHAN.add(new BenhNhan("BN008", "Ngô Quốc Huy", "0944778899", "15 Trần Phú, P.4, Q.5, TP.HCM", "Nam",
                Date.valueOf("1987-11-15"), "Không có", "Thường", 250));
    }

    public List<BenhNhan> getAllBenhNhan() {
        List<BenhNhan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_BENH_NHAN);
        }
        return list;
    }

    public BenhNhan getBenhNhanByMa(String maBN) {
        if (maBN == null || maBN.trim().isEmpty())
            return null;
        maBN = maBN.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
        }
        for (BenhNhan bn : MOCK_BENH_NHAN) {
            if (bn.getMaBN().equalsIgnoreCase(maBN)) {
                return bn;
            }
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
        if (conn != null) {
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
        }

        if (list.isEmpty() && conn == null) {
            for (BenhNhan bn : MOCK_BENH_NHAN) {
                boolean matchTen = !hasTen || (bn.getHoTen() != null && bn.getHoTen().toLowerCase().contains(tenClean));
                boolean matchSdt = !hasSdt || (bn.getSoDienThoai() != null && bn.getSoDienThoai().contains(sdtClean));
                if (matchTen && matchSdt) {
                    list.add(bn);
                }
            }
        }
        return list;
    }

    public boolean themBenhNhan(BenhNhan bn) {
        if (bn == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO BenhNhan (maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("BenhNhanDAO.themBenhNhan DB error: " + e.getMessage());
            }
        }
        // Luôn đồng bộ vào mock cache
        boolean exists = false;
        for (int i = 0; i < MOCK_BENH_NHAN.size(); i++) {
            if (MOCK_BENH_NHAN.get(i).getMaBN().equalsIgnoreCase(bn.getMaBN())) {
                MOCK_BENH_NHAN.set(i, bn);
                exists = true;
                break;
            }
        }
        if (!exists) {
            MOCK_BENH_NHAN.add(bn);
        }
        return dbOk || conn == null;
    }

    public boolean capNhatBenhNhan(BenhNhan bn) {
        if (bn == null || bn.getMaBN() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
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
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("BenhNhanDAO.capNhatBenhNhan DB error: " + e.getMessage());
            }
        }
        for (int i = 0; i < MOCK_BENH_NHAN.size(); i++) {
            if (MOCK_BENH_NHAN.get(i).getMaBN().equalsIgnoreCase(bn.getMaBN())) {
                MOCK_BENH_NHAN.set(i, bn);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public List<BenhNhan> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBenhNhan();
        }
        String cleanKeyword = keyword.trim();
        List<BenhNhan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maBN, hoTen, soDienThoai, diaChi, gioiTinh, ngaySinh, tienSuBenh, loaiKhach, diemTichLuy, trangThai FROM BenhNhan "
                        +
                        "WHERE maBN LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? OR diaChi LIKE ? OR tienSuBenh LIKE ? OR loaiKhach LIKE ? "
                        +
                        "ORDER BY maBN";
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
        }

        // Nếu database không có kết quả hoặc không kết nối được, tìm trong bộ đệm bộ
        // nhớ
        if (list.isEmpty()) {
            String lower = cleanKeyword.toLowerCase();
            for (BenhNhan bn : MOCK_BENH_NHAN) {
                boolean match = (bn.getMaBN() != null && bn.getMaBN().toLowerCase().contains(lower)) ||
                        (bn.getHoTen() != null && bn.getHoTen().toLowerCase().contains(lower)) ||
                        (bn.getSoDienThoai() != null && bn.getSoDienThoai().contains(cleanKeyword)) ||
                        (bn.getDiaChi() != null && bn.getDiaChi().toLowerCase().contains(lower)) ||
                        (bn.getLoaiKhach() != null && bn.getLoaiKhach().toLowerCase().contains(lower));
                if (match) {
                    list.add(bn);
                }
            }
        }
        return list;
    }

    public String kiemTraTrungSoDienThoai(String sdt, String maBNBoQua) {
        if (sdt == null || sdt.trim().isEmpty())
            return null;
        String cleanSDT = sdt.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
        }
        for (BenhNhan bn : MOCK_BENH_NHAN) {
            if (bn.getSoDienThoai() != null && bn.getSoDienThoai().trim().equals(cleanSDT)) {
                if (maBNBoQua == null || !bn.getMaBN().equalsIgnoreCase(maBNBoQua.trim())) {
                    return bn.getMaBN() + " - " + bn.getHoTen();
                }
            }
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
        boolean dbOk = false;
        if (conn != null) {
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
            } finally {
                try {
                    conn.setAutoCommit(oldAutoCommit);
                } catch (Exception ignored) {
                }
            }
        }

        final String fMa = maBN;
        for (BenhNhan bn : MOCK_BENH_NHAN) {
            if (bn.getMaBN().equalsIgnoreCase(fMa)) {
                bn.setTrangThai("Ngừng theo dõi");
                break;
            }
        }
        for (entity.LichHen lh : LichHenDAO.MOCK_LICH_HEN) {
            if (fMa.equalsIgnoreCase(lh.getMaBN())) {
                String st = lh.getTrangThai();
                if ("Đã lên lịch".equalsIgnoreCase(st) || "Chờ khám".equalsIgnoreCase(st)
                        || "Đã xác nhận".equalsIgnoreCase(st) || "Đang khám".equalsIgnoreCase(st)) {
                    lh.setTrangThai("Đã hủy");
                    lh.setGhiChu((lh.getGhiChu() != null ? lh.getGhiChu() + " " : "") + "[Tự động hủy]");
                }
            }
        }
        for (NhaSiDAO.CaKham ck : NhaSiDAO.MOCK_CA_KHAM) {
            if (fMa.equalsIgnoreCase(ck.maBN)) {
                String st = ck.trangThai;
                if ("Đã lên lịch".equalsIgnoreCase(st) || "Chờ khám".equalsIgnoreCase(st)
                        || "Đã xác nhận".equalsIgnoreCase(st) || "Đang khám".equalsIgnoreCase(st)) {
                    ck.trangThai = "Đã hủy";
                }
            }
        }

        return dbOk || conn == null;
    }

    public boolean congDiemTichLuy(String maBN, int diemCong) {
        if (maBN == null || diemCong <= 0)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
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
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("BenhNhanDAO.congDiemTichLuy error: " + e.getMessage());
            }
        }
        return dbOk || conn == null;
    }

    public String sinhMaBNMoi() {
        int maxIndex = 0;
        List<BenhNhan> all = getAllBenhNhan();
        for (BenhNhan bn : all) {
            String ma = bn.getMaBN();
            if (ma != null && ma.toUpperCase().startsWith("BN")) {
                try {
                    int num = Integer.parseInt(ma.substring(2).trim());
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return String.format("BN%03d", maxIndex + 1);
    }
}
