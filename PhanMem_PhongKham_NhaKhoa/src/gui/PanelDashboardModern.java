package gui;

import dao.ThongKeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.Consumer;

/**
 * Giao diện Dashboard Tổng quan y tế - Phòng Khám Nha Khoa ABC
 * Hiển thị KPI thời gian thực, hàng đợi khám chữa bệnh, ghế nha khoa hoạt động
 */
public class PanelDashboardModern extends JPanel {

    private Consumer<String> navigationCallback;
    private DefaultTableModel modelQueue;
    private JTable tableQueue;
    private JLabel lblDoanhThuVal, lblLuotKhamVal, lblBenhNhanVal, lblCanhBaoVal;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PanelDashboardModern(Consumer<String> navCallback) {
        this.navigationCallback = navCallback;
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER WELCOME & QUICK ACTIONS
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. MAIN SCROLLABLE CONTENT
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setOpaque(false);

        // A. KPI Cards Section
        pnlBody.add(createKPICardsSection());
        pnlBody.add(Box.createVerticalStrut(16));

        // B. Dual Panels Section (Queue Table + Doctor & Service Widget)
        pnlBody.add(createDualColumnsSection());

        JScrollPane scrollPane = new JScrollPane(pnlBody);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Load data initially
        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        // Left: Title & Subtitle
        JPanel pnlTitles = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlTitles.setOpaque(false);

        JLabel lblTitle = new JLabel("TỔNG QUAN ĐIỀU HÀNH PHÒNG KHÁM");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Giám sát chỉ số khám chữa bệnh, doanh thu và điều phối hàng đợi bệnh nhân thời gian thực");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);

        pnlTitles.add(lblTitle);
        pnlTitles.add(lblSub);
        pnlHeader.add(pnlTitles, BorderLayout.WEST);

        // Right: Action Buttons
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        JButton btnNewPatient = ModernUITheme.createButton("Tiếp đón bệnh nhân", ModernUITheme.PRIMARY, Color.WHITE);
        btnNewPatient.addActionListener(e -> {
            if (navigationCallback != null)
                navigationCallback.accept("BENH_NHAN");
        });

        JButton btnNewInvoice = ModernUITheme.createButton("Lập hóa đơn khám", ModernUITheme.ACCENT, Color.WHITE);
        btnNewInvoice.addActionListener(e -> {
            if (navigationCallback != null)
                navigationCallback.accept("THANH_TOAN");
        });

        JButton btnRefresh = ModernUITheme.createOutlineButton("Cập nhật", ModernUITheme.TEXT_MUTED);
        btnRefresh.addActionListener(e -> refreshData());

        pnlActions.add(btnRefresh);
        pnlActions.add(btnNewPatient);
        pnlActions.add(btnNewInvoice);

        pnlHeader.add(pnlActions, BorderLayout.EAST);
        return pnlHeader;
    }

    private JPanel createKPICardsSection() {
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlKPI.setOpaque(false);
        pnlKPI.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        pnlKPI.setPreferredSize(new Dimension(1000, 115));

        JPanel card1 = ModernUITheme.createKPICard("LƯỢT KHÁM HÔM NAY", "48 ca", "+14% so với hôm qua",
                ModernUITheme.PRIMARY, "");
        JPanel card2 = ModernUITheme.createKPICard("DOANH THU TRONG NGÀY", "38.650.000 đ", "+8.2% mục tiêu ngày",
                ModernUITheme.SUCCESS, "");
        JPanel card3 = ModernUITheme.createKPICard("HỒ SƠ BỆNH NHÂN", "16 người", "12 ca đặt hẹn trước",
                ModernUITheme.ACCENT, "");
        JPanel card4 = ModernUITheme.createKPICard("CẢNH BÁO KHO VẬT TƯ", "3 mặt hàng", "Cần bổ sung thuốc/vật tư",
                ModernUITheme.DANGER, "");

        pnlKPI.add(card1);
        pnlKPI.add(card2);
        pnlKPI.add(card3);
        pnlKPI.add(card4);

        return pnlKPI;
    }

