package gui;

import dao.ThongKeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Giao diện Báo Cáo & Thống Kê Y Khoa - Thiết kế trực quan hiện đại
 */
public class PanelThongKeModern extends JPanel {

    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private JPanel pnlKPIContainer;
    private JTable tableDichVu, tableBacSi;
    private DefaultTableModel modelDichVu, modelBacSi;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PanelThongKeModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("BÁO CÁO THỐNG KÊ & HIỆU SUẤT ĐIỀU HÀNH");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Phân tích tổng doanh thu, số ca điều trị theo dịch vụ và năng suất khám của đội ngũ Bác sĩ");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        JButton btnRefresh = ModernUITheme.createButton("🔄 Cập nhật số liệu", ModernUITheme.PRIMARY, Color.WHITE);
        btnRefresh.addActionListener(e -> taiDuLieu());
        pnlHeader.add(btnRefresh, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. CENTER
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 16));
        pnlCenter.setOpaque(false);

        // A. KPI CARDS
        pnlKPIContainer = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlKPIContainer.setOpaque(false);
        pnlKPIContainer.setPreferredSize(new Dimension(1000, 115));
        pnlCenter.add(pnlKPIContainer, BorderLayout.NORTH);

        // B. DUAL TABLES
        JSplitPane splitTables = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitTables.setResizeWeight(0.5);
        splitTables.setDividerSize(10);
        splitTables.setOpaque(false);

        // Table 1: Dịch vụ
        JPanel cardDV = ModernUITheme.createCardPanel();
        cardDV.setLayout(new BorderLayout(0, 10));
        JLabel lblDVTitle = new JLabel("🦷 DOANH THU THEO DANH MỤC DỊCH VỤ");
        lblDVTitle.setFont(ModernUITheme.FONT_TITLE);
        lblDVTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        cardDV.add(lblDVTitle, BorderLayout.NORTH);

        String[] colsDV = { "Tên Dịch Vụ Nha Khoa", "Doanh Thu Đạt Được" };
        modelDichVu = new DefaultTableModel(colsDV, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableDichVu = new JTable(modelDichVu);
        ModernUITheme.formatModernTable(tableDichVu);
        JScrollPane spDV = new JScrollPane(tableDichVu);
        spDV.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        cardDV.add(spDV, BorderLayout.CENTER);
        splitTables.setLeftComponent(cardDV);

        // Table 2: Bác sĩ
        JPanel cardBS = ModernUITheme.createCardPanel();
        cardBS.setLayout(new BorderLayout(0, 10));
        JLabel lblBSTitle = new JLabel("👨‍⚕️ NĂNG SUẤT ĐIỀU TRỊ THEO BÁC SĨ");
        lblBSTitle.setFont(ModernUITheme.FONT_TITLE);
        lblBSTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        cardBS.add(lblBSTitle, BorderLayout.NORTH);

        String[] colsBS = { "Bác Sĩ Điều Trị", "Số Lượt Khám Đã Thực Hiện" };
        modelBacSi = new DefaultTableModel(colsBS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableBacSi = new JTable(modelBacSi);
        ModernUITheme.formatModernTable(tableBacSi);
        JScrollPane spBS = new JScrollPane(tableBacSi);
        spBS.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        cardBS.add(spBS, BorderLayout.CENTER);
        splitTables.setRightComponent(cardBS);

        pnlCenter.add(splitTables, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        taiDuLieu();
    }

    private void taiDuLieu() {
        pnlKPIContainer.removeAll();
        modelDichVu.setRowCount(0);
        modelBacSi.setRowCount(0);

        double tongDT = thongKeDAO.getTongDoanhThu();
        int tongLK = thongKeDAO.getTongLuotKham();
        int tongBN = thongKeDAO.getTongBenhNhan();
        int canhBaoVT = thongKeDAO.getSoVatTuCanhBao();

        Map<String, Double> mapDV = thongKeDAO.getDoanhThuTheoDichVu();
        for (Map.Entry<String, Double> entry : mapDV.entrySet()) {
            modelDichVu.addRow(new Object[] { entry.getKey(), df.format(entry.getValue()) });
        }

        Map<String, Integer> mapBS = thongKeDAO.getHieuSuatBacSi();
        for (Map.Entry<String, Integer> entry : mapBS.entrySet()) {
            modelBacSi.addRow(new Object[] { entry.getKey(), entry.getValue() + " lượt khám" });
        }

        pnlKPIContainer.add(ModernUITheme.createKPICard("TỔNG DOANH THU", df.format(tongDT), "Doanh thu thực tế đã thu",
                ModernUITheme.SUCCESS, "💰"));
        pnlKPIContainer.add(ModernUITheme.createKPICard("TỔNG LƯỢT KHÁM", tongLK + " ca", "Tổng hồ sơ đã điều trị",
                ModernUITheme.PRIMARY, "🩺"));
        pnlKPIContainer.add(ModernUITheme.createKPICard("HỒ SƠ BỆNH NHÂN", tongBN + " người", "Bệnh nhân đã đăng ký",
                ModernUITheme.ACCENT, "👥"));
        pnlKPIContainer.add(ModernUITheme.createKPICard("CẢNH BÁO KHO", canhBaoVT + " mặt hàng",
                "Dưới định mức tối thiểu", ModernUITheme.DANGER, "⚠️"));

        pnlKPIContainer.revalidate();
        pnlKPIContainer.repaint();
    }
}
