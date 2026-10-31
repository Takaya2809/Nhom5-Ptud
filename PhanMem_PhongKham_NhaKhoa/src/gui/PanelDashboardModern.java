package gui;

import dao.CaTrucDAO;
import dao.HoSoBenhAnDAO;
import dao.LichHenDAO;
import dao.ThongKeDAO;
import entity.HoSoBenhAn;
import entity.LichHen;
import entity.PhanCongCaTruc;
import entity.TrangThaiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Giao diện Dashboard Tổng quan y tế - Phòng Khám Nha Khoa ABC
 * Tích hợp KPI thời gian thực (Hôm nay/Tháng này/Tổng), so sánh tăng/giảm %,
 * Biểu đồ doanh thu 7 ngày Graphics2D có Tooltip, Khối cảnh báo thông minh,
 * Hàng đợi khám hôm nay theo giờ và tự động làm mới bằng SwingWorker mỗi 60 giây.
 */
public class PanelDashboardModern extends JPanel {

    private Consumer<String> navigationCallback;
    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();

    private JPanel pnlKPIContainer;
    private DefaultTableModel modelQueue;
    private JTable tableQueue;
    private List<LichHen> allAppointmentsToday = new ArrayList<>();
    private String currentFilter = "ALL";

    private JButton btnAll, btnWaiting, btnInChair, btnDone;
    private JPanel pnlDocList, pnlServList, pnlAlertSection;
    private ChartDoanhThuPanel chartPanel;
    private JLabel lblLoadingStatus;

    private Timer autoRefreshTimer;
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
        pnlKPIContainer = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlKPIContainer.setOpaque(false);
        pnlKPIContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        pnlKPIContainer.setPreferredSize(new Dimension(1000, 125));
        pnlBody.add(pnlKPIContainer);
        pnlBody.add(Box.createVerticalStrut(16));

        // B. Cảnh Báo Nhanh & Biểu Đồ Doanh Thu 7 Ngày (Split Dual Cards)
        pnlBody.add(createMidSection());
        pnlBody.add(Box.createVerticalStrut(16));

        // C. Bảng Hàng Đợi & Widget Bác sĩ / Dịch vụ
        pnlBody.add(createDualColumnsSection());

        JScrollPane scrollPane = new JScrollPane(pnlBody);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Khởi động Timer tự làm mới mỗi 60 giây
        setupAutoRefreshTimer();

