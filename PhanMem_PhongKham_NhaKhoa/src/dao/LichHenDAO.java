package dao;

import connectDB.ConnectDB;
import entity.BenhNhan;
import entity.DichVu;
import entity.LichHen;
import entity.NhanVien;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Lịch Hẹn Khám Bệnh từ CSDL SQL Server thật.
 * Chuẩn hóa:
 * - 100% sử dụng PreparedStatement.
 * - Tránh N+1 query: Dùng JOIN để nạp đầy đủ đối tượng BenhNhan, NhanVien (Bác
 * sĩ), DichVu.
 */
public class LichHenDAO {

    public List<LichHen> getAllLichHen() {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                + "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, "
                + "nv.hoTen AS tenNhaSi, "
                + "dv.maDichVu, dv.tenDichVu, dv.giaNiemYet "
                + "FROM LichHen lh "
                + "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN "
                + "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV "
                + "LEFT JOIN DichVu dv ON (lh.lyDoKham = dv.tenDichVu OR lh.lyDoKham = dv.maDichVu) "
                + "ORDER BY lh.ngayHen DESC, lh.gioHen ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BenhNhan bn = new BenhNhan(rs.getString("maBN"), rs.getString("tenBenhNhan"));
                bn.setSoDienThoai(rs.getString("sdtBenhNhan"));

                NhanVien bs = new NhanVien(rs.getString("maNhaSi"), rs.getString("tenNhaSi"));

                String maDV = rs.getString("maDichVu");
                DichVu dv = null;
                if (maDV != null) {
                    dv = new DichVu(maDV, rs.getString("tenDichVu"), rs.getDouble("giaNiemYet"));
                } else if (rs.getString("lyDoKham") != null) {
                    dv = new DichVu("", rs.getString("lyDoKham"), 0);
                }

                LichHen lh = new LichHen(
                        rs.getString("maLichHen"),
                        bn,
                        bs,
                        dv,
                        rs.getDate("ngayHen"),
                        rs.getTime("gioHen"),
                        rs.getString("lyDoKham"),
                        rs.getString("ghiChu"),
                        rs.getString("trangThai"));
                list.add(lh);
            }
        } catch (Exception e) {
            System.err.println("LichHenDAO.getAllLichHen DB error: " + e.getMessage());
        }
        return list;
    }

    public List<LichHen> getLichHenHomNay() {
        return getLichHenTheoNgay(new Date(System.currentTimeMillis()));
    }

    public List<LichHen> getLichHenTheoNgay(Date ngay) {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null || ngay == null) {
            return list;
        }

        String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                + "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, "
                + "nv.hoTen AS tenNhaSi, "
                + "dv.maDichVu, dv.tenDichVu, dv.giaNiemYet "
                + "FROM LichHen lh "
                + "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN "
                + "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV "
                + "LEFT JOIN DichVu dv ON (lh.lyDoKham = dv.tenDichVu OR lh.lyDoKham = dv.maDichVu) "
                + "WHERE lh.ngayHen = ? "
                + "ORDER BY lh.gioHen ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, ngay);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BenhNhan bn = new BenhNhan(rs.getString("maBN"), rs.getString("tenBenhNhan"));
                    bn.setSoDienThoai(rs.getString("sdtBenhNhan"));

                    NhanVien bs = new NhanVien(rs.getString("maNhaSi"), rs.getString("tenNhaSi"));

                    String maDV = rs.getString("maDichVu");
                    DichVu dv = null;
                    if (maDV != null) {
                        dv = new DichVu(maDV, rs.getString("tenDichVu"), rs.getDouble("giaNiemYet"));
                    } else if (rs.getString("lyDoKham") != null) {
                        dv = new DichVu("", rs.getString("lyDoKham"), 0);
                    }

                    LichHen lh = new LichHen(
                            rs.getString("maLichHen"),
                            bn,
                            bs,
                            dv,
                            rs.getDate("ngayHen"),
                            rs.getTime("gioHen"),
                            rs.getString("lyDoKham"),
                            rs.getString("ghiChu"),
                            rs.getString("trangThai"));
                    list.add(lh);
                }
            }
        } catch (Exception e) {
            System.err.println("LichHenDAO.getLichHenTheoNgay error: " + e.getMessage());
        }
        return list;
    }

    public List<LichHen> getLichHenTheoBacSi(String maBacSi, Date ngay) {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null || maBacSi == null) {
            return list;
        }

        String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                + "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, "
                + "nv.hoTen AS tenNhaSi, "
                + "dv.maDichVu, dv.tenDichVu, dv.giaNiemYet "
                + "FROM LichHen lh "
                + "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN "
                + "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV "
                + "LEFT JOIN DichVu dv ON (lh.lyDoKham = dv.tenDichVu OR lh.lyDoKham = dv.maDichVu) "
                + "WHERE lh.maNhaSi = ?" + (ngay != null ? " AND lh.ngayHen = ?" : "") + " "
                + "ORDER BY lh.ngayHen DESC, lh.gioHen ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBacSi);
            if (ngay != null) {
                stmt.setDate(2, ngay);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BenhNhan bn = new BenhNhan(rs.getString("maBN"), rs.getString("tenBenhNhan"));
                    bn.setSoDienThoai(rs.getString("sdtBenhNhan"));

                    NhanVien bs = new NhanVien(rs.getString("maNhaSi"), rs.getString("tenNhaSi"));

                    String maDV = rs.getString("maDichVu");
                    DichVu dv = null;
                    if (maDV != null) {
                        dv = new DichVu(maDV, rs.getString("tenDichVu"), rs.getDouble("giaNiemYet"));
                    } else if (rs.getString("lyDoKham") != null) {
                        dv = new DichVu("", rs.getString("lyDoKham"), 0);
                    }

                    LichHen lh = new LichHen(
                            rs.getString("maLichHen"),
                            bn,
                            bs,
                            dv,
                            rs.getDate("ngayHen"),
                            rs.getTime("gioHen"),
                            rs.getString("lyDoKham"),
                            rs.getString("ghiChu"),
                            rs.getString("trangThai"));
                    list.add(lh);
                }
            }
        } catch (Exception e) {
            System.err.println("LichHenDAO.getLichHenTheoBacSi error: " + e.getMessage());
        }
        return list;
    }

    public boolean themLichHen(LichHen lh) {
        if (lh == null || lh.getMaLichHen() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "INSERT INTO LichHen (maLichHen, maBN, maNhaSi, ngayHen, gioHen, lyDoKham, ghiChu, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lh.getMaLichHen());
            stmt.setString(2, lh.getMaBN());
            stmt.setString(3, lh.getMaNhaSi());
            stmt.setDate(4, lh.getNgayHen());
            stmt.setTime(5, lh.getGioHen());
            stmt.setString(6, lh.getLyDoKham());
            stmt.setString(7, lh.getGhiChu());
            stmt.setString(8, lh.getTrangThai() != null ? lh.getTrangThai() : "Đã lên lịch");
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("LichHenDAO.themLichHen DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatTrangThai(String maLichHen, String trangThai) {
        if (maLichHen == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "UPDATE LichHen SET trangThai = ? WHERE maLichHen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            stmt.setString(2, maLichHen);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("LichHenDAO.capNhatTrangThai DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean doiLichHen(String maLichHen, Date ngayMoi, Time gioMoi) {
        if (maLichHen == null || ngayMoi == null || gioMoi == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "UPDATE LichHen SET ngayHen = ?, gioHen = ? WHERE maLichHen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, ngayMoi);
            stmt.setTime(2, gioMoi);
            stmt.setString(3, maLichHen);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("LichHenDAO.doiLichHen DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean xoaLichHen(String maLichHen) {
        if (maLichHen == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "DELETE FROM LichHen WHERE maLichHen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maLichHen);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("LichHenDAO.xoaLichHen DB error: " + e.getMessage());
            return false;
        }
    }

    public List<LichHen> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllLichHen();
        }
        String clean = keyword.trim().toLowerCase();
        List<LichHen> all = getAllLichHen();
        List<LichHen> res = new ArrayList<>();
        for (LichHen lh : all) {
            boolean match = (lh.getMaLichHen() != null && lh.getMaLichHen().toLowerCase().contains(clean)) ||
                    (lh.getTenBenhNhan() != null && lh.getTenBenhNhan().toLowerCase().contains(clean)) ||
                    (lh.getTenNhaSi() != null && lh.getTenNhaSi().toLowerCase().contains(clean)) ||
                    (lh.getLyDoKham() != null && lh.getLyDoKham().toLowerCase().contains(clean)) ||
                    (lh.getTrangThai() != null && lh.getTrangThai().toLowerCase().contains(clean));
            if (match)
                res.add(lh);
        }
        return res;
    }

    public List<LichHen> timKiem(String keyword, Date ngay) {
        List<LichHen> matches = timKiem(keyword);
        if (ngay == null)
            return matches;
        List<LichHen> filtered = new ArrayList<>();
        for (LichHen lh : matches) {
            if (ngay.equals(lh.getNgayHen()))
                filtered.add(lh);
        }
        return filtered;
    }

    public List<String[]> getBenhNhanOptions() {
        List<String[]> options = new ArrayList<>();
        for (BenhNhan bn : new BenhNhanDAO().getAllBenhNhan()) {
            options.add(new String[] { bn.getMaBN(), bn.getHoTen(), bn.getSoDienThoai() });
        }
        return options;
    }

    public List<String[]> getNhaSiOptions() {
        List<String[]> options = new ArrayList<>();
        for (NhanVien nv : new NhanVienDAO().getAllNhanVien()) {
            String title = nv.getChucDanh() == null ? "" : nv.getChucDanh().toLowerCase();
            if (title.contains("nha sĩ") || title.contains("nha si") || title.contains("bác sĩ")
                    || title.contains("bac si") || nv.getMaNV().startsWith("BS")) {
                options.add(new String[] { nv.getMaNV(), nv.getHoTen() });
            }
        }
        return options;
    }

    public String getLastError() {
        return ConnectDB.getLastError();
    }

    public String sinhMaLichHenMoi() {
        Connection conn = ConnectDB.getConnection();
        int max = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maLichHen FROM LichHen";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maLichHen");
                        if (ma != null && ma.toUpperCase().startsWith("LH")) {
                            try {
                                int n = Integer.parseInt(ma.substring(2).trim());
                                if (n > max)
                                    max = n;
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("LichHenDAO.sinhMaLichHenMoi DB error: " + e.getMessage());
            }
        }
        return String.format("LH%03d", max + 1);
    }

    public boolean kiemTraTrungLichBacSi(String maBacSi, Date ngay, Time gio, String maLichHenBoQua) {
        if (maBacSi == null || ngay == null || gio == null) return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM LichHen " +
                     "WHERE maNhaSi = ? AND ngayHen = ? AND gioHen = ? " +
                     "AND trangThai NOT IN (N'Đã hủy', N'Hủy lịch')";
        if (maLichHenBoQua != null && !maLichHenBoQua.trim().isEmpty()) {
            sql += " AND maLichHen <> ?";
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBacSi);
            stmt.setDate(2, ngay);
            stmt.setTime(3, gio);
            if (maLichHenBoQua != null && !maLichHenBoQua.trim().isEmpty()) {
                stmt.setString(4, maLichHenBoQua.trim());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("LichHenDAO.kiemTraTrungLichBacSi error: " + e.getMessage());
        }
        return false;
    }

    public boolean hoanThanhLichHenTheoBenhNhan(String maBN) {
        if (maBN == null || maBN.trim().isEmpty())
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        String sql = "UPDATE LichHen SET trangThai = N'Đã hoàn thành' " +
                "WHERE maBN = ? AND trangThai IN (N'Đang khám', N'Chờ khám', N'Đã xác nhận', N'Đã lên lịch')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBN.trim());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("LichHenDAO.hoanThanhLichHenTheoBenhNhan DB error: " + e.getMessage());
            return false;
        }
    }
}
