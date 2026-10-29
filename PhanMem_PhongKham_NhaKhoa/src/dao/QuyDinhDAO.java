package dao;

import connectDB.ConnectDB;
import entity.QuyDinh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Bảng Quy Định Hệ Thống (QuyDinhHeThong)
 */
public class QuyDinhDAO {

    private static final List<QuyDinh> MOCK_QUY_DINH = new ArrayList<>();

    static {
        MOCK_QUY_DINH.add(new QuyDinh("QD_DIEM_VIP", "Ngưỡng điểm tích lũy nâng hạng Thẻ VIP", "1000", "INT", "Cứ 10.000 VNĐ chi tiêu được cộng 1 điểm", new Timestamp(System.currentTimeMillis()), "QL001"));
        MOCK_QUY_DINH.add(new QuyDinh("QD_MAX_KHAM_CA", "Số lượt khám tối đa mỗi ca cho 1 Bác sĩ", "15", "INT", "Giới hạn số bệnh nhân tiếp nhận để đảm bảo chất lượng", new Timestamp(System.currentTimeMillis()), "QL001"));
        MOCK_QUY_DINH.add(new QuyDinh("QD_MIN_HUY_LICH", "Thời gian báo trước tối thiểu khi hủy/đổi lịch (giờ)", "2", "INT", "Bệnh nhân cần gọi thông báo đổi lịch hẹn trước ít nhất 2 giờ", new Timestamp(System.currentTimeMillis()), "QL001"));
        MOCK_QUY_DINH.add(new QuyDinh("QD_TON_KHO_CANH_BAO", "Tỷ lệ cảnh báo định mức tồn kho tối thiểu (%)", "100", "INT", "Cảnh báo khi số lượng thực tế chạm hoặc dưới ngưỡng tối thiểu", new Timestamp(System.currentTimeMillis()), "QL001"));
    }

    public List<QuyDinh> getAllQuyDinh() {
        List<QuyDinh> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat FROM QuyDinhHeThong ORDER BY maQuyDinh";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        list.add(new QuyDinh(
                            rs.getString("maQuyDinh"),
                            rs.getString("tenQuyDinh"),
                            rs.getString("giaTri"),
                            rs.getString("kieuDuLieu"),
                            rs.getString("moTa"),
                            rs.getTimestamp("ngayCapNhat"),
                            rs.getString("maNVCapNhat")
                        ));
                    }
                }
            } catch (Exception e) {
                System.err.println("QuyDinhDAO.getAllQuyDinh DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_QUY_DINH);
        }
        return list;
    }

    public boolean capNhatGiaTri(String maQuyDinh, String giaTriMoi, String maNV) {
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE QuyDinhHeThong SET giaTri = ?, ngayCapNhat = GETDATE(), maNVCapNhat = ? WHERE maQuyDinh = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, giaTriMoi);
                    stmt.setString(2, maNV);
                    stmt.setString(3, maQuyDinh);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("QuyDinhDAO.capNhatGiaTri DB error: " + e.getMessage());
                return false;
            }
        }
        for (QuyDinh qd : MOCK_QUY_DINH) {
            if (qd.getMaQuyDinh().equalsIgnoreCase(maQuyDinh)) {
                qd.setGiaTri(giaTriMoi);
                qd.setNgayCapNhat(new Timestamp(System.currentTimeMillis()));
                qd.setMaNVCapNhat(maNV);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean themQuyDinh(QuyDinh qd) {
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO QuyDinhHeThong (maQuyDinh, tenQuyDinh, giaTri, kieuDuLieu, moTa, ngayCapNhat, maNVCapNhat) " +
                             "VALUES (?, ?, ?, ?, ?, GETDATE(), ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, qd.getMaQuyDinh());
                    stmt.setString(2, qd.getTenQuyDinh());
                    stmt.setString(3, qd.getGiaTri());
                    stmt.setString(4, qd.getKieuDuLieu() != null ? qd.getKieuDuLieu() : "STRING");
                    stmt.setString(5, qd.getMoTa());
                    stmt.setString(6, qd.getMaNVCapNhat());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("QuyDinhDAO.themQuyDinh DB error: " + e.getMessage());
                return false;
            }
        }
        MOCK_QUY_DINH.add(qd);
        return dbOk || conn == null;
    }
}