    private JPanel createDualColumnsSection() {
        JPanel pnlDual = new JPanel(new BorderLayout(16, 0));
        pnlDual.setOpaque(false);

        // LEFT COLUMN: BẢNG ĐIỀU PHỐI HÀNG ĐỢI BỆNH NHÂN (65% width)
        JPanel pnlLeftCard = ModernUITheme.createCardPanel();
        pnlLeftCard.setLayout(new BorderLayout(0, 12));

        // Header bảng
        JPanel pnlQueueHeader = new JPanel(new BorderLayout());
        pnlQueueHeader.setOpaque(false);

        JLabel lblQueueTitle = new JLabel("HÀNG ĐỢI ĐIỀU TRỊ TRONG NGÀY");
        lblQueueTitle.setFont(ModernUITheme.FONT_TITLE);
        lblQueueTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlFilters.setOpaque(false);

        JButton btnAll = ModernUITheme.createButton("Tất cả (8)", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnWaiting = ModernUITheme.createOutlineButton("Chờ khám (3)", ModernUITheme.WARNING);
        JButton btnInChair = ModernUITheme.createOutlineButton("Đang điều trị (2)", ModernUITheme.PRIMARY);
        JButton btnDone = ModernUITheme.createOutlineButton("Đã hoàn tất (3)", ModernUITheme.SUCCESS);

        pnlFilters.add(btnAll);
        pnlFilters.add(btnWaiting);
        pnlFilters.add(btnInChair);
        pnlFilters.add(btnDone);

        pnlQueueHeader.add(lblQueueTitle, BorderLayout.WEST);
        pnlQueueHeader.add(pnlFilters, BorderLayout.EAST);
        pnlLeftCard.add(pnlQueueHeader, BorderLayout.NORTH);

        // Table
        String[] cols = { "Mã BN", "Họ và Tên", "Bác sĩ phụ trách", "Dịch vụ chỉ định", "Giờ hẹn", "Ghế/Phòng",
                "Trạng thái" };
        modelQueue = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Dữ liệu mẫu thực tế nha khoa
        modelQueue.addRow(new Object[] { "BN001", "Nguyễn Văn Tuấn", "BS. CKII Nguyễn Văn An",
                "Trồng răng Implant Straumann", "08:30", "Ghế 01", "Đang điều trị" });
        modelQueue.addRow(new Object[] { "BN002", "Trần Mai Phương", "BS. Lê Thị Thảo", "Niềng răng mắc cài tự buộc",
                "09:00", "Ghế 02", "Đang điều trị" });
        modelQueue.addRow(new Object[] { "BN003", "Phạm Quốc Dũng", "ThS. BS Trần Minh Trí",
                "Nhổ răng khôn Piezotome #38", "09:30", "Phòng Tiểu Phẫu", "Chờ khám" });
        modelQueue.addRow(new Object[] { "BN004", "Vũ Bích Ngọc", "BS. Lê Thị Thảo", "Bọc răng toàn sứ Cercon", "10:00",
                "Phòng Chờ 1", "Chờ khám" });
        modelQueue.addRow(new Object[] { "BN005", "Đặng Hoàng Nam", "BS. CKII Nguyễn Văn An",
                "Tẩy trắng răng Laser Whitening", "10:15", "Phòng Chờ 2", "Chờ khám" });
        modelQueue.addRow(new Object[] { "BN006", "Lê Thanh Hương", "ThS. BS Trần Minh Trí",
                "Trám răng thẩm mỹ Composite", "08:00", "Ghế 03", "Hoàn tất" });
        modelQueue.addRow(new Object[] { "BN007", "Hoàng Kim Liên", "BS. Lê Thị Thảo",
                "Lấy cao răng siêu âm & Đánh bóng", "08:15", "Ghế 02", "Hoàn tất" });
        modelQueue.addRow(new Object[] { "BN008", "Ngô Quốc Huy", "BS. CKII Nguyễn Văn An",
                "Tái khám sau cấy ghép Implant", "08:20", "Ghế 01", "Hoàn tất" });

        tableQueue = new JTable(modelQueue);
        ModernUITheme.formatModernTable(tableQueue);

        JScrollPane scrollTable = new JScrollPane(tableQueue);
        scrollTable.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        scrollTable.setPreferredSize(new Dimension(650, 320));
        pnlLeftCard.add(scrollTable, BorderLayout.CENTER);

        // Queue Control Bar (Bottom of Left Card)
        JPanel pnlQueueBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlQueueBottom.setOpaque(false);
        JButton btnMoiVao = ModernUITheme.createButton("Mời vào khám", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHoanTat = ModernUITheme.createButton("Xác nhận hoàn tất", ModernUITheme.SUCCESS, Color.WHITE);
        JButton btnChiTiet = ModernUITheme.createOutlineButton("Xem bệnh án chi tiết", ModernUITheme.TEXT_MUTED);

        btnMoiVao.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                modelQueue.setValueAt("Đang điều trị", row, 6);
                JOptionPane.showMessageDialog(this,
                        "Đã gọi bệnh nhân " + modelQueue.getValueAt(row, 1) + " vào ghế khám!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong hàng đợi!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnHoanTat.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                modelQueue.setValueAt("Hoàn tất", row, 6);
                JOptionPane.showMessageDialog(this, "Ca điều trị đã hoàn tất. Chuyển thông tin sang bộ phận Thu ngân!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân cần hoàn tất!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlQueueBottom.add(btnChiTiet);
        pnlQueueBottom.add(btnMoiVao);
        pnlQueueBottom.add(btnHoanTat);
        pnlLeftCard.add(pnlQueueBottom, BorderLayout.SOUTH);

        pnlDual.add(pnlLeftCard, BorderLayout.CENTER);

        // RIGHT COLUMN: 2 TIỆN ÍCH (Bác sĩ trực & Top dịch vụ) (35% width)
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(new Dimension(360, 400));

        // Widget 1: Bác sĩ trực hôm nay
        JPanel cardDoctors = ModernUITheme.createCardPanel();
        cardDoctors.setLayout(new BorderLayout(0, 10));

        JLabel lblDocTitle = new JLabel("BÁC SĨ TRỰC HÔM NAY");
        lblDocTitle.setFont(ModernUITheme.FONT_TITLE);
        lblDocTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardDoctors.add(lblDocTitle, BorderLayout.NORTH);

        JPanel pnlDocList = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlDocList.setOpaque(false);

        pnlDocList.add(createDoctorItem("BS. CKII Nguyễn Văn An", "Trưởng khoa Implant & Chỉnh nha",
                "Ghế 01 (Đang bận)", ModernUITheme.DANGER));
        pnlDocList.add(createDoctorItem("ThS. BS Lê Thị Thảo", "Chuyên khoa Răng Trẻ Em & Thẩm Mỹ", "Ghế 02 (Đang bận)",
                ModernUITheme.DANGER));
        pnlDocList.add(createDoctorItem("BS. Trần Minh Trí", "Chuyên khoa Phẫu Thuật Răng Hàm Mặt",
                "Ghế 03 (Đang rảnh)", ModernUITheme.SUCCESS));

        cardDoctors.add(pnlDocList, BorderLayout.CENTER);
        pnlRight.add(cardDoctors);
        pnlRight.add(Box.createVerticalStrut(16));

        // Widget 2: Dịch vụ nổi bật
        JPanel cardServices = ModernUITheme.createCardPanel();
        cardServices.setLayout(new BorderLayout(0, 10));

        JLabel lblServTitle = new JLabel("TOP DỊCH VỤ CHỈ ĐỊNH CAO");
        lblServTitle.setFont(ModernUITheme.FONT_TITLE);
        lblServTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardServices.add(lblServTitle, BorderLayout.NORTH);

        JPanel pnlServList = new JPanel(new GridLayout(4, 1, 0, 8));
        pnlServList.setOpaque(false);

        pnlServList.add(createServiceProgressItem("Cấy ghép Implant", "15.000.000 đ", 85, ModernUITheme.PRIMARY));
        pnlServList.add(createServiceProgressItem("Niềng răng mắc cài", "25.000.000 đ", 70, ModernUITheme.ACCENT));
        pnlServList.add(createServiceProgressItem("Răng sứ Cercon HT", "4.500.000 đ", 92, ModernUITheme.SUCCESS));
        pnlServList.add(createServiceProgressItem("Nhổ răng khôn Piezotome", "2.000.000 đ", 60, ModernUITheme.WARNING));

        cardServices.add(pnlServList, BorderLayout.CENTER);
        pnlRight.add(cardServices);

        pnlDual.add(pnlRight, BorderLayout.EAST);

        return pnlDual;
    }

    private JPanel createDoctorItem(String name, String spec, String status, Color statusColor) {
        JPanel pnl = new JPanel(new BorderLayout(8, 2));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlInfo.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(ModernUITheme.FONT_BOLD);
        lblName.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSpec = new JLabel(spec);
        lblSpec.setFont(ModernUITheme.FONT_TINY);
        lblSpec.setForeground(ModernUITheme.TEXT_MUTED);

        pnlInfo.add(lblName);
        pnlInfo.add(lblSpec);

        JLabel lblBadge = ModernUITheme.createBadge(status,
                new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30), statusColor);

        pnl.add(pnlInfo, BorderLayout.CENTER);
        pnl.add(lblBadge, BorderLayout.EAST);
        return pnl;
    }

    private JPanel createServiceProgressItem(String title, String price, int percent, Color barColor) {
        JPanel pnl = new JPanel(new BorderLayout(5, 4));
        pnl.setOpaque(false);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ModernUITheme.FONT_BOLD);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(ModernUITheme.FONT_TINY);
        lblPrice.setForeground(ModernUITheme.TEXT_MUTED);

        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(lblPrice, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(percent);
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(100, 6));

        pnl.add(pnlTop, BorderLayout.NORTH);
        pnl.add(bar, BorderLayout.SOUTH);
        return pnl;
    }

    public void refreshData() {
        // TODO: Cập nhật số liệu KPI từ CSDL tại đây
    }
}
