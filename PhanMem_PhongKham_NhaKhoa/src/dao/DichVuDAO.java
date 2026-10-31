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
 * Lớp truy xuất dữ liệu Danh Mục Dịch Vụ Nha Khoa & Lịch Sử Giá
 * Hỗ trợ SQL Server và mock fallback an toàn
 */
public class DichVuDAO {

    private static final List<DichVu> MOCK_DICH_VU = new ArrayList<>();
    private static final List<LichSuGiaDichVu> MOCK_LICHSU_GIA = new ArrayList<>();

    static {
        MOCK_DICH_VU.add(new DichVu("DV001", "Cấy ghép Implant Straumann Thụy Sĩ", 22000000, 60, TrangThaiConstants.DV_DANG_CUNG_CAP, "Trụ Titanium cao cấp tích hợp xương nhanh", "Implant"));
        MOCK_DICH_VU.add(new DichVu("DV002", "Niềng răng mắc cài kim loại tự buộc", 30000000, 45, TrangThaiConstants.DV_DANG_CUNG_CAP, "Chỉnh nha mắc cài thông minh tự động giảm lực ma sát", "Chỉnh nha"));
        MOCK_DICH_VU.add(new DichVu("DV003", "Bọc răng toàn sứ Cercon cao cấp", 4500000, 90, TrangThaiConstants.DV_DANG_CUNG_CAP, "Phục hình răng sứ nguyên khối chính hãng Đức", "Phục hình sứ"));
        MOCK_DICH_VU.add(new DichVu("DV004", "Nhổ răng khôn Piezotome sóng siêu âm", 2000000, 30, TrangThaiConstants.DV_DANG_CUNG_CAP, "Nhổ răng khôn không đau, không sưng", "Nhổ răng"));
        MOCK_DICH_VU.add(new DichVu("DV005", "Trám răng thẩm mỹ Composite Nano 3M", 400000, 30, TrangThaiConstants.DV_DANG_CUNG_CAP, "Trám răng sâu bằng vật liệu thẩm mỹ", "Trám & nội nha"));
        MOCK_DICH_VU.add(new DichVu("DV006", "Cạo vôi răng & Đánh bóng siêu âm (EMS)", 350000, 30, TrangThaiConstants.DV_DANG_CUNG_CAP, "Làm sạch cao răng mảng bám không đau", "Khám & tư vấn"));
        MOCK_DICH_VU.add(new DichVu("DV007", "Tẩy trắng răng công nghệ Laser Whitening", 2500000, 60, TrangThaiConstants.DV_DANG_CUNG_CAP, "Bật tone răng trắng sáng tức thì chỉ sau 45 phút", "Tẩy trắng"));
        MOCK_DICH_VU.add(new DichVu("DV008", "Điều trị tủy răng (Nội nha 1 chân)", 1200000, 60, TrangThaiConstants.DV_DANG_CUNG_CAP, "Điều trị tủy và hàn bít ống tủy hiện đại", "Trám & nội nha"));
        MOCK_DICH_VU.add(new DichVu("DV009", "Chụp phim X-quang Panorex toàn cảnh", 200000, 15, TrangThaiConstants.DV_DANG_CUNG_CAP, "Chụp khảo sát cung hàm và chóp răng", "Khám & tư vấn"));
    }

