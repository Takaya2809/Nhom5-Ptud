package gui;

import dao.BenhNhanDAO;
import dao.HoaDonDAO;
import dao.LichHenDAO;
import entity.BenhNhan;
import entity.HoaDonKham;
import entity.LichHen;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrangChuModern extends JFrame {

    private TaiKhoan currentUser;
    private JPanel pnlContentArea;
    private CardLayout cardLayout;
    private JLabel lblBreadcrumb, lblClock;

    private List<SidebarMenuItem> menuItems = new ArrayList<>();

    // Các Panels
    private PanelDashboardModern pnlDashboard;
    private PanelBenhNhanModern pnlBenhNhan;
    private PanelDichVuModern pnlDichVu;
    private PanelKhoVatTuModern pnlKhoVatTu;
    private PanelLichTrucModern pnlLichTruc;
    private PanelLichHenModern pnlLichHen;
    private PanelKhamDieuTriModern pnlKhamDieuTri;
    private PanelNhanSuModern pnlNhanSu;
    private PanelTaiKhoanModern pnlTaiKhoan;
    private PanelThanhToanModern pnlThanhToan;
    private PanelUuDaiModern pnlUuDai;
    private PanelQuyDinhModern pnlQuyDinh;
    private PanelThongKeModern pnlThongKe;

    // Các DAO phục vụ tìm kiếm nhanh trên Topbar
    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();

    private JTextField txtQuickSearch;
    private JPopupMenu popupSearch;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public TrangChuModern() {
        this(new TaiKhoan("admin", "123456", null, "Hoạt động", 0, "QL001", "VT_QL"));
    }

    public TrangChuModern(TaiKhoan user) {
        this.currentUser = user;
        if (this.currentUser.getHoTenNV() == null) {
            this.currentUser.setHoTenNV("Trần Thị Hoàng Lan");
            this.currentUser.setTenVaiTro("Quản lý");
        }

        setTitle("Hệ Thống Quản Lý Phòng Khám Nha Khoa ABC");
        setSize(1380, 840);
        setMinimumSize(new Dimension(1180, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. WEST: SIDEBAR MENU NAVIGATION
        add(createSidebarPanel(), BorderLayout.WEST);

        // 2. CENTER: MAIN WORKSPACE
        JPanel pnlMainWorkspace = new JPanel(new BorderLayout());
        pnlMainWorkspace.setBackground(ModernUITheme.BG_MAIN);

        // A. Top Header Bar
        pnlMainWorkspace.add(createTopHeaderBar(), BorderLayout.NORTH);

        // B. Content Area with CardLayout
        cardLayout = new CardLayout();
        pnlContentArea = new JPanel(cardLayout);
        pnlContentArea.setBackground(ModernUITheme.BG_MAIN);

        // Khởi tạo các panels
        pnlDashboard = new PanelDashboardModern(this::switchTab);
        pnlBenhNhan = new PanelBenhNhanModern();
        pnlDichVu = new PanelDichVuModern();
        pnlKhoVatTu = new PanelKhoVatTuModern();
        pnlLichTruc = new PanelLichTrucModern();
        pnlLichHen = new PanelLichHenModern();
        pnlKhamDieuTri = new PanelKhamDieuTriModern(currentUser);
        pnlNhanSu = new PanelNhanSuModern();
        pnlTaiKhoan = new PanelTaiKhoanModern(currentUser);
        pnlThanhToan = new PanelThanhToanModern();
        pnlUuDai = new PanelUuDaiModern();
        pnlQuyDinh = new PanelQuyDinhModern();
        pnlThongKe = new PanelThongKeModern();

        pnlContentArea.add(pnlDashboard, "DASHBOARD");
        pnlContentArea.add(pnlBenhNhan, "BENH_NHAN");
        pnlContentArea.add(pnlDichVu, "DICH_VU");
        pnlContentArea.add(pnlKhoVatTu, "KHO_VAT_TU");
        pnlContentArea.add(pnlLichTruc, "LICH_TRUC");
        pnlContentArea.add(pnlLichHen, "LICH_HEN");
        pnlContentArea.add(pnlKhamDieuTri, "NHA_SI");
        pnlContentArea.add(pnlNhanSu, "NHAN_SU");
        pnlContentArea.add(pnlTaiKhoan, "TAI_KHOAN");
        pnlContentArea.add(pnlThanhToan, "THANH_TOAN");
        pnlContentArea.add(pnlUuDai, "UU_DAI");
        pnlContentArea.add(pnlQuyDinh, "QUY_DINH");
        pnlContentArea.add(pnlThongKe, "THONG_KE");

        pnlMainWorkspace.add(pnlContentArea, BorderLayout.CENTER);
        add(pnlMainWorkspace, BorderLayout.CENTER);

        // Mặc định chọn tab phù hợp theo từng vị trí
        String defaultTab = "DASHBOARD";
        String rLower = this.currentUser.getTenVaiTro() != null ? this.currentUser.getTenVaiTro().toLowerCase() : "";
        if (rLower.contains("nha sĩ") || rLower.contains("bác sĩ")) {
            defaultTab = "NHA_SI";
        } else if (rLower.contains("lễ tân") || rLower.contains("tiếp tân")) {
            defaultTab = "BENH_NHAN";
        } else if (rLower.contains("thu ngân")) {
            defaultTab = "THANH_TOAN";
        } else if (rLower.contains("y tá") || rLower.contains("phụ tá")) {
            defaultTab = "KHO_VAT_TU";
        }

        switchTab(defaultTab);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(256, 0));
        sidebar.setBackground(ModernUITheme.SIDEBAR_BG);

        // --- BRAND HEADER ---
        JPanel pnlBrand = new JPanel(new BorderLayout(12, 0));
        pnlBrand.setOpaque(false);
        pnlBrand.setBorder(new EmptyBorder(20, 18, 20, 18));

        // Logo ABC
        JLabel lblLogo = null;
        String[] logoPaths = {
                "PhanMem_PhongKham_NhaKhoa/images/dental_logo.png",
                "images/dental_logo.png",
                "../images/dental_logo.png"
        };
        for (String p : logoPaths) {
            File f = new File(p);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(
                        new ImageIcon(f.getAbsolutePath()).getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH));
                lblLogo = new JLabel(icon);
                break;
            }
        }
        if (lblLogo == null) {
            lblLogo = new JLabel(new AppIcon(AppIcon.Type.TOOTH, 30, new Color(56, 189, 248)));
        }

        JPanel pnlBrandText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlBrandText.setOpaque(false);

        JLabel lblBrandName = new JLabel("NHA KHOA ABC");
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBrandName.setForeground(Color.WHITE);

        JLabel lblBrandSub = new JLabel("HỆ THỐNG QUẢN TRỊ v2.0");
        lblBrandSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBrandSub.setForeground(new Color(56, 189, 248));

        pnlBrandText.add(lblBrandName);
        pnlBrandText.add(lblBrandSub);

        pnlBrand.add(lblLogo, BorderLayout.WEST);
        pnlBrand.add(pnlBrandText, BorderLayout.CENTER);
        sidebar.add(pnlBrand, BorderLayout.NORTH);

        // --- MENU ITEMS THEO PHÂN QUYỀN VỊ TRÍ ---
        JPanel pnlMenuItems = new JPanel();
        pnlMenuItems.setLayout(new BoxLayout(pnlMenuItems, BoxLayout.Y_AXIS));
        pnlMenuItems.setOpaque(false);
        pnlMenuItems.setBorder(new EmptyBorder(6, 12, 10, 12));

        buildRoleBasedMenu(pnlMenuItems);

        JScrollPane scrollMenu = new JScrollPane(pnlMenuItems);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(14);

        sidebar.add(scrollMenu, BorderLayout.CENTER);

        // --- FOOTER USER PROFILE ---
        JPanel pnlUserFooter = new JPanel(new BorderLayout(10, 8));
        pnlUserFooter.setBackground(ModernUITheme.SIDEBAR_HOVER);
        pnlUserFooter.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel pnlUserTop = new JPanel(new BorderLayout(10, 0));
        pnlUserTop.setOpaque(false);

        // Avatar icon
        JLabel lblAvatar = new JLabel(new AppIcon(AppIcon.Type.USER, 22, new Color(56, 189, 248)));
        pnlUserTop.add(lblAvatar, BorderLayout.WEST);

        JPanel pnlUserInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlUserInfo.setOpaque(false);

        String displayName = currentUser.getHoTenNV() != null ? currentUser.getHoTenNV() : currentUser.getTenDangNhap();
        JLabel lblName = new JLabel(displayName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(Color.WHITE);

        String roleStr = "Quyền: " + (currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro() : "Nhân viên");
        JLabel lblRole = new JLabel(roleStr);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(new Color(148, 163, 184));

        pnlUserInfo.add(lblName);
        pnlUserInfo.add(lblRole);
        pnlUserTop.add(pnlUserInfo, BorderLayout.CENTER);

        // Nút Đổi MK & Đăng xuất
        JPanel pnlUserBtns = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlUserBtns.setOpaque(false);

        JButton btnDoiMK = ModernUITheme.createOutlineIconButton(
                new AppIcon(AppIcon.Type.LOCK, 13, new Color(224, 242, 254)), "Đổi MK", new Color(100, 116, 139));
        btnDoiMK.setFont(ModernUITheme.FONT_TINY);
        btnDoiMK.setBorder(new EmptyBorder(5, 8, 5, 8));
        btnDoiMK.addActionListener(e -> showDoiMatKhauDialog());

        JButton btnLogout = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CLOSE, 13, Color.WHITE),
                "Đăng xuất", new Color(185, 28, 28), Color.WHITE);
        btnLogout.setFont(ModernUITheme.FONT_TINY);
        btnLogout.setBorder(new EmptyBorder(5, 8, 5, 8));
        btnLogout.addActionListener(e -> xuLyDangXuat());

        pnlUserBtns.add(btnDoiMK);
        pnlUserBtns.add(btnLogout);

        pnlUserFooter.add(pnlUserTop, BorderLayout.NORTH);
        pnlUserFooter.add(pnlUserBtns, BorderLayout.SOUTH);

        sidebar.add(pnlUserFooter, BorderLayout.SOUTH);
        return sidebar;
    }

    /**
     * Phân quyền menu khoa học:
     * Quản lý: Full quyền giám sát điều hành và cấu hình hệ thống
     * Các vị trí khác: Giới hạn theo đúng phân hệ nghiệp vụ
     */
    private void buildRoleBasedMenu(JPanel pnlMenuItems) {
        String role = currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro().toLowerCase() : "";
        String maRole = currentUser.getMaVaiTro() != null ? currentUser.getMaVaiTro().toUpperCase() : "";

        if (role.contains("nha sĩ") || role.contains("bác sĩ") || maRole.contains("BS")) {
            addCategoryHeader(pnlMenuItems, "CHỨC NĂNG NHA SĨ");
            addMenuItem(pnlMenuItems, AppIcon.Type.PATIENT, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.PATIENT, "Khám & Điều Trị", "NHA_SI");
            addMenuItem(pnlMenuItems, AppIcon.Type.SERVICE, "Dịch Vụ & Bảng Giá", "DICH_VU");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Hẹn", "LICH_HEN");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Trực & Ca Khám", "LICH_TRUC");

        } else if (role.contains("lễ tân") || role.contains("tiếp tân") || maRole.contains("LT")) {
            addCategoryHeader(pnlMenuItems, "TIẾP ĐÓN & ĐẶT LỊCH");
            addMenuItem(pnlMenuItems, AppIcon.Type.PATIENT, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Hẹn", "LICH_HEN");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Trực & Ca Khám", "LICH_TRUC");
            addMenuItem(pnlMenuItems, AppIcon.Type.SERVICE, "Dịch Vụ & Bảng Giá", "DICH_VU");
            addMenuItem(pnlMenuItems, AppIcon.Type.DISCOUNT, "Chính Sách Ưu Đãi", "UU_DAI");

        } else if (role.contains("thu ngân") || maRole.contains("TN")) {
            addCategoryHeader(pnlMenuItems, "NGHIỆP VỤ THU NGÂN");
            addMenuItem(pnlMenuItems, AppIcon.Type.PAYMENT, "Thu Ngân & Hóa Đơn", "THANH_TOAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.DISCOUNT, "Chính Sách Ưu Đãi", "UU_DAI");
            addMenuItem(pnlMenuItems, AppIcon.Type.SERVICE, "Dịch Vụ & Bảng Giá", "DICH_VU");

        } else if (role.contains("y tá") || role.contains("phụ tá") || role.contains("điều dưỡng")
                || maRole.contains("YT")) {
            addCategoryHeader(pnlMenuItems, "HỖ TRỢ & VẬT TƯ");
            addMenuItem(pnlMenuItems, AppIcon.Type.INVENTORY, "Kho Dược & Vật Tư", "KHO_VAT_TU");
            addMenuItem(pnlMenuItems, AppIcon.Type.PATIENT, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Trực & Ca Khám", "LICH_TRUC");

        } else {
            // VỊ TRÍ QUẢN LÝ / ADMIN: Toàn quyền truy cập tất cả chức năng
            addCategoryHeader(pnlMenuItems, "TỔNG QUAN & ĐIỀU HÀNH");
            addMenuItem(pnlMenuItems, AppIcon.Type.DASHBOARD, "Dashboard Tổng Quan", "DASHBOARD");
            addMenuItem(pnlMenuItems, AppIcon.Type.REPORT, "Báo Cáo & Thống Kê", "THONG_KE");

            addCategoryHeader(pnlMenuItems, "NGHIỆP VỤ NHA KHOA");
            addMenuItem(pnlMenuItems, AppIcon.Type.PATIENT, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.SERVICE, "Dịch Vụ & Bảng Giá", "DICH_VU");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Trực & Ca Khám", "LICH_TRUC");
            addMenuItem(pnlMenuItems, AppIcon.Type.CALENDAR, "Lịch Hẹn", "LICH_HEN");

            addCategoryHeader(pnlMenuItems, "TÀI CHÍNH & VẬT TƯ");
            addMenuItem(pnlMenuItems, AppIcon.Type.PAYMENT, "Thu Ngân & Hóa Đơn", "THANH_TOAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.DISCOUNT, "Chính Sách Ưu Đãi", "UU_DAI");
            addMenuItem(pnlMenuItems, AppIcon.Type.INVENTORY, "Kho Dược & Vật Tư", "KHO_VAT_TU");

            addCategoryHeader(pnlMenuItems, "QUẢN TRỊ & HỆ THỐNG");
            addMenuItem(pnlMenuItems, AppIcon.Type.STAFF, "Bác Sĩ & Nhân Sự", "NHAN_SU");
            addMenuItem(pnlMenuItems, AppIcon.Type.ACCOUNT, "Quản Trị Tài Khoản", "TAI_KHOAN");
            addMenuItem(pnlMenuItems, AppIcon.Type.SETTINGS, "Cài Đặt & Quy Chế", "QUY_DINH");
        }
    }

    private void addCategoryHeader(JPanel parent, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(ModernUITheme.SIDEBAR_MUTED);
        lbl.setBorder(new EmptyBorder(12, 8, 4, 8));
        parent.add(lbl);
    }

    private void addMenuItem(JPanel parent, AppIcon.Type iconType, String title, String cardKey) {
        SidebarMenuItem item = new SidebarMenuItem(iconType, title, cardKey);
        item.addActionListener(e -> switchTab(cardKey));
        menuItems.add(item);
        parent.add(item);
        parent.add(Box.createVerticalStrut(2));
    }

    /**
     * TOPBAR CHUẨN HÓA VỚI THANH TÌM KIẾM THÔNG MINH
     */
    private JPanel createTopHeaderBar() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUITheme.BORDER_LIGHT),
                new EmptyBorder(8, 20, 8, 20)));

        // 1. Bên trái: Breadcrumb hiển thị vị trí hiện tại
        JPanel pnlBreadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBreadcrumb.setOpaque(false);
        JLabel lblHomeIcon = new JLabel(new AppIcon(AppIcon.Type.DASHBOARD, 16, ModernUITheme.PRIMARY));
        lblBreadcrumb = new JLabel("Trang chủ  /  Dashboard Tổng Quan");
        lblBreadcrumb.setFont(ModernUITheme.FONT_BOLD);
        lblBreadcrumb.setForeground(ModernUITheme.TEXT_MAIN);
        pnlBreadcrumb.add(lblHomeIcon);
        pnlBreadcrumb.add(lblBreadcrumb);
        header.add(pnlBreadcrumb, BorderLayout.WEST);

        // 2. Ở giữa: Thanh tìm kiếm nhanh hoạt động thực tế
        JPanel pnlCenterSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        pnlCenterSearch.setOpaque(false);

        JPanel pnlSearchBox = new JPanel(new BorderLayout(6, 0));
        pnlSearchBox.setBackground(ModernUITheme.BG_MAIN);
        pnlSearchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(4, 10, 4, 8)));
        pnlSearchBox.setPreferredSize(new Dimension(360, 36));

        JLabel lblSearchIco = new JLabel(new AppIcon(AppIcon.Type.SEARCH, 15, ModernUITheme.TEXT_MUTED));
        pnlSearchBox.add(lblSearchIco, BorderLayout.WEST);

        txtQuickSearch = new JTextField("Tra cứu nhanh bệnh nhân, hóa đơn, lịch hẹn...");
        txtQuickSearch.setFont(ModernUITheme.FONT_REGULAR);
        txtQuickSearch.setForeground(ModernUITheme.TEXT_MUTED);
        txtQuickSearch.setBorder(null);
        txtQuickSearch.setOpaque(false);

        // Xử lý placeholder
        txtQuickSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtQuickSearch.getText().equals("Tra cứu nhanh bệnh nhân, hóa đơn, lịch hẹn...")) {
                    txtQuickSearch.setText("");
                    txtQuickSearch.setForeground(ModernUITheme.TEXT_MAIN);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtQuickSearch.getText().trim().isEmpty()) {
                    txtQuickSearch.setText("Tra cứu nhanh bệnh nhân, hóa đơn, lịch hẹn...");
                    txtQuickSearch.setForeground(ModernUITheme.TEXT_MUTED);
                }
            }
        });

        // Nhấn Enter để tìm kiếm
        txtQuickSearch.addActionListener(e -> thucHienTraCuuNhanh());

        JButton btnSearchAction = new JButton(new AppIcon(AppIcon.Type.SEARCH, 14, ModernUITheme.PRIMARY));
        btnSearchAction.setToolTipText("Nhấn Enter hoặc click để tìm kiếm");
        btnSearchAction.setContentAreaFilled(false);
        btnSearchAction.setFocusPainted(false);
        btnSearchAction.setBorder(new EmptyBorder(2, 4, 2, 4));
        btnSearchAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearchAction.addActionListener(e -> thucHienTraCuuNhanh());

        pnlSearchBox.add(txtQuickSearch, BorderLayout.CENTER);
        pnlSearchBox.add(btnSearchAction, BorderLayout.EAST);
        pnlCenterSearch.add(pnlSearchBox);
        header.add(pnlCenterSearch, BorderLayout.CENTER);

        // Khởi tạo popup kết quả tìm kiếm
        popupSearch = new JPopupMenu();
        popupSearch.setBackground(Color.WHITE);
        popupSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 6, 6, 6)));

        // 3. Bên phải: Trạng thái SQL Server & Đồng hồ hệ thống
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        pnlRight.setOpaque(false);

        boolean dbOk = connectDB.ConnectDB.getConnection() != null;
        JLabel lblDbStatus = ModernUITheme.createBadge(
                dbOk ? "SQL Server: Đã kết nối" : "SQL Server: Ngoại tuyến",
                dbOk ? ModernUITheme.SUCCESS_LIGHT : ModernUITheme.WARNING_LIGHT,
                dbOk ? ModernUITheme.SUCCESS : ModernUITheme.WARNING);

        lblClock = new JLabel();
        lblClock.setFont(ModernUITheme.FONT_SMALL);
        lblClock.setForeground(ModernUITheme.TEXT_MUTED);

        Timer timer = new Timer(1000, e -> {
            lblClock.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss").format(new Date()));
        });
        timer.start();

        pnlRight.add(lblDbStatus);
        pnlRight.add(lblClock);

        header.add(pnlRight, BorderLayout.EAST);
        return header;
    }

    /**
     * THỰC HIỆN TRA CỨU NHANH BỆNH NHÂN, HÓA ĐƠN & LỊCH HẸN TỪ CSDL
     */
    private void thucHienTraCuuNhanh() {
        String keyword = txtQuickSearch.getText().trim();
        if (keyword.isEmpty() || keyword.equals("Tra cứu nhanh bệnh nhân, hóa đơn, lịch hẹn...")) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã bệnh nhân, tên, số điện thoại hoặc mã hóa đơn để tra cứu!",
                    "Nhắc nhở", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        popupSearch.removeAll();
        popupSearch.setPreferredSize(new Dimension(460, 0));

        // Tìm kiếm đa luồng / async nhẹ để không khóa EDT
        SwingWorker<SearchResults, Void> worker = new SwingWorker<>() {
            @Override
            protected SearchResults doInBackground() {
                List<BenhNhan> bnList = benhNhanDAO.timKiem(keyword);
                List<HoaDonKham> hdList = hoaDonDAO.timKiem(keyword);
                List<LichHen> lhList = lichHenDAO.timKiem(keyword);
                return new SearchResults(bnList, hdList, lhList);
            }

            @Override
            protected void done() {
                try {
                    SearchResults res = get();
                    hienThiKetQuaTraCuu(keyword, res);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TrangChuModern.this,
                            "Đã xảy ra sự cố trong quá trình tra cứu: " + ex.getMessage(),
                            "Lỗi tra cứu", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private static class SearchResults {
        List<BenhNhan> benhNhans;
        List<HoaDonKham> hoaDons;
        List<LichHen> lichHens;

        SearchResults(List<BenhNhan> b, List<HoaDonKham> h, List<LichHen> l) {
            this.benhNhans = b != null ? b : new ArrayList<>();
            this.hoaDons = h != null ? h : new ArrayList<>();
            this.lichHens = l != null ? l : new ArrayList<>();
        }

        boolean isEmpty() {
            return benhNhans.isEmpty() && hoaDons.isEmpty() && lichHens.isEmpty();
        }
    }

    private void hienThiKetQuaTraCuu(String keyword, SearchResults res) {
        popupSearch.removeAll();

        if (res.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("Không tìm thấy kết quả phù hợp cho: \"" + keyword + "\"");
            emptyItem.setFont(ModernUITheme.FONT_REGULAR);
            emptyItem.setForeground(ModernUITheme.TEXT_MUTED);
            emptyItem.setEnabled(false);
            popupSearch.add(emptyItem);
        } else {
            // 1. NHÓM BỆNH NHÂN
            if (!res.benhNhans.isEmpty()) {
                popupSearch.add(
                        createSearchHeader("HỒ SƠ BỆNH NHÂN (" + res.benhNhans.size() + ")", ModernUITheme.PRIMARY));
                int limit = Math.min(3, res.benhNhans.size());
                for (int i = 0; i < limit; i++) {
                    BenhNhan bn = res.benhNhans.get(i);
                    String title = bn.getMaBN() + " - " + bn.getHoTen();
                    String sub = "SĐT: " + bn.getSoDienThoai() + " | " + bn.getGioiTinh() + " | Thẻ: "
                            + bn.getLoaiKhach();
                    JMenuItem item = createSearchResultItem(AppIcon.Type.PATIENT, title, sub, () -> {
                        if (kiemTraQuyenTruyCap("BENH_NHAN")) {
                            switchTab("BENH_NHAN");
                            pnlBenhNhan.selectBenhNhan(bn.getMaBN());
                        }
                    });
                    popupSearch.add(item);
                }
            }

            // 2. NHÓM HÓA ĐƠN
            if (!res.hoaDons.isEmpty()) {
                popupSearch.add(createSearchHeader("HÓA ĐƠN KHÁM (" + res.hoaDons.size() + ")", ModernUITheme.SUCCESS));
                int limit = Math.min(3, res.hoaDons.size());
                for (int i = 0; i < limit; i++) {
                    HoaDonKham hd = res.hoaDons.get(i);
                    String title = "HD" + String.format("%03d", hd.getMaHD()) + " - "
                            + (hd.getTenBN() != null ? hd.getTenBN() : hd.getMaBN());
                    String sub = df.format(hd.getThanhTien()) + " | "
                            + (hd.getTenDichVu() != null ? hd.getTenDichVu() : "Dịch vụ nha khoa") + " | "
                            + hd.getTrangThai();
                    JMenuItem item = createSearchResultItem(AppIcon.Type.PAYMENT, title, sub, () -> {
                        if (kiemTraQuyenTruyCap("THANH_TOAN")) {
                            switchTab("THANH_TOAN");
                            pnlThanhToan.selectHoaDon(hd.getMaHD());
                        }
                    });
                    popupSearch.add(item);
                }
            }

            // 3. NHÓM LỊCH HẸN
            if (!res.lichHens.isEmpty()) {
                popupSearch
                        .add(createSearchHeader("LỊCH HẸN KHÁM (" + res.lichHens.size() + ")", ModernUITheme.WARNING));
                int limit = Math.min(3, res.lichHens.size());
                for (int i = 0; i < limit; i++) {
                    LichHen lh = res.lichHens.get(i);
                    String title = lh.getMaLichHen() + " - "
                            + (lh.getTenBenhNhan() != null ? lh.getTenBenhNhan() : lh.getMaBN());
                    String sub = (lh.getTenNhaSi() != null ? lh.getTenNhaSi() : "Nha sĩ") + " | "
                            + (lh.getLyDoKham() != null ? lh.getLyDoKham() : "") + " | " + lh.getTrangThai();
                    JMenuItem item = createSearchResultItem(AppIcon.Type.CALENDAR, title, sub, () -> {
                        if (kiemTraQuyenTruyCap("LICH_TRUC")) {
                            switchTab("LICH_TRUC");
                            pnlLichTruc.selectPhanCong(lh.getMaLichHen());
                        }
                    });
                    popupSearch.add(item);
                }
            }
        }

        popupSearch.show(txtQuickSearch, -20, txtQuickSearch.getHeight() + 8);
    }

    private JLabel createSearchHeader(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ModernUITheme.FONT_TINY);
        lbl.setForeground(color);
        lbl.setBorder(new EmptyBorder(6, 12, 2, 12));
        return lbl;
    }

    private JMenuItem createSearchResultItem(AppIcon.Type iconType, String title, String sub, Runnable onClick) {
        JMenuItem item = new JMenuItem();
        item.setLayout(new BorderLayout(8, 2));
        item.setBorder(new EmptyBorder(6, 10, 6, 10));
        item.setBackground(Color.WHITE);

        JLabel lblIco = new JLabel(new AppIcon(iconType, 16, ModernUITheme.PRIMARY));
        item.add(lblIco, BorderLayout.WEST);

        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 1));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ModernUITheme.FONT_BOLD);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(ModernUITheme.FONT_TINY);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);

        pnlText.add(lblTitle);
        pnlText.add(lblSub);
        item.add(pnlText, BorderLayout.CENTER);

        item.addActionListener(e -> onClick.run());
        return item;
    }

    private boolean kiemTraQuyenTruyCap(String cardKey) {
        // Kiểm tra xem menu của tài khoản có mục này không
        for (SidebarMenuItem it : menuItems) {
            if (it.getCardKey().equals(cardKey)) {
                return true;
            }
        }
        JOptionPane.showMessageDialog(this,
                "Vị trí công tác của bạn ["
                        + (currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro() : "Nhân viên") + "]\n"
                        + "không được phân quyền truy cập trực tiếp vào phân hệ này!",
                "Giới hạn quyền truy cập", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    public void switchTab(String cardKey) {
        cardLayout.show(pnlContentArea, cardKey);
        if ("NHA_SI".equals(cardKey) && pnlKhamDieuTri != null) pnlKhamDieuTri.refresh();

        // Cập nhật trạng thái active sidebar
        for (SidebarMenuItem item : menuItems) {
            boolean active = item.getCardKey().equals(cardKey);
            item.setActive(active);
            if (active) {
                lblBreadcrumb.setText("Trang chủ  /  " + item.getTitle());
            }
        }
    }

    private void xuLyDangXuat() {
        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống phòng khám?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginModern().setVisible(true);
        }
    }

    private void showDoiMatKhauDialog() {
        JDialog dlg = new JDialog(this, "Đổi Mật Khẩu Đăng Nhập", true);
        dlg.setSize(380, 270);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel pnl = new JPanel(new GridLayout(3, 2, 10, 12));
        pnl.setBorder(new EmptyBorder(22, 22, 10, 22));

        JPasswordField txtOld = new JPasswordField();
        JPasswordField txtNew = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        pnl.add(new JLabel("Mật khẩu hiện tại:"));
        pnl.add(txtOld);
        pnl.add(new JLabel("Mật khẩu mới:"));
        pnl.add(txtNew);
        pnl.add(new JLabel("Xác nhận mật khẩu:"));
        pnl.add(txtConfirm);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        JButton btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Lưu mật khẩu",
                ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);

        btnLuu.addActionListener(e -> {
            String o = new String(txtOld.getPassword());
            String n = new String(txtNew.getPassword());
            String c = new String(txtConfirm.getPassword());
            if (o.isEmpty() || n.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ thông tin mật khẩu!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!n.equals(c)) {
                JOptionPane.showMessageDialog(dlg, "Mật khẩu xác nhận không trùng khớp!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(dlg, "Đổi mật khẩu tài khoản thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });
        btnHuy.addActionListener(e -> dlg.dispose());

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);

        dlg.add(pnl, BorderLayout.CENTER);
        dlg.add(pnlBtns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    /**
     * Nút Menu Sidebar tùy biến chuẩn Y Khoa, có AppIcon vector và vạch active sáng
     */
    private static class SidebarMenuItem extends JButton {
        private String cardKey;
        private String title;
        private AppIcon.Type iconType;
        private boolean isActive = false;

        public SidebarMenuItem(AppIcon.Type iconType, String title, String cardKey) {
            this.iconType = iconType;
            this.cardKey = cardKey;
            this.title = title;
            setText("  " + title);
            setIcon(new AppIcon(iconType, 16, ModernUITheme.SIDEBAR_TEXT));
            setIconTextGap(10);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(ModernUITheme.SIDEBAR_TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }

        public String getCardKey() {
            return cardKey;
        }

        public String getTitle() {
            return title;
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? Color.WHITE : ModernUITheme.SIDEBAR_TEXT);
            setIcon(new AppIcon(iconType, 16, active ? Color.WHITE : ModernUITheme.SIDEBAR_TEXT));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(ModernUITheme.SIDEBAR_ACTIVE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));

                // Vạch chỉ báo trắng nổi bật mép trái
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(2, 6, 4, getHeight() - 12, 4, 4));
            } else if (getModel().isRollover()) {
                g2.setColor(ModernUITheme.SIDEBAR_HOVER);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new TrangChuModern().setVisible(true);
        });
    }
}
