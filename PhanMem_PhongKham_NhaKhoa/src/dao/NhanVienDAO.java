package dao;

import connectDB.ConnectDB;
import entity.NhanVien;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp truy xuất dữ liệu Nhân Viên, Bác Sĩ & Điều Dưỡng
 * Hỗ trợ đồng thời CSDL SQL Server và bộ đệm bộ nhớ (offline/fallback)
 */
public class NhanVienDAO {

    private static final List<NhanVien> MOCK_NHAN_VIEN = new ArrayList<>();

    static {
        MOCK_NHAN_VIEN.add(new NhanVien("QL001", "Trần Thị Hoàng Lan", Date.valueOf("1985-05-15"), "Nữ", "0903123456", "lan.tran@nhakhoaabc.vn", "Quản lý", "ThS. Quản trị Y tế - ĐH Y Dược", Date.valueOf("2020-01-10"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("BS001", "BS.CKI Nguyễn Minh Tuấn", Date.valueOf("1988-08-20"), "Nam", "0912345678", "tuan.nguyen@nhakhoaabc.vn", "Nha sĩ", "ĐH Y Dược TP.HCM", Date.valueOf("2020-03-01"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("BS002", "ThS.BS Lê Thu Hằng", Date.valueOf("1990-11-12"), "Nữ", "0918765432", "hang.le@nhakhoaabc.vn", "Nha sĩ", "ThS Răng Hàm Mặt", Date.valueOf("2021-06-15"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("BS003", "BS Phạm Quốc Đạt", Date.valueOf("1993-02-28"), "Nam", "0922334455", "dat.pham@nhakhoaabc.vn", "Nha sĩ", "Bác sĩ Răng Hàm Mặt", Date.valueOf("2022-01-05"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("NV001", "BS. CKII Nguyễn Văn An", Date.valueOf("1982-04-18"), "Nam", "0913998877", "an.nguyen@nhakhoaabc.vn", "Nha sĩ", "BS. Chuyên khoa II RHM", Date.valueOf("2019-10-01"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("NV002", "ThS. BS Lê Thị Thảo", Date.valueOf("1989-07-25"), "Nữ", "0914887766", "thao.le@nhakhoaabc.vn", "Nha sĩ", "ThS. Bác sĩ Răng Hàm Mặt", Date.valueOf("2020-11-15"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("NV003", "BS. Trần Minh Trí", Date.valueOf("1994-10-05"), "Nam", "0915776655", "tri.tran@nhakhoaabc.vn", "Nha sĩ", "Bác sĩ Răng Hàm Mặt", Date.valueOf("2022-05-20"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("LT001", "Phạm Ngọc Trâm", Date.valueOf("1998-04-25"), "Nữ", "0934567890", "tram.pham@nhakhoaabc.vn", "Lễ tân", "Cử nhân Quan hệ Quốc tế", Date.valueOf("2022-02-01"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("LT002", "Đỗ Thùy Trang", Date.valueOf("2000-08-14"), "Nữ", "0935123456", "trang.do@nhakhoaabc.vn", "Lễ tân", "Cử nhân Ngoại ngữ", Date.valueOf("2023-03-01"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("TN001", "Đỗ Hoài Phương", Date.valueOf("1997-09-18"), "Nữ", "0945678901", "phuong.do@nhakhoaabc.vn", "Thu ngân", "Cử nhân Kế toán Doanh nghiệp", Date.valueOf("2022-03-10"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("YT001", "Nguyễn Thị Kim Oanh", Date.valueOf("1995-12-05"), "Nữ", "0978901234", "oanh.nguyen@nhakhoaabc.vn", "Y tá", "Cử nhân Điều dưỡng Đa khoa", Date.valueOf("2021-08-01"), "Đang làm việc"));
        MOCK_NHAN_VIEN.add(new NhanVien("YT002", "Võ Thị Mỹ Duyên", Date.valueOf("1999-07-20"), "Nữ", "0971239876", "duyen.vo@nhakhoaabc.vn", "Y tá", "Điều dưỡng Nha khoa", Date.valueOf("2023-04-15"), "Đang làm việc"));
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai " +
                             "FROM NhanVien ORDER BY maNV";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        NhanVien nv = new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("hoTen"),
                            rs.getDate("ngaySinh"),
                            rs.getString("gioiTinh"),
                            rs.getString("soDienThoai"),
                            rs.getString("email"),
                            rs.getString("chucDanh"),
                            rs.getString("bangCapChungChi"),
                            rs.getDate("ngayVaoLam"),
                            rs.getString("trangThai")
                        );
                        list.add(nv);
                    }
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.getAllNhanVien DB error: " + e.getMessage());
            }
        }
        if (list.isEmpty()) {
            list.addAll(MOCK_NHAN_VIEN);
        }
        return list;
    }

    public NhanVien getNhanVienByMa(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return null;
        maNV = maNV.trim();
        List<NhanVien> all = getAllNhanVien();
        for (NhanVien nv : all) {
            if (nv.getMaNV().equalsIgnoreCase(maNV)) {
                return nv;
            }
        }
        return null;
    }

    public boolean themNhanVien(NhanVien nv) {
        if (nv == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "INSERT INTO NhanVien (maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nv.getMaNV());
                    stmt.setString(2, nv.getHoTen());
                    stmt.setDate(3, nv.getNgaySinh() != null ? nv.getNgaySinh() : Date.valueOf("1990-01-01"));
                    stmt.setString(4, nv.getGioiTinh() != null ? nv.getGioiTinh() : "Nam");
                    stmt.setString(5, nv.getSoDienThoai());
                    stmt.setString(6, nv.getEmail() != null ? nv.getEmail() : (nv.getMaNV() + "@nhakhoaabc.vn"));
                    stmt.setString(7, nv.getChucDanh() != null ? nv.getChucDanh() : "Bác sĩ Chuyên khoa RHM");
                    stmt.setString(8, nv.getBangCapChungChi() != null ? nv.getBangCapChungChi() : "");
                    stmt.setDate(9, nv.getNgayVaoLam() != null ? nv.getNgayVaoLam() : Date.valueOf("2024-01-01"));
                    stmt.setString(10, nv.getTrangThai() != null ? nv.getTrangThai() : "Đang làm việc");
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.themNhanVien DB error: " + e.getMessage());
            }
        }
        boolean exists = false;
        for (int i = 0; i < MOCK_NHAN_VIEN.size(); i++) {
            if (MOCK_NHAN_VIEN.get(i).getMaNV().equalsIgnoreCase(nv.getMaNV())) {
                MOCK_NHAN_VIEN.set(i, nv);
                exists = true;
                break;
            }
        }
        if (!exists) {
            MOCK_NHAN_VIEN.add(nv);
        }
        return dbOk || conn == null;
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        if (nv == null || nv.getMaNV() == null) return false;
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "UPDATE NhanVien SET hoTen = ?, ngaySinh = ?, gioiTinh = ?, soDienThoai = ?, " +
                             "email = ?, chucDanh = ?, bangCapChungChi = ?, ngayVaoLam = ?, trangThai = ? WHERE maNV = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nv.getHoTen());
                    stmt.setDate(2, nv.getNgaySinh());
                    stmt.setString(3, nv.getGioiTinh());
                    stmt.setString(4, nv.getSoDienThoai());
                    stmt.setString(5, nv.getEmail());
                    stmt.setString(6, nv.getChucDanh());
                    stmt.setString(7, nv.getBangCapChungChi());
                    stmt.setDate(8, nv.getNgayVaoLam());
                    stmt.setString(9, nv.getTrangThai());
                    stmt.setString(10, nv.getMaNV());
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.capNhatNhanVien DB error: " + e.getMessage());
            }
        }
        for (int i = 0; i < MOCK_NHAN_VIEN.size(); i++) {
            if (MOCK_NHAN_VIEN.get(i).getMaNV().equalsIgnoreCase(nv.getMaNV())) {
                MOCK_NHAN_VIEN.set(i, nv);
                break;
            }
        }
        return dbOk || conn == null;
    }

    public boolean chuyenTrangThaiNghiViec(String maNV) {
        if (maNV == null) return false;
        List<NhanVien> all = getAllNhanVien();
        for (NhanVien nv : all) {
            if (nv.getMaNV().equalsIgnoreCase(maNV)) {
                nv.setTrangThai("Đã nghỉ việc");
                return capNhatNhanVien(nv);
            }
        }
        return false;
    }

    public List<NhanVien> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNhanVien();
        }
        String cleanKeyword = keyword.trim();
        List<NhanVien> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT maNV, hoTen, ngaySinh, gioiTinh, soDienThoai, email, chucDanh, bangCapChungChi, ngayVaoLam, trangThai FROM NhanVien " +
                             "WHERE maNV LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? OR email LIKE ? OR chucDanh LIKE ? OR bangCapChungChi LIKE ? OR trangThai LIKE ? " +
                             "ORDER BY maNV";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String param = "%" + cleanKeyword + "%";
                    for (int i = 1; i <= 7; i++) {
                        stmt.setString(i, param);
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new NhanVien(
                                rs.getString("maNV"),
                                rs.getString("hoTen"),
                                rs.getDate("ngaySinh"),
                                rs.getString("gioiTinh"),
                                rs.getString("soDienThoai"),
                                rs.getString("email"),
                                rs.getString("chucDanh"),
                                rs.getString("bangCapChungChi"),
                                rs.getDate("ngayVaoLam"),
                                rs.getString("trangThai")
                            ));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.timKiem DB error: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            String lower = cleanKeyword.toLowerCase();
            for (NhanVien nv : MOCK_NHAN_VIEN) {
                boolean match = (nv.getMaNV() != null && nv.getMaNV().toLowerCase().contains(lower)) ||
                                (nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(lower)) ||
                                (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(cleanKeyword)) ||
                                (nv.getEmail() != null && nv.getEmail().toLowerCase().contains(lower)) ||
                                (nv.getChucDanh() != null && nv.getChucDanh().toLowerCase().contains(lower)) ||
                                (nv.getBangCapChungChi() != null && nv.getBangCapChungChi().toLowerCase().contains(lower)) ||
                                (nv.getTrangThai() != null && nv.getTrangThai().toLowerCase().contains(lower));
                if (match) {
                    list.add(nv);
                }
            }
        }
        return list;
    }

    public boolean xoaNhanVien(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return false;
        maNV = maNV.trim();
        Connection conn = ConnectDB.getConnection();
        boolean dbOk = false;
        if (conn != null) {
            try {
                String sql = "DELETE FROM NhanVien WHERE maNV = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maNV);
                    dbOk = stmt.executeUpdate() > 0;
                }
            } catch (Exception e) {
                System.err.println("NhanVienDAO.xoaNhanVien DB error (foreign key dependency, fallback to marking inactive): " + e.getMessage());
                return chuyenTrangThaiNghiViec(maNV);
            }
        }
        final String fMa = maNV;
        MOCK_NHAN_VIEN.removeIf(n -> n.getMaNV().equalsIgnoreCase(fMa));
        return dbOk || conn == null;
    }

    public String sinhMaNVMoi(String chucDanh) {
        String prefix = "NV";
        if (chucDanh != null) {
            String cdLower = chucDanh.toLowerCase();
            if (cdLower.contains("bác sĩ") || cdLower.contains("nha sĩ")) {
                prefix = "BS";
            } else if (cdLower.contains("lễ tân")) {
                prefix = "LT";
            } else if (cdLower.contains("thu ngân")) {
                prefix = "TN";
            } else if (cdLower.contains("y tá") || cdLower.contains("điều dưỡng") || cdLower.contains("phụ tá")) {
                prefix = "YT";
            } else if (cdLower.contains("quản lý")) {
                prefix = "QL";
            }
        }
        int maxIndex = 0;
        List<NhanVien> all = getAllNhanVien();
        for (NhanVien nv : all) {
            String ma = nv.getMaNV();
            if (ma != null && ma.toUpperCase().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(ma.substring(prefix.length()).trim());
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (Exception ignored) {}
            }
        }
        return String.format("%s%03d", prefix, maxIndex + 1);
    }
}
