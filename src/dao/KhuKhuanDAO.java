package dao;

import connectDB.ConnectDB;
import entity.DungCuKhuKhuan;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Quản lý Dữ liệu Theo dõi Khử khuẩn Dụng cụ Y tế
 * Hỗ trợ lưu trữ bền vững vào CSDL SQL Server và bộ lưu trữ file cục bộ khi
 * offline.
 */
public class KhuKhuanDAO {

    private static final String DATA_FILE = "data/theo_doi_khu_khuan.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static boolean tableChecked = false;

    public KhuKhuanDAO() {
        initTableIfNeeded();
    }

    private synchronized void initTableIfNeeded() {
        if (tableChecked)
            return;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            String sql = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='TheoDoiKhuKhuan' AND xtype='U') " +
                    "CREATE TABLE TheoDoiKhuKhuan (" +
                    "    maDungCu VARCHAR(20) PRIMARY KEY," +
                    "    tenDungCu NVARCHAR(100) NOT NULL," +
                    "    loaiDungCu NVARCHAR(50)," +
                    "    trangThai NVARCHAR(50)," +
                    "    thoiGianCapNhat NVARCHAR(50)" +
                    ");";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.execute();
                tableChecked = true;
            } catch (SQLException e) {
                System.err.println("KhuKhuanDAO.initTable error: " + e.getMessage());
            }
        }
    }

    public List<DungCuKhuKhuan> getAll() {
        List<DungCuKhuKhuan> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            initTableIfNeeded();
            String sql = "SELECT maDungCu, tenDungCu, loaiDungCu, trangThai, thoiGianCapNhat FROM TheoDoiKhuKhuan ORDER BY maDungCu ASC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DungCuKhuKhuan(
                            rs.getString("maDungCu"),
                            rs.getString("tenDungCu"),
                            rs.getString("loaiDungCu"),
                            rs.getString("trangThai"),
                            rs.getString("thoiGianCapNhat")));
                }
            } catch (SQLException e) {
                System.err.println("KhuKhuanDAO.getAll DB error: " + e.getMessage());
            }
        }

        // Nếu DB chưa có dữ liệu hoặc offline, nạp từ file cục bộ
        if (list.isEmpty()) {
            list = loadFromFile();
        }

        // Nếu cả file và DB đều chưa có dữ liệu, khởi tạo dữ liệu mẫu và lưu bền vững
        if (list.isEmpty()) {
            list = initDefaultData();
            saveToFile(list);
            syncToDatabase(list);
        }

        return list;
    }

    public boolean updateTrangThai(String maDungCu, String trangThaiMoi, String thoiGian) {
        if (maDungCu == null || trangThaiMoi == null)
            return false;
        if (thoiGian == null || thoiGian.trim().isEmpty()) {
            thoiGian = LocalDateTime.now().format(FORMATTER);
        }

        boolean dbOk = false;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            initTableIfNeeded();
            String sql = "UPDATE TheoDoiKhuKhuan SET trangThai = ?, thoiGianCapNhat = ? WHERE maDungCu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, trangThaiMoi);
                ps.setString(2, thoiGian);
                ps.setString(3, maDungCu);
                dbOk = ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("KhuKhuanDAO.updateTrangThai DB error: " + e.getMessage());
            }
        }

        // Cập nhật và lưu bền vững vào file cục bộ
        List<DungCuKhuKhuan> list = loadFromFile();
        boolean found = false;
        for (DungCuKhuKhuan dc : list) {
            if (dc.getMaDungCu().equalsIgnoreCase(maDungCu)) {
                dc.setTrangThai(trangThaiMoi);
                dc.setThoiGianCapNhat(thoiGian);
                found = true;
                break;
            }
        }
        if (found) {
            saveToFile(list);
        }

        return dbOk || found;
    }

    public boolean themDungCu(DungCuKhuKhuan dc) {
        if (dc == null || dc.getMaDungCu() == null)
            return false;
        boolean dbOk = false;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            initTableIfNeeded();
            String sql = "INSERT INTO TheoDoiKhuKhuan (maDungCu, tenDungCu, loaiDungCu, trangThai, thoiGianCapNhat) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dc.getMaDungCu());
                ps.setString(2, dc.getTenDungCu());
                ps.setString(3, dc.getLoaiDungCu());
                ps.setString(4, dc.getTrangThai());
                ps.setString(5, dc.getThoiGianCapNhat());
                dbOk = ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("KhuKhuanDAO.themDungCu DB error: " + e.getMessage());
            }
        }

        List<DungCuKhuKhuan> list = loadFromFile();
        list.add(dc);
        saveToFile(list);
        return dbOk || true;
    }

    private List<DungCuKhuKhuan> initDefaultData() {
        List<DungCuKhuKhuan> list = new ArrayList<>();
        list.add(new DungCuKhuKhuan("DC001", "Bộ kìm nhổ răng Inox", "Dụng cụ phẫu thuật", "Đã tiệt trùng (Sẵn sàng)",
                "01/11/2026 08:30"));
        list.add(new DungCuKhuKhuan("DC002", "Gương nha khoa & Thám trâm", "Dụng cụ khám", "Đang khử khuẩn (Autoclave)",
                "01/11/2026 11:00"));
        list.add(new DungCuKhuKhuan("DC003", "Tay khoan turbine tốc độ cao", "Thiết bị cầm tay", "Chưa khử khuẩn",
                "Chưa xử lý"));
        list.add(new DungCuKhuKhuan("DC004", "Bộ đầu lấy cao răng siêu âm", "Dụng cụ nha chu",
                "Đã tiệt trùng (Sẵn sàng)", "01/11/2026 09:15"));
        list.add(new DungCuKhuKhuan("DC005", "Khay lấy dấu răng Inox", "Dụng cụ phục hình",
                "Đang khử khuẩn (Autoclave)", "01/11/2026 10:45"));
        return list;
    }

    private synchronized void syncToDatabase(List<DungCuKhuKhuan> list) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || list == null)
            return;
        initTableIfNeeded();
        String sql = "IF NOT EXISTS (SELECT 1 FROM TheoDoiKhuKhuan WHERE maDungCu = ?) " +
                "INSERT INTO TheoDoiKhuKhuan (maDungCu, tenDungCu, loaiDungCu, trangThai, thoiGianCapNhat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DungCuKhuKhuan dc : list) {
                ps.setString(1, dc.getMaDungCu());
                ps.setString(2, dc.getMaDungCu());
                ps.setString(3, dc.getTenDungCu());
                ps.setString(4, dc.getLoaiDungCu());
                ps.setString(5, dc.getTrangThai());
                ps.setString(6, dc.getThoiGianCapNhat());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("KhuKhuanDAO.syncToDatabase error: " + e.getMessage());
        }
    }

    private synchronized List<DungCuKhuKhuan> loadFromFile() {
        List<DungCuKhuKhuan> list = new ArrayList<>();
        File file = new File(DATA_FILE);
        if (!file.exists())
            return list;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#"))
                    continue;
                String[] parts = line.split("\t");
                if (parts.length >= 5) {
                    list.add(new DungCuKhuKhuan(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            System.err.println("KhuKhuanDAO.loadFromFile error: " + e.getMessage());
        }
        return list;
    }

    private synchronized void saveToFile(List<DungCuKhuKhuan> list) {
        File file = new File(DATA_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("# MaDC\tTenDC\tLoaiDC\tTrangThai\tThoiGianCapNhat\n");
            for (DungCuKhuKhuan dc : list) {
                writer.write(String.format("%s\t%s\t%s\t%s\t%s\n",
                        dc.getMaDungCu(),
                        dc.getTenDungCu(),
                        dc.getLoaiDungCu(),
                        dc.getTrangThai(),
                        dc.getThoiGianCapNhat()));
            }
        } catch (IOException e) {
            System.err.println("KhuKhuanDAO.saveToFile error: " + e.getMessage());
        }
    }
}
