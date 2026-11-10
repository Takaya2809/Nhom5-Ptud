package dao;

import connectDB.ConnectDB;
import entity.TrangThaiConstants;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp truy xuất dữ liệu Thống Kê, Báo Cáo Hiệu Suất & Nhật Ký Hệ Thống từ CSDL
 * SQL Server thật.
 * Tối ưu hóa SQL Server, không chặn EDT, dùng PreparedStatement và
 * try-with-resources. Không duy trì dữ liệu ảo hay fallback demo.
 */
public class ThongKeDAO {

    private static final String SQL_FROM_HOADON_TIEN = "(SELECT h.maHD, h.maNV, h.ngayLap, h.phuongThucTT, " +
            "        COALESCE(ct.tongChiTiet, dv.giaNiemYet, 0) - COALESCE(h.giamGia, 0) AS tienHD " +
            " FROM HoaDonKham h " +
            " LEFT JOIN (SELECT maHD, SUM(soLuong * donGia) AS tongChiTiet FROM ChiTietHoaDonKham GROUP BY maHD) ct ON h.maHD = ct.maHD "
            +
            " LEFT JOIN DichVu dv ON h.maDichVu = dv.maDichVu " +
            " WHERE h.trangThai = ?) h";

    // =========================================================================
    // 1. CÁC HÀM TỔNG QUAN KPI CHO DASHBOARD
    // =========================================================================

