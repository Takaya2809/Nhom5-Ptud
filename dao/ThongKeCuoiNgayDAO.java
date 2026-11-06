package dao;

import connectDB.ConnectDB;
import entity.ThongKeCaTruc;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Quản lý Thống kê & Tổng kết Cuối ngày
 * Lưu trữ bền vững số liệu ca trực, tiêu hao vật tư vào SQL Server và file cục
 * bộ khi offline.
 */
public class ThongKeCuoiNgayDAO {

    private static final String DATA_FILE = "data/thong_ke_cuoi_ngay.txt";
    private static boolean tableChecked = false;

    public ThongKeCuoiNgayDAO() {
        initTableIfNeeded();
    }

    private synchronized void initTableIfNeeded() {
        if (tableChecked)
            return;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            String sql = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ThongKeCuoiNgay' AND xtype='U') " +
                    "CREATE TABLE ThongKeCuoiNgay (" +
                    "    caTruc NVARCHAR(50) PRIMARY KEY," +
                    "    khungGio NVARCHAR(50)," +
                    "    soLuongBN INT," +
                    "    vatTuTieuHao NVARCHAR(500)," +
                    "    trangThai NVARCHAR(50)" +
                    ");";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.execute();
                tableChecked = true;
            } catch (SQLException e) {
                System.err.println("ThongKeCuoiNgayDAO.initTable error: " + e.getMessage());
            }
        }
    }

    public List<ThongKeCaTruc> getAll() {
        List<ThongKeCaTruc> list = new ArrayList<>();
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            initTableIfNeeded();
            String sql = "SELECT caTruc, khungGio, soLuongBN, vatTuTieuHao, trangThai FROM ThongKeCuoiNgay ORDER BY caTruc ASC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ThongKeCaTruc(
                            rs.getString("caTruc"),
                            rs.getString("khungGio"),
                            rs.getInt("soLuongBN"),
                            rs.getString("vatTuTieuHao"),
                            rs.getString("trangThai")));
                }
            } catch (SQLException e) {
                System.err.println("ThongKeCuoiNgayDAO.getAll DB error: " + e.getMessage());
            }
        }

        if (list.isEmpty()) {
            list = loadFromFile();
        }

        if (list.isEmpty()) {
            list = initDefaultShifts();
            saveToFile(list);
            syncToDatabase(list);
        }

        return list;
    }

    public boolean saveOrUpdate(ThongKeCaTruc item) {
        if (item == null || item.getCaTruc() == null)
            return false;
        boolean dbOk = false;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            initTableIfNeeded();
            String sql = "IF EXISTS (SELECT 1 FROM ThongKeCuoiNgay WHERE caTruc = ?) " +
                    "UPDATE ThongKeCuoiNgay SET khungGio = ?, soLuongBN = ?, vatTuTieuHao = ?, trangThai = ? WHERE caTruc = ? "
                    +
                    "ELSE " +
                    "INSERT INTO ThongKeCuoiNgay (caTruc, khungGio, soLuongBN, vatTuTieuHao, trangThai) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, item.getCaTruc());
                ps.setString(2, item.getKhungGio());
                ps.setInt(3, item.getSoLuongBN());
                ps.setString(4, item.getVatTuTieuHao());
                ps.setString(5, item.getTrangThai());
                ps.setString(6, item.getCaTruc());
                ps.setString(7, item.getCaTruc());
                ps.setString(8, item.getKhungGio());
                ps.setInt(9, item.getSoLuongBN());
                ps.setString(10, item.getVatTuTieuHao());
                ps.setString(11, item.getTrangThai());
                dbOk = ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("ThongKeCuoiNgayDAO.saveOrUpdate DB error: " + e.getMessage());
            }
        }

        // Cập nhật file cục bộ
        List<ThongKeCaTruc> list = loadFromFile();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCaTruc().equalsIgnoreCase(item.getCaTruc())) {
                list.set(i, item);
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(item);
        }
        saveToFile(list);

        return dbOk || true;
    }

    public boolean guiTatCaBaoCao() {
        boolean dbOk = false;
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            initTableIfNeeded();
            String sql = "UPDATE ThongKeCuoiNgay SET trangThai = N'Đã gửi quản lý'";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                dbOk = ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("ThongKeCuoiNgayDAO.guiTatCaBaoCao DB error: " + e.getMessage());
            }
        }

        List<ThongKeCaTruc> list = loadFromFile();
        for (ThongKeCaTruc item : list) {
            item.setTrangThai("Đã gửi quản lý");
        }
        saveToFile(list);

        return dbOk || true;
    }

    private List<ThongKeCaTruc> initDefaultShifts() {
        List<ThongKeCaTruc> list = new ArrayList<>();
        list.add(new ThongKeCaTruc("Ca Sáng", "08:00 - 12:00", 12,
                "Găng tay y tế Nitrile (SL: 2), Khẩu trang y tế (SL: 2)", "Đã chỉnh sửa"));
        list.add(new ThongKeCaTruc("Ca Chiều", "13:30 - 17:30", 15,
                "Găng tay y tế Nitrile (SL: 2), Khẩu trang y tế (SL: 2)", "Chưa gửi"));
        list.add(new ThongKeCaTruc("Ca Tối", "18:00 - 21:00", 8,
                "Găng tay y tế Nitrile (SL: 2), Khẩu trang y tế (SL: 2)", "Chưa gửi"));
        return list;
    }

    private synchronized void syncToDatabase(List<ThongKeCaTruc> list) {
        Connection conn = ConnectDB.getConnection();
        if (conn == null || list == null)
            return;
        initTableIfNeeded();
        String sql = "IF NOT EXISTS (SELECT 1 FROM ThongKeCuoiNgay WHERE caTruc = ?) " +
                "INSERT INTO ThongKeCuoiNgay (caTruc, khungGio, soLuongBN, vatTuTieuHao, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ThongKeCaTruc item : list) {
                ps.setString(1, item.getCaTruc());
                ps.setString(2, item.getCaTruc());
                ps.setString(3, item.getKhungGio());
                ps.setInt(4, item.getSoLuongBN());
                ps.setString(5, item.getVatTuTieuHao());
                ps.setString(6, item.getTrangThai());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("ThongKeCuoiNgayDAO.syncToDatabase error: " + e.getMessage());
        }
    }

    private synchronized List<ThongKeCaTruc> loadFromFile() {
        List<ThongKeCaTruc> list = new ArrayList<>();
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
                    try {
                        int bn = Integer.parseInt(parts[2].trim());
                        list.add(new ThongKeCaTruc(parts[0], parts[1], bn, parts[3], parts[4]));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ThongKeCuoiNgayDAO.loadFromFile error: " + e.getMessage());
        }
        return list;
    }

    private synchronized void saveToFile(List<ThongKeCaTruc> list) {
        File file = new File(DATA_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("# CaTruc\tKhungGio\tSoLuongBN\tVatTuTieuHao\tTrangThai\n");
            for (ThongKeCaTruc item : list) {
                writer.write(String.format("%s\t%s\t%d\t%s\t%s\n",
                        item.getCaTruc(),
                        item.getKhungGio(),
                        item.getSoLuongBN(),
                        item.getVatTuTieuHao(),
                        item.getTrangThai()));
            }
        } catch (IOException e) {
            System.err.println("ThongKeCuoiNgayDAO.saveToFile error: " + e.getMessage());
        }
    }
}
