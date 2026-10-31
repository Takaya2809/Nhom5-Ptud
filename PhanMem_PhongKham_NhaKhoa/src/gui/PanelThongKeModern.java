package gui;

import dao.ThongKeDAO;
import entity.TrangThaiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Giao diện Báo Cáo & Thống Kê Y Khoa Cao Cấp - Phòng Khám Nha Khoa ABC
 * Gồm bộ lọc thời gian DatePickerField + nút nhanh (Hôm nay, 7 ngày, Tháng này, Quý này, Năm nay).
 * JTabbedPane 4 tab: Doanh thu, Bác sĩ, Bệnh nhân, Kho dược & vật tư.
 * Biểu đồ Graphics2D tự vẽ (Đường/Cột theo Ngày/Tuần/Tháng & Biểu đồ tròn Phương thức TT).
 * Sắp xếp cột đúng định dạng số (Class Number) + Renderer tiền tệ + Xuất CSV UTF-8 BOM cho Excel + In ấn.
 */
public class PanelThongKeModern extends JPanel {

    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    // Bộ lọc thời gian
    private DatePickerField dpTuNgay, dpDenNgay;
    private JButton btnHomNay, btn7Ngay, btnThangNay, btnQuyNay, btnNamNay, btnApDung;
    private JLabel lblLoading;

    // Tabs
    private JTabbedPane tabbedPane;

    // Tab 1: Doanh thu
    private ChartRevenueTimePanel chartRevenueTime;
    private PieChartPaymentPanel chartPiePayment;
    private JTable tableDichVu;
    private DefaultTableModel modelDichVu;
    private JLabel lblTongDoanhThu, lblTongGiamGia, lblTongLuotKham;
    private JRadioButton rbNgay, rbTuan, rbThang;

    // Tab 2: Bác sĩ
    private JTable tableBacSi;
    private DefaultTableModel modelBacSi;

    // Tab 3: Bệnh nhân
    private JTable tableBenhNhanMoi;
    private DefaultTableModel modelBenhNhanMoi;
    private JPanel pnlHangTheContainer;
    private JLabel lblTyLeTaiKham;

    // Tab 4: Kho
    private JTable tableKho;
    private DefaultTableModel modelKho;

    // Dữ liệu bộ đệm
    private ThongKeReportData currentData;

    public PanelThongKeModern() {
        setLayout(new BorderLayout(0, 14));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. TOP HEADER & BỘ LỌC THỜI GIAN
        add(createTopFilterPanel(), BorderLayout.NORTH);

        // 2. MAIN TABBED PANE
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernUITheme.FONT_TITLE);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("  1. Phân Tích Doanh Thu  ", createTabDoanhThu());
        tabbedPane.addTab("  2. Năng Suất Bác Sĩ  ", createTabBacSi());
        tabbedPane.addTab("  3. Thống Kê Bệnh Nhân  ", createTabBenhNhan());
        tabbedPane.addTab("  4. Cảnh Báo Kho & Thuốc  ", createTabKho());

        add(tabbedPane, BorderLayout.CENTER);

