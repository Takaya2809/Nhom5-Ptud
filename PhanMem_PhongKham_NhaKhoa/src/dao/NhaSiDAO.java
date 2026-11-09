package dao;

import connectDB.ConnectDB;
import entity.HoSoBenhAn;
import entity.HoaDonKham;
import entity.LichHen;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access for the dentist's examination, diagnosis, treatment and follow-up
 * use cases (UC001-UC005) từ CSDL SQL Server thật.
 */
public class NhaSiDAO {

    public static class CaKham {
        public String maLichHen, maBN, tenBenhNhan, soDienThoai, tienSuBenh;
        public String ngayHen, gioHen, lyDo, trangThai;
    }

    private static volatile boolean migrated = false;

    private static synchronized void checkAndMigrate(Connection c) {
        if (migrated || c == null)
            return;
        String[] migrations = {
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'tinhTrangLamSang') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD tinhTrangLamSang NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'ketQuaCanLamSang') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD ketQuaCanLamSang NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'chiDinhCanLamSang') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD chiDinhCanLamSang NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'viTriRang') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD viTriRang NVARCHAR(255) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'soDoRang') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD soDoRang NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'keHoachDieuTri') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD keHoachDieuTri NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'noiDungCanThiep') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD noiDungCanThiep NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'donThuoc') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD donThuoc NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'huongDanSauDieuTri') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD huongDanSauDieuTri NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'ngayTaiKham') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD ngayTaiKham DATE NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'gioTaiKham') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD gioTaiKham TIME NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'danhGiaTaiKham') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD danhGiaTaiKham NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'huongXuLyTiepTheo') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD huongXuLyTiepTheo NVARCHAR(MAX) NULL;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'xacNhanDongY') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD xacNhanDongY BIT NOT NULL CONSTRAINT DF_HoSoBenhAn_xacNhanDongY DEFAULT 0;",
                "IF COL_LENGTH('dbo.HoSoBenhAn', 'maLichHenTaiKham') IS NULL ALTER TABLE dbo.HoSoBenhAn ADD maLichHenTaiKham VARCHAR(30) NULL;",
                "IF OBJECT_ID('dbo.NhatKyBenhAn', 'U') IS NULL CREATE TABLE dbo.NhatKyBenhAn ("
                        + "maAudit BIGINT IDENTITY(1,1) PRIMARY KEY, "
                        + "maHoSo VARCHAR(30) NOT NULL, "
                        + "maBN VARCHAR(20) NOT NULL, "
                        + "tenDangNhap VARCHAR(50) NOT NULL, "
                        + "hanhDong NVARCHAR(50) NOT NULL, "
                        + "chiTiet NVARCHAR(MAX) NULL, "
                        + "thoiGian DATETIME2 NOT NULL DEFAULT SYSDATETIME(), "
                        + "noiDungTruoc NVARCHAR(MAX) NULL, "
                        + "noiDungSau NVARCHAR(MAX) NULL);"
        };
        for (String sql : migrations) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.execute();
            } catch (Exception ignored) {
            }
        }
        migrated = true;
    }

    public List<CaKham> getCaKham(String maNhaSi) throws SQLException {
        List<CaKham> rows = new ArrayList<>();
        Connection c = ConnectDB.getConnection();
        if (c == null) {
            return rows;
        }

        checkAndMigrate(c);
        String sql = "SELECT lh.maLichHen, lh.maBN, bn.hoTen, bn.soDienThoai, bn.tienSuBenh, "
                + "CONVERT(VARCHAR(10),lh.ngayHen,23) ngayHen, CONVERT(VARCHAR(5),lh.gioHen,108) gioHen, "
                + "lh.lyDoKham, lh.trangThai FROM LichHen lh JOIN BenhNhan bn ON bn.maBN=lh.maBN "
                + "WHERE (lh.maNhaSi=? OR lh.maNhaSi IS NULL OR lh.maNhaSi='' OR ? IS NULL OR ?='') "
                + "AND lh.trangThai NOT IN (N'Đã hủy', N'Đã hoàn thành', N'Hoàn thành') "
                + "AND (lh.ngayHen>=CAST(GETDATE() AS DATE) OR lh.trangThai IN (N'Đã lên lịch', N'Đã xác nhận', N'Đang khám', N'Chờ khám')) "
                + "ORDER BY lh.ngayHen, lh.gioHen";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maNhaSi);
            ps.setString(2, maNhaSi);
            ps.setString(3, maNhaSi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CaKham row = new CaKham();
                    row.maLichHen = rs.getString("maLichHen");
                    row.maBN = rs.getString("maBN");
                    row.tenBenhNhan = rs.getString("hoTen");
                    row.soDienThoai = rs.getString("soDienThoai");
                    row.tienSuBenh = rs.getString("tienSuBenh");
                    row.ngayHen = rs.getString("ngayHen");
                    row.gioHen = rs.getString("gioHen");
                    row.lyDo = rs.getString("lyDoKham");
                    row.trangThai = rs.getString("trangThai");
                    rows.add(row);
                }
            }
        } catch (SQLException ex) {
            System.err.println("NhaSiDAO.getCaKham SQL error: " + ex.getMessage());
        }
        return rows;
    }

    public List<HoSoBenhAn> getLichSu(String maBN) throws SQLException {
        List<HoSoBenhAn> rows = new ArrayList<>();
        Connection c = ConnectDB.getConnection();
        if (c == null) {
            return rows;
        }

        checkAndMigrate(c);
        String sql = "SELECT hs.*, nv.hoTen tenNhaSi, bn.hoTen tenBenhNhan FROM HoSoBenhAn hs "
                + "LEFT JOIN NhanVien nv ON nv.maNV=hs.maNhaSi "
                + "LEFT JOIN BenhNhan bn ON bn.maBN=hs.maBN "
                + "WHERE hs.maBN=? ORDER BY hs.ngayKham DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maBN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    rows.add(mapRecord(rs));
            }
        } catch (SQLException ex) {
            System.err.println("NhaSiDAO.getLichSu SQL error: " + ex.getMessage());
        }
        return rows;
    }

    public List<HoSoBenhAn> getTheoDoiDieuTri(String maNhaSi) throws SQLException {
        List<HoSoBenhAn> rows = new ArrayList<>();
        Connection c = ConnectDB.getConnection();
        if (c == null) {
            return rows;
        }

        checkAndMigrate(c);
        String sql = "SELECT hs.*, nv.hoTen tenNhaSi, bn.hoTen tenBenhNhan FROM HoSoBenhAn hs "
                + "LEFT JOIN NhanVien nv ON nv.maNV=hs.maNhaSi "
                + "LEFT JOIN BenhNhan bn ON bn.maBN=hs.maBN "
                + "WHERE (hs.maNhaSi=? OR ? IS NULL OR ?='') AND (hs.trangThai IN (N'Đang điều trị', N'Chờ kết quả cận lâm sàng', N'Chờ tái khám', N'Chờ hoàn tất') "
                + "OR hs.ngayTaiKham>=CAST(GETDATE() AS DATE)) ORDER BY hs.ngayTaiKham, hs.ngayKham DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maNhaSi);
            ps.setString(2, maNhaSi);
            ps.setString(3, maNhaSi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    rows.add(mapRecord(rs));
            }
        } catch (SQLException ex) {
            System.err.println("NhaSiDAO.getTheoDoiDieuTri SQL error: " + ex.getMessage());
        }
        return rows;
    }

    public HoSoBenhAn getHoSoTheoLich(String maLichHen) throws SQLException {
        Connection c = ConnectDB.getConnection();
        if (c == null) {
            return null;
        }

        checkAndMigrate(c);
        String sql = "SELECT TOP 1 hs.*, nv.hoTen tenNhaSi, bn.hoTen tenBenhNhan FROM HoSoBenhAn hs "
                + "LEFT JOIN NhanVien nv ON nv.maNV=hs.maNhaSi "
                + "LEFT JOIN BenhNhan bn ON bn.maBN=hs.maBN "
                + "WHERE hs.maLichHen=? ORDER BY hs.ngayKham DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maLichHen);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRecord(rs);
            }
        } catch (SQLException ex) {
            System.err.println("NhaSiDAO.getHoSoTheoLich SQL error: " + ex.getMessage());
        }
        return null;
    }

    public HoSoBenhAn getHoSo(String maHoSo) throws SQLException {
        Connection c = ConnectDB.getConnection();
        if (c == null) {
            return null;
        }

        checkAndMigrate(c);
        String sql = "SELECT hs.*, nv.hoTen tenNhaSi, bn.hoTen tenBenhNhan FROM HoSoBenhAn hs "
                + "LEFT JOIN NhanVien nv ON nv.maNV=hs.maNhaSi "
                + "LEFT JOIN BenhNhan bn ON bn.maBN=hs.maBN "
                + "WHERE hs.maHoSo=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maHoSo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRecord(rs);
            }
        } catch (SQLException ex) {
            System.err.println("NhaSiDAO.getHoSo SQL error: " + ex.getMessage());
        }
        return null;
    }

    private HoSoBenhAn mapRecord(ResultSet rs) throws SQLException {
        HoSoBenhAn h = new HoSoBenhAn(
                optionalString(rs, "maHoSo"),
                optionalString(rs, "maBN"),
                optionalString(rs, "maNhaSi"),
                optionalString(rs, "maLichHen"),
                optionalTimestamp(rs, "ngayKham"),
                optionalString(rs, "trieuChung"),
                optionalString(rs, "chanDoan"),
                optionalString(rs, "ketQuaDieuTri"),
                optionalString(rs, "ghiChu"),
                optionalString(rs, "trangThai"));
        h.setTenBenhNhan(optionalString(rs, "tenBenhNhan"));
        h.setTenNhaSi(optionalString(rs, "tenNhaSi"));
        h.setMaLichHenTaiKham(optionalString(rs, "maLichHenTaiKham"));
        h.setTinhTrangLamSang(optionalString(rs, "tinhTrangLamSang"));
        h.setChiDinhCanLamSang(optionalString(rs, "chiDinhCanLamSang"));
        h.setKetQuaCanLamSang(optionalString(rs, "ketQuaCanLamSang"));
        h.setViTriRang(optionalString(rs, "viTriRang"));
        h.setSoDoRang(optionalString(rs, "soDoRang"));
        h.setKeHoachDieuTri(optionalString(rs, "keHoachDieuTri"));
        h.setNoiDungCanThiep(optionalString(rs, "noiDungCanThiep"));
        h.setDonThuoc(optionalString(rs, "donThuoc"));
        h.setHuongDanSauDieuTri(optionalString(rs, "huongDanSauDieuTri"));
        h.setDanhGiaTaiKham(optionalString(rs, "danhGiaTaiKham"));
        h.setHuongXuLyTiepTheo(optionalString(rs, "huongXuLyTiepTheo"));
        try {
            h.setNgayTaiKham(rs.getDate("ngayTaiKham"));
        } catch (SQLException ignored) {
        }
        try {
            h.setGioTaiKham(rs.getTime("gioTaiKham"));
        } catch (SQLException ignored) {
        }
        try {
            h.setXacNhanDongY(rs.getBoolean("xacNhanDongY"));
        } catch (SQLException ignored) {
        }
        return h;
    }

    private String optionalString(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException ignored) {
            return null;
        }
    }

    private Timestamp optionalTimestamp(ResultSet rs, String column) {
        try {
            return rs.getTimestamp(column);
        } catch (SQLException ignored) {
            return new Timestamp(System.currentTimeMillis());
        }
    }

    /**
     * Lưu hồ sơ điều trị nha sĩ và thiết lập lịch tái khám nguyên tử
     */
    public void luuHoSo(HoSoBenhAn h, String taiKhoan, boolean hoanTat, boolean datLichTheoDoi)
            throws SQLException {
        Connection c = ConnectDB.getConnection();
        if (c == null) {
            throw new SQLException("Chưa kết nối CSDL SQL Server!");
        }

        checkAndMigrate(c);
        boolean oldAutoCommit = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            HoSoBenhAn old = findByAppointment(c, h.getMaLichHen());
            if (old != null) {
                if (isCompleted(old.getTrangThai())) {
                    throw new SQLException("Hồ sơ đã hoàn tất và được khóa chỉnh sửa.");
                }
                h.setMaHoSo(old.getMaHoSo());
                if (h.getMaLichHenTaiKham() == null && !h.isHuyLichTaiKham())
                    h.setMaLichHenTaiKham(old.getMaLichHenTaiKham());
                if (h.isHuyLichTaiKham() && h.getMaLichHenTaiKham() == null)
                    h.setMaLichHenTaiKham(old.getMaLichHenTaiKham());
                updateRecord(c, h);
            } else {
                closePreviousFollowUp(c, h.getMaLichHen(), h.getMaNhaSi());
                insertRecord(c, h);
            }

            if (datLichTheoDoi) {
                if (h.getNgayTaiKham() == null || h.getGioTaiKham() == null) {
                    throw new SQLException("Cần nhập ngày và giờ tái khám để đặt lịch theo dõi.");
                }
                String followUpId = saveFollowUpAppointment(c, h);
                h.setMaLichHenTaiKham(followUpId);
                updateFollowUpLink(c, h);
            } else if (h.isHuyLichTaiKham() && h.getMaLichHenTaiKham() != null) {
                cancelFollowUpAppointment(c, h.getMaLichHenTaiKham());
                h.setMaLichHenTaiKham(null);
                updateFollowUpLink(c, h);
            }

            writeAudit(c, h, taiKhoan, old == null ? "Tạo hồ sơ nha sĩ" : "Cập nhật hồ sơ nha sĩ");
            updateAppointmentStatus(c, h.getMaLichHen(), hoanTat);
            if (hoanTat) {
                taoHoaDonChoHoSo(c, h);
            }
            c.commit();
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(oldAutoCommit);
        }
    }

    private void taoHoaDonChoHoSo(Connection c, HoSoBenhAn h) {
        if (h == null || h.getMaBN() == null)
            return;
        try {
            String checkSql = "SELECT COUNT(*) FROM HoaDonKham WHERE maBN = ? AND trangThai = N'Chờ thanh toán'";
            try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                ps.setString(1, h.getMaBN());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0)
                        return;
                }
            }

            String maDV = "DV001";
            double donGia = 300000;
            String sqlDV = "SELECT TOP 1 maDichVu, giaNiemYet FROM DichVu WHERE trangThai LIKE N'%cung cấp%' OR trangThai = '1' ORDER BY maDichVu ASC";
            try (PreparedStatement psDV = c.prepareStatement(sqlDV);
                    ResultSet rsDV = psDV.executeQuery()) {
                if (rsDV.next()) {
                    maDV = rsDV.getString("maDichVu");
                    donGia = rsDV.getDouble("giaNiemYet");
                }
            }

            String insertSql = "INSERT INTO HoaDonKham (ngayLap, maBN, maNV, maDichVu, tongTien, giamGia, thanhTien, phuongThucTT, trangThai) "
                    + "VALUES (GETDATE(), ?, ?, ?, ?, 0, ?, N'Chưa chọn', N'Chờ thanh toán')";
            try (PreparedStatement psIns = c.prepareStatement(insertSql)) {
                psIns.setString(1, h.getMaBN());
                psIns.setString(2, h.getMaNhaSi() != null ? h.getMaNhaSi() : "NV001");
                psIns.setString(3, maDV);
                psIns.setDouble(4, donGia);
                psIns.setDouble(5, donGia);
                psIns.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("NhaSiDAO.taoHoaDonChoHoSo error: " + e.getMessage());
        }
    }

    private HoSoBenhAn findByAppointment(Connection c, String maLichHen) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT TOP 1 * FROM HoSoBenhAn WHERE maLichHen=? ORDER BY ngayKham DESC")) {
            ps.setString(1, maLichHen);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRecord(rs) : null;
            }
        }
    }

    private void closePreviousFollowUp(Connection c, String maLichHen, String maNhaSi) throws SQLException {
        String sql = "UPDATE HoSoBenhAn SET trangThai=N'Đã tái khám' "
                + "WHERE maLichHenTaiKham=? AND trangThai=N'Chờ tái khám'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maLichHen);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private void insertRecord(Connection c, HoSoBenhAn h) throws SQLException {
        String sql = "INSERT INTO HoSoBenhAn(maHoSo, maBN, maNhaSi, maLichHen, trieuChung, chanDoan, ketQuaDieuTri, ghiChu, trangThai,"
                + "tinhTrangLamSang, chiDinhCanLamSang, ketQuaCanLamSang, viTriRang, soDoRang, keHoachDieuTri, noiDungCanThiep, donThuoc,"
                + "huongDanSauDieuTri, danhGiaTaiKham, huongXuLyTiepTheo, ngayTaiKham, gioTaiKham, xacNhanDongY, maLichHenTaiKham) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bindRecord(ps, h, false);
            ps.executeUpdate();
        }
    }

    private void updateRecord(Connection c, HoSoBenhAn h) throws SQLException {
        String sql = "UPDATE HoSoBenhAn SET trieuChung=?, chanDoan=?, ketQuaDieuTri=?, ghiChu=?, trangThai=?, tinhTrangLamSang=?,"
                + "chiDinhCanLamSang=?, ketQuaCanLamSang=?, viTriRang=?, soDoRang=?, keHoachDieuTri=?, noiDungCanThiep=?, donThuoc=?,"
                + "huongDanSauDieuTri=?, danhGiaTaiKham=?, huongXuLyTiepTheo=?, ngayTaiKham=?, gioTaiKham=?, xacNhanDongY=?, maLichHenTaiKham=? "
                + "WHERE maHoSo=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bindRecord(ps, h, true);
            ps.executeUpdate();
        }
    }

    private void bindRecord(PreparedStatement ps, HoSoBenhAn h, boolean update) throws SQLException {
        int i = 1;
        if (!update) {
            ps.setString(i++, h.getMaHoSo());
            ps.setString(i++, h.getMaBN());
            ps.setString(i++, h.getMaNhaSi());
            ps.setString(i++, h.getMaLichHen());
        }
        String tc = h.getTrieuChung();
        if (tc == null || tc.trim().isEmpty())
            tc = "Bệnh nhân đến khám theo lịch hẹn";
        String cd = h.getChanDoan();
        if (cd == null || cd.trim().isEmpty())
            cd = "Khám và điều trị nha khoa tổng quát";
        ps.setString(i++, tc);
        ps.setString(i++, cd);
        ps.setString(i++, h.getKetQuaDieuTri() != null ? h.getKetQuaDieuTri() : "Điều trị đạt kết quả tốt");
        ps.setString(i++, h.getGhiChu() != null ? h.getGhiChu() : "");
        ps.setString(i++, h.getTrangThai() != null ? h.getTrangThai() : "Hoàn thành");
        ps.setString(i++, h.getTinhTrangLamSang());
        ps.setString(i++, h.getChiDinhCanLamSang());
        ps.setString(i++, h.getKetQuaCanLamSang());
        ps.setString(i++, h.getViTriRang());
        ps.setString(i++, h.getSoDoRang());
        ps.setString(i++, h.getKeHoachDieuTri());
        ps.setString(i++, h.getNoiDungCanThiep());
        ps.setString(i++, h.getDonThuoc());
        ps.setString(i++, h.getHuongDanSauDieuTri());
        ps.setString(i++, h.getDanhGiaTaiKham());
        ps.setString(i++, h.getHuongXuLyTiepTheo());
        ps.setDate(i++, h.getNgayTaiKham());
        ps.setTime(i++, h.getGioTaiKham());
        ps.setBoolean(i++, h.isXacNhanDongY());
        ps.setString(i++, h.getMaLichHenTaiKham());
        if (update) {
            ps.setString(i++, h.getMaHoSo());
        }
    }

    private String saveFollowUpAppointment(Connection c, HoSoBenhAn h) throws SQLException {
        String id = h.getMaLichHenTaiKham();
        String note = "Theo dõi hồ sơ " + h.getMaHoSo() + ". " + nullToEmpty(h.getHuongDanSauDieuTri());
        if (id == null || id.trim().isEmpty()) {
            id = "LH_TK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase();
            String sql = "INSERT INTO LichHen(maLichHen, maBN, maNhaSi, ngayHen, gioHen, lyDoKham, ghiChu, trangThai) "
                    + "VALUES(?,?,?,?,?,?,?,N'Đã lên lịch')";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, h.getMaBN());
                ps.setString(3, h.getMaNhaSi());
                ps.setDate(4, h.getNgayTaiKham());
                ps.setTime(5, h.getGioTaiKham());
                ps.setString(6, "Tái khám theo hồ sơ " + h.getMaHoSo());
                ps.setString(7, note);
                ps.executeUpdate();
            }
        } else {
            String sql = "UPDATE LichHen SET ngayHen=?, gioHen=?, lyDoKham=?, ghiChu=?, trangThai=N'Đã lên lịch' "
                    + "WHERE maLichHen=?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDate(1, h.getNgayTaiKham());
                ps.setTime(2, h.getGioTaiKham());
                ps.setString(3, "Tái khám theo hồ sơ " + h.getMaHoSo());
                ps.setString(4, note);
                ps.setString(5, id);
                ps.executeUpdate();
            }
        }
        return id;
    }

    private void updateFollowUpLink(Connection c, HoSoBenhAn h) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE HoSoBenhAn SET maLichHenTaiKham=? WHERE maHoSo=?")) {
            ps.setString(1, h.getMaLichHenTaiKham());
            ps.setString(2, h.getMaHoSo());
            ps.executeUpdate();
        }
    }

    private void cancelFollowUpAppointment(Connection c, String id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE LichHen SET trangThai=N'Đã hủy' WHERE maLichHen=? AND ngayHen>=CAST(GETDATE() AS DATE)")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private void updateAppointmentStatus(Connection c, String maLichHen, boolean complete) throws SQLException {
        String state = complete ? "Đã hoàn tất" : "Đang khám";
        try (PreparedStatement ps = c.prepareStatement("UPDATE LichHen SET trangThai=? WHERE maLichHen=?")) {
            ps.setString(1, state);
            ps.setString(2, maLichHen);
            ps.executeUpdate();
        }
    }

    private void writeAudit(Connection c, HoSoBenhAn h, String taiKhoan, String action) {
        String text = "Triệu chứng=" + nullToEmpty(h.getTrieuChung()) + "; khám=" + nullToEmpty(h.getTinhTrangLamSang())
                + "; chỉ định CLS=" + nullToEmpty(h.getChiDinhCanLamSang()) + "; kết quả CLS="
                + nullToEmpty(h.getKetQuaCanLamSang())
                + "; chẩn đoán=" + nullToEmpty(h.getChanDoan()) + "; kế hoạch=" + nullToEmpty(h.getKeHoachDieuTri())
                + "; can thiệp=" + nullToEmpty(h.getNoiDungCanThiep()) + "; kết quả="
                + nullToEmpty(h.getKetQuaDieuTri())
                + "; đánh giá tái khám=" + nullToEmpty(h.getDanhGiaTaiKham()) + "; hướng xử lý="
                + nullToEmpty(h.getHuongXuLyTiepTheo())
                + "; tái khám=" + h.getNgayTaiKham() + " " + h.getGioTaiKham() + "; trạng thái=" + h.getTrangThai();
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO NhatKyBenhAn(maHoSo,maBN,tenDangNhap,hanhDong,noiDungSau) VALUES(?,?,?,?,?)")) {
            ps.setString(1, h.getMaHoSo());
            ps.setString(2, h.getMaBN());
            ps.setString(3, taiKhoan);
            ps.setString(4, action);
            ps.setString(5, text);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private boolean isCompleted(String state) {
        return state != null && (state.equalsIgnoreCase("Hoàn thành") || state.equalsIgnoreCase("Đã hoàn thành")
                || state.equalsIgnoreCase("Đã hoàn tất"));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
