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
 * Lớp truy xuất dữ liệu Lịch Hẹn Khám Bệnh.
 * Chuẩn hóa:
 * - 100% sử dụng PreparedStatement.
 * - Tránh N+1 query: Dùng JOIN để nạp đầy đủ đối tượng BenhNhan, NhanVien (Bác
 * sĩ), DichVu.
 */
public class LichHenDAO {

    public static final List<LichHen> MOCK_LICH_HEN = new ArrayList<>();

    static {
        BenhNhan bn1 = new BenhNhan("BN001", "Nguyễn Văn Tuấn");
        bn1.setSoDienThoai("0901234567");
        NhanVien bs1 = new NhanVien("NV001", "BS. CKII Nguyễn Văn An");
        DichVu dv1 = new DichVu("DV001", "Cấy ghép Implant Straumann R46", 22000000);
        LichHen lh1 = new LichHen("LH001", bn1, bs1, dv1, Date.valueOf("2026-10-24"), Time.valueOf("08:30:00"),
                "Cấy ghép Implant Straumann R46", "Bệnh nhân đã chụp CT ConeBeam", "Đang khám");
        MOCK_LICH_HEN.add(lh1);

        BenhNhan bn2 = new BenhNhan("BN002", "Trần Mai Phương");
        bn2.setSoDienThoai("0909876543");
        NhanVien bs2 = new NhanVien("NV002", "ThS. BS Lê Thị Thảo");
        DichVu dv2 = new DichVu("DV002", "Tái khám siết dây cung niềng răng", 500000);
        LichHen lh2 = new LichHen("LH002", bn2, bs2, dv2, Date.valueOf("2026-10-24"), Time.valueOf("09:15:00"),
                "Tái khám siết dây cung niềng răng", "Thay thun màu xanh ngọc", "Đã xác nhận");
        MOCK_LICH_HEN.add(lh2);

        BenhNhan bn3 = new BenhNhan("BN003", "Phạm Quốc Dũng");
        bn3.setSoDienThoai("0988776655");
        NhanVien bs3 = new NhanVien("NV003", "BS. Trần Minh Trí");
        DichVu dv3 = new DichVu("DV004", "Nhổ răng khôn mọc ngầm R38", 2000000);
        LichHen lh3 = new LichHen("LH003", bn3, bs3, dv3, Date.valueOf("2026-10-24"), Time.valueOf("10:00:00"),
                "Nhổ răng khôn mọc ngầm R38", "Chụp Panorex trước tiểu phẫu", "Đã xác nhận");
        MOCK_LICH_HEN.add(lh3);

        BenhNhan bn4 = new BenhNhan("BN004", "Vũ Bích Ngọc");
        bn4.setSoDienThoai("0977665544");
        NhanVien bs4 = new NhanVien("NV002", "ThS. BS Lê Thị Thảo");
        DichVu dv4 = new DichVu("DV003", "Thử sườn răng sứ Cercon 4 răng cửa", 4500000);
        LichHen lh4 = new LichHen("LH004", bn4, bs4, dv4, Date.valueOf("2026-10-24"), Time.valueOf("14:00:00"),
                "Thử sườn răng sứ Cercon 4 răng cửa", "Bảo hành 10 năm", "Đã lên lịch");
        MOCK_LICH_HEN.add(lh4);
    }

