package dao;

import connectDB.ConnectDB;
import entity.HoaDonKham;
import entity.UuDai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Hóa Đơn Khám & Thanh Toán Viện Phí
 * Hỗ trợ đồng thời CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class HoaDonDAO {

    private static final List<HoaDonKham> MOCK_HOA_DON = new ArrayList<>();

    static {
        HoaDonKham hd1 = new HoaDonKham(1, new Timestamp(System.currentTimeMillis() - 86400000L), "BN001", "NV001", "DV001", 22000000, 2200000, 19800000, "Chuyển khoản QR", "Đã thanh toán");
        hd1.setTenBN("Nguyễn Văn Tuấn");
        hd1.setTenNV("BS. CKII Nguyễn Văn An");
        hd1.setTenDichVu("Cấy ghép Implant Straumann Thụy Sĩ");
        MOCK_HOA_DON.add(hd1);

        HoaDonKham hd2 = new HoaDonKham(2, new Timestamp(System.currentTimeMillis() - 86400000L), "BN002", "NV002", "DV002", 30000000, 3000000, 27000000, "Quẹt thẻ POS", "Đã thanh toán");
        hd2.setTenBN("Trần Mai Phương");
        hd2.setTenNV("ThS. BS Lê Thị Thảo");
        hd2.setTenDichVu("Niềng răng mắc cài kim loại tự buộc");
        MOCK_HOA_DON.add(hd2);

        HoaDonKham hd3 = new HoaDonKham(3, new Timestamp(System.currentTimeMillis()), "BN003", "NV003", "DV004", 2000000, 0, 2000000, "Tiền mặt", "Đã thanh toán");
        hd3.setTenBN("Phạm Quốc Dũng");
        hd3.setTenNV("BS. Trần Minh Trí");
        hd3.setTenDichVu("Nhổ răng khôn Piezotome sóng siêu âm");
        MOCK_HOA_DON.add(hd3);

        HoaDonKham hd4 = new HoaDonKham(4, new Timestamp(System.currentTimeMillis()), "BN004", "NV002", "DV003", 4500000, 450000, 4050000, "Chuyển khoản QR", "Đã thanh toán");
        hd4.setTenBN("Vũ Bích Ngọc");
        hd4.setTenNV("ThS. BS Lê Thị Thảo");
        hd4.setTenDichVu("Bọc răng toàn sứ Cercon cao cấp");
        MOCK_HOA_DON.add(hd4);
    }

    public List<HoaDonKham> getAllHoaDon() {
        List<HoaDonKham> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT h.maHD, h.ngayLap, h.maBN, h.maNV, h.maDichVu, h.tongTien, h.giamGia, h.thanhTien, " +
                             "h.phuongThucTT, h.trangThai, bn.hoTen AS tenBN, nv.hoTen AS tenNV, dv.tenDichVu " +
                             "FROM HoaDonKham h " +
                             "LEFT JOIN BenhNhan bn ON h.maBN = bn.maBN " +
                             "LEFT JOIN NhanVien nv ON h.maNV = nv.maNV " +
                             "LEFT JOIN DichVu dv ON h.maDichVu = dv.maDichVu " +
                             "ORDER BY h.maHD DESC";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        HoaDonKham hd = new HoaDonKham(
                            rs.getInt("maHD"),
                            rs.getTimestamp("ngayLap"),
                            rs.getString("maBN"),
                            rs.getString("maNV"),
                            rs.getString("maDichVu"),
                            rs.getDouble("tongTien"),
                            rs.getDouble("giamGia"),
                            rs.getDouble("thanhTien"),
                            rs.getString("phuongThucTT"),
                            rs.getString("trangThai")
                        );
                        hd.setTenBN(rs.getString("tenBN"));
                        hd.setTenNV(rs.getString("tenNV"));
                        hd.setTenDichVu(rs.getString("tenDichVu"));
                        list.add(hd);
                    }
                }
            } catch (Exception e) {
                System.err.println("HoaDonDAO.getAllHoaDon DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_HOA_DON);
        }
        return list;
    }

    public boolean themHoaDon(HoaDonKham hd) {
        if (hd == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO HoaDonKham (ngayLap, maBN, maNV, maDichVu, tongTien, giamGia, thanhTien, phuongThucTT, trangThai) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setTimestamp(1, hd.getNgayLap() != null ? hd.getNgayLap() : new Timestamp(System.currentTimeMillis()));
                    stmt.setString(2, hd.getMaBN());
                    stmt.setString(3, hd.getMaNV());
                    stmt.setString(4, hd.getMaDichVu());
                    stmt.setDouble(5, hd.getTongTien());
                    stmt.setDouble(6, hd.getGiamGia());
                    stmt.setDouble(7, hd.getThanhTien());
                    stmt.setString(8, hd.getPhuongThucTT() != null ? hd.getPhuongThucTT() : "Tiền mặt");
                    stmt.setString(9, hd.getTrangThai() != null ? hd.getTrangThai() : "Đã thanh toán");
                    dbOk = stmt.executeUpdate() > 0;
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            hd.setMaHD(rs.getInt(1));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("HoaDonDAO.themHoaDon DB error: " + e.getMessage());
            }
        }
        if (hd.getMaHD() <= 0) {
            hd.setMaHD(MOCK_HOA_DON.size() + 1);
        }
        MOCK_HOA_DON.add(0, hd);
        return dbOk || conn == null;
    }

    public List<HoaDonKham> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHoaDon();
        }
        String cleanKeyword = keyword.trim();
        List<HoaDonKham> all = getAllHoaDon();
        List<HoaDonKham> res = new ArrayList<>();
        String lower = cleanKeyword.toLowerCase();
        for (HoaDonKham hd : all) {
            String maStr = "HD" + String.format("%03d", hd.getMaHD());
            boolean match = maStr.toLowerCase().contains(lower) ||
                            String.valueOf(hd.getMaHD()).contains(cleanKeyword) ||
                            (hd.getTenBN() != null && hd.getTenBN().toLowerCase().contains(lower)) ||
                            (hd.getTenNV() != null && hd.getTenNV().toLowerCase().contains(lower)) ||
                            (hd.getTenDichVu() != null && hd.getTenDichVu().toLowerCase().contains(lower)) ||
                            (hd.getPhuongThucTT() != null && hd.getPhuongThucTT().toLowerCase().contains(lower));
            if (match) {
                res.add(hd);
            }
        }
        return res;
    }

    public boolean huyHoaDon(int maHD) {
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE HoaDonKham SET trangThai = N'Đã hủy' WHERE maHD = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, maHD);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("HoaDonDAO.huyHoaDon DB error: " + e.getMessage());
            }
        }
        for (HoaDonKham hd : MOCK_HOA_DON) {
            if (hd.getMaHD() == maHD) {
                hd.setTrangThai("Đã hủy");
                break;
            }
        }
        return dbOk || conn == null;
    }

    public List<UuDai> getAllUuDai() {
        return new UuDaiDAO().getUuDaiDangApDung();
    }
}
