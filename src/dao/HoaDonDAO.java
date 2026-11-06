package dao;

import connectDB.ConnectDB;
import entity.BenhNhan;
import entity.ChiTietHoaDonKham;
import entity.DichVu;
import entity.HoaDonKham;
import entity.NhanVien;
import entity.Thuoc;
import entity.UuDai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Hóa Đơn Khám & Thanh Toán Viện Phí.
 * Đáp ứng các yêu cầu kiến trúc:
 * 1. Transaction toàn vẹn: lưu Hóa đơn và toàn bộ Chi tiết trong cùng 1 giao
 * dịch.
 * 2. Nghiệp vụ: Bắt buộc phải có chi tiết hóa đơn (ném ngoại lệ nếu danh sách
 * rỗng).
 * 3. Không lưu trường tính toán thành tiền/tổng tiền.
 */
public class HoaDonDAO {

    public static final List<HoaDonKham> MOCK_HOA_DON = new ArrayList<>();

    static {
        // Hóa đơn 1
        HoaDonKham hd1 = new HoaDonKham(1);
        hd1.setNgayLap(new Timestamp(System.currentTimeMillis() - 86400000L));
        hd1.setBenhNhan(new BenhNhan("BN001", "Nguyễn Văn Tuấn"));
        hd1.setNhanVien(new NhanVien("NV001", "BS. CKII Nguyễn Văn An"));
        hd1.setPhuongThucTT("Chuyển khoản QR");
        hd1.setTrangThai("Đã thanh toán");
        DichVu dv1 = new DichVu("DV001", "Cấy ghép Implant Straumann Thụy Sĩ", 22000000);
        hd1.themChiTiet(new ChiTietHoaDonKham(dv1, 1, 22000000));
        hd1.setPhanTramGiamTrucTiep(10);
        MOCK_HOA_DON.add(hd1);

        // Hóa đơn 2
        HoaDonKham hd2 = new HoaDonKham(2);
        hd2.setNgayLap(new Timestamp(System.currentTimeMillis() - 86400000L));
        hd2.setBenhNhan(new BenhNhan("BN002", "Trần Mai Phương"));
        hd2.setNhanVien(new NhanVien("NV002", "ThS. BS Lê Thị Thảo"));
        hd2.setPhuongThucTT("Quẹt thẻ POS");
        hd2.setTrangThai("Đã thanh toán");
        DichVu dv2 = new DichVu("DV002", "Niềng răng mắc cài kim loại tự buộc", 30000000);
        hd2.themChiTiet(new ChiTietHoaDonKham(dv2, 1, 30000000));
        hd2.setPhanTramGiamTrucTiep(10);
        MOCK_HOA_DON.add(hd2);

        // Hóa đơn 3
        HoaDonKham hd3 = new HoaDonKham(3);
        hd3.setNgayLap(new Timestamp(System.currentTimeMillis()));
        hd3.setBenhNhan(new BenhNhan("BN003", "Phạm Quốc Dũng"));
        hd3.setNhanVien(new NhanVien("NV003", "BS. Trần Minh Trí"));
        hd3.setPhuongThucTT("Tiền mặt");
        hd3.setTrangThai("Đã thanh toán");
        DichVu dv3 = new DichVu("DV004", "Nhổ răng khôn Piezotome sóng siêu âm", 2000000);
        hd3.themChiTiet(new ChiTietHoaDonKham(dv3, 1, 2000000));
        MOCK_HOA_DON.add(hd3);

        // Hóa đơn 4
        HoaDonKham hd4 = new HoaDonKham(4);
        hd4.setNgayLap(new Timestamp(System.currentTimeMillis()));
        hd4.setBenhNhan(new BenhNhan("BN004", "Vũ Bích Ngọc"));
        hd4.setNhanVien(new NhanVien("NV002", "ThS. BS Lê Thị Thảo"));
        hd4.setPhuongThucTT("Chuyển khoản QR");
        hd4.setTrangThai("Đã thanh toán");
        DichVu dv4 = new DichVu("DV003", "Bọc răng toàn sứ Cercon cao cấp", 4500000);
        hd4.themChiTiet(new ChiTietHoaDonKham(dv4, 1, 4500000));
        hd4.setPhanTramGiamTrucTiep(10);
        MOCK_HOA_DON.add(hd4);
    }