    public List<LichHen> getAllLichHen() {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                    +
                    "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, " +
                    "nv.hoTen AS tenNhaSi, " +
                    "dv.maDichVu, dv.tenDichVu, dv.giaNiemYet " +
                    "FROM LichHen lh " +
                    "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN " +
                    "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV " +
                    "LEFT JOIN DichVu dv ON (lh.lyDoKham = dv.tenDichVu OR lh.lyDoKham = dv.maDichVu) " +
                    "ORDER BY lh.ngayHen DESC, lh.gioHen ASC";
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
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_LICH_HEN);
        }
        return list;
    }

    public List<LichHen> getLichHenHomNay() {
        return getLichHenTheoNgay(new Date(System.currentTimeMillis()));
    }

    public List<LichHen> getLichHenTheoNgay(Date ngay) {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null && ngay != null) {
            String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                    +
                    "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, " +
                    "nv.hoTen AS tenNhaSi, " +
                    "dv.maDichVu, dv.tenDichVu, dv.giaNiemYet " +
                    "FROM LichHen lh " +
                    "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN " +
                    "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV " +
                    "LEFT JOIN DichVu dv ON (lh.lyDoKham = dv.tenDichVu OR lh.lyDoKham = dv.maDichVu) " +
                    "WHERE lh.ngayHen = ? " +
                    "ORDER BY lh.gioHen ASC";
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
        }
        if (list.isEmpty() && conn == null) {
            String ngayStr = ngay != null ? ngay.toString() : "";
            for (LichHen lh : MOCK_LICH_HEN) {
                if (lh.getNgayHen() != null && lh.getNgayHen().toString().equals(ngayStr)) {
                    list.add(lh);
                }
            }
        }
        return list;
    }

    public List<LichHen> getLichHenTheoBacSi(String maBacSi, Date ngay) {
        List<LichHen> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null && maBacSi != null) {
            String sql = "SELECT lh.maLichHen, lh.maBN, lh.maNhaSi, lh.ngayHen, lh.gioHen, lh.lyDoKham, lh.ghiChu, lh.trangThai, "
                    +
                    "bn.hoTen AS tenBenhNhan, bn.soDienThoai AS sdtBenhNhan, " +
                    "nv.hoTen AS tenNhaSi, " +
                    "dv.maDichVu, dv.tenDichVu, dv.giaNiemYet " +
                    "FROM LichHen lh " +
                    "LEFT JOIN BenhNhan bn ON lh.maBN = bn.maBN " +
                    "LEFT JOIN NhanVien nv ON lh.maNhaSi = nv.maNV " +
                    "LEFT JOIN DichVu dv ON (lh.lyDoKham = dv.tenDichVu OR lh.lyDoKham = dv.maDichVu) " +
                    "WHERE lh.maNhaSi = ?" + (ngay != null ? " AND lh.ngayHen = ?" : "") + " " +
                    "ORDER BY lh.ngayHen DESC, lh.gioHen ASC";
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
        }
        if (list.isEmpty() && conn == null) {
            for (LichHen lh : MOCK_LICH_HEN) {
                if (lh.getMaNhaSi() != null && lh.getMaNhaSi().equalsIgnoreCase(maBacSi)) {
                    if (ngay == null
                            || (lh.getNgayHen() != null && lh.getNgayHen().toString().equals(ngay.toString()))) {
                        list.add(lh);
                    }
                }
            }
        }
        return list;
    }

    public boolean themLichHen(LichHen lh) {
        if (lh == null || lh.getMaLichHen() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            String sql = "INSERT INTO LichHen (maLichHen, maBN, maNhaSi, ngayHen, gioHen, lyDoKham, ghiChu, trangThai) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, lh.getMaLichHen());
                stmt.setString(2, lh.getMaBN());
                stmt.setString(3, lh.getMaNhaSi());
                stmt.setDate(4, lh.getNgayHen());
                stmt.setTime(5, lh.getGioHen());
                stmt.setString(6, lh.getLyDoKham());
                stmt.setString(7, lh.getGhiChu());
                stmt.setString(8, lh.getTrangThai() != null ? lh.getTrangThai() : "Đã lên lịch");
                dbOk = stmt.executeUpdate() > 0;
            } catch (Exception e) {
                System.err.println("LichHenDAO.themLichHen DB error: " + e.getMessage());
            }
        }
        MOCK_LICH_HEN.add(0, lh);
        return dbOk || conn == null;
    }

    public boolean capNhatTrangThai(String maLichHen, String trangThai) {
        if (maLichHen == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            String sql = "UPDATE LichHen SET trangThai = ? WHERE maLichHen = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, trangThai);
                stmt.setString(2, maLichHen);
                dbOk = stmt.executeUpdate() > 0;
            } catch (Exception e) {
                System.err.println("LichHenDAO.capNhatTrangThai DB error: " + e.getMessage());
            }
        }
        for (LichHen lh : MOCK_LICH_HEN) {
            if (lh.getMaLichHen().equalsIgnoreCase(maLichHen)) {
                lh.setTrangThai(trangThai);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean doiLichHen(String maLichHen, Date ngayMoi, Time gioMoi) {
        if (maLichHen == null || ngayMoi == null || gioMoi == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            String sql = "UPDATE LichHen SET ngayHen = ?, gioHen = ? WHERE maLichHen = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, ngayMoi);
                stmt.setTime(2, gioMoi);
                stmt.setString(3, maLichHen);
                dbOk = stmt.executeUpdate() > 0;
            } catch (Exception e) {
                System.err.println("LichHenDAO.doiLichHen DB error: " + e.getMessage());
            }
        }
        for (LichHen lh : MOCK_LICH_HEN) {
            if (lh.getMaLichHen().equalsIgnoreCase(maLichHen)) {
                lh.setNgayHen(ngayMoi);
                lh.setGioHen(gioMoi);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean xoaLichHen(String maLichHen) {
        if (maLichHen == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            String sql = "DELETE FROM LichHen WHERE maLichHen = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maLichHen);
                dbOk = stmt.executeUpdate() > 0;
            } catch (Exception e) {
                System.err.println("LichHenDAO.xoaLichHen DB error: " + e.getMessage());
            }
        }
        final String fMa = maLichHen;
        MOCK_LICH_HEN.removeIf(l -> l.getMaLichHen().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
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
        int max = 0;
        List<LichHen> all = getAllLichHen();
        for (LichHen lh : all) {
            String ma = lh.getMaLichHen();
            if (ma != null && ma.toUpperCase().startsWith("LH")) {
                try {
                    int n = Integer.parseInt(ma.substring(2).trim());
                    if (n > max)
                        max = n;
                } catch (Exception ignored) {
                }
            }
        }
        return String.format("LH%03d", max + 1);
    }

    public boolean kiemTraTrungLichBacSi(String maBacSi, Date ngay, Time gio, String maLichHenBoQua) {
        if (maBacSi == null || ngay == null || gio == null) return false;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
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
        }
        for (LichHen lh : getAllLichHen()) {
            if (maLichHenBoQua != null && lh.getMaLichHen().equalsIgnoreCase(maLichHenBoQua)) continue;
            if (lh.getMaNhaSi() != null && lh.getMaNhaSi().equalsIgnoreCase(maBacSi)) {
                if (ngay.equals(lh.getNgayHen()) && gio.equals(lh.getGioHen())) {
                    String st = lh.getTrangThai();
                    if (!"Đã hủy".equalsIgnoreCase(st) && !"Hủy lịch".equalsIgnoreCase(st)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hoanThanhLichHenTheoBenhNhan(String maBN) {
        if (maBN == null || maBN.trim().isEmpty())
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            String sql = "UPDATE LichHen SET trangThai = N'Đã hoàn thành' " +
                    "WHERE maBN = ? AND trangThai IN (N'Đang khám', N'Chờ khám', N'Đã xác nhận', N'Đã lên lịch')";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maBN.trim());
                stmt.executeUpdate();
                dbOk = true;
            } catch (Exception e) {
                System.err.println("LichHenDAO.hoanThanhLichHenTheoBenhNhan DB error: " + e.getMessage());
            }
        }
        for (LichHen lh : MOCK_LICH_HEN) {
            if (maBN.trim().equalsIgnoreCase(lh.getMaBN())) {
                String st = lh.getTrangThai();
                if ("Đang khám".equalsIgnoreCase(st) || "Chờ khám".equalsIgnoreCase(st)
                        || "Đã xác nhận".equalsIgnoreCase(st) || "Đã lên lịch".equalsIgnoreCase(st)) {
                    lh.setTrangThai("Đã hoàn thành");
                }
            }
        }
        return dbOk || conn == null;
    }
}