    public List<DichVu> getAllDichVu() {
        List<DichVu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                // Kiểm tra xem bảng có cột nhomDichVu hay chưa
                String sql = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa, " +
                             "ISNULL(nhomDichVu, N'Khám & tư vấn') AS nhomDichVu FROM DichVu ORDER BY maDichVu";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        DichVu dv = new DichVu(
                            rs.getString("maDichVu"),
                            rs.getString("tenDichVu"),
                            rs.getDouble("giaNiemYet"),
                            rs.getInt("thoiGianDuKien"),
                            rs.getString("trangThai"),
                            rs.getString("moTa"),
                            rs.getString("nhomDichVu")
                        );
                        list.add(dv);
                    }
                }
            } catch (Exception e) {
                // Fallback nếu chưa có cột nhomDichVu
                try {
                    String sqlFallback = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa FROM DichVu ORDER BY maDichVu";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sqlFallback)) {
                        while (rs.next()) {
                            DichVu dv = new DichVu(
                                rs.getString("maDichVu"),
                                rs.getString("tenDichVu"),
                                rs.getDouble("giaNiemYet"),
                                rs.getInt("thoiGianDuKien"),
                                rs.getString("trangThai"),
                                rs.getString("moTa")
                            );
                            list.add(dv);
                        }
                    }
                } catch (Exception ex2) {
                    System.err.println("DichVuDAO.getAllDichVu error: " + ex2.getMessage());
                }
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_DICH_VU);
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
        if (maDV == null || maDV.trim().isEmpty()) return null;
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
        if (dv == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa, nhomDichVu) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, dv.getMaDichVu());
                    stmt.setString(2, dv.getTenDichVu());
                    stmt.setDouble(3, dv.getGiaNiemYet());
                    stmt.setDouble(4, dv.getGiaNiemYet());
                    stmt.setInt(5, dv.getThoiGianDuKien());
                    stmt.setString(6, dv.getTrangThai() != null ? dv.getTrangThai() : TrangThaiConstants.DV_DANG_CUNG_CAP);
                    stmt.setString(7, dv.getMoTa() != null ? dv.getMoTa() : "");
                    stmt.setString(8, dv.getNhomDichVu() != null ? dv.getNhomDichVu() : "Khám & tư vấn");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                // Thử insert không có cột nhomDichVu
                try {
                    String sqlFallback = "INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa) " +
                                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                        stmt.setString(1, dv.getMaDichVu());
                        stmt.setString(2, dv.getTenDichVu());
                        stmt.setDouble(3, dv.getGiaNiemYet());
                        stmt.setDouble(4, dv.getGiaNiemYet());
                        stmt.setInt(5, dv.getThoiGianDuKien());
                        stmt.setString(6, dv.getTrangThai() != null ? dv.getTrangThai() : TrangThaiConstants.DV_DANG_CUNG_CAP);
                        stmt.setString(7, dv.getMoTa() != null ? dv.getMoTa() : "");
                        dbOk = stmt.executeUpdate() > 0;
                    }
                } catch (Exception ex2) {
                    System.err.println("DichVuDAO.themDichVu DB error: " + ex2.getMessage());
                }
            }
        }
        boolean exists = false;
        for (int i = 0; i < MOCK_DICH_VU.size(); i++) {
            if (MOCK_DICH_VU.get(i).getMaDichVu().equalsIgnoreCase(dv.getMaDichVu())) {
                MOCK_DICH_VU.set(i, dv);
                exists = true;
                break;
            }
        }
        if (!exists) {
            MOCK_DICH_VU.add(dv);
        }
        return dbOk || conn == null;
    }

    public boolean capNhatDichVu(DichVu dv) {
        if (dv == null || dv.getMaDichVu() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
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
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                try {
                    String sqlFallback = "UPDATE DichVu SET tenDichVu = ?, giaDichVu = ?, giaNiemYet = ?, thoiGianDuKien = ?, " +
                                         "trangThai = ?, moTa = ? WHERE maDichVu = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                        stmt.setString(1, dv.getTenDichVu());
                        stmt.setDouble(2, dv.getGiaNiemYet());
                        stmt.setDouble(3, dv.getGiaNiemYet());
                        stmt.setInt(4, dv.getThoiGianDuKien());
                        stmt.setString(5, dv.getTrangThai());
                        stmt.setString(6, dv.getMoTa());
                        stmt.setString(7, dv.getMaDichVu());
                        dbOk = stmt.executeUpdate() > 0;
                    }
                } catch (Exception ex2) {
                    System.err.println("DichVuDAO.capNhatDichVu DB error: " + ex2.getMessage());
                }
            }
        }
        for (int i = 0; i < MOCK_DICH_VU.size(); i++) {
            if (MOCK_DICH_VU.get(i).getMaDichVu().equalsIgnoreCase(dv.getMaDichVu())) {
                MOCK_DICH_VU.set(i, dv);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean chuyenTrangThai(String maDichVu, String trangThaiMoi) {
        if (maDichVu == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE DichVu SET trangThai = ? WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, trangThaiMoi);
                    stmt.setString(2, maDichVu);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.chuyenTrangThai DB error: " + e.getMessage());
            }
        }
        for (DichVu dv : MOCK_DICH_VU) {
            if (dv.getMaDichVu().equalsIgnoreCase(maDichVu)) {
                dv.setTrangThai(trangThaiMoi);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean kiemTraTenDichVuTrung(String tenDV, String maDVBoQua) {
        if (tenDV == null || tenDV.trim().isEmpty()) return false;
        String tenClean = tenDV.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
        }
        for (DichVu dv : MOCK_DICH_VU) {
            if (maDVBoQua != null && dv.getMaDichVu().equalsIgnoreCase(maDVBoQua.trim())) {
                continue;
            }
            if (dv.getTenDichVu() != null && dv.getTenDichVu().trim().equalsIgnoreCase(tenClean)) {
                return true;
            }
        }
        return false;
    }

    public boolean daTungSuDung(String maDV) {
        if (maDV == null || maDV.trim().isEmpty()) return false;
        maDV = maDV.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                // Kiểm tra trong ChiTietHoaDon hoặc ChiTietDieuTri
                String sql = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) return true;
                    }
                }
                String sql2 = "SELECT COUNT(*) FROM ChiTietDieuTri WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) return true;
                    }
                }
            } catch (Exception e) {
                // Có thể bảng không tồn tại hoặc lỗi khác
            }
        }
        return false;
    }

    public boolean ghiLichSuGia(String maDV, double giaCu, double giaMoi, String nguoiDoi, String lyDo) {
        if (maDV == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        Date now = new Date();
        if (conn != null) {
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
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.ghiLichSuGia DB error: " + e.getMessage());
            }
        }
        MOCK_LICHSU_GIA.add(new LichSuGiaDichVu(MOCK_LICHSU_GIA.size() + 1, maDV, giaCu, giaMoi, new Timestamp(now.getTime()), nguoiDoi, lyDo));
        return dbOk || conn == null;
    }

    public List<LichSuGiaDichVu> getLichSuGia(String maDV) {
        List<LichSuGiaDichVu> list = new ArrayList<>();
        if (maDV == null || maDV.trim().isEmpty()) return list;
        maDV = maDV.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
                                rs.getString("lyDo")
                            ));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.getLichSuGia DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            for (LichSuGiaDichVu ls : MOCK_LICHSU_GIA) {
                if (ls.getMaDichVu().equalsIgnoreCase(maDV)) {
                    list.add(ls);
                }
            }
        }
        return list;
    }

    public int getSoLuotSuDung(String maDV) {
        if (maDV == null) return 0;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT ISNULL(SUM(ct.soLuong), 0) FROM ChiTietHoaDon ct " +
                             "JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon " +
                             "WHERE ct.maDichVu = ? AND hd.trangThai = N'Đã thanh toán'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) return rs.getInt(1);
                    }
                }
            } catch (Exception ignored) {}
        }
        return 0;
    }

    public double getDoanhThu30Ngay(String maDV) {
        if (maDV == null) return 0;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT ISNULL(SUM(ct.thanhTien), 0) FROM ChiTietHoaDon ct " +
                             "JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon " +
                             "WHERE ct.maDichVu = ? AND hd.trangThai = N'Đã thanh toán' " +
                             "AND hd.ngayLap >= DATEADD(day, -30, GETDATE())";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) return rs.getDouble(1);
                    }
                }
            } catch (Exception ignored) {}
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
        if (conn != null) {
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
                                rs.getString("nhomDichVu")
                            ));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.timKiem DB error: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            String lower = cleanKeyword.toLowerCase();
            for (DichVu dv : MOCK_DICH_VU) {
                boolean match = (dv.getMaDichVu() != null && dv.getMaDichVu().toLowerCase().contains(lower)) ||
                                (dv.getTenDichVu() != null && dv.getTenDichVu().toLowerCase().contains(lower)) ||
                                (dv.getMoTa() != null && dv.getMoTa().toLowerCase().contains(lower)) ||
                                (dv.getTrangThai() != null && dv.getTrangThai().toLowerCase().contains(lower)) ||
                                (dv.getNhomDichVu() != null && dv.getNhomDichVu().toLowerCase().contains(lower));
                if (match) {
                    list.add(dv);
                }
            }
        }
        return list;
    }

    public boolean xoaDichVu(String maDV) {
        if (maDV == null || maDV.trim().isEmpty()) return false;
        maDV = maDV.trim();
        
        // Nếu đã từng sử dụng, không xóa vật lý mà chuyển sang Tạm ngừng cung cấp
        if (daTungSuDung(maDV)) {
            return chuyenTrangThai(maDV, TrangThaiConstants.DV_TAM_NGUNG);
        }

        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM DichVu WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maDV);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.xoaDichVu foreign key fallback to marking inactive: " + e.getMessage());
                return chuyenTrangThai(maDV, TrangThaiConstants.DV_TAM_NGUNG);
            }
        }
        final String fMa = maDV;
        MOCK_DICH_VU.removeIf(d -> d.getMaDichVu().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
    }

    public String sinhMaDVMoi() {
        int maxIndex = 0;
        List<DichVu> all = getAllDichVu();
        for (DichVu dv : all) {
            String ma = dv.getMaDichVu();
            if (ma != null && ma.toUpperCase().startsWith("DV")) {
                try {
                    int num = Integer.parseInt(ma.substring(2).trim());
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (Exception ignored) {}
            }
        }
        return String.format("DV%03d", maxIndex + 1);
    }
}
