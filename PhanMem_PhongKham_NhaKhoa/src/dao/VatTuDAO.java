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
 * Lớp truy xuất dữ liệu Vật Tư & Dụng Cụ Y Tế từ CSDL SQL Server thật
 */
public class VatTuDAO {

    public List<VatTu> getAllVatTu() {
        List<VatTu> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT v.maVatTu, v.tenVatTu, v.loaiVatTu, v.donViTinh, v.soLuongTon, v.dinhMucToiThieu, v.donGia, v.trangThai, "
                    + "v.maNCC, ncc.tenNCC "
                    + "FROM VatTu v "
                    + "LEFT JOIN NCC ncc ON v.maNCC = ncc.maNCC "
                    + "ORDER BY v.maVatTu";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maNCC = rs.getString("maNCC");
                    entity.NCC ncc = maNCC != null ? new entity.NCC(maNCC, rs.getString("tenNCC"))
                            : null;
                    VatTu vt = new VatTu(
                            rs.getString("maVatTu"),
                            rs.getString("tenVatTu"),
                            rs.getString("loaiVatTu"),
                            rs.getString("donViTinh"),
                            rs.getInt("soLuongTon"),
                            rs.getInt("dinhMucToiThieu"),
                            rs.getDouble("donGia"),
                            rs.getString("trangThai"),
                            ncc);
                    list.add(vt);
                }
            }
        } catch (Exception e) {
            // Fallback nếu chưa có cột maNCC
            try {
                String sqlFb = "SELECT maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai FROM VatTu ORDER BY maVatTu";
                try (PreparedStatement stmtFb = conn.prepareStatement(sqlFb);
                        ResultSet rsFb = stmtFb.executeQuery()) {
                    while (rsFb.next()) {
                        VatTu vt = new VatTu(
                                rsFb.getString("maVatTu"),
                                rsFb.getString("tenVatTu"),
                                rsFb.getString("loaiVatTu"),
                                rsFb.getString("donViTinh"),
                                rsFb.getInt("soLuongTon"),
                                rsFb.getInt("dinhMucToiThieu"),
                                rsFb.getDouble("donGia"),
                                rsFb.getString("trangThai"));
                        list.add(vt);
                    }
                }
            } catch (Exception exFb) {
                System.err.println("VatTuDAO.getAllVatTu DB error: " + exFb.getMessage());
            }
        }
        return list;
    }

    public List<VatTu> getVatTuCanhBao() {
        List<VatTu> all = getAllVatTu();
        List<VatTu> res = new ArrayList<>();
        for (VatTu vt : all) {
            if (vt.isDuoiDinhMuc() || "Cảnh báo hết hàng".equalsIgnoreCase(vt.getTrangThai())
                    || "Hết hàng".equalsIgnoreCase(vt.getTrangThai())) {
                res.add(vt);
            }
        }
        return res;
    }

    public boolean themVatTu(VatTu vt) {
        if (vt == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO VatTu (maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, vt.getMaVatTu());
                stmt.setString(2, vt.getTenVatTu());
                stmt.setString(3, vt.getLoaiVatTu() != null ? vt.getLoaiVatTu() : "Tiêu hao");
                stmt.setString(4, vt.getDonViTinh() != null ? vt.getDonViTinh() : "Hộp");
                stmt.setInt(5, vt.getSoLuongTon());
                stmt.setInt(6, vt.getDinhMucToiThieu());
                stmt.setDouble(7, vt.getDonGia());
                stmt.setString(8, vt.getTrangThai() != null ? vt.getTrangThai()
                        : (vt.isDuoiDinhMuc() ? "Cảnh báo hết hàng" : "Còn hàng"));
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("VatTuDAO.themVatTu DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatVatTu(VatTu vt) {
        if (vt == null || vt.getMaVatTu() == null)
            return false;
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

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
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("VatTuDAO.capNhatVatTu DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean nhapXuatKho(String maVT, int soLuongThayDoi) {
        if (maVT == null)
            return false;
        List<VatTu> all = getAllVatTu();
        for (VatTu vt : all) {
            if (vt.getMaVatTu().equalsIgnoreCase(maVT)) {
                int slMoi = Math.max(0, vt.getSoLuongTon() + soLuongThayDoi);
                vt.setSoLuongTon(slMoi);
                vt.setTrangThai(slMoi <= 0 ? "Hết hàng"
                        : (slMoi <= vt.getDinhMucToiThieu() ? "Cảnh báo hết hàng" : "Còn hàng"));
                return capNhatVatTu(vt);
            }
        }
        return false;
    }

    public boolean thietLapDinhMuc(String maVT, int dinhMucMoi) {
        if (maVT == null)
            return false;
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
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maVatTu, tenVatTu, loaiVatTu, donViTinh, soLuongTon, dinhMucToiThieu, donGia, trangThai FROM VatTu "
                    + "WHERE maVatTu LIKE ? OR tenVatTu LIKE ? OR loaiVatTu LIKE ? OR donViTinh LIKE ? OR trangThai LIKE ? "
                    + "ORDER BY maVatTu";
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
                                rs.getString("trangThai")));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("VatTuDAO.timKiem DB error: " + e.getMessage());
        }
        return list;
    }

    public boolean xoaVatTu(String maVT) {
        if (maVT == null || maVT.trim().isEmpty())
            return false;
        maVT = maVT.trim();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "DELETE FROM VatTu WHERE maVatTu = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maVT);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            try {
                String sqlUp = "UPDATE VatTu SET trangThai = N'Ngừng sử dụng' WHERE maVatTu = ?";
                try (PreparedStatement stmtUp = conn.prepareStatement(sqlUp)) {
                    stmtUp.setString(1, maVT);
                    return stmtUp.executeUpdate() > 0;
                }
            } catch (Exception ignored) {
                return false;
            }
        }
    }

    public String sinhMaVTMoi() {
        Connection conn = ConnectDB.getConnection();
        int maxIndex = 0;
        if (conn != null) {
            try {
                String sql = "SELECT maVatTu FROM VatTu";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String ma = rs.getString("maVatTu");
                        if (ma != null && ma.toUpperCase().startsWith("VT")) {
                            try {
                                int num = Integer.parseInt(ma.substring(2).trim());
                                if (num > maxIndex) maxIndex = num;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("VatTuDAO.sinhMaVTMoi error: " + e.getMessage());
            }
        }
        return String.format("VT%03d", maxIndex + 1);
    }
}
