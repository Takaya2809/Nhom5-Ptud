package dao;

import connectDB.ConnectDB;
import entity.QuyDinh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Bảng Quy Định Hệ Thống (QuyDinhHeThong) từ CSDL SQL
 * Server thật
 */
public class QuyDinhDAO {

    public List<QuyDinh> getAllQuyDinh() {
        List<QuyDinh> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return list;
        }

        try {
            String sql = "SELECT maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat FROM QuyDinhHeThong ORDER BY maQuyDinh";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new QuyDinh(
                            rs.getString("maQuyDinh"),
                            rs.getString("tenQuyDinh"),
                            rs.getString("giaTri"),
                            rs.getString("kieuDuLieu"),
                            rs.getString("moTa"),
                            rs.getTimestamp("ngayCapNhat"),
                            rs.getString("maNVCapNhat")));
                }
            }
        } catch (Exception e) {
            System.err.println("QuyDinhDAO.getAllQuyDinh DB error: " + e.getMessage());
        }
        return list;
    }

    public boolean capNhatGiaTri(String maQuyDinh, String giaTriMoi, String maNV) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "UPDATE QuyDinhHeThong SET giaTri = ?, ngayCapNhat = GETDATE(), maNVCapNhat = ? WHERE maQuyDinh = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, giaTriMoi);
                stmt.setString(2, maNV);
                stmt.setString(3, maQuyDinh);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("QuyDinhDAO.capNhatGiaTri DB error: " + e.getMessage());
            return false;
        }
    }

    public boolean themQuyDinh(QuyDinh qd) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            String sql = "INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat) "
                    +
                    "VALUES (?, ?, ?, ?, ?, GETDATE(), ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, qd.getMaQuyDinh());
                stmt.setString(2, qd.getTenQuyDinh());
                stmt.setString(3, qd.getGiaTri());
                stmt.setString(4, qd.getKieuDuLieu() != null ? qd.getKieuDuLieu() : "STRING");
                stmt.setString(5, qd.getMoTa());
                stmt.setString(6, qd.getMaNVCapNhat());
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("QuyDinhDAO.themQuyDinh DB error: " + e.getMessage());
            return false;
        }
    }
}
