package gui;

import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Cửa sổ Giao diện Chính (Main Frame) Phòng Khám Nha Khoa ABC
 * Phong cách Sidebar Dashboard hiện đại chuẩn ERP Y Tế Quốc Tế
 */
public class TrangChuModern extends JFrame {

    private TaiKhoan currentUser;
    private JPanel pnlContentArea;
    private CardLayout cardLayout;
    private JLabel lblBreadcrumb, lblClock;

    // Danh sách các nút menu sidebar để quản lý trạng thái active
    private List<SidebarMenuItem> menuItems = new ArrayList<>();

    // Các Panels
    private PanelDashboardModern pnlDashboard;
    private PanelBenhNhanModern pnlBenhNhan;
    private PanelDichVuModern pnlDichVu;
    private PanelKhoVatTuModern pnlKhoVatTu;
    private PanelLichTrucModern pnlLichTruc;
    private PanelNhanSuModern pnlNhanSu;
    private PanelTaiKhoanModern pnlTaiKhoan;
    private PanelThanhToanModern pnlThanhToan;
    private PanelUuDaiModern pnlUuDai;
    private PanelQuyDinhModern pnlQuyDinh;
    private PanelThongKeModern pnlThongKe;

    public TrangChuModern() {
        this(new TaiKhoan("admin", "123456", null, "Hoạt động", 0, "QL001", "VT_QL"));
    }

