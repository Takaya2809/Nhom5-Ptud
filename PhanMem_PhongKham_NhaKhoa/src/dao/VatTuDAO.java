package dao;

import connectDB.ConnectDB;
import entity.VatTu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Vật Tư & Dụng Cụ Y Tế
 * Hỗ trợ đồng thời CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class VatTuDAO {

    private static final List<VatTu> MOCK_VAT_TU = new ArrayList<>();

    static {
        MOCK_VAT_TU.add(new VatTu("VT001", "Găng tay y tế Nitrile không bột (Hộp 100 chiếc)", "Tiêu hao", "Hộp", 45, 10, 120000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT002", "Khẩu trang y tế 4 lớp kháng khuẩn (Hộp 50 chiếc)", "Tiêu hao", "Hộp", 60, 15, 65000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT003", "Thuốc tê tiêm Lidocaine 2% Septodont (Hộp 50 ống)", "Tiêu hao", "Hộp", 8, 10, 480000, "Cảnh báo hết hàng"));
        MOCK_VAT_TU.add(new VatTu("VT004", "Vật liệu trám Composite thẩm mỹ 3M Z350 XT", "Tiêu hao", "Tuýp", 5, 8, 850000, "Cảnh báo hết hàng"));
        MOCK_VAT_TU.add(new VatTu("VT005", "Gạc phẫu thuật vô trùng (Gói 100 miếng)", "Tiêu hao", "Gói", 35, 10, 45000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT006", "Bộ mũi khoan kim cương Mani Nhật Bản", "Dụng cụ", "Bộ", 12, 5, 350000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT007", "Kim tiêm nha khoa dùng một lần 27G Short", "Tiêu hao", "Hộp", 22, 10, 95000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT008", "Đầu tip máy cạo vôi siêu âm EMS Woodpecker", "Dụng cụ", "Cái", 15, 6, 250000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT009", "Trụ Implant Straumann SLActive chính hãng", "Tiêu hao", "Bộ", 4, 6, 12500000, "Cảnh báo hết hàng"));
        MOCK_VAT_TU.add(new VatTu("VT010", "Chỉ khâu phẫu thuật tự tiêu Vicryl 4.0 Ethicon", "Tiêu hao", "Hộp", 18, 8, 380000, "Còn hàng"));
        MOCK_VAT_TU.add(new VatTu("VT011", "Dung dịch ngâm khử khuẩn dụng cụ Aniosyme DD1 (Chai 1L)", "Tiêu hao", "Chai", 14, 5, 420000, "Còn hàng"));
    }

    public List<VatTu> getAllVatTu() {
        List<VatTu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai FROM VatTu ORDER BY maVatTu";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        VatTu vt = new VatTu(
                            rs.getString("maVatTu"),
                            rs.getString("tenVatTu"),
                            rs.getString("loaiVatTu"),
                            rs.getString("donViTinh"),
                            rs.getInt("soLuongTon"),
                            rs.getInt("dinhMucToiThieu"),
                            rs.getDouble("donGia"),
                            rs.getString("trangThai")
                        );
                        list.add(vt);
                    }
                }
            } catch (Exception e) {
                System.err.println("VatTuDAO.getAllVatTu DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_VAT_TU);
        }
        return list;
    }

    public List<VatTu> getVatTuCanhBao() {
        List<VatTu> all = getAllVatTu();
        List<VatTu> res = new ArrayList<>();
        for (VatTu vt : all) {
            if (vt.isDuoiDinhMuc() || "Cảnh báo hết hàng".equalsIgnoreCase(vt.getTrangThai()) || "Hết hàng".equalsIgnoreCase(vt.getTrangThai())) {
                res.add(vt);
            }
        }
        return res;
    }

    public boolean themVatTu(VatTu vt) {
        if (vt == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO VatTu (maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, vt.getMaVatTu());
                    stmt.setString(2, vt.getTenVatTu());
                    stmt.setString(3, vt.getLoaiVatTu() != null ? vt.getLoaiVatTu() : "Tiêu hao");
                    stmt.setString(4, vt.getDonViTinh() != null ? vt.getDonViTinh() : "Hộp");
                    stmt.setInt(5, vt.getSoLuongTon());
                    stmt.setInt(6, vt.getDinhMucToiThieu());
                    stmt.setDouble(7, vt.getDonGia());
                    stmt.setString(8, vt.getTrangThai() != null ? vt.getTrangThai() : (vt.isDuoiDinhMuc() ? "Cảnh báo hết hàng" : "Còn hàng"));
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("VatTuDAO.themVatTu DB error: " + e.getMessage());
            }
        }
        boolean exists = false;
        for (int i = 0; i < MOCK_VAT_TU.size(); i++) {
            if (MOCK_VAT_TU.get(i).getMaVatTu().equalsIgnoreCase(vt.getMaVatTu())) {
                MOCK_VAT_TU.set(i, vt);
                exists = true;
                break;
            }
        }
        if (!exists) {
            MOCK_VAT_TU.add(vt);
        }
        return dbOk || conn == null;
    }

    public boolean capNhatVatTu(VatTu vt) {
        if (vt == null || vt.getMaVatTu() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE VatTu SET tenVatTu = ?, loaiVatTu = ?, donViTinh = ?, soLuongTon = ?, " +
                             "dinhMucToiThieu = ?, donGia = ?, trangThai = ? WHERE maVatTu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, vt.getTenVatTu());
                    stmt.setString(2, vt.getLoaiVatTu());
                    stmt.setString(3, vt.getDonViTinh());
                    stmt.setInt(4, vt.getSoLuongTon());
                    stmt.setInt(5, vt.getDinhMucToiThieu());
                    stmt.setDouble(6, vt.getDonGia());
                    stmt.setString(7, vt.getTrangThai());
                    stmt.setString(8, vt.getMaVatTu());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("VatTuDAO.capNhatVatTu DB error: " + e.getMessage());
            }
        }
        for (int i = 0; i < MOCK_VAT_TU.size(); i++) {
            if (MOCK_VAT_TU.get(i).getMaVatTu().equalsIgnoreCase(vt.getMaVatTu())) {
                MOCK_VAT_TU.set(i, vt);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean nhapXuatKho(String maVT, int soLuongThayDoi) {
        if (maVT == null) return false;
        List<VatTu> all = getAllVatTu();
        for (VatTu vt : all) {
            if (vt.getMaVatTu().equalsIgnoreCase(maVT)) {
                int slMoi = Math.max(0, vt.getSoLuongTon() + soLuongThayDoi);
                vt.setSoLuongTon(slMoi);
                vt.setTrangThai(slMoi <= 0 ? "Hết hàng" : (slMoi <= vt.getDinhMucToiThieu() ? "Cảnh báo hết hàng" : "Còn hàng"));
                return capNhatVatTu(vt);
            }
        }
        return false;
    }

    public boolean thietLapDinhMuc(String maVT, int dinhMucMoi) {
        if (maVT == null) return false;
        List<VatTu> all = getAllVatTu();
        for (VatTu vt : all) {
            if (vt.getMaVatTu().equalsIgnoreCase(maVT)) {
                vt.setDinhMucToiThieu(dinhMucMoi);
                return capNhatVatTu(vt);
            }
        }
        return false;
    }

    public List<VatTu> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllVatTu();
        }
        String cleanKeyword = keyword.trim();
        List<VatTu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai FROM VatTu " +
                             "WHERE maVatTu LIKE ? OR tenVatTu LIKE ? OR loaiVatTu LIKE ? OR donViTinh LIKE ? OR trangThai LIKE ? " +
                             "ORDER BY maVatTu";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String param = "%" + cleanKeyword + "%";
                    for (int i = 1; i <= 5; i++) {
                        stmt.setString(i, param);
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new VatTu(
                                rs.getString("maVatTu"),
                                rs.getString("tenVatTu"),
                                rs.getString("loaiVatTu"),
                                rs.getString("donViTinh"),
                                rs.getInt("soLuongTon"),
                                rs.getInt("dinhMucToiThieu"),
                                rs.getDouble("donGia"),
                                rs.getString("trangThai")
                            ));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("VatTuDAO.timKiem DB error: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            String lower = cleanKeyword.toLowerCase();
            for (VatTu vt : MOCK_VAT_TU) {
                boolean match = (vt.getMaVatTu() != null && vt.getMaVatTu().toLowerCase().contains(lower)) ||
                                (vt.getTenVatTu() != null && vt.getTenVatTu().toLowerCase().contains(lower)) ||
                                (vt.getLoaiVatTu() != null && vt.getLoaiVatTu().toLowerCase().contains(lower)) ||
                                (vt.getDonViTinh() != null && vt.getDonViTinh().toLowerCase().contains(lower)) ||
                                (vt.getTrangThai() != null && vt.getTrangThai().toLowerCase().contains(lower));
                if (match) {
                    list.add(vt);
                }
            }
        }
        return list;
    }

    public boolean xoaVatTu(String maVT) {
        if (maVT == null || maVT.trim().isEmpty()) return false;
        maVT = maVT.trim();
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM VatTu WHERE maVatTu = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maVT);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("VatTuDAO.xoaVatTu DB error (foreign key dependency, fallback to marking inactive): " + e.getMessage());
                try {
                    String sqlUp = "UPDATE VatTu SET trangThai = N'Ngừng sử dụng' WHERE maVatTu = ?";
                    try (PreparedStatement stmtUp = conn.prepareStatement(sqlUp)) {
                        stmtUp.setString(1, maVT);
                        dbOk = stmtUp.executeUpdate() > 0;
                    }
                } catch (Exception ignored) {}
            }
        }
        final String fMa = maVT;
        MOCK_VAT_TU.removeIf(v -> v.getMaVatTu().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
    }

    public String sinhMaVTMoi() {
        int maxIndex = 0;
        List<VatTu> all = getAllVatTu();
        for (VatTu vt : all) {
            String ma = vt.getMaVatTu();
            if (ma != null && ma.toUpperCase().startsWith("VT")) {
                try {
                    int num = Integer.parseInt(ma.substring(2).trim());
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (Exception ignored) {}
            }
        }
        return String.format("VT%03d", maxIndex + 1);
    }
}