        // Tải dữ liệu ban đầu qua SwingWorker
        refreshData();
    }

    private void setupAutoRefreshTimer() {
        // Tự động làm mới mỗi 60 giây (60000ms)
        autoRefreshTimer = new Timer(60000, e -> refreshData());

        // Lắng nghe sự kiện ẩn/hiện của Panel để dừng/chạy Timer, tránh tốn tài nguyên
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (autoRefreshTimer != null && !autoRefreshTimer.isRunning()) {
                    autoRefreshTimer.start();
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
                    autoRefreshTimer.stop();
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private JPanel createHeaderPanel() {
        JPanel pnlHero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                RoundRectangle2D roundedRect = new RoundRectangle2D.Double(0, 0, w, h, 14, 14);
                g2.setClip(roundedRect);

                GradientPaint grad = new GradientPaint(
                        0, 0, ModernUITheme.SIDEBAR_BG,
                        w, h, ModernUITheme.PRIMARY_DARK);
                g2.setPaint(grad);
                g2.fillRect(0, 0, w, h);

                g2.setClip(null);
                g2.setColor(ModernUITheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 14, 14));
                g2.dispose();
            }
        };

        pnlHero.setOpaque(false);
        pnlHero.setLayout(new BorderLayout(15, 0));
        pnlHero.setBorder(new EmptyBorder(18, 24, 18, 24));
        pnlHero.setPreferredSize(new Dimension(0, 96));

        JPanel pnlTitles = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlTitles.setOpaque(false);

        JLabel lblTitle = new JLabel("TRUNG TÂM ĐIỀU HÀNH PHÒNG KHÁM NHA KHOA ABC");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(Color.WHITE);

        JPanel pnlSub = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSub.setOpaque(false);
        JLabel lblSubText = new JLabel("Giám sát chỉ số y tế thời gian thực, cảnh báo tồn kho và điều phối hàng đợi");
        lblSubText.setFont(ModernUITheme.FONT_SMALL);
        lblSubText.setForeground(new Color(224, 242, 254));

        lblLoadingStatus = new JLabel("");
        lblLoadingStatus.setFont(ModernUITheme.FONT_TINY);
        lblLoadingStatus.setForeground(new Color(254, 240, 138));

        pnlSub.add(lblSubText);
        pnlSub.add(lblLoadingStatus);

        pnlTitles.add(lblTitle);
        pnlTitles.add(pnlSub);
        pnlHero.add(pnlTitles, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        JButton btnNewPatient = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PATIENT, 15, Color.WHITE), "Tiếp đón bệnh nhân", ModernUITheme.PRIMARY, Color.WHITE);
        btnNewPatient.addActionListener(e -> {
            if (navigationCallback != null) navigationCallback.accept("BENH_NHAN");
        });

        JButton btnNewInvoice = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PAYMENT, 15, Color.WHITE), "Lập hóa đơn khám", ModernUITheme.ACCENT, Color.WHITE);
        btnNewInvoice.addActionListener(e -> {
            if (navigationCallback != null) navigationCallback.accept("THANH_TOAN");
        });

        JButton btnRefresh = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, Color.WHITE), "Cập nhật dữ liệu", Color.WHITE);
        btnRefresh.addActionListener(e -> refreshData());

        pnlActions.add(btnRefresh);
        pnlActions.add(btnNewPatient);
        pnlActions.add(btnNewInvoice);

        pnlHero.add(pnlActions, BorderLayout.EAST);
        return pnlHero;
    }

    // =========================================================================
    // SECTION: KHỐI CẢNH BÁO + BIỂU ĐỒ 7 NGÀY
    // =========================================================================

    private JPanel createMidSection() {
        JPanel pnlMid = new JPanel(new BorderLayout(16, 0));
        pnlMid.setOpaque(false);
        pnlMid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        pnlMid.setPreferredSize(new Dimension(1000, 260));

        // LEFT: Biểu đồ doanh thu 7 ngày (68% width)
        JPanel cardChart = ModernUITheme.createCardPanel();
        cardChart.setLayout(new BorderLayout(0, 8));

        JPanel pnlChartHeader = new JPanel(new BorderLayout());
        pnlChartHeader.setOpaque(false);
        JLabel lblChartTitle = new JLabel("BIỂU ĐỒ DOANH THU 7 NGÀY GẦN NHẤT");
        lblChartTitle.setFont(ModernUITheme.FONT_TITLE);
        lblChartTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblChartHint = new JLabel("Rê chuột vào cột để xem chi tiết");
        lblChartHint.setFont(ModernUITheme.FONT_TINY);
        lblChartHint.setForeground(ModernUITheme.TEXT_MUTED);

        pnlChartHeader.add(lblChartTitle, BorderLayout.WEST);
        pnlChartHeader.add(lblChartHint, BorderLayout.EAST);
        cardChart.add(pnlChartHeader, BorderLayout.NORTH);

        chartPanel = new ChartDoanhThuPanel();
        cardChart.add(chartPanel, BorderLayout.CENTER);
        pnlMid.add(cardChart, BorderLayout.CENTER);

        // RIGHT: Khối cảnh báo y tế & kho (32% width)
        JPanel cardAlert = ModernUITheme.createCardPanel();
        cardAlert.setLayout(new BorderLayout(0, 10));
        cardAlert.setPreferredSize(new Dimension(360, 0));

        JPanel pnlAlertHeader = new JPanel(new BorderLayout());
        pnlAlertHeader.setOpaque(false);
        JLabel lblAlertTitle = new JLabel("CẢNH BÁO HỆ THỐNG");
        lblAlertTitle.setFont(ModernUITheme.FONT_TITLE);
        lblAlertTitle.setForeground(ModernUITheme.DANGER);
        JLabel lblAlertBadge = ModernUITheme.createBadge("Tự động kiểm tra", new Color(254, 226, 226), ModernUITheme.DANGER);
        pnlAlertHeader.add(lblAlertTitle, BorderLayout.WEST);
        pnlAlertHeader.add(lblAlertBadge, BorderLayout.EAST);
        cardAlert.add(pnlAlertHeader, BorderLayout.NORTH);

        pnlAlertSection = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlAlertSection.setOpaque(false);
        cardAlert.add(pnlAlertSection, BorderLayout.CENTER);

        pnlMid.add(cardAlert, BorderLayout.EAST);
        return pnlMid;
    }

    // =========================================================================
    // SECTION: HÀNG ĐỢI KHÁM HÔM NAY + BÁC SĨ TRỰC + DỊCH VỤ TRỌNG ĐIỂM
    // =========================================================================

    private JPanel createDualColumnsSection() {
        JPanel pnlDual = new JPanel(new BorderLayout(16, 0));
        pnlDual.setOpaque(false);

        // LEFT COLUMN: BẢNG ĐIỀU PHỐI HÀNG ĐỢI HÔM NAY
        JPanel pnlLeftCard = ModernUITheme.createCardPanel();
        pnlLeftCard.setLayout(new BorderLayout(0, 12));

        // Header bảng
        JPanel pnlQueueHeader = new JPanel(new BorderLayout());
        pnlQueueHeader.setOpaque(false);

        JLabel lblQueueTitle = new JLabel("HÀNG ĐỢI KHÁM & ĐIỀU TRỊ HÔM NAY");
        lblQueueTitle.setFont(ModernUITheme.FONT_TITLE);
        lblQueueTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlFilters.setOpaque(false);

        btnAll = ModernUITheme.createButton("Tất cả", ModernUITheme.PRIMARY, Color.WHITE);
        btnWaiting = ModernUITheme.createOutlineButton("Chờ khám", ModernUITheme.WARNING);
        btnInChair = ModernUITheme.createOutlineButton("Đang khám", ModernUITheme.PRIMARY);
        btnDone = ModernUITheme.createOutlineButton("Đã hoàn tất", ModernUITheme.SUCCESS);

        btnAll.addActionListener(e -> { currentFilter = "ALL"; renderQueueTable(); });
        btnWaiting.addActionListener(e -> { currentFilter = "WAITING"; renderQueueTable(); });
        btnInChair.addActionListener(e -> { currentFilter = "IN_CHAIR"; renderQueueTable(); });
        btnDone.addActionListener(e -> { currentFilter = "DONE"; renderQueueTable(); });

        pnlFilters.add(btnAll);
        pnlFilters.add(btnWaiting);
        pnlFilters.add(btnInChair);
        pnlFilters.add(btnDone);

        pnlQueueHeader.add(lblQueueTitle, BorderLayout.WEST);
        pnlQueueHeader.add(pnlFilters, BorderLayout.EAST);
        pnlLeftCard.add(pnlQueueHeader, BorderLayout.NORTH);

        // Table
        String[] cols = { "Mã Hẹn", "Mã BN", "Họ và Tên", "Bác Sĩ Phụ Trách", "Nội Dung Khám", "Giờ Hẹn", "Trạng Thái" };
        modelQueue = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tableQueue = new JTable(modelQueue);
        ModernUITheme.formatModernTable(tableQueue);

        // Đổi màu cột trạng thái
        tableQueue.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if (isSelected) return lbl;

                if (TrangThaiConstants.LH_CHO_KHAM.equals(val) || TrangThaiConstants.LH_DA_XAC_NHAN.equals(val)) {
                    lbl.setForeground(new Color(217, 119, 6)); // Cam cảnh báo
                    lbl.setFont(ModernUITheme.FONT_BOLD);
                } else if (TrangThaiConstants.LH_DANG_KHAM.equals(val)) {
                    lbl.setForeground(ModernUITheme.PRIMARY);
                    lbl.setFont(ModernUITheme.FONT_BOLD);
                } else if (TrangThaiConstants.LH_HOAN_TAT.equals(val)) {
                    lbl.setForeground(ModernUITheme.SUCCESS);
                    lbl.setFont(ModernUITheme.FONT_BOLD);
                } else {
                    lbl.setForeground(ModernUITheme.TEXT_MUTED);
                }
                return lbl;
            }
        });

        // Double-click mở chi tiết bệnh án
        tableQueue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableQueue.getSelectedRow() != -1) {
                    int r = tableQueue.getSelectedRow();
                    String maBN = modelQueue.getValueAt(r, 1).toString();
                    String tenBN = modelQueue.getValueAt(r, 2).toString();
                    hienThiBenhAnChiTiet(maBN, tenBN);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tableQueue);
        scrollTable.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        scrollTable.setPreferredSize(new Dimension(650, 320));
        pnlLeftCard.add(scrollTable, BorderLayout.CENTER);

        // Queue Control Bar (Bottom of Left Card)
        JPanel pnlQueueBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlQueueBottom.setOpaque(false);
        JButton btnMoiVao = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Mời vào khám", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHoanTat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Xác nhận hoàn tất", ModernUITheme.SUCCESS, Color.WHITE);
        JButton btnChiTiet = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.HISTORY, 14, ModernUITheme.TEXT_MUTED), "Xem bệnh án chi tiết", ModernUITheme.TEXT_MUTED);

        btnMoiVao.addActionListener(e -> xuLyMoiVaoKham());
        btnHoanTat.addActionListener(e -> xuLyHoanTatKham());
        btnChiTiet.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                String maBN = modelQueue.getValueAt(row, 1).toString();
                String tenBN = modelQueue.getValueAt(row, 2).toString();
                hienThiBenhAnChiTiet(maBN, tenBN);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlQueueBottom.add(btnChiTiet);
        pnlQueueBottom.add(btnMoiVao);
        pnlQueueBottom.add(btnHoanTat);
        pnlLeftCard.add(pnlQueueBottom, BorderLayout.SOUTH);

        pnlDual.add(pnlLeftCard, BorderLayout.CENTER);

        // RIGHT COLUMN: BÁC SĨ TRỰC & DỊCH VỤ TRỌNG ĐIỂM (35% width)
        JPanel pnlRightColumn = new JPanel();
        pnlRightColumn.setLayout(new BoxLayout(pnlRightColumn, BoxLayout.Y_AXIS));
        pnlRightColumn.setOpaque(false);
        pnlRightColumn.setPreferredSize(new Dimension(360, 0));

        // Widget 1: Bác sĩ trực hôm nay
        JPanel cardDoctors = ModernUITheme.createCardPanel();
        cardDoctors.setLayout(new BorderLayout(0, 10));

        JPanel pnlDocHeader = new JPanel(new BorderLayout());
        pnlDocHeader.setOpaque(false);
        JLabel lblDocTitle = new JLabel("BÁC SĨ TRỰC HÔM NAY");
        lblDocTitle.setFont(ModernUITheme.FONT_TITLE);
        lblDocTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblDocBadge = ModernUITheme.createBadge("Theo giờ hệ thống", ModernUITheme.PRIMARY_LIGHT, ModernUITheme.PRIMARY);
        pnlDocHeader.add(lblDocTitle, BorderLayout.WEST);
        pnlDocHeader.add(lblDocBadge, BorderLayout.EAST);
        cardDoctors.add(pnlDocHeader, BorderLayout.NORTH);

        pnlDocList = new JPanel();
        pnlDocList.setLayout(new BoxLayout(pnlDocList, BoxLayout.Y_AXIS));
        pnlDocList.setOpaque(false);
        cardDoctors.add(pnlDocList, BorderLayout.CENTER);

        pnlRightColumn.add(cardDoctors);
        pnlRightColumn.add(Box.createVerticalStrut(16));

        // Widget 2: Dịch vụ điều trị phổ biến
        JPanel cardServices = ModernUITheme.createCardPanel();
        cardServices.setLayout(new BorderLayout(0, 10));

        JPanel pnlServHeader = new JPanel(new BorderLayout());
        pnlServHeader.setOpaque(false);
        JLabel lblServTitle = new JLabel("TOP DỊCH VỤ TRỌNG ĐIỂM (30 NGÀY)");
        lblServTitle.setFont(ModernUITheme.FONT_TITLE);
        lblServTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardServices.add(lblServTitle, BorderLayout.NORTH);

        pnlServList = new JPanel(new GridLayout(4, 1, 0, 8));
        pnlServList.setOpaque(false);
        cardServices.add(pnlServList, BorderLayout.CENTER);

        pnlRightColumn.add(cardServices);
        pnlDual.add(pnlRightColumn, BorderLayout.EAST);
        return pnlDual;
    }

    // =========================================================================
    // XỬ LÝ LUỒNG TRẠNG THÁI HÀNG ĐỢI
    // =========================================================================

    private void xuLyMoiVaoKham() {
        int row = tableQueue.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách hàng đợi!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = modelQueue.getValueAt(row, 6).toString();
        // Kiểm tra luồng trạng thái chặt chẽ bằng hằng số
        if (!TrangThaiConstants.LH_CHO_KHAM.equals(currentStatus) &&
            !TrangThaiConstants.LH_DA_XAC_NHAN.equals(currentStatus) &&
            !TrangThaiConstants.LH_DA_LEN_LICH.equals(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể mời vào khám!\nBệnh nhân đang ở trạng thái [" + currentStatus + "].\nChỉ bệnh nhân ở trạng thái 'Chờ khám' hoặc 'Đã xác nhận' mới được mời vào ghế.",
                    "Sai luồng trạng thái", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maLH = modelQueue.getValueAt(row, 0).toString();
        String tenBN = modelQueue.getValueAt(row, 2).toString();

        boolean ok = lichHenDAO.capNhatTrangThai(maLH, TrangThaiConstants.LH_DANG_KHAM);
        if (ok) {
            thongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "GỌI KHÁM", "Mời bệnh nhân [" + tenBN + " - " + maLH + "] vào ghế điều trị");
            JOptionPane.showMessageDialog(this,
                    "Đã mời bệnh nhân [" + tenBN + "] vào ghế khám (Cập nhật thành công)!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại. Vui lòng kiểm tra lại kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHoanTatKham() {
        int row = tableQueue.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách hàng đợi!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = modelQueue.getValueAt(row, 6).toString();
        // Kiểm tra luồng trạng thái: Chỉ khi đang khám mới được hoàn tất
        if (!TrangThaiConstants.LH_DANG_KHAM.equals(currentStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể hoàn tất ca điều trị!\nBệnh nhân đang ở trạng thái [" + currentStatus + "].\nChỉ ca đang ở trạng thái 'Đang khám' mới có thể xác nhận hoàn tất.",
                    "Sai luồng trạng thái", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maLH = modelQueue.getValueAt(row, 0).toString();
        String tenBN = modelQueue.getValueAt(row, 2).toString();

        boolean ok = lichHenDAO.capNhatTrangThai(maLH, TrangThaiConstants.LH_HOAN_TAT);
        if (ok) {
            thongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "HOÀN TẤT KHÁM", "Xác nhận hoàn tất ca điều trị cho [" + tenBN + " - " + maLH + "]");
            JOptionPane.showMessageDialog(this,
                    "Bệnh nhân [" + tenBN + "] đã hoàn thành điều trị thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại. Vui lòng kiểm tra lại kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderQueueTable() {
        modelQueue.setRowCount(0);
        int cntWaiting = 0, cntInChair = 0, cntDone = 0;

        for (LichHen lh : allAppointmentsToday) {
            String tt = lh.getTrangThai() != null ? lh.getTrangThai() : TrangThaiConstants.LH_CHO_KHAM;

            if (TrangThaiConstants.LH_CHO_KHAM.equals(tt) || TrangThaiConstants.LH_DA_XAC_NHAN.equals(tt) || TrangThaiConstants.LH_DA_LEN_LICH.equals(tt)) {
                cntWaiting++;
            } else if (TrangThaiConstants.LH_DANG_KHAM.equals(tt)) {
                cntInChair++;
            } else if (TrangThaiConstants.LH_HOAN_TAT.equals(tt)) {
                cntDone++;
            }

            boolean show = false;
            if ("ALL".equals(currentFilter)) {
                show = true;
            } else if ("WAITING".equals(currentFilter)) {
                show = TrangThaiConstants.LH_CHO_KHAM.equals(tt) || TrangThaiConstants.LH_DA_XAC_NHAN.equals(tt) || TrangThaiConstants.LH_DA_LEN_LICH.equals(tt);
            } else if ("IN_CHAIR".equals(currentFilter)) {
                show = TrangThaiConstants.LH_DANG_KHAM.equals(tt);
            } else if ("DONE".equals(currentFilter)) {
                show = TrangThaiConstants.LH_HOAN_TAT.equals(tt);
            }

            if (show) {
                String gio = lh.getGioHen() != null ? lh.getGioHen().toString().substring(0, 5) : "--:--";
                modelQueue.addRow(new Object[] {
                        lh.getMaLichHen(),
                        lh.getMaBN(),
                        lh.getTenBenhNhan() != null ? lh.getTenBenhNhan() : lh.getMaBN(),
                        lh.getTenNhaSi() != null ? lh.getTenNhaSi() : "BS phụ trách",
                        lh.getLyDoKham() != null ? lh.getLyDoKham() : "Khám nha khoa",
                        gio,
                        tt
                });
            }
        }

        btnAll.setText("Tất cả (" + allAppointmentsToday.size() + ")");
        btnWaiting.setText("Chờ khám (" + cntWaiting + ")");
        btnInChair.setText("Đang khám (" + cntInChair + ")");
        btnDone.setText("Đã hoàn tất (" + cntDone + ")");
    }

    private void hienThiBenhAnChiTiet(String maBN, String tenBN) {
        List<HoSoBenhAn> dsHoSo = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                  HỒ SƠ BỆNH ÁN ĐIỆN TỬ NHA KHOA                        \n");
        sb.append("========================================================================\n");
        sb.append("Mã bệnh nhân: ").append(maBN).append(" | Họ và tên: ").append(tenBN).append("\n");
        sb.append("Tổng số ca điều trị trong CSDL: ").append(dsHoSo.size()).append("\n");
        sb.append("------------------------------------------------------------------------\n\n");

        if (dsHoSo.isEmpty()) {
            sb.append("Chưa có hồ sơ bệnh án nào được ghi nhận cho bệnh nhân này.\n");
            sb.append("(Bác sĩ có thể tạo bệnh án mới từ màn hình 'Hồ sơ bệnh nhân')\n");
        } else {
            for (int i = 0; i < dsHoSo.size(); i++) {
                HoSoBenhAn hs = dsHoSo.get(i);
                sb.append("[").append(i + 1).append("] Mã bệnh án: ").append(hs.getMaHoSo()).append("\n");
                sb.append("    - Ngày khám: ").append(hs.getNgayKham() != null ? hs.getNgayKham().toString() : "Hôm nay").append("\n");
                sb.append("    - Bác sĩ điều trị: ").append(hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi()).append("\n");
                sb.append("    - Triệu chứng lâm sàng: ").append(hs.getTrieuChung()).append("\n");
                sb.append("    - Chẩn đoán y khoa: ").append(hs.getChanDoan()).append("\n");
                sb.append("    - Kết quả điều trị: ").append(hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "Tốt").append("\n");
                sb.append("    - Lời dặn / Ghi chú: ").append(hs.getGhiChu() != null ? hs.getGhiChu() : "Tái khám theo hẹn").append("\n");
                sb.append("    - Trạng thái hồ sơ: ").append(hs.getTrangThai()).append("\n");
                sb.append("------------------------------------------------------------------------\n");
            }
        }

        JTextArea ta = new JTextArea(sb.toString(), 18, 55);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setCaretPosition(0);

        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Bệnh án điện tử: " + tenBN + " (" + maBN + ")", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel createDoctorItem(String name, String caInfo, String status, Color statusColor) {
        JPanel pnl = new JPanel(new BorderLayout(8, 2));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlInfo.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(ModernUITheme.FONT_BOLD);
        lblName.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSpec = new JLabel(caInfo);
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

    private JPanel createAlertItem(String title, String desc, int count, Color color, Runnable onClick) {
        JPanel pnl = new JPanel(new BorderLayout(10, 0));
        pnl.setOpaque(true);
        pnl.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 12));
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        pnl.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlText.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(ModernUITheme.FONT_BOLD);
        lblT.setForeground(color);

        JLabel lblD = new JLabel(desc);
        lblD.setFont(ModernUITheme.FONT_TINY);
        lblD.setForeground(ModernUITheme.TEXT_MAIN);

        pnlText.add(lblT);
        pnlText.add(lblD);

        JLabel lblCount = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCount.setForeground(Color.WHITE);
        lblCount.setOpaque(true);
        lblCount.setBackground(color);
        lblCount.setPreferredSize(new Dimension(28, 28));

        pnl.add(pnlText, BorderLayout.CENTER);
        pnl.add(lblCount, BorderLayout.EAST);

        pnl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });
        return pnl;
    }

    // =========================================================================
    // TẢI DỮ LIỆU ĐA LUỒNG BẰNG SWINGWORKER
    // =========================================================================

    public void refreshData() {
        if (lblLoadingStatus != null) {
            lblLoadingStatus.setText("(Đang cập nhật CSDL...)");
        }

        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                DashboardData data = new DashboardData();
                data.tongDT = thongKeDAO.getTongDoanhThu();
                data.dtHomNay = thongKeDAO.getDoanhThuHomNay();
                data.dtHomQua = thongKeDAO.getDoanhThuHomQua();
                data.dtThangNay = thongKeDAO.getDoanhThuThangNay();
                data.dtThangTruoc = thongKeDAO.getDoanhThuThangTruoc();

                data.tongLK = thongKeDAO.getTongLuotKham();
                data.lkHomNay = thongKeDAO.getLuotKhamHomNay();
                data.lkHomQua = thongKeDAO.getLuotKhamHomQua();
                data.lkThangNay = thongKeDAO.getLuotKhamThangNay();

                data.tongBN = thongKeDAO.getTongBenhNhan();
                data.canhBao = thongKeDAO.getCanhBaoDashboard();

                data.dsLichHenHomNay = lichHenDAO.getLichHenHomNay();
                data.dsPhanCongHomNay = caTrucDAO.getPhanCongHomNay();
                data.dsTopDichVu = thongKeDAO.getTopDichVu30Ngay(4);
                data.dsDoanhThu7Ngay = thongKeDAO.getDoanhThu7NgayGanNhat();
                return data;
            }

            @Override
            protected void done() {
                try {
                    DashboardData res = get();
                    capNhatGiaoDien(res);
                } catch (Exception ex) {
                    System.err.println("Lỗi nạp dữ liệu Dashboard: " + ex.getMessage());
                } finally {
                    if (lblLoadingStatus != null) {
                        lblLoadingStatus.setText("");
                    }
                }
            }
        };
        worker.execute();
    }

    private void capNhatGiaoDien(DashboardData res) {
        // 1. CẬP NHẬT KPI CARDS VỚI TÍNH % TĂNG/GIẢM
        pnlKPIContainer.removeAll();

        // A. Lượt khám hôm nay (So với hôm qua)
        double diffLK = res.lkHomNay - res.lkHomQua;
        double pctLK = res.lkHomQua > 0 ? (diffLK / res.lkHomQua) * 100 : 0;
        String subLK = (pctLK >= 0 ? "+" : "") + String.format("%.0f%%", pctLK) + " so với hôm qua | Tháng này: " + res.lkThangNay + " ca";
        pnlKPIContainer.add(ModernUITheme.createKPICard("LƯỢT KHÁM HÔM NAY", res.lkHomNay + " ca", subLK,
                ModernUITheme.PRIMARY, new AppIcon(AppIcon.Type.SERVICE, 24, ModernUITheme.PRIMARY)));

        // B. Doanh thu hôm nay (So với hôm qua)
        double diffDT = res.dtHomNay - res.dtHomQua;
        double pctDT = res.dtHomQua > 0 ? (diffDT / res.dtHomQua) * 100 : 0;
        String subDT = (pctDT >= 0 ? "+" : "") + String.format("%.0f%%", pctDT) + " so với hôm qua | Tháng: " + df.format(res.dtThangNay);
        pnlKPIContainer.add(ModernUITheme.createKPICard("DOANH THU HÔM NAY", df.format(res.dtHomNay), subDT,
                ModernUITheme.SUCCESS, new AppIcon(AppIcon.Type.PAYMENT, 24, ModernUITheme.SUCCESS)));

        // C. Tổng số bệnh nhân
        pnlKPIContainer.add(ModernUITheme.createKPICard("HỒ SƠ BỆNH NHÂN", res.tongBN + " người", "Tổng ca điều trị: " + res.tongLK,
                ModernUITheme.ACCENT, new AppIcon(AppIcon.Type.PATIENT, 24, ModernUITheme.ACCENT)));

        // D. Tổng cảnh báo
        int tongCanhBao = res.canhBao != null ? res.canhBao.getTongSoCanhBao() : 0;
        pnlKPIContainer.add(ModernUITheme.createKPICard("CẢNH BÁO Y TẾ & KHO", tongCanhBao + " mục", "Cần xử lý kịp thời",
                ModernUITheme.DANGER, new AppIcon(AppIcon.Type.WARNING, 24, ModernUITheme.DANGER)));

        pnlKPIContainer.revalidate();
        pnlKPIContainer.repaint();

        // 2. CẬP NHẬT BIỂU ĐỒ 7 NGÀY
        if (chartPanel != null) {
            chartPanel.setData(res.dsDoanhThu7Ngay);
        }

        // 3. CẬP NHẬT KHỐI CẢNH BÁO
        pnlAlertSection.removeAll();
        if (res.canhBao != null) {
            pnlAlertSection.add(createAlertItem(
                    "Vật tư / Thuốc dưới định mức",
                    "Số lượng tồn kho chạm hoặc thấp hơn mức an toàn",
                    res.canhBao.vatTuThap,
                    ModernUITheme.DANGER,
                    () -> { if (navigationCallback != null) navigationCallback.accept("KHO_VAT_TU"); }
            ));

            pnlAlertSection.add(createAlertItem(
                    "Thuốc sắp hết hạn (30 ngày)",
                    "Dược phẩm cận hạn dùng cần luân chuyển hoặc tiêu hủy",
                    res.canhBao.thuocSapHetHan,
                    ModernUITheme.WARNING,
                    () -> { if (navigationCallback != null) navigationCallback.accept("KHO_VAT_TU"); }
            ));

            pnlAlertSection.add(createAlertItem(
                    "Lịch hẹn quá giờ chưa đến",
                    "Bệnh nhân chưa có mặt theo khung giờ hẹn hôm nay",
                    res.canhBao.lichHenTreGio,
                    ModernUITheme.PRIMARY,
                    () -> { if (navigationCallback != null) navigationCallback.accept("LICH_TRUC"); }
            ));
        }
        pnlAlertSection.revalidate();
        pnlAlertSection.repaint();

        // 4. CẬP NHẬT HÀNG ĐỢI KHÁM HÔM NAY (ĐÃ LỌC & SORT THEO GIỜ)
        allAppointmentsToday = res.dsLichHenHomNay != null ? res.dsLichHenHomNay : new ArrayList<>();
        renderQueueTable();

        // 5. CẬP NHẬT WIDGET BÁC SĨ TRỰC HÔM NAY
        pnlDocList.removeAll();
        if (res.dsPhanCongHomNay == null || res.dsPhanCongHomNay.isEmpty()) {
            JLabel lblEmptyDoc = new JLabel("Hôm nay chưa có ca trực nào được phân công", SwingConstants.CENTER);
            lblEmptyDoc.setFont(ModernUITheme.FONT_SMALL);
            lblEmptyDoc.setForeground(ModernUITheme.TEXT_MUTED);
            lblEmptyDoc.setBorder(new EmptyBorder(25, 10, 25, 10));
            pnlDocList.add(lblEmptyDoc);
        } else {
            int shown = 0;
            for (PhanCongCaTruc pc : res.dsPhanCongHomNay) {
                String tt = pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : TrangThaiConstants.DD_CHUA_DEN;
                Color statusColor = ModernUITheme.TEXT_MUTED;
                if (TrangThaiConstants.DD_DANG_TRUC.equals(tt) || TrangThaiConstants.DD_DA_DEN_TRUC.equals(tt)) {
                    statusColor = ModernUITheme.SUCCESS;
                } else if (TrangThaiConstants.DD_CHUA_DEN.equals(tt)) {
                    statusColor = ModernUITheme.WARNING;
                } else if (TrangThaiConstants.DD_VANG_MAT.equals(tt)) {
                    statusColor = ModernUITheme.DANGER;
                } else if (TrangThaiConstants.DD_HOAN_THANH_CA.equals(tt)) {
                    statusColor = ModernUITheme.PRIMARY;
                }

                pnlDocList.add(createDoctorItem(
                        pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV(),
                        pc.getTenCa() != null ? pc.getTenCa() : "Ca trực",
                        tt,
                        statusColor
                ));
                pnlDocList.add(Box.createVerticalStrut(6));
                shown++;
                if (shown >= 3) break;
            }
        }
        pnlDocList.revalidate();
        pnlDocList.repaint();

        // 6. CẬP NHẬT WIDGET TOP DỊCH VỤ TRỌNG ĐIỂM (TỶ LỆ THẬT)
        pnlServList.removeAll();
        if (res.dsTopDichVu != null && !res.dsTopDichVu.isEmpty()) {
            Color[] colors = {ModernUITheme.PRIMARY, ModernUITheme.ACCENT, ModernUITheme.SUCCESS, ModernUITheme.WARNING};
            int idx = 0;
            for (ThongKeDAO.TopDichVu td : res.dsTopDichVu) {
                pnlServList.add(createServiceProgressItem(
                        td.tenDichVu,
                        df.format(td.doanhThu) + " (" + td.soLuot + " lượt)",
                        td.tiLe,
                        colors[idx % colors.length]
                ));
                idx++;
            }
        }
        pnlServList.revalidate();
        pnlServList.repaint();
    }

    private static class DashboardData {
        double tongDT, dtHomNay, dtHomQua, dtThangNay, dtThangTruoc;
        int tongLK, lkHomNay, lkHomQua, lkThangNay;
        int tongBN;
        ThongKeDAO.CanhBaoDashboard canhBao;
        List<LichHen> dsLichHenHomNay;
        List<PhanCongCaTruc> dsPhanCongHomNay;
        List<ThongKeDAO.TopDichVu> dsTopDichVu;
        List<ThongKeDAO.DoanhThuNgay> dsDoanhThu7Ngay;
    }

    // =========================================================================
    // CUSTOM COMPONENT: BIỂU ĐỒ DOANH THU 7 NGÀY BẰNG GRAPHICS2D + TOOLTIP
    // =========================================================================

    public static class ChartDoanhThuPanel extends JPanel {
        private List<ThongKeDAO.DoanhThuNgay> dataList = new ArrayList<>();
        private int hoveredIndex = -1;
        private Point mousePoint = null;
        private DecimalFormat dfChart = new DecimalFormat("#,### VNĐ");

        public ChartDoanhThuPanel() {
            setOpaque(false);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePoint = e.getPoint();
                    int old = hoveredIndex;
                    hoveredIndex = getBarIndexAt(e.getX());
                    if (old != hoveredIndex) {
                        repaint();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredIndex = -1;
                    mousePoint = null;
                    repaint();
                }
            });
        }

        public void setData(List<ThongKeDAO.DoanhThuNgay> list) {
            this.dataList = list != null ? list : new ArrayList<>();
            repaint();
        }

        private int getBarIndexAt(int mouseX) {
            if (dataList.isEmpty()) return -1;
            int n = dataList.size();
            int padLeft = 45;
            int padRight = 20;
            int chartW = getWidth() - padLeft - padRight;
            if (chartW <= 0) return -1;

            double slotW = (double) chartW / n;
            for (int i = 0; i < n; i++) {
                int startX = (int) (padLeft + i * slotW);
                int endX = (int) (startX + slotW);
                if (mouseX >= startX && mouseX <= endX) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int padLeft = 55;
            int padRight = 25;
            int padTop = 20;
            int padBottom = 35;

            int plotW = w - padLeft - padRight;
            int plotH = h - padTop - padBottom;

            if (plotW <= 0 || plotH <= 0 || dataList.isEmpty()) {
                g2.dispose();
                return;
            }

            // Tìm max doanh thu
            double maxVal = 10000000.0;
            for (ThongKeDAO.DoanhThuNgay d : dataList) {
                if (d.doanhThu > maxVal) maxVal = d.doanhThu;
            }
            // Làm tròn maxVal lên mốc đẹp
            maxVal = Math.ceil(maxVal / 2000000.0) * 2000000.0;

            // 1. Vẽ các đường lưới ngang (Grid lines)
            g2.setFont(ModernUITheme.FONT_TINY);
            int gridSteps = 4;
            for (int i = 0; i <= gridSteps; i++) {
                int y = padTop + (int) (plotH * (1.0 - (double) i / gridSteps));
                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(padLeft, y, padLeft + plotW, y);

                double v = (maxVal / gridSteps) * i;
                String lblY = String.format("%.0f tr", v / 1000000.0);
                g2.setColor(ModernUITheme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lblY, padLeft - fm.stringWidth(lblY) - 8, y + 4);
            }

            // 2. Vẽ các cột doanh thu (Bars)
            int n = dataList.size();
            double slotW = (double) plotW / n;
            int barWidth = Math.max(16, (int) (slotW * 0.45));

            for (int i = 0; i < n; i++) {
                ThongKeDAO.DoanhThuNgay item = dataList.get(i);
                int cx = (int) (padLeft + i * slotW + slotW / 2);
                int bx = cx - barWidth / 2;

                int barHeight = (int) ((item.doanhThu / maxVal) * plotH);
                if (barHeight < 4 && item.doanhThu > 0) barHeight = 4;
                int by = padTop + plotH - barHeight;

                boolean isHover = (i == hoveredIndex);

                // Gradient cho cột
                Color c1 = isHover ? new Color(2, 132, 199) : ModernUITheme.PRIMARY;
                Color c2 = isHover ? new Color(56, 189, 248) : new Color(14, 165, 233);
                GradientPaint barGrad = new GradientPaint(bx, by, c2, bx, by + barHeight, c1);
                g2.setPaint(barGrad);
                g2.fill(new RoundRectangle2D.Double(bx, by, barWidth, barHeight, 6, 6));

                // Nhãn ngày bên dưới
                g2.setColor(isHover ? ModernUITheme.PRIMARY_DARK : ModernUITheme.TEXT_MUTED);
                g2.setFont(isHover ? ModernUITheme.FONT_BOLD : ModernUITheme.FONT_TINY);
                FontMetrics fm = g2.getFontMetrics();
                int lx = cx - fm.stringWidth(item.label) / 2;
                g2.drawString(item.label, lx, padTop + plotH + 18);
            }

            // 3. Vẽ Tooltip nổi nếu đang hover
            if (hoveredIndex >= 0 && hoveredIndex < dataList.size() && mousePoint != null) {
                ThongKeDAO.DoanhThuNgay hItem = dataList.get(hoveredIndex);
                String t1 = "Ngày " + hItem.label + ": " + dfChart.format(hItem.doanhThu);
                String t2 = "Số lượt điều trị: " + hItem.soCa + " ca";

                g2.setFont(ModernUITheme.FONT_BOLD);
                FontMetrics fm1 = g2.getFontMetrics();
                int tw = Math.max(fm1.stringWidth(t1), fm1.stringWidth(t2)) + 20;
                int th = 46;

                int tx = mousePoint.x - tw / 2;
                int ty = mousePoint.y - th - 12;
                if (tx < 10) tx = 10;
                if (tx + tw > w - 10) tx = w - tw - 10;
                if (ty < 5) ty = mousePoint.y + 15;

                // Nền tooltip
                g2.setColor(new Color(15, 23, 42, 230));
                g2.fill(new RoundRectangle2D.Double(tx, ty, tw, th, 8, 8));
                g2.setColor(ModernUITheme.PRIMARY);
                g2.draw(new RoundRectangle2D.Double(tx, ty, tw, th, 8, 8));

                g2.setColor(Color.WHITE);
                g2.drawString(t1, tx + 10, ty + 18);
                g2.setFont(ModernUITheme.FONT_TINY);
                g2.setColor(new Color(191, 219, 254));
                g2.drawString(t2, tx + 10, ty + 36);
            }

            g2.dispose();
        }
    }
}
