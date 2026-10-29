package dao;

import connectDB.ConnectDB;
import entity.HoSoBenhAn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Hồ Sơ Bệnh Án & Lịch Sử Điều Trị
 */
public class HoSoBenhAnDAO {

    private static final List<HoSoBenhAn> MOCK_HO_SO = new ArrayList<>();

    static {
        HoSoBenhAn hs1 = new HoSoBenhAn("BA001", "BN001", "NV001", "LH001", new Timestamp(System.currentTimeMillis() - 86400000L), "Mất răng hàm R46, tiêu xương nhẹ", "Chỉ định cấy ghép Implant R46 Straumann Thụy Sĩ", "Đặt trụ thành công, lành thương tốt", "Cắt chỉ sau 10 ngày", "Hoàn thành");
        hs1.setTenBenhNhan("Nguyễn Văn Tuấn");
        hs1.setTenNhaSi("BS. CKII Nguyễn Văn An");
        MOCK_HO_SO.add(hs1);

        HoSoBenhAn hs2 = new HoSoBenhAn("BA002", "BN002", "NV002", "LH002", new Timestamp(System.currentTimeMillis() - 86400000L), "Răng chen chúc hàm trên độ II", "Sai khớp cắn Angle II tiểu loại 1", "Gắn mắc cài tự buộc 2 hàm", "Tái khám sau 4 tuần", "Đang điều trị");
        hs2.setTenBenhNhan("Trần Mai Phương");
        hs2.setTenNhaSi("ThS. BS Lê Thị Thảo");
        MOCK_HO_SO.add(hs2);
    }

    public List<HoSoBenhAn> getAllHoSoBenhAn() {
        List<HoSoBenhAn> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT hs.maHoSo, hs.maBN, hs.maNhaSi, hs.maLichHen, hs.ngayKham, hs.trieuChung, hs.chanDoan, " +
                             "hs.ketQuaDieuTri, hs.ghiChu, hs.trangThai, bn.hoTen AS tenBenhNhan, nv.hoTen AS tenNhaSi " +
                             "FROM HoSoBenhAn hs " +
                             "LEFT JOIN BenhNhan bn ON hs.maBN = bn.maBN " +
                             "LEFT JOIN NhanVien nv ON hs.maNhaSi = nv.maNV " +
                             "ORDER BY hs.ngayKham DESC";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        HoSoBenhAn hs = new HoSoBenhAn(
                            rs.getString("maHoSo"),
                            rs.getString("maBN"),
                            rs.getString("maNhaSi"),
                            rs.getString("maLichHen"),
                            rs.getTimestamp("ngayKham"),
                            rs.getString("trieuChung"),
                            rs.getString("chanDoan"),
                            rs.getString("ketQuaDieuTri"),
                            rs.getString("ghiChu"),
                            rs.getString("trangThai")
                        );
                        hs.setTenBenhNhan(rs.getString("tenBenhNhan"));
                        hs.setTenNhaSi(rs.getString("tenNhaSi"));
                        list.add(hs);
                    }
                }
            } catch (Exception e) {
                System.err.println("HoSoBenhAnDAO.getAllHoSoBenhAn DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_HO_SO);
        }
        return list;
    }

    public List<HoSoBenhAn> getHoSoByBenhNhan(String maBN) {
        List<HoSoBenhAn> all = getAllHoSoBenhAn();
        List<HoSoBenhAn> res = new ArrayList<>();
        for (HoSoBenhAn hs : all) {
            if (hs.getMaBN() != null && hs.getMaBN().equalsIgnoreCase(maBN)) {
                res.add(hs);
            }
        }
        return res;
    }

    public boolean themHoSoBenhAn(HoSoBenhAn hs) {
        if (hs == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO HoSoBenhAn (maHoSo, maBN, maNhaSi, maLichHen, ngayKham, trieuChung, chanDoan, ketQuaDieuTri, ghiChu, trangThai) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, hs.getMaHoSo());
                    stmt.setString(2, hs.getMaBN());
                    stmt.setString(3, hs.getMaNhaSi());
                    stmt.setString(4, hs.getMaLichHen());
                    stmt.setTimestamp(5, hs.getNgayKham() != null ? hs.getNgayKham() : new Timestamp(System.currentTimeMillis()));
                    stmt.setString(6, hs.getTrieuChung() != null ? hs.getTrieuChung() : "");
                    stmt.setString(7, hs.getChanDoan() != null ? hs.getChanDoan() : "");
                    stmt.setString(8, hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "");
                    stmt.setString(9, hs.getGhiChu() != null ? hs.getGhiChu() : "");
                    stmt.setString(10, hs.getTrangThai() != null ? hs.getTrangThai() : "Hoàn thành");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("HoSoBenhAnDAO.themHoSoBenhAn DB error: " + e.getMessage());
                return false;
            }
        }
        MOCK_HO_SO.add(0, hs);
        return dbOk || conn == null;
    }

    public boolean capNhatHoSoBenhAn(HoSoBenhAn hs) {
        if (hs == null || hs.getMaHoSo() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE HoSoBenhAn SET trieuChung = ?, chanDoan = ?, ketQuaDieuTri = ?, ghiChu = ?, trangThai = ? WHERE maHoSo = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, hs.getTrieuChung());
                    stmt.setString(2, hs.getChanDoan());
                    stmt.setString(3, hs.getKetQuaDieuTri());
                    stmt.setString(4, hs.getGhiChu());
                    stmt.setString(5, hs.getTrangThai());
                    stmt.setString(6, hs.getMaHoSo());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("HoSoBenhAnDAO.capNhatHoSoBenhAn DB error: " + e.getMessage());
                return false;
            }
        }
        for (int i = 0; i < MOCK_HO_SO.size(); i++) {
            if (MOCK_HO_SO.get(i).getMaHoSo().equalsIgnoreCase(hs.getMaHoSo())) {
                MOCK_HO_SO.set(i, hs);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean xoaHoSoBenhAn(String maHoSo) {
        if (maHoSo == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM HoSoBenhAn WHERE maHoSo = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maHoSo);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("HoSoBenhAnDAO.xoaHoSoBenhAn DB error: " + e.getMessage());
            }
        }
        MOCK_HO_SO.removeIf(h -> h.getMaHoSo().equalsIgnoreCase(maHoSo));
        return dbOk || conn == null;
    }

    public String sinhMaHoSoMoi() {
        int max = 0;
        List<HoSoBenhAn> all = getAllHoSoBenhAn();
        for (HoSoBenhAn hs : all) {
            String ma = hs.getMaHoSo();
            if (ma != null && (ma.startsWith("BA") || ma.startsWith("HS"))) {
                try {
                    String numPart = ma.replaceAll("[^0-9]", "");
                    if (!numPart.isEmpty()) {
                        int val = Integer.parseInt(numPart);
                        if (val > max) max = val;
                    }
                } catch (Exception ignored) {}
            }
        }
        return String.format("BA%03d", max + 1);
    }
}
