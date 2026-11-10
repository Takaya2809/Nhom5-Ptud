package dao;

import connectDB.ConnectDB;
import entity.BenhNhan;
import entity.HoSoBenhAn;
import entity.LichHen;
import entity.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Hồ Sơ Bệnh Án & Lịch Sử Điều Trị từ CSDL SQL Server
 * thật.
 * Chuẩn hóa:
 * - Sử dụng 100% PreparedStatement.
 * - Dùng JOIN dựng đối tượng BenhNhan, NhanVien đầy đủ, tránh N+1 query.
 */
public class HoSoBenhAnDAO {

    public List<HoSoBenhAn> getAllHoSoBenhAn() {
        List<HoSoBenhAn> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        String sql = "SELECT hs.maHoSo, hs.maBN, hs.maNhaSi, hs.maLichHen, hs.ngayKham, hs.trieuChung, hs.chanDoan, "
                + "hs.ketQuaDieuTri, hs.ghiChu, hs.trangThai, bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, nv.hoTen AS tenNhaSi "
                + "FROM HoSoBenhAn hs "
                + "LEFT JOIN BenhNhan bn ON hs.maBN = bn.maBN "
                + "LEFT JOIN NhanVien nv ON hs.maNhaSi = nv.maNV "
                + "ORDER BY hs.ngayKham DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BenhNhan bn = new BenhNhan(rs.getString("maBN"), rs.getString("tenBenhNhan"));
                bn.setSoDienThoai(rs.getString("sdtBenhNhan"));
                NhanVien bs = new NhanVien(rs.getString("maNhaSi"), rs.getString("tenNhaSi"));
                String maLH = rs.getString("maLichHen");
                LichHen lh = maLH != null ? new LichHen(maLH) : null;

                HoSoBenhAn hs = new HoSoBenhAn(
                        rs.getString("maHoSo"),
                        bn,
                        bs,
                        lh,
                        rs.getTimestamp("ngayKham"),
                        rs.getString("trieuChung"),
                        rs.getString("chanDoan"),
                        rs.getString("ketQuaDieuTri"),
                        rs.getString("ghiChu"),
                        rs.getString("trangThai"));
                list.add(hs);
            }
        } catch (Exception e) {
            System.err.println("HoSoBenhAnDAO.getAllHoSoBenhAn DB error: " + e.getMessage());
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
        if (hs == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "INSERT INTO HoSoBenhAn (maHoSo, maBN, maNhaSi, maLichHen, ngayKham, trieuChung, chanDoan, ketQuaDieuTri, ghiChu, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hs.getMaHoSo());
            stmt.setString(2, hs.getMaBN());
            stmt.setString(3, hs.getMaNhaSi());
            stmt.setString(4, hs.getMaLichHen());
            stmt.setTimestamp(5,
                    hs.getNgayKham() != null ? hs.getNgayKham() : new Timestamp(System.currentTimeMillis()));
            stmt.setString(6, hs.getTrieuChung() != null ? hs.getTrieuChung() : "");
            stmt.setString(7, hs.getChanDoan() != null ? hs.getChanDoan() : "");
            stmt.setString(8, hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "");
            stmt.setString(9, hs.getGhiChu() != null ? hs.getGhiChu() : "");
            stmt.setString(10, hs.getTrangThai() != null ? hs.getTrangThai() : "Hoàn thành");
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("HoSoBenhAnDAO.themHoSoBenhAn DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatHoSoBenhAn(HoSoBenhAn hs) {
        if (hs == null || hs.getMaHoSo() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "UPDATE HoSoBenhAn SET trieuChung = ?, chanDoan = ?, ketQuaDieuTri = ?, ghiChu = ?, trangThai = ? WHERE maHoSo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hs.getTrieuChung());
            stmt.setString(2, hs.getChanDoan());
            stmt.setString(3, hs.getKetQuaDieuTri());
            stmt.setString(4, hs.getGhiChu());
            stmt.setString(5, hs.getTrangThai());
            stmt.setString(6, hs.getMaHoSo());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("HoSoBenhAnDAO.capNhatHoSoBenhAn DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaHoSoBenhAn(String maHoSo) {
        if (maHoSo == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "DELETE FROM HoSoBenhAn WHERE maHoSo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoSo);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("HoSoBenhAnDAO.xoaHoSoBenhAn DB error: " + e.getMessage());
            return false;
        }
    }

    public String sinhMaHoSoMoi() {
        Connection conn = ConnectDB.getConnection();
        int max = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maHoSo FROM HoSoBenhAn";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maHoSo");
                        if (ma != null && (ma.startsWith("BA") || ma.startsWith("HS"))) {
                            try {
                                String numPart = ma.replaceAll("[^0-9]", "");
                                if (!numPart.isEmpty()) {
                                    int val = Integer.parseInt(numPart);
                                    if (val > max)
                                        max = val;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("HoSoBenhAnDAO.sinhMaHoSoMoi error: " + e.getMessage());
            }
        }
        return String.format("BA%03d", max + 1);
    }
}