    public double getTongDoanhThu() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COALESCE(SUM(h.tienHD), 0) FROM " + SQL_FROM_HOADON_TIEN;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getTongDoanhThu error: " + e.getMessage());
            }
        }
        return 0.0;
    }

    public double getDoanhThuHomNay() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COALESCE(SUM(h.tienHD), 0) FROM " + SQL_FROM_HOADON_TIEN
                        + " WHERE CAST(h.ngayLap AS DATE) = CAST(GETDATE() AS DATE)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuHomNay error: " + e.getMessage());
            }
        }
        return 0.0;
    }

    public double getDoanhThuHomQua() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COALESCE(SUM(h.tienHD), 0) FROM " + SQL_FROM_HOADON_TIEN
                        + " WHERE CAST(h.ngayLap AS DATE) = CAST(DATEADD(day, -1, GETDATE()) AS DATE)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuHomQua error: " + e.getMessage());
            }
        }
        return 0.0;
    }

    public double getDoanhThuThangNay() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COALESCE(SUM(h.tienHD), 0) FROM " + SQL_FROM_HOADON_TIEN
                        + " WHERE MONTH(h.ngayLap) = MONTH(GETDATE()) AND YEAR(h.ngayLap) = YEAR(GETDATE())";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuThangNay error: " + e.getMessage());
            }
        }
        return 0.0;
    }

    public double getDoanhThuThangTruoc() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COALESCE(SUM(h.tienHD), 0) FROM " + SQL_FROM_HOADON_TIEN
                        + " WHERE MONTH(h.ngayLap) = MONTH(DATEADD(month, -1, GETDATE())) AND YEAR(h.ngayLap) = YEAR(DATEADD(month, -1, GETDATE()))";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuThangTruoc error: " + e.getMessage());
            }
        }
        return 0.0;
    }

    public int getTongLuotKham() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM HoaDonKham WHERE trangThai != ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_HUY);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getTongLuotKham error: " + e.getMessage());
            }
        }
        return 0;
    }

    public int getLuotKhamHomNay() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM LichHen " +
                        "WHERE ngayHen = CAST(GETDATE() AS DATE) AND trangThai != ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.LH_DA_HUY);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getLuotKhamHomNay error: " + e.getMessage());
            }
        }
        return 0;
    }

    public int getLuotKhamHomQua() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM LichHen " +
                        "WHERE ngayHen = CAST(DATEADD(day, -1, GETDATE()) AS DATE) AND trangThai != ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.LH_DA_HUY);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getLuotKhamHomQua error: " + e.getMessage());
            }
        }
        return 0;
    }

    public int getLuotKhamThangNay() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM LichHen " +
                        "WHERE MONTH(ngayHen) = MONTH(GETDATE()) AND YEAR(ngayHen) = YEAR(GETDATE()) AND trangThai != ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.LH_DA_HUY);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getLuotKhamThangNay error: " + e.getMessage());
            }
        }
        return 0;
    }

    public int getTongBenhNhan() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM BenhNhan WHERE trangThai IS NULL OR trangThai != ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.BN_NGUNG_THEO_DOI);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getTongBenhNhan error: " + e.getMessage());
            }
        }
        return 0;
    }

    public int getSoVatTuCanhBao() {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                int count = 0;
                String sqlVT = "SELECT COUNT(*) FROM VatTu WHERE soLuongTon <= dinhMucToiThieu";
                try (PreparedStatement stmt = conn.prepareStatement(sqlVT); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        count += rs.getInt(1);
                }
                String sqlTh = "SELECT COUNT(*) FROM Thuoc WHERE soLuongTon <= dinhMucToiThieu " +
                        "OR (hanSuDung IS NOT NULL AND hanSuDung <= DATEADD(day, 30, GETDATE()))";
                try (PreparedStatement stmt = conn.prepareStatement(sqlTh); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        count += rs.getInt(1);
                }
                return count;
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getSoVatTuCanhBao error: " + e.getMessage());
            }
        }
        return 0;
    }

    // =========================================================================
    // 2. BIỂU ĐỒ DOANH THU 7 NGÀY GẦN NHẤT & TOP DỊCH VỤ TRỌNG ĐIỂM
    // =========================================================================

    public static class DoanhThuNgay {
        public Date ngay;
        public String label;
        public double doanhThu;
        public int soCa;

        public DoanhThuNgay(Date ngay, String label, double doanhThu, int soCa) {
            this.ngay = ngay;
            this.label = label;
            this.doanhThu = doanhThu;
            this.soCa = soCa;
        }
    }

    public List<DoanhThuNgay> getDoanhThu7NgayGanNhat() {
        List<DoanhThuNgay> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "WITH Dates AS (" +
                        "    SELECT CAST(GETDATE() AS DATE) AS d, 0 AS r " +
                        "    UNION ALL " +
                        "    SELECT DATEADD(day, -1, d), r + 1 FROM Dates WHERE r < 6 " +
                        "), " +
                        "HoaDonTien AS (" +
                        "    SELECT h.maHD, CAST(h.ngayLap AS DATE) AS ngayLap, " +
                        "           COALESCE(ct.tongChiTiet, dv.giaNiemYet, 0) - COALESCE(h.giamGia, 0) AS tienHD " +
                        "    FROM HoaDonKham h " +
                        "    LEFT JOIN (SELECT maHD, SUM(soLuong * donGia) AS tongChiTiet FROM ChiTietHoaDonKham GROUP BY maHD) ct ON h.maHD = ct.maHD "
                        +
                        "    LEFT JOIN DichVu dv ON h.maDichVu = dv.maDichVu " +
                        "    WHERE h.trangThai = ? " +
                        ") " +
                        "SELECT Dates.d AS Ngay, " +
                        "       CONVERT(VARCHAR(5), Dates.d, 103) AS Label, " +
                        "       COALESCE(SUM(h.tienHD), 0) AS DoanhThu, " +
                        "       COUNT(h.maHD) AS SoCa " +
                        "FROM Dates " +
                        "LEFT JOIN HoaDonTien h ON h.ngayLap = Dates.d " +
                        "GROUP BY Dates.d " +
                        "ORDER BY Dates.d ASC " +
                        "OPTION (MAXRECURSION 10)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new DoanhThuNgay(
                                    rs.getDate("Ngay"),
                                    rs.getString("Label"),
                                    rs.getDouble("DoanhThu"),
                                    rs.getInt("SoCa")));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThu7NgayGanNhat error: " + e.getMessage());
            }
        }
        return list;
    }

    public static class TopDichVu {
        public String tenDichVu;
        public int soLuot;
        public double doanhThu;
        public int tiLe;

        public TopDichVu(String tenDichVu, int soLuot, double doanhThu, int tiLe) {
            this.tenDichVu = tenDichVu;
            this.soLuot = soLuot;
            this.doanhThu = doanhThu;
            this.tiLe = tiLe;
        }
    }

    public List<TopDichVu> getTopDichVu30Ngay(int limit) {
        List<TopDichVu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT TOP (?) dv.tenDichVu, " +
                        "       COUNT(DISTINCT h.maHD) AS SoLuot, " +
                        "       COALESCE(SUM(CASE WHEN ct.donGia IS NOT NULL THEN ct.soLuong * ct.donGia ELSE dv.giaNiemYet END), 0) AS DoanhThu "
                        +
                        "FROM HoaDonKham h " +
                        "LEFT JOIN ChiTietHoaDonKham ct ON h.maHD = ct.maHD " +
                        "LEFT JOIN DichVu dv ON COALESCE(ct.maDichVu, h.maDichVu) = dv.maDichVu " +
                        "WHERE h.trangThai = ? AND h.ngayLap >= DATEADD(day, -30, GETDATE()) AND dv.tenDichVu IS NOT NULL "
                        +
                        "GROUP BY dv.tenDichVu " +
                        "ORDER BY DoanhThu DESC, SoLuot DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, limit);
                    stmt.setString(2, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        double maxDoanhThu = 0;
                        List<TopDichVu> temp = new ArrayList<>();
                        while (rs.next()) {
                            String ten = rs.getString("tenDichVu");
                            int luot = rs.getInt("SoLuot");
                            double dt = rs.getDouble("DoanhThu");
                            if (dt > maxDoanhThu)
                                maxDoanhThu = dt;
                            temp.add(new TopDichVu(ten, luot, dt, 0));
                        }
                        for (TopDichVu td : temp) {
                            int pct = maxDoanhThu > 0 ? (int) Math.round((td.doanhThu / maxDoanhThu) * 100) : 50;
                            if (pct < 10)
                                pct = 10;
                            list.add(new TopDichVu(td.tenDichVu, td.soLuot, td.doanhThu, pct));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getTopDichVu30Ngay error: " + e.getMessage());
            }
        }
        return list;
    }

    // =========================================================================
    // 3. CẢNH BÁO TỔNG QUAN (Vật tư, Thuốc hết hạn, Lịch hẹn quá giờ)
    // =========================================================================

    public static class CanhBaoDashboard {
        public int vatTuThap;
        public int thuocSapHetHan;
        public int lichHenTreGio;

        public CanhBaoDashboard(int vatTuThap, int thuocSapHetHan, int lichHenTreGio) {
            this.vatTuThap = vatTuThap;
            this.thuocSapHetHan = thuocSapHetHan;
            this.lichHenTreGio = lichHenTreGio;
        }

        public int getTongSoCanhBao() {
            return vatTuThap + thuocSapHetHan + lichHenTreGio;
        }
    }

    public CanhBaoDashboard getCanhBaoDashboard() {
        int vtThap = 0;
        int thHetHan = 0;
        int lhTre = 0;

        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                // 1. Vật tư/thuốc dưới định mức
                String sqlVT = "SELECT (SELECT COUNT(*) FROM VatTu WHERE soLuongTon <= dinhMucToiThieu) + " +
                        "       (SELECT COUNT(*) FROM Thuoc WHERE soLuongTon <= dinhMucToiThieu)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlVT); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        vtThap = rs.getInt(1);
                }

                // 2. Thuốc sắp hết hạn trong 30 ngày
                String sqlTH = "SELECT COUNT(*) FROM Thuoc WHERE hanSuDung IS NOT NULL AND hanSuDung <= DATEADD(day, 30, GETDATE())";
                try (PreparedStatement stmt = conn.prepareStatement(sqlTH); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        thHetHan = rs.getInt(1);
                }

                // 3. Lịch hẹn quá giờ chưa đến hôm nay
                String sqlLH = "SELECT COUNT(*) FROM LichHen " +
                        "WHERE ngayHen = CAST(GETDATE() AS DATE) " +
                        "  AND gioHen < CAST(GETDATE() AS TIME) " +
                        "  AND trangThai IN (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlLH)) {
                    stmt.setString(1, TrangThaiConstants.LH_DA_LEN_LICH);
                    stmt.setString(2, TrangThaiConstants.LH_DA_XAC_NHAN);
                    stmt.setString(3, TrangThaiConstants.LH_CHO_KHAM);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            lhTre = rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getCanhBaoDashboard error: " + e.getMessage());
            }
        }
        return new CanhBaoDashboard(vtThap, thHetHan, lhTre);
    }

    // =========================================================================
    // 4. PHỤC VỤ PANEL THỐNG KÊ CHI TIẾT (LỌC THEO KHOẢNG THỜI GIAN)
    // =========================================================================

    public Map<String, Double> getDoanhThuTheoDichVu() {
        return getDoanhThuTheoDichVu(null, null);
    }

    public Map<String, Double> getDoanhThuTheoDichVu(Date tuNgay, Date denNgay) {
        Map<String, Double> map = new LinkedHashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                StringBuilder sql = new StringBuilder(
                        "SELECT dv.tenDichVu, " +
                                "       COALESCE(SUM(CASE WHEN ct.donGia IS NOT NULL THEN ct.soLuong * ct.donGia ELSE dv.giaNiemYet END), 0) AS DoanhThu "
                                +
                                "FROM HoaDonKham h " +
                                "LEFT JOIN ChiTietHoaDonKham ct ON h.maHD = ct.maHD " +
                                "LEFT JOIN DichVu dv ON COALESCE(ct.maDichVu, h.maDichVu) = dv.maDichVu " +
                                "WHERE h.trangThai = ? AND dv.tenDichVu IS NOT NULL ");
                if (tuNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) >= ? ");
                if (denNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) <= ? ");
                sql.append("GROUP BY dv.tenDichVu ORDER BY DoanhThu DESC");

                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    int idx = 2;
                    if (tuNgay != null)
                        stmt.setDate(idx++, tuNgay);
                    if (denNgay != null)
                        stmt.setDate(idx++, denNgay);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            map.put(rs.getString("tenDichVu"), rs.getDouble("DoanhThu"));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuTheoDichVu error: " + e.getMessage());
            }
        }
        return map;
    }

    public Map<String, Integer> getHieuSuatBacSi() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT nv.hoTen, COUNT(h.maHD) AS SoLuot " +
                        "FROM HoaDonKham h " +
                        "JOIN NhanVien nv ON h.maNV = nv.maNV " +
                        "WHERE h.trangThai = ? " +
                        "GROUP BY nv.hoTen " +
                        "ORDER BY SoLuot DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            map.put(rs.getString("hoTen"), rs.getInt("SoLuot"));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getHieuSuatBacSi error: " + e.getMessage());
            }
        }
        return map;
    }

    public static class BieuDoThoiGianItem {
        public String label;
        public double doanhThu;
        public int soCa;

        public BieuDoThoiGianItem(String label, double doanhThu, int soCa) {
            this.label = label;
            this.doanhThu = doanhThu;
            this.soCa = soCa;
        }
    }

    public List<BieuDoThoiGianItem> getDoanhThuTheoMoc(Date tuNgay, Date denNgay, String cheDo) {
        List<BieuDoThoiGianItem> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String groupCol = "CONVERT(VARCHAR(10), h.ngayLap, 103)";
                if ("THANG".equalsIgnoreCase(cheDo)) {
                    groupCol = "FORMAT(h.ngayLap, 'MM/yyyy')";
                } else if ("TUAN".equalsIgnoreCase(cheDo)) {
                    groupCol = "'Tuần ' + CAST(DATEPART(week, h.ngayLap) AS VARCHAR) + '/' + CAST(YEAR(h.ngayLap) AS VARCHAR)";
                }

                StringBuilder sql = new StringBuilder(
                        "SELECT " + groupCol + " AS Label, " +
                                "       COALESCE(SUM(h.tienHD), 0) AS DoanhThu, " +
                                "       COUNT(h.maHD) AS SoCa " +
                                "FROM " + SQL_FROM_HOADON_TIEN + " " +
                                "WHERE 1=1 ");
                if (tuNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) >= ? ");
                if (denNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) <= ? ");
                sql.append("GROUP BY ").append(groupCol).append(" ORDER BY MIN(h.ngayLap) ASC");

                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    int idx = 2;
                    if (tuNgay != null)
                        stmt.setDate(idx++, tuNgay);
                    if (denNgay != null)
                        stmt.setDate(idx++, denNgay);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new BieuDoThoiGianItem(
                                    rs.getString("Label"),
                                    rs.getDouble("DoanhThu"),
                                    rs.getInt("SoCa")));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuTheoMoc error: " + e.getMessage());
            }
        }
        return list;
    }

    public Map<String, Double> getDoanhThuTheoPhuongThuc(Date tuNgay, Date denNgay) {
        Map<String, Double> map = new LinkedHashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                StringBuilder sql = new StringBuilder(
                        "SELECT h.phuongThucTT, COALESCE(SUM(h.tienHD), 0) AS TongTien " +
                                "FROM " + SQL_FROM_HOADON_TIEN + " " +
                                "WHERE 1=1 ");
                if (tuNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) >= ? ");
                if (denNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) <= ? ");
                sql.append("GROUP BY h.phuongThucTT ORDER BY TongTien DESC");

                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    int idx = 2;
                    if (tuNgay != null)
                        stmt.setDate(idx++, tuNgay);
                    if (denNgay != null)
                        stmt.setDate(idx++, denNgay);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String pt = rs.getString("phuongThucTT");
                            map.put(pt != null ? pt : "Tiền mặt", rs.getDouble("TongTien"));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDoanhThuTheoPhuongThuc error: " + e.getMessage());
            }
        }
        return map;
    }

    public double getTongGiamGiaDaCap(Date tuNgay, Date denNgay) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                StringBuilder sql = new StringBuilder(
                        "SELECT COALESCE(SUM(giamGia), 0) FROM HoaDonKham WHERE trangThai = ? ");
                if (tuNgay != null)
                    sql.append("AND CAST(ngayLap AS DATE) >= ? ");
                if (denNgay != null)
                    sql.append("AND CAST(ngayLap AS DATE) <= ? ");

                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    int idx = 2;
                    if (tuNgay != null)
                        stmt.setDate(idx++, tuNgay);
                    if (denNgay != null)
                        stmt.setDate(idx++, denNgay);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next())
                            return rs.getDouble(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getTongGiamGiaDaCap error: " + e.getMessage());
            }
        }
        return 0.0;
    }

    public static class BacSiThongKe {
        public String maNV;
        public String hoTen;
        public String chucDanh;
        public int soCa;
        public double doanhThu;

        public BacSiThongKe(String maNV, String hoTen, String chucDanh, int soCa, double doanhThu) {
            this.maNV = maNV;
            this.hoTen = hoTen;
            this.chucDanh = chucDanh;
            this.soCa = soCa;
            this.doanhThu = doanhThu;
        }
    }

    public List<BacSiThongKe> getThongKeBacSiChiTiet(Date tuNgay, Date denNgay) {
        List<BacSiThongKe> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                StringBuilder sql = new StringBuilder(
                        "SELECT nv.maNV, nv.hoTen, nv.chucDanh, " +
                                "       COUNT(DISTINCT h.maHD) AS SoCa, " +
                                "       COALESCE(SUM(h.tienHD), 0) AS DoanhThu " +
                                "FROM NhanVien nv " +
                                "LEFT JOIN (" +
                                "    SELECT h.maHD, COALESCE(ct.maNV, h.maNV) AS maNVThucHien, h.ngayLap, " +
                                "           COALESCE(ct.tongChiTiet, dv.giaNiemYet, 0) - COALESCE(h.giamGia, 0) AS tienHD "
                                +
                                "    FROM HoaDonKham h " +
                                "    LEFT JOIN (SELECT maHD, maNV, SUM(soLuong * donGia) AS tongChiTiet FROM ChiTietHoaDonKham GROUP BY maHD, maNV) ct ON h.maHD = ct.maHD "
                                +
                                "    LEFT JOIN DichVu dv ON h.maDichVu = dv.maDichVu " +
                                "    WHERE h.trangThai = ? " +
                                ") h ON nv.maNV = h.maNVThucHien ");
                if (tuNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) >= ? ");
                if (denNgay != null)
                    sql.append("AND CAST(h.ngayLap AS DATE) <= ? ");
                sql.append("WHERE nv.chucDanh LIKE N'%sĩ%' OR nv.chucDanh LIKE N'%Bác sĩ%' " +
                        "GROUP BY nv.maNV, nv.hoTen, nv.chucDanh " +
                        "ORDER BY DoanhThu DESC, SoCa DESC");

                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    int idx = 2;
                    if (tuNgay != null)
                        stmt.setDate(idx++, tuNgay);
                    if (denNgay != null)
                        stmt.setDate(idx++, denNgay);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new BacSiThongKe(
                                    rs.getString("maNV"),
                                    rs.getString("hoTen"),
                                    rs.getString("chucDanh"),
                                    rs.getInt("SoCa"),
                                    rs.getDouble("DoanhThu")));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getThongKeBacSiChiTiet error: " + e.getMessage());
            }
        }
        return list;
    }

    public Map<String, Integer> getPhanBoHangThe() {
        Map<String, Integer> map = new LinkedHashMap<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT COALESCE(loaiKhach, N'Khách mới') AS HangThe, COUNT(*) AS SoLuong " +
                        "FROM BenhNhan WHERE trangThai IS NULL OR trangThai != ? " +
                        "GROUP BY loaiKhach ORDER BY SoLuong DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.BN_NGUNG_THEO_DOI);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            map.put(rs.getString("HangThe"), rs.getInt("SoLuong"));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getPhanBoHangThe error: " + e.getMessage());
            }
        }
        return map;
    }

    public double[] getTyLeTaiKham() {
        // [Tổng số bệnh nhân từng khám, số bệnh nhân khám >= 2 lần, tỷ lệ %]
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "WITH BN_Kham AS (" +
                        "    SELECT maBN, COUNT(maHD) AS SoLan FROM HoaDonKham " +
                        "    WHERE trangThai = ? GROUP BY maBN" +
                        ") " +
                        "SELECT COUNT(*) AS TongBN, " +
                        "       SUM(CASE WHEN SoLan >= 2 THEN 1 ELSE 0 END) AS BNTaiKham " +
                        "FROM BN_Kham";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, TrangThaiConstants.HD_DA_THANH_TOAN);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int tong = rs.getInt("TongBN");
                            int taiKham = rs.getInt("BNTaiKham");
                            double pct = tong > 0 ? (double) taiKham * 100 / tong : 0.0;
                            return new double[] { tong, taiKham, Math.round(pct * 10) / 10.0 };
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getTyLeTaiKham error: " + e.getMessage());
            }
        }
        return new double[] { 0, 0, 0.0 };
    }

    public static class CanhBaoKhoItem {
        public String loaiItem; // Vật tư y tế / Thuốc
        public String ma;
        public String ten;
        public String donVi;
        public int soLuongTon;
        public int dinhMuc;
        public String hanSuDung;
        public String lyDo;

        public CanhBaoKhoItem(String loaiItem, String ma, String ten, String donVi, int soLuongTon, int dinhMuc,
                String hanSuDung, String lyDo) {
            this.loaiItem = loaiItem;
            this.ma = ma;
            this.ten = ten;
            this.donVi = donVi;
            this.soLuongTon = soLuongTon;
            this.dinhMuc = dinhMuc;
            this.hanSuDung = hanSuDung;
            this.lyDo = lyDo;
        }
    }

    public List<CanhBaoKhoItem> getDanhSachCanhBaoKho() {
        List<CanhBaoKhoItem> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                // 1. Vật tư
                String sqlVT = "SELECT maVatTu, tenVatTu, donViTinh, soLuongTon, dinhMucToiThieu " +
                        "FROM VatTu WHERE soLuongTon <= dinhMucToiThieu";
                try (PreparedStatement stmt = conn.prepareStatement(sqlVT); ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new CanhBaoKhoItem(
                                "Vật tư tiêu hao",
                                rs.getString("maVatTu"),
                                rs.getString("tenVatTu"),
                                rs.getString("donViTinh"),
                                rs.getInt("soLuongTon"),
                                rs.getInt("dinhMucToiThieu"),
                                "N/A",
                                "Tồn kho dưới mức tối thiểu"));
                    }
                }

                // 2. Thuốc
                String sqlTH = "SELECT maThuoc, tenThuoc, donViTinh, soLuongTon, dinhMucToiThieu, hanSuDung " +
                        "FROM Thuoc WHERE soLuongTon <= dinhMucToiThieu OR (hanSuDung IS NOT NULL AND hanSuDung <= DATEADD(day, 30, GETDATE()))";
                try (PreparedStatement stmt = conn.prepareStatement(sqlTH); ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int ton = rs.getInt("soLuongTon");
                        int dm = rs.getInt("dinhMucToiThieu");
                        Date hsd = rs.getDate("hanSuDung");
                        String lyDo = ton <= dm ? "Tồn kho dưới mức tối thiểu" : "Thuốc sắp hết hạn trong 30 ngày";
                        if (ton <= dm && hsd != null)
                            lyDo = "Vừa chạm đáy vừa sắp hết hạn";

                        list.add(new CanhBaoKhoItem(
                                "Thuốc & Dược phẩm",
                                rs.getString("maThuoc"),
                                rs.getString("tenThuoc"),
                                rs.getString("donViTinh"),
                                ton,
                                dm,
                                hsd != null ? hsd.toString() : "Chưa có",
                                lyDo));
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getDanhSachCanhBaoKho error: " + e.getMessage());
            }
        }
        return list;
    }

    // =========================================================================
    // 5. AUDIT LOG HỆ THỐNG
    // =========================================================================

    public List<String[]> getLichSuAuditLog() {
        List<String[]> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT TOP 50 thoiGian, tenDangNhap, hanhDong, chiTiet FROM LichSuHeThong ORDER BY thoiGian DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new String[] {
                                rs.getString("thoiGian"),
                                rs.getString("tenDangNhap"),
                                rs.getString("hanhDong"),
                                rs.getString("chiTiet")
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("ThongKeDAO.getLichSuAuditLog DB error: " + e.getMessage());
            }
        }
        return list;
    }

    public static void ghiLog(String tenDangNhap, String hanhDong, String chiTiet) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "INSERT INTO LichSuHeThong (thoiGian, tenDangNhap, hanhDong, chiTiet) VALUES (GETDATE(), ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, tenDangNhap != null ? tenDangNhap : "SYSTEM");
                    stmt.setString(2, hanhDong);
                    stmt.setString(3, chiTiet);
                    stmt.executeUpdate();
                }
            } catch (Exception ignored) {
            }
        }
    }
}
