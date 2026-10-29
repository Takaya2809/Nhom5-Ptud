package dao;

import connectDB.ConnectDB;
import entity.DichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Danh Mục Dịch Vụ Nha Khoa
 * Hỗ trợ đồng bộ CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class DichVuDAO {

    private static final List<DichVu> MOCK_DICH_VU = new ArrayList<>();

    static {
        MOCK_DICH_VU.add(new DichVu("DV001", "Cấy ghép Implant Straumann Thụy Sĩ", 22000000, 60, "Đang cung cấp", "Trụ Titanium cao cấp tích hợp xương nhanh"));
        MOCK_DICH_VU.add(new DichVu("DV002", "Niềng răng mắc cài kim loại tự buộc", 30000000, 45, "Đang cung cấp", "Chỉnh nha mắc cài thông minh tự động giảm lực ma sát"));
        MOCK_DICH_VU.add(new DichVu("DV003", "Bọc răng toàn sứ Cercon cao cấp", 4500000, 90, "Đang cung cấp", "Phục hình răng sứ nguyên khối chính hãng Đức"));
        MOCK_DICH_VU.add(new DichVu("DV004", "Nhổ răng khôn Piezotome sóng siêu âm", 2000000, 30, "Đang cung cấp", "Nhổ răng khôn không đau, không sưng"));
        MOCK_DICH_VU.add(new DichVu("DV005", "Trám răng thẩm mỹ Composite Nano 3M", 400000, 30, "Đang cung cấp", "Trám răng sâu bằng vật liệu thẩm mỹ"));
        MOCK_DICH_VU.add(new DichVu("DV006", "Cạo vôi răng & Đánh bóng siêu âm (EMS)", 350000, 30, "Đang cung cấp", "Làm sạch cao răng mảng bám không đau"));
        MOCK_DICH_VU.add(new DichVu("DV007", "Tẩy trắng răng công nghệ Laser Whitening", 2500000, 60, "Đang cung cấp", "Bật tone răng trắng sáng tức thì chỉ sau 45 phút"));
        MOCK_DICH_VU.add(new DichVu("DV008", "Điều trị tủy răng (Nội nha 1 chân)", 1200000, 60, "Đang cung cấp", "Điều trị tủy và hàn bít ống tủy hiện đại"));
        MOCK_DICH_VU.add(new DichVu("DV009", "Chụp phim X-quang Panorex toàn cảnh", 200000, 15, "Đang cung cấp", "Chụp khảo sát cung hàm và chóp răng"));
    }

    public List<DichVu> getAllDichVu() {
        List<DichVu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa FROM DichVu ORDER BY maDichVu";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
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
            } catch (Exception e) {
                System.err.println("DichVuDAO.getAllDichVu DB error: " + e.getMessage());
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
            if ("Đang cung cấp".equalsIgnoreCase(dv.getTrangThai())) {
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
                String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, dv.getMaDichVu());
                    stmt.setString(2, dv.getTenDichVu());
                    stmt.setDouble(3, dv.getGiaNiemYet());
                    stmt.setDouble(4, dv.getGiaNiemYet());
                    stmt.setInt(5, dv.getThoiGianDuKien());
                    stmt.setString(6, dv.getTrangThai() != null ? dv.getTrangThai() : "Đang cung cấp");
                    stmt.setString(7, dv.getMoTa() != null ? dv.getMoTa() : "");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.themDichVu DB error: " + e.getMessage());
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
                             "trangThai = ?, moTa = ? WHERE maDichVu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, dv.getTenDichVu());
                    stmt.setDouble(2, dv.getGiaNiemYet());
                    stmt.setDouble(3, dv.getGiaNiemYet());
                    stmt.setInt(4, dv.getThoiGianDuKien());
                    stmt.setString(5, dv.getTrangThai());
                    stmt.setString(6, dv.getMoTa());
                    stmt.setString(7, dv.getMaDichVu());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("DichVuDAO.capNhatDichVu DB error: " + e.getMessage());
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

    public List<DichVu> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDichVu();
        }
        String cleanKeyword = keyword.trim();
        List<DichVu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maDichVu, tenDichVu, giaNiemYet, thoiGianDuKien, trangThai, moTa FROM DichVu " +
                             "WHERE maDichVu LIKE ? OR tenDichVu LIKE ? OR moTa LIKE ? OR trangThai LIKE ? " +
                             "ORDER BY maDichVu";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String param = "%" + cleanKeyword + "%";
                    for (int i = 1; i <= 4; i++) {
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
                                rs.getString("moTa")
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
                                (dv.getTrangThai() != null && dv.getTrangThai().toLowerCase().contains(lower));
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
                System.err.println("DichVuDAO.xoaDichVu DB error (foreign key dependency, fallback to marking inactive): " + e.getMessage());
                try {
                    String sqlUp = "UPDATE DichVu SET trangThai = N'Tạm ngừng cung cấp' WHERE maDichVu = ?";
                    try (PreparedStatement stmtUp = conn.prepareStatement(sqlUp)) {
                        stmtUp.setString(1, maDV);
                        dbOk = stmtUp.executeUpdate() > 0;
                    }
                } catch (Exception ignored) {}
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