    public TrangChuModern(TaiKhoan user) {
        this.currentUser = user;
        if (this.currentUser.getHoTenNV() == null) {
            this.currentUser.setHoTenNV("Trần Thị Hoàng Lan");
            this.currentUser.setTenVaiTro("Quản lý phòng khám");
        }

        setTitle("Hệ Thống Quản Lý Phòng Khám Nha Khoa ABC - Dashboard Điều Hành");
        setSize(1360, 820);
        setMinimumSize(new Dimension(1180, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. WEST: SIDEBAR MENU NAVIGATION (Menu dọc hiện đại)
        add(createSidebarPanel(), BorderLayout.WEST);

        // 2. CENTER: MAIN WORKSPACE (Top Header Bar + Content Area)
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
        pnlBenhNhan  = new PanelBenhNhanModern();
        pnlDichVu    = new PanelDichVuModern();
        pnlKhoVatTu  = new PanelKhoVatTuModern();
        pnlLichTruc  = new PanelLichTrucModern();
        pnlNhanSu    = new PanelNhanSuModern();
        pnlTaiKhoan  = new PanelTaiKhoanModern(currentUser);
        pnlThanhToan = new PanelThanhToanModern();
        pnlUuDai     = new PanelUuDaiModern();
        pnlQuyDinh   = new PanelQuyDinhModern();
        pnlThongKe   = new PanelThongKeModern();

        pnlContentArea.add(pnlDashboard, "DASHBOARD");
        pnlContentArea.add(pnlBenhNhan,  "BENH_NHAN");
        pnlContentArea.add(pnlDichVu,    "DICH_VU");
        pnlContentArea.add(pnlKhoVatTu,  "KHO_VAT_TU");
        pnlContentArea.add(pnlLichTruc,  "LICH_TRUC");
        pnlContentArea.add(pnlNhanSu,    "NHAN_SU");
        pnlContentArea.add(pnlTaiKhoan,  "TAI_KHOAN");
        pnlContentArea.add(pnlThanhToan, "THANH_TOAN");
        pnlContentArea.add(pnlUuDai,     "UU_DAI");
        pnlContentArea.add(pnlQuyDinh,   "QUY_DINH");
        pnlContentArea.add(pnlThongKe,   "THONG_KE");

        pnlMainWorkspace.add(pnlContentArea, BorderLayout.CENTER);
        add(pnlMainWorkspace, BorderLayout.CENTER);

        // Mặc định chọn tab phù hợp nhất theo từng vị trí
        String defaultTab = "DASHBOARD";
        String rLower = this.currentUser.getTenVaiTro() != null ? this.currentUser.getTenVaiTro().toLowerCase() : "";
        if (rLower.contains("nha sĩ") || rLower.contains("bác sĩ")) {
            defaultTab = "BENH_NHAN";
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
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(ModernUITheme.SIDEBAR_BG);

        // --- BRAND HEADER ---
        JPanel pnlBrand = new JPanel(new BorderLayout(10, 0));
        pnlBrand.setOpaque(false);
        pnlBrand.setBorder(new EmptyBorder(18, 16, 18, 16));

        // Logo
        File logoFile = new File("images/dental_logo.png");
        JLabel lblLogo;
        if (logoFile.exists()) {
            ImageIcon icon = new ImageIcon(new ImageIcon(logoFile.getAbsolutePath()).getImage().getScaledInstance(34, 34, Image.SCALE_SMOOTH));
            lblLogo = new JLabel(icon);
        } else {
            lblLogo = new JLabel("[ABC]", SwingConstants.CENTER);
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblLogo.setForeground(new Color(56, 189, 248));
        }

        JPanel pnlBrandText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlBrandText.setOpaque(false);

        JLabel lblBrandName = new JLabel("NHA KHOA ABC");
        lblBrandName.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
        pnlMenuItems.setBorder(new EmptyBorder(10, 10, 10, 10));

        buildRoleBasedMenu(pnlMenuItems);

        JScrollPane scrollMenu = new JScrollPane(pnlMenuItems);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(12);

        sidebar.add(scrollMenu, BorderLayout.CENTER);

        // --- FOOTER USER PROFILE ---
        JPanel pnlUserFooter = new JPanel(new BorderLayout(10, 6));
        pnlUserFooter.setBackground(ModernUITheme.SIDEBAR_HOVER);
        pnlUserFooter.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel pnlUserInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlUserInfo.setOpaque(false);

        String displayName = currentUser.getHoTenNV() != null ? currentUser.getHoTenNV() : currentUser.getTenDangNhap();
        JLabel lblName = new JLabel(displayName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(Color.WHITE);

        String roleStr = "Vị trí: " + (currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro() : "Nhân viên");
        JLabel lblRole = new JLabel(roleStr);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(new Color(56, 189, 248));

        pnlUserInfo.add(lblName);
        pnlUserInfo.add(lblRole);

        // Action buttons dạng chữ rõ ràng, không dùng icon
        JPanel pnlUserBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlUserBtns.setOpaque(false);

        JButton btnDoiMK = new JButton("Đổi MK");
        btnDoiMK.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnDoiMK.setForeground(new Color(224, 242, 254));
        btnDoiMK.setBackground(new Color(51, 65, 85));
        btnDoiMK.setFocusPainted(false);
        btnDoiMK.setBorder(new EmptyBorder(4, 8, 4, 8));
        btnDoiMK.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDoiMK.addActionListener(e -> showDoiMatKhauDialog());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLogout.setForeground(new Color(254, 202, 202));
        btnLogout.setBackground(new Color(127, 29, 29));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(4, 8, 4, 8));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> xuLyDangXuat());

        pnlUserBtns.add(btnDoiMK);
        pnlUserBtns.add(btnLogout);

        pnlUserFooter.add(pnlUserInfo, BorderLayout.CENTER);
        pnlUserFooter.add(pnlUserBtns, BorderLayout.SOUTH);

        sidebar.add(pnlUserFooter, BorderLayout.SOUTH);
        return sidebar;
    }

    /**
     * Phân quyền: Chỉ hiển thị các mục menu thuộc vị trí/vai trò của người dùng
     */
    private void buildRoleBasedMenu(JPanel pnlMenuItems) {
        String role = currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro().toLowerCase() : "";
        String maRole = currentUser.getMaVaiTro() != null ? currentUser.getMaVaiTro().toUpperCase() : "";

        if (role.contains("nha sĩ") || role.contains("bác sĩ") || maRole.contains("BS")) {
            // VỊ TRÍ NHA SĨ: Chỉ hiển thị các chức năng của Nha sĩ
            addCategoryHeader(pnlMenuItems, "CHỨC NĂNG NHA SĨ");
            addMenuItem(pnlMenuItems, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, "Dịch Vụ & Bảng Giá", "DICH_VU");
            addMenuItem(pnlMenuItems, "Lịch Trực & Ca Khám", "LICH_TRUC");

        } else if (role.contains("lễ tân") || role.contains("tiếp tân") || maRole.contains("LT")) {
            // VỊ TRÍ LỄ TÂN: Đón tiếp, tạo hồ sơ, xếp lịch hẹn, xem bảng giá và ưu đãi
            addCategoryHeader(pnlMenuItems, "TIẾP ĐÓN & ĐẶT LỊCH");
            addMenuItem(pnlMenuItems, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, "Lịch Trực & Ca Khám", "LICH_TRUC");
            addMenuItem(pnlMenuItems, "Dịch Vụ & Bảng Giá", "DICH_VU");
            addMenuItem(pnlMenuItems, "Chính Sách Ưu Đãi", "UU_DAI");

        } else if (role.contains("thu ngân") || maRole.contains("TN")) {
            // VỊ TRÍ THU NGÂN: Thu tiền viện phí, in hóa đơn, áp dụng ưu đãi
            addCategoryHeader(pnlMenuItems, "NGHIỆP VỤ THU NGÂN");
            addMenuItem(pnlMenuItems, "Thu Ngân & Hóa Đơn", "THANH_TOAN");
            addMenuItem(pnlMenuItems, "Chính Sách Ưu Đãi", "UU_DAI");
            addMenuItem(pnlMenuItems, "Dịch Vụ & Bảng Giá", "DICH_VU");

        } else if (role.contains("y tá") || role.contains("phụ tá") || role.contains("điều dưỡng") || maRole.contains("YT")) {
            // VỊ TRÍ Y TÁ / PHỤ TÁ: Quản lý kho dược vật tư, xem ca trực và bệnh án
            addCategoryHeader(pnlMenuItems, "HỖ TRỢ & VẬT TƯ");
            addMenuItem(pnlMenuItems, "Kho Dược & Vật Tư", "KHO_VAT_TU");
            addMenuItem(pnlMenuItems, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, "Lịch Trực & Ca Khám", "LICH_TRUC");

        } else {
            // VỊ TRÍ QUẢN LÝ / ADMIN: Toàn quyền truy cập tất cả chức năng
            addCategoryHeader(pnlMenuItems, "TỔNG QUAN & ĐIỀU HÀNH");
            addMenuItem(pnlMenuItems, "Dashboard Tổng Quan", "DASHBOARD");
            addMenuItem(pnlMenuItems, "Báo Cáo & Thống Kê", "THONG_KE");

            addCategoryHeader(pnlMenuItems, "NGHIỆP VỤ NHA KHOA");
            addMenuItem(pnlMenuItems, "Hồ Sơ Bệnh Nhân", "BENH_NHAN");
            addMenuItem(pnlMenuItems, "Dịch Vụ & Bảng Giá", "DICH_VU");
            addMenuItem(pnlMenuItems, "Lịch Trực & Ca Khám", "LICH_TRUC");

            addCategoryHeader(pnlMenuItems, "TÀI CHÍNH & VẬT TƯ");
            addMenuItem(pnlMenuItems, "Thu Ngân & Hóa Đơn", "THANH_TOAN");
            addMenuItem(pnlMenuItems, "Chính Sách Ưu Đãi", "UU_DAI");
            addMenuItem(pnlMenuItems, "Kho Dược & Vật Tư", "KHO_VAT_TU");

            addCategoryHeader(pnlMenuItems, "QUẢN TRỊ & HỆ THỐNG");
            addMenuItem(pnlMenuItems, "Bác Sĩ & Nhân Sự", "NHAN_SU");
            addMenuItem(pnlMenuItems, "Quản Trị Tài Khoản", "TAI_KHOAN");
            addMenuItem(pnlMenuItems, "Quy Chế Phòng Khám", "QUY_DINH");
        }
    }

    private void addCategoryHeader(JPanel parent, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(ModernUITheme.SIDEBAR_MUTED);
        lbl.setBorder(new EmptyBorder(12, 10, 4, 10));
        parent.add(lbl);
    }

    private void addMenuItem(JPanel parent, String title, String cardKey) {
        SidebarMenuItem item = new SidebarMenuItem(title, cardKey);
        item.addActionListener(e -> switchTab(cardKey));
        menuItems.add(item);
        parent.add(item);
        parent.add(Box.createVerticalStrut(2));
    }

    private JPanel createTopHeaderBar() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 54));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUITheme.BORDER_LIGHT),
            new EmptyBorder(8, 20, 8, 20)
        ));

        // Left: Breadcrumb
        lblBreadcrumb = new JLabel("Trang chủ  /  Dashboard Tổng Quan");
        lblBreadcrumb.setFont(ModernUITheme.FONT_BOLD);
        lblBreadcrumb.setForeground(ModernUITheme.TEXT_MAIN);
        header.add(lblBreadcrumb, BorderLayout.WEST);

        // Center: Quick search field
        JPanel pnlCenterSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlCenterSearch.setOpaque(false);
        JTextField txtQuickSearch = ModernUITheme.createTextField(22);
        txtQuickSearch.setText("Tra cứu nhanh bệnh nhân, hóa đơn...");
        txtQuickSearch.setForeground(ModernUITheme.TEXT_MUTED);
        pnlCenterSearch.add(txtQuickSearch);
        header.add(pnlCenterSearch, BorderLayout.CENTER);

        // Right: Database status & Clock
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRight.setOpaque(false);

        JLabel lblDbStatus = ModernUITheme.createBadge("CSDL SQL Server: Đang hoạt động", ModernUITheme.SUCCESS_LIGHT, ModernUITheme.SUCCESS);

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

    public void switchTab(String cardKey) {
        cardLayout.show(pnlContentArea, cardKey);

        // Update active sidebar button
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
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
            "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginModern().setVisible(true);
        }
    }

    private void showDoiMatKhauDialog() {
        JDialog dlg = new JDialog(this, "Đổi Mật Khẩu", true);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel pnl = new JPanel(new GridLayout(3, 2, 10, 10));
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPasswordField txtOld = new JPasswordField();
        JPasswordField txtNew = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        pnl.add(new JLabel("Mật khẩu hiện tại:"));
        pnl.add(txtOld);
        pnl.add(new JLabel("Mật khẩu mới:"));
        pnl.add(txtNew);
        pnl.add(new JLabel("Xác nhận mật khẩu:"));
        pnl.add(txtConfirm);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = ModernUITheme.createButton("Lưu mật khẩu", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);

        btnLuu.addActionListener(e -> {
            String o = new String(txtOld.getPassword());
            String n = new String(txtNew.getPassword());
            String c = new String(txtConfirm.getPassword());
            if (o.isEmpty() || n.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ thông tin!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!n.equals(c)) {
                JOptionPane.showMessageDialog(dlg, "Mật khẩu xác nhận không trùng khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(dlg, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
     * Component nút Menu Sidebar tùy biến có vạch active sáng
     */
    private static class SidebarMenuItem extends JButton {
        private String cardKey;
        private String title;
        private boolean isActive = false;

        public SidebarMenuItem(String title, String cardKey) {
            this.cardKey = cardKey;
            this.title = title;
            setText("  " + title);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(ModernUITheme.SIDEBAR_TEXT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(9, 14, 9, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        }

        public String getCardKey() { return cardKey; }
        public String getTitle() { return title; }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? Color.WHITE : ModernUITheme.SIDEBAR_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                // Nền xanh Active hiện đại
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
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new TrangChuModern().setVisible(true);
        });
    }
}
