package dao;

import connectDB.ConnectDB;
import entity.HoSoBenhAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Truy vấn lịch khám và lưu hồ sơ lâm sàng của nha sĩ đang đăng nhập. */
public class NhaSiDAO {
    public static class CaKham {
        public String maLichHen, maBN, tenBenhNhan, soDienThoai, tienSuBenh, ngayHen, gioHen, lyDo, trangThai;
    }

    public List<CaKham> getCaKham(String maNhaSi) throws SQLException {
        List<CaKham> rows = new ArrayList<>();
        String sql = "SELECT lh.maLichHen, lh.maBN, bn.hoTen, bn.soDienThoai, bn.tienSuBenh, CONVERT(VARCHAR(10),lh.ngayHen,23) ngayHen, " +
                "CONVERT(VARCHAR(5), lh.gioHen, 108) gioHen, lh.lyDoKham, lh.trangThai " +
                "FROM LichHen lh JOIN BenhNhan bn ON bn.maBN=lh.maBN " +
                "WHERE lh.maNhaSi=? AND lh.trangThai NOT IN (N'Đã hủy',N'Đã hoàn thành',N'Hoàn thành') " +
                "AND (lh.ngayHen>=CAST(GETDATE() AS DATE) OR lh.trangThai IN (N'Đã lên lịch',N'Đã xác nhận',N'Đang khám',N'Chờ khám')) " +
                "ORDER BY lh.ngayHen, lh.gioHen";
        try (Connection c = ConnectDB.getConnection()) {
            if (c == null) throw new SQLException(ConnectDB.getLastError());
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maNhaSi);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CaKham row = new CaKham();
                        row.maLichHen=rs.getString("maLichHen"); row.maBN=rs.getString("maBN");
                        row.tenBenhNhan=rs.getString("hoTen"); row.soDienThoai=rs.getString("soDienThoai");
                        row.tienSuBenh=rs.getString("tienSuBenh");
                        row.ngayHen=rs.getString("ngayHen");
                        row.gioHen=rs.getString("gioHen"); row.lyDo=rs.getString("lyDoKham");
                        row.trangThai=rs.getString("trangThai"); rows.add(row);
                    }
                }
            }
        }
        return rows;
    }

    public List<HoSoBenhAn> getLichSu(String maBN) throws SQLException {
        List<HoSoBenhAn> rows = new ArrayList<>();
        String sql = "SELECT hs.*, nv.hoTen tenNhaSi FROM HoSoBenhAn hs " +
                "LEFT JOIN NhanVien nv ON nv.maNV=hs.maNhaSi WHERE hs.maBN=? ORDER BY hs.ngayKham DESC";
        try (Connection c = ConnectDB.getConnection()) {
            if (c == null) throw new SQLException(ConnectDB.getLastError());
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maBN);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        HoSoBenhAn hs = new HoSoBenhAn(rs.getString("maHoSo"), rs.getString("maBN"),
                                rs.getString("maNhaSi"), rs.getString("maLichHen"), rs.getTimestamp("ngayKham"),
                                rs.getString("trieuChung"), rs.getString("chanDoan"),
                                rs.getString("ketQuaDieuTri"), rs.getString("ghiChu"), rs.getString("trangThai"));
                        hs.setTenNhaSi(rs.getString("tenNhaSi"));
                        hs.setTinhTrangLamSang(rs.getString("tinhTrangLamSang")); hs.setKetQuaCanLamSang(rs.getString("ketQuaCanLamSang"));
                        hs.setViTriRang(rs.getString("viTriRang")); hs.setKeHoachDieuTri(rs.getString("keHoachDieuTri"));
                        hs.setNoiDungCanThiep(rs.getString("noiDungCanThiep")); hs.setDonThuoc(rs.getString("donThuoc"));
                        hs.setHuongDanSauDieuTri(rs.getString("huongDanSauDieuTri")); hs.setNgayTaiKham(rs.getDate("ngayTaiKham"));
                        rows.add(hs);
                    }
                }
            }
        }
        return rows;
    }

    public HoSoBenhAn getHoSoTheoLich(String maLichHen) throws SQLException {
        String sql = "SELECT TOP 1 * FROM HoSoBenhAn WHERE maLichHen=? ORDER BY ngayKham DESC";
        try (Connection c = ConnectDB.getConnection()) {
            if (c == null) throw new SQLException(ConnectDB.getLastError());
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maLichHen);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    return mapRecord(rs);
                }
            }
        }
    }

    public HoSoBenhAn getHoSo(String maHoSo) throws SQLException {
        try (Connection c = ConnectDB.getConnection()) {
            if (c == null) throw new SQLException(ConnectDB.getLastError());
            try (PreparedStatement ps = c.prepareStatement("SELECT hs.*,nv.hoTen tenNhaSi FROM HoSoBenhAn hs LEFT JOIN NhanVien nv ON nv.maNV=hs.maNhaSi WHERE hs.maHoSo=?")) {
                ps.setString(1, maHoSo);
                try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapRecord(rs) : null; }
            }
        }
    }

    private HoSoBenhAn mapRecord(ResultSet rs) throws SQLException {
        HoSoBenhAn h = new HoSoBenhAn(rs.getString("maHoSo"), rs.getString("maBN"), rs.getString("maNhaSi"),
                rs.getString("maLichHen"), rs.getTimestamp("ngayKham"), rs.getString("trieuChung"),
                rs.getString("chanDoan"), rs.getString("ketQuaDieuTri"), rs.getString("ghiChu"), rs.getString("trangThai"));
        try { h.setTenNhaSi(rs.getString("tenNhaSi")); } catch (SQLException ignored) { }
        h.setTinhTrangLamSang(rs.getString("tinhTrangLamSang")); h.setKetQuaCanLamSang(rs.getString("ketQuaCanLamSang"));
        h.setViTriRang(rs.getString("viTriRang")); h.setKeHoachDieuTri(rs.getString("keHoachDieuTri"));
        h.setNoiDungCanThiep(rs.getString("noiDungCanThiep")); h.setDonThuoc(rs.getString("donThuoc"));
        h.setHuongDanSauDieuTri(rs.getString("huongDanSauDieuTri")); h.setNgayTaiKham(rs.getDate("ngayTaiKham"));
        return h;
    }

    /** Lưu hồ sơ theo lịch hẹn; hồ sơ hoàn tất được khóa, mọi lần ghi đều được đưa vào nhật ký. */
    public boolean luuHoSo(String maHoSo, String maBN, String maLichHen, String maNhaSi,
            String trieuChung, String chanDoan, String ketQua, String ghiChu, String trangThai,
            String tinhTrang, String canLamSang, String viTriRang, String keHoach,
            String canThiep, String donThuoc, String huongDan, Date ngayTaiKham,
            String taiKhoan) throws SQLException {
        try (Connection c = ConnectDB.getConnection()) {
            if (c == null) throw new SQLException("Không kết nối được cơ sở dữ liệu.");
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT 1 FROM LichHen WHERE maLichHen=? AND maBN=? AND maNhaSi=?")) {
                    ps.setString(1, maLichHen); ps.setString(2, maBN); ps.setString(3, maNhaSi);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Ca khám không thuộc bệnh nhân hoặc nha sĩ đang đăng nhập.");
                    }
                }
                HoSoBenhAn old = null;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT TOP 1 * FROM HoSoBenhAn WITH (UPDLOCK, HOLDLOCK) WHERE maLichHen=? ORDER BY ngayKham DESC")) {
                    ps.setString(1, maLichHen);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if ("Hoàn thành".equalsIgnoreCase(rs.getString("trangThai")) || "Hoan thanh".equalsIgnoreCase(rs.getString("trangThai")))
                                throw new SQLException("Hồ sơ đã hoàn tất và được khóa chỉnh sửa.");
                            maHoSo = rs.getString("maHoSo");
                            old = new HoSoBenhAn(); old.setTrieuChung(rs.getString("trieuChung"));
                            old.setChanDoan(rs.getString("chanDoan")); old.setKetQuaDieuTri(rs.getString("ketQuaDieuTri"));
                            old.setGhiChu(rs.getString("ghiChu"));
                            old.setTinhTrangLamSang(rs.getString("tinhTrangLamSang")); old.setKetQuaCanLamSang(rs.getString("ketQuaCanLamSang"));
                            old.setViTriRang(rs.getString("viTriRang")); old.setKeHoachDieuTri(rs.getString("keHoachDieuTri"));
                            old.setNoiDungCanThiep(rs.getString("noiDungCanThiep")); old.setDonThuoc(rs.getString("donThuoc"));
                            old.setHuongDanSauDieuTri(rs.getString("huongDanSauDieuTri")); old.setNgayTaiKham(rs.getDate("ngayTaiKham"));
                        }
                    }
                }
                if (old == null) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO HoSoBenhAn(maHoSo,maBN,maNhaSi,maLichHen,trieuChung,chanDoan,ketQuaDieuTri,ghiChu,trangThai,tinhTrangLamSang,ketQuaCanLamSang,viTriRang,keHoachDieuTri,noiDungCanThiep,donThuoc,huongDanSauDieuTri,ngayTaiKham) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                        int i=1; ps.setString(i++,maHoSo); ps.setString(i++,maBN); ps.setString(i++,maNhaSi);
                        ps.setString(i++,maLichHen); ps.setString(i++,trieuChung); ps.setString(i++,chanDoan);
                        ps.setString(i++,ketQua); ps.setString(i++,ghiChu); ps.setString(i++,trangThai); ps.setString(i++,tinhTrang);
                        ps.setString(i++,canLamSang); ps.setString(i++,viTriRang); ps.setString(i++,keHoach); ps.setString(i++,canThiep);
                        ps.setString(i++,donThuoc); ps.setString(i++,huongDan); ps.setDate(i,ngayTaiKham); ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE HoSoBenhAn SET trieuChung=?,chanDoan=?,ketQuaDieuTri=?,ghiChu=?,trangThai=?,tinhTrangLamSang=?,ketQuaCanLamSang=?,viTriRang=?,keHoachDieuTri=?,noiDungCanThiep=?,donThuoc=?,huongDanSauDieuTri=?,ngayTaiKham=? WHERE maHoSo=? AND maNhaSi=?")) {
                        int i=1; ps.setString(i++,trieuChung); ps.setString(i++,chanDoan); ps.setString(i++,ketQua);
                        ps.setString(i++,ghiChu); ps.setString(i++,trangThai); ps.setString(i++,tinhTrang); ps.setString(i++,canLamSang);
                        ps.setString(i++,viTriRang); ps.setString(i++,keHoach); ps.setString(i++,canThiep); ps.setString(i++,donThuoc);
                        ps.setString(i++,huongDan); ps.setDate(i++,ngayTaiKham); ps.setString(i++,maHoSo); ps.setString(i,maNhaSi);
                        if (ps.executeUpdate()!=1) throw new SQLException("Không tìm thấy hồ sơ thuộc nha sĩ đang đăng nhập.");
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO LichSuHeThong(tenDangNhap,hanhDong,chiTiet) VALUES(?,?,?)")) {
                    ps.setString(1,taiKhoan); ps.setString(2,old==null?"Tạo hồ sơ khám":"Cập nhật hồ sơ khám");
                    String after = "Triệu chứng="+trieuChung+"; chẩn đoán="+chanDoan+"; kết quả="+ketQua+"; ghi chú="+ghiChu+"; trạng thái="+trangThai;
                    String before = old==null?"(hồ sơ mới)":"Trước: triệu chứng="+old.getTrieuChung()+"; chẩn đoán="+old.getChanDoan()+"; kết quả="+old.getKetQuaDieuTri()+"; ghi chú="+old.getGhiChu()+". Sau: ";
                    String detail = "Hồ sơ "+maHoSo+", BN "+maBN+". "+before+after;
                    ps.setString(3, detail.length()>500?detail.substring(0,500):detail);
                    ps.executeUpdate();
                }
                HoSoBenhAn after = new HoSoBenhAn();
                after.setTrieuChung(trieuChung); after.setChanDoan(chanDoan); after.setKetQuaDieuTri(ketQua);
                after.setGhiChu(ghiChu); after.setTrangThai(trangThai); after.setTinhTrangLamSang(tinhTrang);
                after.setKetQuaCanLamSang(canLamSang); after.setViTriRang(viTriRang); after.setKeHoachDieuTri(keHoach);
                after.setNoiDungCanThiep(canThiep); after.setDonThuoc(donThuoc); after.setHuongDanSauDieuTri(huongDan);
                after.setNgayTaiKham(ngayTaiKham);
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO NhatKyBenhAn(maHoSo,maBN,tenDangNhap,hanhDong,noiDungTruoc,noiDungSau) VALUES(?,?,?,?,?,?)")) {
                    ps.setString(1,maHoSo); ps.setString(2,maBN); ps.setString(3,taiKhoan);
                    ps.setString(4,old==null?"Tạo hồ sơ":"Cập nhật hồ sơ");
                    ps.setString(5,old==null?null:toAuditText(old)); ps.setString(6,toAuditText(after)); ps.executeUpdate();
                }
                c.commit(); return true;
            } catch (SQLException ex) { c.rollback(); throw ex; }
            finally { c.setAutoCommit(true); }
        }
    }

    private String toAuditText(HoSoBenhAn h) {
        return "triệu chứng="+h.getTrieuChung()+"; khám="+h.getTinhTrangLamSang()+"; cận lâm sàng="+h.getKetQuaCanLamSang()+
                "; chẩn đoán="+h.getChanDoan()+"; vị trí="+h.getViTriRang()+"; kế hoạch="+h.getKeHoachDieuTri()+
                "; can thiệp="+h.getNoiDungCanThiep()+"; kết quả="+h.getKetQuaDieuTri()+"; đơn thuốc="+h.getDonThuoc()+
                "; hướng dẫn="+h.getHuongDanSauDieuTri()+"; tái khám="+h.getNgayTaiKham()+"; ghi chú="+h.getGhiChu()+"; trạng thái="+h.getTrangThai();
    }
}