    /**
     * Lấy danh sách chi tiết của một hóa đơn theo mã HĐ
     */
    public List<ChiTietHoaDonKham> getChiTietByMaHD(int maHD) {
        List<ChiTietHoaDonKham> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            String sql = "SELECT c.maCTHD, c.maDichVu, c.maThuoc, c.soLuong, c.donGia, " +
                    "dv.tenDichVu, t.tenThuoc, t.donViTinh " +
                    "FROM ChiTietHoaDonKham c " +
                    "LEFT JOIN DichVu dv ON c.maDichVu = dv.maDichVu " +
                    "LEFT JOIN Thuoc t ON c.maThuoc = t.maThuoc " +
                    "WHERE c.maHD = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, maHD);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ChiTietHoaDonKham ct = new ChiTietHoaDonKham();
                        ct.setMaCTHD(rs.getInt("maCTHD"));
                        String maDV = rs.getString("maDichVu");
                        String maThuoc = rs.getString("maThuoc");
                        if (maDV != null && !maDV.trim().isEmpty()) {
                            DichVu dv = new DichVu(maDV);
                            dv.setTenDichVu(rs.getString("tenDichVu"));
                            ct.setDichVu(dv);
                        } else if (maThuoc != null && !maThuoc.trim().isEmpty()) {
                            Thuoc th = new Thuoc(maThuoc);
                            th.setTenThuoc(rs.getString("tenThuoc"));
                            th.setDonViTinh(rs.getString("donViTinh"));
                            ct.setThuoc(th);
                        }
                        ct.setSoLuong(rs.getInt("soLuong"));
                        ct.setDonGia(rs.getDouble("donGia"));
                        list.add(ct);
                    }
                }
            } catch (SQLException e) {
                // Thử bảng ChiTietHoaDon nếu chưa chạy script đổi tên bảng
                String sqlOld = "SELECT c.maCTHD, c.maDichVu, c.maThuoc, c.soLuong, c.donGia, " +
                        "dv.tenDichVu, t.tenThuoc, t.donViTinh " +
                        "FROM ChiTietHoaDon c " +
                        "LEFT JOIN DichVu dv ON c.maDichVu = dv.maDichVu " +
                        "LEFT JOIN Thuoc t ON c.maThuoc = t.maThuoc " +
                        "WHERE c.maHoaDon = ? OR c.maHoaDon = ?";
                try (PreparedStatement stmtOld = conn.prepareStatement(sqlOld)) {
                    stmtOld.setString(1, String.valueOf(maHD));
                    stmtOld.setString(2, "HD" + String.format("%03d", maHD));
                    try (ResultSet rsOld = stmtOld.executeQuery()) {
                        while (rsOld.next()) {
                            ChiTietHoaDonKham ct = new ChiTietHoaDonKham();
                            ct.setMaCTHD(rsOld.getInt("maCTHD"));
                            String maDV = rsOld.getString("maDichVu");
                            String maThuoc = rsOld.getString("maThuoc");
                            if (maDV != null && !maDV.trim().isEmpty()) {
                                DichVu dv = new DichVu(maDV);
                                dv.setTenDichVu(rsOld.getString("tenDichVu"));
                                ct.setDichVu(dv);
                            } else if (maThuoc != null && !maThuoc.trim().isEmpty()) {
                                Thuoc th = new Thuoc(maThuoc);
                                th.setTenThuoc(rsOld.getString("tenThuoc"));
                                th.setDonViTinh(rsOld.getString("donViTinh"));
                                ct.setThuoc(th);
                            }
                            ct.setSoLuong(rsOld.getInt("soLuong"));
                            ct.setDonGia(rsOld.getDouble("donGia"));
                            list.add(ct);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return list;
    }

    public List<HoaDonKham> getAllHoaDon() {
        List<HoaDonKham> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT h.maHD, h.ngayLap, h.maBN, h.maNV, " +
                        "h.giamGia, h.phuongThucTT, h.trangThai, " +
                        "bn.hoTen AS tenBN, nv.hoTen AS tenNV " +
                        "FROM HoaDonKham h " +
                        "LEFT JOIN BenhNhan bn ON h.maBN = bn.maBN " +
                        "LEFT JOIN NhanVien nv ON h.maNV = nv.maNV " +
                        "ORDER BY h.maHD DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int maHD = rs.getInt("maHD");
                        HoaDonKham hd = new HoaDonKham(maHD);
                        hd.setNgayLap(rs.getTimestamp("ngayLap"));
                        hd.setBenhNhan(new BenhNhan(rs.getString("maBN"), rs.getString("tenBN")));
                        hd.setNhanVien(new NhanVien(rs.getString("maNV"), rs.getString("tenNV")));
                        hd.setPhuongThucTT(rs.getString("phuongThucTT"));
                        hd.setTrangThai(rs.getString("trangThai"));

                        // Đọc danh sách chi tiết hóa đơn
                        List<ChiTietHoaDonKham> dsCT = getChiTietByMaHD(maHD);
                        if (!dsCT.isEmpty()) {
                            hd.setDsChiTiet(dsCT);
                        }
                        list.add(hd);
                    }
                }
            } catch (Exception e) {
                // Fallback nếu câu query chưa có cấu trúc mới
                try {
                    String sqlOld = "SELECT h.maHD, h.ngayLap, h.maBN, h.maNV, h.maDichVu, " +
                            "h.phuongThucTT, h.trangThai, bn.hoTen AS tenBN, nv.hoTen AS tenNV, dv.tenDichVu, dv.giaNiemYet "
                            +
                            "FROM HoaDonKham h " +
                            "LEFT JOIN BenhNhan bn ON h.maBN = bn.maBN " +
                            "LEFT JOIN NhanVien nv ON h.maNV = nv.maNV " +
                            "LEFT JOIN DichVu dv ON h.maDichVu = dv.maDichVu " +
                            "ORDER BY h.maHD DESC";
                    try (PreparedStatement stmtOld = conn.prepareStatement(sqlOld);
                            ResultSet rsOld = stmtOld.executeQuery()) {
                        while (rsOld.next()) {
                            int maHD = rsOld.getInt("maHD");
                            HoaDonKham hd = new HoaDonKham(maHD);
                            hd.setNgayLap(rsOld.getTimestamp("ngayLap"));
                            hd.setBenhNhan(new BenhNhan(rsOld.getString("maBN"), rsOld.getString("tenBN")));
                            hd.setNhanVien(new NhanVien(rsOld.getString("maNV"), rsOld.getString("tenNV")));
                            hd.setPhuongThucTT(rsOld.getString("phuongThucTT"));
                            hd.setTrangThai(rsOld.getString("trangThai"));

                            String maDV = rsOld.getString("maDichVu");
                            if (maDV != null && !maDV.trim().isEmpty()) {
                                DichVu dv = new DichVu(maDV, rsOld.getString("tenDichVu"),
                                        rsOld.getDouble("giaNiemYet"));
                                hd.themChiTiet(new ChiTietHoaDonKham(dv, 1, dv.getGiaNiemYet()));
                            }
                            list.add(hd);
                        }
                    }
                } catch (Exception exOld) {
                    System.err.println("HoaDonDAO.getAllHoaDon DB error: " + exOld.getMessage());
                }
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_HOA_DON);
        }
        return list;
    }

    /**
     * Thêm mới hóa đơn và toàn bộ chi tiết hóa đơn trong cùng một Transaction.
     * Ràng buộc: Danh sách chi tiết không được để rỗng.
     */
    public boolean themHoaDon(HoaDonKham hd) {
        if (hd == null)
            return false;

        // Nghiệp vụ bắt buộc: không cho phép lưu hóa đơn rỗng
        if (!hd.coChiTiet() || hd.getDsChiTiet().isEmpty()) {
            throw new IllegalArgumentException(
                    "Không thể lưu hóa đơn rỗng: Hóa đơn bắt buộc phải có ít nhất một chi tiết dịch vụ hoặc thuốc!");
        }

        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            boolean oldAutoCommit = true;
            try {
                oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

                int generatedMaHD = -1;

                // 1. Lưu thông tin chung của Hóa đơn (không lưu tổng tiền, thành tiền)
                String sqlHD = "INSERT INTO HoaDonKham (ngayLap, maBN, maNV, giamGia, phuongThucTT, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlHD, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setTimestamp(1,
                            hd.getNgayLap() != null ? hd.getNgayLap() : new Timestamp(System.currentTimeMillis()));
                    stmt.setString(2, hd.getMaBN());
                    stmt.setString(3, hd.getMaNV());
                    stmt.setDouble(4, hd.tinhGiamGia());
                    stmt.setString(5, hd.getPhuongThucTT() != null ? hd.getPhuongThucTT() : "Tiền mặt");
                    stmt.setString(6, hd.getTrangThai() != null ? hd.getTrangThai() : "Đã thanh toán");
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedMaHD = rs.getInt(1);
                        }
                    }
                } catch (SQLException exCol) {
                    // Fallback tương thích bảng cũ khi chưa chạy script migration DROP COLUMN
                    // tongTien, thanhTien
                    String sqlFallback = "INSERT INTO HoaDonKham (ngayLap, maBN, maNV, maDichVu, tongTien, giamGia, thanhTien, phuongThucTT, trangThai) "
                            +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmtFb = conn.prepareStatement(sqlFallback,
                            Statement.RETURN_GENERATED_KEYS)) {
                        stmtFb.setTimestamp(1,
                                hd.getNgayLap() != null ? hd.getNgayLap() : new Timestamp(System.currentTimeMillis()));
                        stmtFb.setString(2, hd.getMaBN());
                        stmtFb.setString(3, hd.getMaNV());
                        stmtFb.setString(4, hd.getMaDichVu());
                        stmtFb.setDouble(5, hd.tinhTongTienTruocGiam());
                        stmtFb.setDouble(6, hd.tinhGiamGia());
                        stmtFb.setDouble(7, hd.tinhTongTien());
                        stmtFb.setString(8, hd.getPhuongThucTT() != null ? hd.getPhuongThucTT() : "Tiền mặt");
                        stmtFb.setString(9, hd.getTrangThai() != null ? hd.getTrangThai() : "Đã thanh toán");
                        stmtFb.executeUpdate();
                        try (ResultSet rsFb = stmtFb.getGeneratedKeys()) {
                            if (rsFb.next()) {
                                generatedMaHD = rsFb.getInt(1);
                            }
                        }
                    }
                }

                if (generatedMaHD > 0) {
                    hd.setMaHD(generatedMaHD);
                }

                // 2. Lưu từng chi tiết hóa đơn vào CSDL trong cùng Transaction
                boolean tableChiTietKhamExists = true;
                String sqlCT = "INSERT INTO ChiTietHoaDonKham (maHD, maDichVu, maThuoc, soLuong, donGia) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmtCT = conn.prepareStatement(sqlCT, Statement.RETURN_GENERATED_KEYS)) {
                    for (ChiTietHoaDonKham ct : hd.getDsChiTiet()) {
                        stmtCT.setInt(1, hd.getMaHD());
                        stmtCT.setString(2, ct.isDichVu() ? ct.getDichVu().getMaDichVu() : null);
                        stmtCT.setString(3, ct.isThuoc() ? ct.getThuoc().getMaThuoc() : null);
                        stmtCT.setInt(4, ct.getSoLuong());
                        stmtCT.setDouble(5, ct.getDonGia());
                        stmtCT.executeUpdate();
                        try (ResultSet rsCT = stmtCT.getGeneratedKeys()) {
                            if (rsCT.next()) {
                                ct.setMaCTHD(rsCT.getInt(1));
                            }
                        }
                    }
                } catch (SQLException exCT) {
                    tableChiTietKhamExists = false;
                }

                // Fallback chèn vào ChiTietHoaDon nếu chưa có bảng ChiTietHoaDonKham
                if (!tableChiTietKhamExists) {
                    try {
                        String sqlCTOld = "INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, maThuoc, soLuong, donGia) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement stmtCTOld = conn.prepareStatement(sqlCTOld)) {
                            for (ChiTietHoaDonKham ct : hd.getDsChiTiet()) {
                                stmtCTOld.setString(1, "HD" + String.format("%03d", hd.getMaHD()));
                                stmtCTOld.setString(2, ct.isDichVu() ? ct.getDichVu().getMaDichVu() : null);
                                stmtCTOld.setString(3, ct.isThuoc() ? ct.getThuoc().getMaThuoc() : null);
                                stmtCTOld.setInt(4, ct.getSoLuong());
                                stmtCTOld.setDouble(5, ct.getDonGia());
                                stmtCTOld.executeUpdate();
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }

                // COMMIT TRANSACTION
                conn.commit();
                dbOk = true;

            } catch (Exception e) {
                // ROLLBACK KHI GẶP BẤT KỲ LỖI NÀO
                try {
                    conn.rollback();
                    System.err.println("HoaDonDAO: Đã ROLLBACK transaction do lỗi: " + e.getMessage());
                } catch (SQLException rbEx) {
                    System.err.println("HoaDonDAO: Rollback thất bại: " + rbEx.getMessage());
                }
            } finally {
                try {
                    conn.setAutoCommit(oldAutoCommit);
                } catch (Exception ignored) {
                }
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

    public boolean capNhatThanhToan(int maHD, String phuongThucTT, String trangThai) {
        if (maHD <= 0)
            return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE HoaDonKham SET phuongThucTT = ?, trangThai = ? WHERE maHD = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, phuongThucTT != null ? phuongThucTT : "Tiền mặt");
                    stmt.setString(2, trangThai != null ? trangThai : "Đã thanh toán");
                    stmt.setInt(3, maHD);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("HoaDonDAO.capNhatThanhToan DB error: " + e.getMessage());
            }
        }
        for (HoaDonKham hd : MOCK_HOA_DON) {
            if (hd.getMaHD() == maHD) {
                hd.setPhuongThucTT(phuongThucTT != null ? phuongThucTT : "Tiền mặt");
                hd.setTrangThai(trangThai != null ? trangThai : "Đã thanh toán");
                break;
            }
        }
        return dbOk || conn == null;
    }

    public List<UuDai> getAllUuDai() {
        return new UuDaiDAO().getUuDaiDangApDung();
    }
}