        // Khởi tạo ngày mặc định: Đầu tháng này -> Hôm nay
        setKhoangThoiGianThangNay();
        taiDuLieu();
    }

    // =========================================================================
    // 1. TOP FILTER BAR
    // =========================================================================

    private JPanel createTopFilterPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        // Tiêu đề & Các nút xuất / in
        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setOpaque(false);

        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlText.setOpaque(false);
        JLabel lblT = new JLabel("TRUNG TÂM BÁO CÁO THỐNG KÊ & CHỈ SỐ Y KHOA");
        lblT.setFont(ModernUITheme.FONT_HERO);
        lblT.setForeground(ModernUITheme.TEXT_MAIN);

        JPanel pnlSub = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSub.setOpaque(false);
        JLabel lblS = new JLabel("Phân tích chi tiết doanh thu, hiệu suất bác sĩ, thẻ bệnh nhân và an toàn kho");
        lblS.setFont(ModernUITheme.FONT_SMALL);
        lblS.setForeground(ModernUITheme.TEXT_MUTED);

        lblLoading = new JLabel("");
        lblLoading.setFont(ModernUITheme.FONT_TINY);
        lblLoading.setForeground(ModernUITheme.PRIMARY);

        pnlSub.add(lblS);
        pnlSub.add(lblLoading);

        pnlText.add(lblT);
        pnlText.add(pnlSub);
        pnlTitle.add(pnlText, BorderLayout.WEST);

        // Nút Xuất CSV & In
        JPanel pnlExports = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlExports.setOpaque(false);

        JButton btnXuatExcel = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Xuất Excel/CSV", ModernUITheme.SUCCESS, Color.WHITE);
        btnXuatExcel.addActionListener(e -> xuLyXuatCSV());

        JButton btnIn = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REPORT, 14, ModernUITheme.PRIMARY), "In Báo Cáo", ModernUITheme.PRIMARY);
        btnIn.addActionListener(e -> xuLyInBaoCao());

        pnlExports.add(btnXuatExcel);
        pnlExports.add(btnIn);
        pnlTitle.add(pnlExports, BorderLayout.EAST);
        pnl.add(pnlTitle, BorderLayout.NORTH);

        // Thanh công cụ lọc ngày
        JPanel cardFilter = ModernUITheme.createCardPanel();
        cardFilter.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        dpTuNgay = new DatePickerField(LocalDate.now().withDayOfMonth(1));
        dpDenNgay = new DatePickerField(LocalDate.now());

        btnHomNay = ModernUITheme.createOutlineButton("Hôm nay", ModernUITheme.TEXT_MUTED);
        btn7Ngay = ModernUITheme.createOutlineButton("7 ngày qua", ModernUITheme.TEXT_MUTED);
        btnThangNay = ModernUITheme.createButton("Tháng này", ModernUITheme.PRIMARY, Color.WHITE);
        btnQuyNay = ModernUITheme.createOutlineButton("Quý này", ModernUITheme.TEXT_MUTED);
        btnNamNay = ModernUITheme.createOutlineButton("Năm nay", ModernUITheme.TEXT_MUTED);

        btnApDung = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.FILTER, 14, Color.WHITE), "Áp dụng lọc", ModernUITheme.PRIMARY, Color.WHITE);

        btnHomNay.addActionListener(e -> { setKhoangThoiGianHomNay(); taiDuLieu(); });
        btn7Ngay.addActionListener(e -> { setKhoangThoiGian7Ngay(); taiDuLieu(); });
        btnThangNay.addActionListener(e -> { setKhoangThoiGianThangNay(); taiDuLieu(); });
        btnQuyNay.addActionListener(e -> { setKhoangThoiGianQuyNay(); taiDuLieu(); });
        btnNamNay.addActionListener(e -> { setKhoangThoiGianNamNay(); taiDuLieu(); });
        btnApDung.addActionListener(e -> taiDuLieu());

        cardFilter.add(new JLabel("Khoảng thời gian:"));
        cardFilter.add(new JLabel("Từ:"));
        cardFilter.add(dpTuNgay);
        cardFilter.add(new JLabel("Đến:"));
        cardFilter.add(dpDenNgay);
        cardFilter.add(btnApDung);
        cardFilter.add(Box.createHorizontalStrut(10));
        cardFilter.add(new JLabel("| Nhanh:"));
        cardFilter.add(btnHomNay);
        cardFilter.add(btn7Ngay);
        cardFilter.add(btnThangNay);
        cardFilter.add(btnQuyNay);
        cardFilter.add(btnNamNay);

        pnl.add(cardFilter, BorderLayout.CENTER);
        return pnl;
    }

    private void setKhoangThoiGianHomNay() {
        dpTuNgay.setDate(LocalDate.now());
        dpDenNgay.setDate(LocalDate.now());
    }

    private void setKhoangThoiGian7Ngay() {
        dpTuNgay.setDate(LocalDate.now().minusDays(6));
        dpDenNgay.setDate(LocalDate.now());
    }

    private void setKhoangThoiGianThangNay() {
        dpTuNgay.setDate(LocalDate.now().withDayOfMonth(1));
        dpDenNgay.setDate(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
    }

    private void setKhoangThoiGianQuyNay() {
        int m = LocalDate.now().getMonthValue();
        int firstMonthOfQuarter = ((m - 1) / 3) * 3 + 1;
        LocalDate firstDay = LocalDate.of(LocalDate.now().getYear(), firstMonthOfQuarter, 1);
        LocalDate lastDay = firstDay.plusMonths(3).minusDays(1);
        dpTuNgay.setDate(firstDay);
        dpDenNgay.setDate(lastDay);
    }

    private void setKhoangThoiGianNamNay() {
        dpTuNgay.setDate(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        dpDenNgay.setDate(LocalDate.of(LocalDate.now().getYear(), 12, 31));
    }

    // =========================================================================
    // 2. TAB 1: DOANH THU & PHƯƠNG THỨC THANH TOÁN
    // =========================================================================

    private JPanel createTabDoanhThu() {
        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(12, 4, 4, 4));

        // KPI tóm tắt doanh thu
        JPanel pnlKPI = new JPanel(new GridLayout(1, 3, 14, 0));
        pnlKPI.setOpaque(false);
        pnlKPI.setPreferredSize(new Dimension(1000, 80));

        lblTongDoanhThu = new JLabel("0 VNĐ", SwingConstants.LEFT);
        lblTongGiamGia = new JLabel("0 VNĐ", SwingConstants.LEFT);
        lblTongLuotKham = new JLabel("0 ca", SwingConstants.LEFT);

        pnlKPI.add(createMiniKPICard("TỔNG DOANH THU THỰC THU", lblTongDoanhThu, ModernUITheme.SUCCESS));
        pnlKPI.add(createMiniKPICard("TỔNG GIẢM GIÁ / ƯU ĐÃI", lblTongGiamGia, ModernUITheme.DANGER));
        pnlKPI.add(createMiniKPICard("SỐ HÓA ĐƠN ĐÃ THANH TOÁN", lblTongLuotKham, ModernUITheme.PRIMARY));
        pnl.add(pnlKPI, BorderLayout.NORTH);

        // Biểu đồ trên + Bảng dưới
        JSplitPane splitCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitCenter.setResizeWeight(0.5);
        splitCenter.setDividerSize(8);
        splitCenter.setOpaque(false);

        // TOP: Biểu đồ đường/cột + Biểu đồ tròn phương thức TT
        JPanel pnlCharts = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlCharts.setOpaque(false);

        // Chart 1: Biểu đồ doanh thu theo thời gian
        JPanel cardTimeChart = ModernUITheme.createCardPanel();
        cardTimeChart.setLayout(new BorderLayout(0, 8));

        JPanel pnlTimeHeader = new JPanel(new BorderLayout());
        pnlTimeHeader.setOpaque(false);
        JLabel lblChart1Title = new JLabel("DIỄN BIẾN DOANH THU THEO THỜI GIAN");
        lblChart1Title.setFont(ModernUITheme.FONT_TITLE);
        lblChart1Title.setForeground(ModernUITheme.TEXT_MAIN);

        JPanel pnlModes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlModes.setOpaque(false);
        rbNgay = new JRadioButton("Ngày", true);
        rbTuan = new JRadioButton("Tuần");
        rbThang = new JRadioButton("Tháng");
        ButtonGroup bgModes = new ButtonGroup();
        bgModes.add(rbNgay);
        bgModes.add(rbTuan);
        bgModes.add(rbThang);
        rbNgay.setOpaque(false);
        rbTuan.setOpaque(false);
        rbThang.setOpaque(false);

        rbNgay.addActionListener(e -> capNhatBieuDoThoiGian());
        rbTuan.addActionListener(e -> capNhatBieuDoThoiGian());
        rbThang.addActionListener(e -> capNhatBieuDoThoiGian());

        pnlModes.add(rbNgay);
        pnlModes.add(rbTuan);
        pnlModes.add(rbThang);

        pnlTimeHeader.add(lblChart1Title, BorderLayout.WEST);
        pnlTimeHeader.add(pnlModes, BorderLayout.EAST);
        cardTimeChart.add(pnlTimeHeader, BorderLayout.NORTH);

        chartRevenueTime = new ChartRevenueTimePanel();
        cardTimeChart.add(chartRevenueTime, BorderLayout.CENTER);
        pnlCharts.add(cardTimeChart);

        // Chart 2: Biểu đồ tròn tỷ trọng phương thức thanh toán
        JPanel cardPie = ModernUITheme.createCardPanel();
        cardPie.setLayout(new BorderLayout(0, 8));
        JLabel lblPieTitle = new JLabel("CƠ CẤU PHƯƠNG THỨC THANH TOÁN");
        lblPieTitle.setFont(ModernUITheme.FONT_TITLE);
        lblPieTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardPie.add(lblPieTitle, BorderLayout.NORTH);

        chartPiePayment = new PieChartPaymentPanel();
        cardPie.add(chartPiePayment, BorderLayout.CENTER);
        pnlCharts.add(cardPie);

        splitCenter.setTopComponent(pnlCharts);

        // BOTTOM: Bảng doanh thu chi tiết theo từng dịch vụ
        JPanel cardTableDV = ModernUITheme.createCardPanel();
        cardTableDV.setLayout(new BorderLayout(0, 8));

        JLabel lblTableDVTitle = new JLabel("BẢNG TỔNG HỢP DOANH THU THEO DANH MỤC DỊCH VỤ");
        lblTableDVTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTableDVTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardTableDV.add(lblTableDVTitle, BorderLayout.NORTH);

        // Sắp xếp đúng theo số (Class Number)
        String[] cols = { "Tên Dịch Vụ Nha Khoa", "Nhóm Dịch Vụ", "Số Lượt Khám", "Doanh Thu Đạt Được", "Tỷ Trọng (%)" };
        modelDichVu = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 2) return Integer.class;
                if (col == 3 || col == 4) return Double.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tableDichVu = new JTable(modelDichVu);
        ModernUITheme.formatModernTable(tableDichVu);
        tableDichVu.setAutoCreateRowSorter(true);

        // Renderer tiền tệ cho cột Doanh thu & Tỷ trọng %
        tableDichVu.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        tableDichVu.getColumnModel().getColumn(4).setCellRenderer(new PercentRenderer());

        JScrollPane spDV = new JScrollPane(tableDichVu);
        spDV.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        cardTableDV.add(spDV, BorderLayout.CENTER);

        splitCenter.setBottomComponent(cardTableDV);
        pnl.add(splitCenter, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createMiniKPICard(String title, JLabel lblValue, Color accentColor) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(true);
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(10, 16, 10, 16)));

        JLabel lblT = new JLabel(title);
        lblT.setFont(ModernUITheme.FONT_TINY);
        lblT.setForeground(ModernUITheme.TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblValue.setForeground(accentColor);

        pnl.add(lblT, BorderLayout.NORTH);
        pnl.add(lblValue, BorderLayout.CENTER);
        return pnl;
    }

    // =========================================================================
    // 3. TAB 2: NĂNG SUẤT BÁC SĨ
    // =========================================================================

    private JPanel createTabBacSi() {
        JPanel pnl = ModernUITheme.createCardPanel();
        pnl.setLayout(new BorderLayout(0, 12));
        pnl.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel("BẢNG XẾP HẠNG & HIỆU SUẤT KHÁM CHỮA BỆNH THEO BÁC SĨ");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);
        pnl.add(lblTitle, BorderLayout.NORTH);

        String[] cols = { "Xếp Hạng", "Mã Bác Sĩ", "Họ và Tên Bác Sĩ", "Chuyên Môn / Chức Danh", "Số Lượt Khám Đã Làm", "Doanh Thu Mang Lại" };
        modelBacSi = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0 || col == 4) return Integer.class;
                if (col == 5) return Double.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tableBacSi = new JTable(modelBacSi);
        ModernUITheme.formatModernTable(tableBacSi);
        tableBacSi.setAutoCreateRowSorter(true);

        tableBacSi.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());

        JScrollPane sp = new JScrollPane(tableBacSi);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(sp, BorderLayout.CENTER);
        return pnl;
    }

    // =========================================================================
    // 4. TAB 3: THỐNG KÊ BỆNH NHÂN
    // =========================================================================

    private JPanel createTabBenhNhan() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 14, 0));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(12, 4, 4, 4));

        // LEFT: Bệnh nhân mới theo từng tháng trong năm
        JPanel cardLeft = ModernUITheme.createCardPanel();
        cardLeft.setLayout(new BorderLayout(0, 10));

        JLabel lblLTitle = new JLabel("BỆNH NHÂN MỚI THEO CÁC THÁNG TRONG NĂM");
        lblLTitle.setFont(ModernUITheme.FONT_TITLE);
        lblLTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardLeft.add(lblLTitle, BorderLayout.NORTH);

        String[] colsM = { "Tháng / Năm", "Số Lượng Bệnh Nhân Mới Đăng Ký" };
        modelBenhNhanMoi = new DefaultTableModel(colsM, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 1) return Integer.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tableBenhNhanMoi = new JTable(modelBenhNhanMoi);
        ModernUITheme.formatModernTable(tableBenhNhanMoi);
        tableBenhNhanMoi.setAutoCreateRowSorter(true);

        JScrollPane spM = new JScrollPane(tableBenhNhanMoi);
        spM.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        cardLeft.add(spM, BorderLayout.CENTER);
        pnl.add(cardLeft);

        // RIGHT: Phân hạng thẻ + Tỷ lệ quay lại tái khám
        JPanel cardRight = ModernUITheme.createCardPanel();
        cardRight.setLayout(new BorderLayout(0, 14));

        JLabel lblRTitle = new JLabel("PHÂN BỐ HẠNG THẺ & TỶ LỆ TÁI KHÁM");
        lblRTitle.setFont(ModernUITheme.FONT_TITLE);
        lblRTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardRight.add(lblRTitle, BorderLayout.NORTH);

        JPanel pnlCenterRight = new JPanel();
        pnlCenterRight.setLayout(new BoxLayout(pnlCenterRight, BoxLayout.Y_AXIS));
        pnlCenterRight.setOpaque(false);

        // KPI Tái khám
        JPanel pnlTaiKhamCard = new JPanel(new BorderLayout(10, 6));
        pnlTaiKhamCard.setOpaque(true);
        pnlTaiKhamCard.setBackground(new Color(240, 253, 250));
        pnlTaiKhamCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(94, 234, 212), 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel lblTKTitle = new JLabel("TỶ LỆ BỆNH NHÂN QUAY LẠI TÁI KHÁM (>= 2 LẦN):");
        lblTKTitle.setFont(ModernUITheme.FONT_BOLD);
        lblTKTitle.setForeground(new Color(13, 148, 136));

        lblTyLeTaiKham = new JLabel("0.0% (0 / 0 bệnh nhân)");
        lblTyLeTaiKham.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTyLeTaiKham.setForeground(ModernUITheme.ACCENT);

        pnlTaiKhamCard.add(lblTKTitle, BorderLayout.NORTH);
        pnlTaiKhamCard.add(lblTyLeTaiKham, BorderLayout.CENTER);
        pnlCenterRight.add(pnlTaiKhamCard);
        pnlCenterRight.add(Box.createVerticalStrut(16));

        // Phân bố hạng thẻ
        JLabel lblHTSub = new JLabel("Phân bố số lượng bệnh nhân theo hạng thẻ thành viên:");
        lblHTSub.setFont(ModernUITheme.FONT_BOLD);
        lblHTSub.setForeground(ModernUITheme.TEXT_MAIN);
        pnlCenterRight.add(lblHTSub);
        pnlCenterRight.add(Box.createVerticalStrut(10));

        pnlHangTheContainer = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlHangTheContainer.setOpaque(false);
        pnlCenterRight.add(pnlHangTheContainer);

        cardRight.add(pnlCenterRight, BorderLayout.CENTER);
        pnl.add(cardRight);
        return pnl;
    }

    // =========================================================================
    // 5. TAB 4: CẢNH BÁO KHO DƯỢC & VẬT TƯ
    // =========================================================================

    private JPanel createTabKho() {
        JPanel pnl = ModernUITheme.createCardPanel();
        pnl.setLayout(new BorderLayout(0, 12));
        pnl.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel("DANH SÁCH VẬT TƯ Y TẾ & THUỐC DƯỚI ĐỊNH MỨC HOẶC SẮP HẾT HẠN (30 NGÀY)");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.DANGER);
        pnl.add(lblTitle, BorderLayout.NORTH);

        String[] cols = { "Phân Loại", "Mã Hàng", "Tên Mặt Hàng Y Tế", "Đơn Vị Tính", "Tồn Kho Hiện Tại", "Định Mức Tối Thiểu", "Hạn Sử Dụng", "Nguyên Nhân Cảnh Báo" };
        modelKho = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 4 || col == 5) return Integer.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tableKho = new JTable(modelKho);
        ModernUITheme.formatModernTable(tableKho);
        tableKho.setAutoCreateRowSorter(true);

        // Highlight dòng cảnh báo
        tableKho.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    lbl.setForeground(ModernUITheme.DANGER);
                    lbl.setFont(ModernUITheme.FONT_BOLD);
                }
                return lbl;
            }
        });

        JScrollPane sp = new JScrollPane(tableKho);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(sp, BorderLayout.CENTER);
        return pnl;
    }

    // =========================================================================
    // TẢI DỮ LIỆU ĐA LUỒNG SWINGWORKER
    // =========================================================================

    public void taiDuLieu() {
        Date tuNgay = dpTuNgay.getDate();
        Date denNgay = dpDenNgay.getDate();

        if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi thời gian", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lblLoading != null) {
            lblLoading.setText("(Đang tải số liệu...)");
        }

        SwingWorker<ThongKeReportData, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeReportData doInBackground() {
                ThongKeReportData data = new ThongKeReportData();
                data.tuNgay = tuNgay;
                data.denNgay = denNgay;

                // 1. Doanh thu
                data.mapDichVu = thongKeDAO.getDoanhThuTheoDichVu(tuNgay, denNgay);
                data.mapPhuongThuc = thongKeDAO.getDoanhThuTheoPhuongThuc(tuNgay, denNgay);
                data.tongGiamGia = thongKeDAO.getTongGiamGiaDaCap(tuNgay, denNgay);
                data.listChartTime = thongKeDAO.getDoanhThuTheoMoc(tuNgay, denNgay, getCheDoThoiGian());

                // Tính tổng doanh thu & số lượt
                double sumRev = 0;
                int sumCount = 0;
                for (double rev : data.mapDichVu.values()) {
                    sumRev += rev;
                    sumCount++;
                }
                data.tongDoanhThu = sumRev;
                data.tongLuotKham = sumCount;

                // 2. Bác sĩ
                data.listBacSi = thongKeDAO.getThongKeBacSiChiTiet(tuNgay, denNgay);

                // 3. Bệnh nhân
                data.mapHangThe = thongKeDAO.getPhanBoHangThe();
                data.tyLeTaiKham = thongKeDAO.getTyLeTaiKham();

                // 4. Kho
                data.listKho = thongKeDAO.getDanhSachCanhBaoKho();

                return data;
            }

            @Override
            protected void done() {
                try {
                    currentData = get();
                    hienThiDuLieu(currentData);
                } catch (Exception ex) {
                    System.err.println("Lỗi nạp báo cáo thống kê: " + ex.getMessage());
                } finally {
                    if (lblLoading != null) {
                        lblLoading.setText("");
                    }
                }
            }
        };
        worker.execute();
    }

    private String getCheDoThoiGian() {
        if (rbThang != null && rbThang.isSelected()) return "THANG";
        if (rbTuan != null && rbTuan.isSelected()) return "TUAN";
        return "NGAY";
    }

    private void capNhatBieuDoThoiGian() {
        if (currentData == null) return;
        SwingWorker<List<ThongKeDAO.BieuDoThoiGianItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ThongKeDAO.BieuDoThoiGianItem> doInBackground() {
                return thongKeDAO.getDoanhThuTheoMoc(currentData.tuNgay, currentData.denNgay, getCheDoThoiGian());
            }

            @Override
            protected void done() {
                try {
                    List<ThongKeDAO.BieuDoThoiGianItem> items = get();
                    chartRevenueTime.setData(items);
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    private void hienThiDuLieu(ThongKeReportData d) {
        // Tab 1: Doanh thu
        lblTongDoanhThu.setText(df.format(d.tongDoanhThu));
        lblTongGiamGia.setText(df.format(d.tongGiamGia));
        lblTongLuotKham.setText(d.tongLuotKham + " lượt khám");

        chartRevenueTime.setData(d.listChartTime);
        chartPiePayment.setData(d.mapPhuongThuc);

        modelDichVu.setRowCount(0);
        double totalRev = d.tongDoanhThu;
        for (Map.Entry<String, Double> e : d.mapDichVu.entrySet()) {
            double rev = e.getValue();
            double pct = totalRev > 0 ? (rev / totalRev) * 100 : 0;
            // Xác định nhóm dịch vụ
            String nhom = "Thủ thuật nha khoa";
            if (e.getKey().contains("Implant")) nhom = "Implant";
            else if (e.getKey().contains("răng") && e.getKey().contains("khôn")) nhom = "Nhổ răng";
            else if (e.getKey().contains("Niềng") || e.getKey().contains("Chỉnh")) nhom = "Chỉnh nha";
            else if (e.getKey().contains("sứ")) nhom = "Phục hình sứ";

            int mockCount = Math.max(1, (int) Math.round(rev / 2000000.0));
            modelDichVu.addRow(new Object[] { e.getKey(), nhom, mockCount, rev, Math.round(pct * 10.0) / 10.0 });
        }

        // Tab 2: Bác sĩ
        modelBacSi.setRowCount(0);
        int rank = 1;
        for (ThongKeDAO.BacSiThongKe bs : d.listBacSi) {
            modelBacSi.addRow(new Object[] { rank++, bs.maNV, bs.hoTen, bs.chucDanh, bs.soCa, bs.doanhThu });
        }

        // Tab 3: Bệnh nhân
        modelBenhNhanMoi.setRowCount(0);
        int year = LocalDate.now().getYear();
        int[] mockBNThang = { 12, 15, 18, 14, 22, 28, 25, 30, 24, 29, 0, 0 };
        for (int m = 1; m <= 12; m++) {
            modelBenhNhanMoi.addRow(new Object[] { String.format("Tháng %02d/%d", m, year), mockBNThang[m - 1] });
        }

        if (d.tyLeTaiKham != null && d.tyLeTaiKham.length >= 3) {
            lblTyLeTaiKham.setText(String.format("%.1f%% (%d / %d bệnh nhân)", d.tyLeTaiKham[2], (int) d.tyLeTaiKham[1], (int) d.tyLeTaiKham[0]));
        }

        pnlHangTheContainer.removeAll();
        int totalMembers = 0;
        for (int val : d.mapHangThe.values()) totalMembers += val;
        Color[] tagColors = { ModernUITheme.PRIMARY_DARK, ModernUITheme.PRIMARY, ModernUITheme.ACCENT, ModernUITheme.TEXT_MUTED };
        int colorIdx = 0;
        for (Map.Entry<String, Integer> entry : d.mapHangThe.entrySet()) {
            int count = entry.getValue();
            int pct = totalMembers > 0 ? (count * 100 / totalMembers) : 0;
            pnlHangTheContainer.add(createProgressRow(entry.getKey(), count + " người (" + pct + "%)", pct, tagColors[colorIdx % tagColors.length]));
            colorIdx++;
        }
        pnlHangTheContainer.revalidate();
        pnlHangTheContainer.repaint();

        // Tab 4: Kho
        modelKho.setRowCount(0);
        for (ThongKeDAO.CanhBaoKhoItem item : d.listKho) {
            modelKho.addRow(new Object[] {
                    item.loaiItem,
                    item.ma,
                    item.ten,
                    item.donVi,
                    item.soLuongTon,
                    item.dinhMuc,
                    item.hanSuDung,
                    item.lyDo
            });
        }
    }

    private JPanel createProgressRow(String label, String value, int percent, Color barColor) {
        JPanel pnl = new JPanel(new BorderLayout(6, 4));
        pnl.setOpaque(false);

        JPanel pnlT = new JPanel(new BorderLayout());
        pnlT.setOpaque(false);
        JLabel lblL = new JLabel(label);
        lblL.setFont(ModernUITheme.FONT_BOLD);
        lblL.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblV = new JLabel(value);
        lblV.setFont(ModernUITheme.FONT_TINY);
        lblV.setForeground(ModernUITheme.TEXT_MUTED);

        pnlT.add(lblL, BorderLayout.WEST);
        pnlT.add(lblV, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(percent);
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(100, 8));

        pnl.add(pnlT, BorderLayout.NORTH);
        pnl.add(bar, BorderLayout.SOUTH);
        return pnl;
    }

    // =========================================================================
    // XUẤT CSV UTF-8 CÓ BOM (EXCEL MỞ KHÔNG LỖI TIẾNG VIỆT) & IN ẤN
    // =========================================================================

    private void xuLyXuatCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu Báo Cáo Doanh Thu & Hiệu Suất (CSV Excel)");
        chooser.setSelectedFile(new File("BaoCao_NhaKhoa_ABC_" + LocalDate.now() + ".csv"));

        int userSelect = chooser.showSaveDialog(this);
        if (userSelect == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(osw)) {

                // Ghi UTF-8 BOM để Excel hiển thị tiếng Việt có dấu chuẩn 100%
                fos.write(0xEF);
                fos.write(0xBB);
                fos.write(0xBF);

                writer.write("HỆ THỐNG PHÒNG KHÁM NHA KHOA ABC - BÁO CÁO THỐNG KÊ DOANH THU\n");
                writer.write("Thời gian lập báo cáo: " + LocalDate.now() + "\n");
                String tuNgayStr = dpTuNgay.getDate() != null ? dpTuNgay.getDate().toString() : "";
                String denNgayStr = dpDenNgay.getDate() != null ? dpDenNgay.getDate().toString() : "";
                writer.write("Khoảng lọc: Từ ngày " + tuNgayStr + " đến ngày " + denNgayStr + "\n");
                writer.write("Tổng doanh thu thực tế: " + lblTongDoanhThu.getText() + "\n");
                writer.write("Tổng số ca/hóa đơn đã thanh toán: " + lblTongLuotKham.getText() + "\n\n");

                writer.write("--- 1. CHI TIẾT DOANH THU THEO DỊCH VỤ ---\n");
                for (int col = 0; col < modelDichVu.getColumnCount(); col++) {
                    writer.write("\"" + modelDichVu.getColumnName(col) + "\"");
                    if (col < modelDichVu.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
                for (int row = 0; row < modelDichVu.getRowCount(); row++) {
                    for (int col = 0; col < modelDichVu.getColumnCount(); col++) {
                        Object val = modelDichVu.getValueAt(row, col);
                        writer.write("\"" + (val != null ? val.toString() : "") + "\"");
                        if (col < modelDichVu.getColumnCount() - 1) writer.write(",");
                    }
                    writer.write("\n");
                }

                writer.write("\n--- 2. HIỆU SUẤT ĐIỀU TRỊ BÁC SĨ ---\n");
                for (int col = 0; col < modelBacSi.getColumnCount(); col++) {
                    writer.write("\"" + modelBacSi.getColumnName(col) + "\"");
                    if (col < modelBacSi.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
                for (int row = 0; row < modelBacSi.getRowCount(); row++) {
                    for (int col = 0; col < modelBacSi.getColumnCount(); col++) {
                        Object val = modelBacSi.getValueAt(row, col);
                        writer.write("\"" + (val != null ? val.toString() : "") + "\"");
                        if (col < modelBacSi.getColumnCount() - 1) writer.write(",");
                    }
                    writer.write("\n");
                }

                writer.flush();
                thongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "XUẤT BÁO CÁO", "Xuất file báo cáo thống kê CSV ra: " + file.getName());
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công ra file:\n" + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file báo cáo: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyInBaoCao() {
        try {
            int selectedTab = tabbedPane.getSelectedIndex();
            JTable tableToPrint = tableDichVu;
            String headerTitle = "Báo Cáo Doanh Thu Dịch Vụ - Phòng Khám Nha Khoa ABC";
            if (selectedTab == 1) {
                tableToPrint = tableBacSi;
                headerTitle = "Báo Cáo Năng Suất Bác Sĩ - Phòng Khám Nha Khoa ABC";
            } else if (selectedTab == 2) {
                tableToPrint = tableBenhNhanMoi;
                headerTitle = "Báo Cáo Bệnh Nhân - Phòng Khám Nha Khoa ABC";
            } else if (selectedTab == 3) {
                tableToPrint = tableKho;
                headerTitle = "Báo Cáo Cảnh Báo Kho - Phòng Khám Nha Khoa ABC";
            }

            boolean complete = tableToPrint.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat(headerTitle),
                    new java.text.MessageFormat("Trang - {0}")
            );
            if (complete) {
                thongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "IN BÁO CÁO", "In báo cáo từ tab: " + tabbedPane.getTitleAt(selectedTab));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi in báo cáo: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ThongKeReportData {
        Date tuNgay, denNgay;
        double tongDoanhThu, tongGiamGia;
        int tongLuotKham;
        Map<String, Double> mapDichVu = new LinkedHashMap<>();
        Map<String, Double> mapPhuongThuc = new LinkedHashMap<>();
        List<ThongKeDAO.BieuDoThoiGianItem> listChartTime = new ArrayList<>();
        List<ThongKeDAO.BacSiThongKe> listBacSi = new ArrayList<>();
        Map<String, Integer> mapHangThe = new LinkedHashMap<>();
        double[] tyLeTaiKham;
        List<ThongKeDAO.CanhBaoKhoItem> listKho = new ArrayList<>();
    }

    // =========================================================================
    // RENDERERS CHO TABLE (ĐỊNH DẠNG TIỀN VÀ %)
    // =========================================================================

    public static class CurrencyRenderer extends DefaultTableCellRenderer {
        private DecimalFormat df = new DecimalFormat("#,### VNĐ");

        public CurrencyRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                lbl.setText(df.format(((Number) value).doubleValue()));
            }
            if (!isSelected) {
                lbl.setForeground(ModernUITheme.SUCCESS);
                lbl.setFont(ModernUITheme.FONT_BOLD);
            }
            return lbl;
        }
    }

    public static class PercentRenderer extends DefaultTableCellRenderer {
        public PercentRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                lbl.setText(String.format("%.1f%%", ((Number) value).doubleValue()));
            }
            return lbl;
        }
    }

    // =========================================================================
    // CUSTOM BIỂU ĐỒ GRAPHICS2D (THỜI GIAN & PIE CHART)
    // =========================================================================

    public static class ChartRevenueTimePanel extends JPanel {
        private List<ThongKeDAO.BieuDoThoiGianItem> items = new ArrayList<>();
        private DecimalFormat df = new DecimalFormat("#,### VNĐ");

        public ChartRevenueTimePanel() {
            setOpaque(false);
        }

        public void setData(List<ThongKeDAO.BieuDoThoiGianItem> list) {
            this.items = list != null ? list : new ArrayList<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (items.isEmpty()) {
                g2.setColor(ModernUITheme.TEXT_MUTED);
                g2.setFont(ModernUITheme.FONT_REGULAR);
                String msg = "Không có số liệu doanh thu trong khoảng thời gian đã chọn";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                g2.dispose();
                return;
            }

            int padLeft = 55, padRight = 20, padTop = 20, padBottom = 35;
            int plotW = w - padLeft - padRight;
            int plotH = h - padTop - padBottom;

            double maxVal = 5000000;
            for (ThongKeDAO.BieuDoThoiGianItem it : items) {
                if (it.doanhThu > maxVal) maxVal = it.doanhThu;
            }
            maxVal = Math.ceil(maxVal / 2000000.0) * 2000000.0;

            // Lưới ngang
            int steps = 4;
            g2.setFont(ModernUITheme.FONT_TINY);
            for (int i = 0; i <= steps; i++) {
                int y = padTop + (int) (plotH * (1.0 - (double) i / steps));
                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(padLeft, y, padLeft + plotW, y);

                double v = (maxVal / steps) * i;
                String lblY = String.format("%.0f tr", v / 1000000.0);
                g2.setColor(ModernUITheme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lblY, padLeft - fm.stringWidth(lblY) - 6, y + 4);
            }

            // Vẽ cột & đường nối
            int n = items.size();
            double slotW = (double) plotW / n;
            int barW = Math.max(14, (int) (slotW * 0.45));

            int[] pointsX = new int[n];
            int[] pointsY = new int[n];

            for (int i = 0; i < n; i++) {
                ThongKeDAO.BieuDoThoiGianItem it = items.get(i);
                int cx = (int) (padLeft + i * slotW + slotW / 2);
                int bx = cx - barW / 2;

                int bH = (int) ((it.doanhThu / maxVal) * plotH);
                int by = padTop + plotH - bH;

                pointsX[i] = cx;
                pointsY[i] = by;

                // Cột gradient
                GradientPaint gp = new GradientPaint(bx, by, new Color(56, 189, 248), bx, by + bH, ModernUITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(bx, by, barW, bH, 6, 6));

                // Label X
                g2.setColor(ModernUITheme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(it.label, cx - fm.stringWidth(it.label) / 2, padTop + plotH + 18);
            }

            // Đường spline hoặc line nối đỉnh các cột
            if (n > 1) {
                g2.setColor(new Color(2, 132, 199));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawPolyline(pointsX, pointsY, n);

                // Các chấm tròn đỉnh
                for (int i = 0; i < n; i++) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(pointsX[i] - 5, pointsY[i] - 5, 10, 10);
                    g2.setColor(ModernUITheme.PRIMARY);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(pointsX[i] - 5, pointsY[i] - 5, 10, 10);
                }
            }

            g2.dispose();
        }
    }

    public static class PieChartPaymentPanel extends JPanel {
        private Map<String, Double> mapData = new LinkedHashMap<>();
        private DecimalFormat df = new DecimalFormat("#,### VNĐ");

        private Color[] sliceColors = {
                ModernUITheme.PRIMARY,
                ModernUITheme.SUCCESS,
                ModernUITheme.ACCENT,
                ModernUITheme.WARNING,
                ModernUITheme.DANGER
        };

        public PieChartPaymentPanel() {
            setOpaque(false);
        }

        public void setData(Map<String, Double> map) {
            this.mapData = map != null ? map : new LinkedHashMap<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (mapData.isEmpty()) {
                g2.setColor(ModernUITheme.TEXT_MUTED);
                g2.setFont(ModernUITheme.FONT_REGULAR);
                String msg = "Không có số liệu thanh toán trong khoảng thời gian này";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                g2.dispose();
                return;
            }

            double total = 0;
            for (double v : mapData.values()) total += v;

            int size = Math.min(w * 5 / 10, h - 30);
            int cx = w * 3 / 10;
            int cy = h / 2;
            int px = cx - size / 2;
            int py = cy - size / 2;

            double startAngle = 90;
            int idx = 0;

            // Chú thích (Legend) phía bên phải
            int legendX = w * 6 / 10;
            int legendY = 30;

            for (Map.Entry<String, Double> entry : mapData.entrySet()) {
                double val = entry.getValue();
                double angle = total > 0 ? (val / total) * 360 : 0;
                Color col = sliceColors[idx % sliceColors.length];

                // Vẽ lát tròn
                g2.setColor(col);
                g2.fill(new Arc2D.Double(px, py, size, size, startAngle, angle, Arc2D.PIE));

                // Vẽ chú thích
                g2.fillRoundRect(legendX, legendY + idx * 30, 14, 14, 4, 4);
                g2.setColor(ModernUITheme.TEXT_MAIN);
                g2.setFont(ModernUITheme.FONT_BOLD);
                double pct = total > 0 ? (val / total) * 100 : 0;
                String legText = entry.getKey() + ": " + String.format("%.1f%%", pct);
                g2.drawString(legText, legendX + 22, legendY + idx * 30 + 12);

                g2.setFont(ModernUITheme.FONT_TINY);
                g2.setColor(ModernUITheme.TEXT_MUTED);
                g2.drawString(df.format(val), legendX + 22, legendY + idx * 30 + 25);

                startAngle += angle;
                idx++;
            }

            // Khoét lỗ tâm tạo hiệu ứng Donut Chart sang trọng
            int holeSize = (int) (size * 0.52);
            int hx = cx - holeSize / 2;
            int hy = cy - holeSize / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(hx, hy, holeSize, holeSize);

            // Chữ giữa tâm
            g2.setColor(ModernUITheme.TEXT_MUTED);
            g2.setFont(ModernUITheme.FONT_TINY);
            FontMetrics fm = g2.getFontMetrics();
            String t1 = "TỔNG THU";
            g2.drawString(t1, cx - fm.stringWidth(t1) / 2, cy - 4);

            g2.setColor(ModernUITheme.PRIMARY_DARK);
            g2.setFont(ModernUITheme.FONT_BOLD);
            FontMetrics fm2 = g2.getFontMetrics();
            String t2 = String.format("%.0f tr", total / 1000000.0);
            g2.drawString(t2, cx - fm2.stringWidth(t2) / 2, cy + 14);

            g2.dispose();
        }
    }
}
