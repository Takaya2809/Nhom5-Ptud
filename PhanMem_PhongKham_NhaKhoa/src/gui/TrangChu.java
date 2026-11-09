package gui;

import entity.BenhNhan;

import entity.HoaDonKham;

import entity.LichHen;

import entity.TaiKhoan;
import entity.NhanVien;
import entity.PhienDangNhap;
import entity.VaiTro;

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

public class TrangChu extends JFrame {

    private TaiKhoan currentUser;

    private JPanel pnlContentArea;

    private CardLayout cardLayout;

    private JLabel lblBreadcrumb, lblClock;

    private List<SidebarMenuItem> menuItems = new ArrayList<>();

    // Các Panels

    private PanelBenhNhan_DungChung pnlBenhNhan;

    private PanelKhamBenh_BacSi pnlKhamDieuTri;

    private PanelDichVu_DungChung pnlDichVu;

    private PanelKhoVatTu_DungChung pnlKhoVatTu;

    private PanelLichHen_DungChung pnlLichTruc;

    private PanelNhanSu_QuanLy pnlNhanSu;

    private PanelTaiKhoan_QuanLy pnlTaiKhoan;

    private PanelThanhToan_LeTan pnlThanhToan;

    private PanelUuDai_QuanLy pnlUuDai;

    private PanelQuyDinh_QuanLy pnlQuyDinh;

    private PanelThongKe_QuanLy pnlThongKe;

    private PanelVatTuTieuHao_YTa pnlVatTuTieuHao;

    private PanelTheoDoiKhuKhuan_YTa pnlTheoDoiKhuKhuan;

    private PanelThongKeCuoiNgay_LeTan pnlThongKeCuoiNgay;

    // Các DAO phục vụ tìm kiếm nhanh trên Topbar

    public TrangChu() {

        this(new TaiKhoan("admin", "123456", null, "Hoạt động", 0, "QL001", "VT_QL"));

    }

    public TrangChu(NhanVien nhanVien) {
        this(taoTaiKhoanTuNhanVien(nhanVien));
        if (nhanVien != null) {
            PhienDangNhap.setNhanVien(nhanVien);
        }
    }

    private static TaiKhoan taoTaiKhoanTuNhanVien(NhanVien nv) {
        if (nv == null) {
            return new TaiKhoan("admin", "123456", null, "Hoạt động", 0, "QL001", "VT_QL");
        }
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(nv.getMaNV());
        tk.setMaNV(nv.getMaNV());
        tk.setHoTenNV(nv.getHoTen());
        tk.setNhanVien(nv);
        if (nv.getVaiTro() != null) {
            tk.setTenVaiTro(nv.getVaiTro().getTenHienThi());
            tk.setMaVaiTro(nv.getVaiTro().getMaVaiTro());
        } else {
            tk.setTenVaiTro("Nhân viên");
            tk.setMaVaiTro("VT_NV");
        }
        return tk;
    }

    public VaiTro getVaiTroHienTai() {
        if (PhienDangNhap.getNhanVien() != null && PhienDangNhap.getNhanVien().getVaiTro() != null) {
            return PhienDangNhap.getNhanVien().getVaiTro();
        }
        if (currentUser != null) {
            if (currentUser.getNhanVien() != null && currentUser.getNhanVien().getVaiTro() != null) {
                return currentUser.getNhanVien().getVaiTro();
            }
            if (currentUser.getMaVaiTro() != null) {
                VaiTro vt = VaiTro.fromMa(currentUser.getMaVaiTro());
                if (vt != null)
                    return vt;
            }
            if (currentUser.getTenVaiTro() != null) {
                VaiTro vt = VaiTro.fromTen(currentUser.getTenVaiTro());
                if (vt != null)
                    return vt;
            }
            if (currentUser.getMaNV() != null) {
                String ma = currentUser.getMaNV().toUpperCase();
                if (ma.startsWith("BS"))
                    return VaiTro.BAC_SI;
                if (ma.startsWith("LT"))
                    return VaiTro.LE_TAN;
                if (ma.startsWith("YT"))
                    return VaiTro.Y_TA;
                if (ma.startsWith("QL"))
                    return VaiTro.QUAN_LY;
            }
        }
        return VaiTro.QUAN_LY;
    }

    public TrangChu(TaiKhoan user) {

        this.currentUser = user;

        if (this.currentUser.getHoTenNV() == null) {

            this.currentUser.setHoTenNV("Trần Thị Hoàng Lan");

            this.currentUser.setTenVaiTro("Quản lý");

        }

        SessionManager.setCurrentUser(this.currentUser);

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

        pnlMainWorkspace.setBackground(UITheme.BG_MAIN);

        // A. Top Header Bar

        pnlMainWorkspace.add(createTopHeaderBar(), BorderLayout.NORTH);

        // B. Content Area with CardLayout

        cardLayout = new CardLayout();

        pnlContentArea = new JPanel(cardLayout);

        pnlContentArea.setBackground(UITheme.BG_MAIN);

        // Khởi tạo các panels

        pnlThongKeCuoiNgay = new PanelThongKeCuoiNgay_LeTan();

        pnlTheoDoiKhuKhuan = new PanelTheoDoiKhuKhuan_YTa();

        pnlVatTuTieuHao = new PanelVatTuTieuHao_YTa();

        pnlBenhNhan = new PanelBenhNhan_DungChung();

        pnlKhamDieuTri = new PanelKhamBenh_BacSi(currentUser);

        pnlKhamDieuTri.setOnTabChangeCallback(this::syncSidebarOnly);

        pnlKhamDieuTri.setTabSwitcher(this::switchTab);

        pnlDichVu = new PanelDichVu_DungChung();

        pnlKhoVatTu = new PanelKhoVatTu_DungChung();

        pnlLichTruc = new PanelLichHen_DungChung();

        pnlNhanSu = new PanelNhanSu_QuanLy();

        pnlTaiKhoan = new PanelTaiKhoan_QuanLy(currentUser);

        pnlThanhToan = new PanelThanhToan_LeTan();

        pnlUuDai = new PanelUuDai_QuanLy();

        pnlQuyDinh = new PanelQuyDinh_QuanLy();

        pnlThongKe = new PanelThongKe_QuanLy();

        pnlContentArea.add(pnlVatTuTieuHao, "VAT_TU_TIEU_HAO");

        pnlContentArea.add(pnlBenhNhan, "BENH_NHAN");

        pnlContentArea.add(pnlKhamDieuTri, "NHA_SI");

        pnlContentArea.add(pnlDichVu, "DICH_VU");

        pnlContentArea.add(pnlKhoVatTu, "KHO_VAT_TU");

        pnlContentArea.add(pnlLichTruc, "LICH_TRUC");

        pnlContentArea.add(pnlNhanSu, "NHAN_SU");

        pnlContentArea.add(pnlTaiKhoan, "TAI_KHOAN");

        pnlContentArea.add(pnlThanhToan, "THANH_TOAN");

        pnlContentArea.add(pnlUuDai, "UU_DAI");

        pnlContentArea.add(pnlQuyDinh, "QUY_DINH");

        pnlContentArea.add(pnlThongKe, "THONG_KE");

        pnlContentArea.add(pnlTheoDoiKhuKhuan, "KHU_KHUAN");

        pnlContentArea.add(pnlThongKeCuoiNgay, "THONG_KE_CUOI_NGAY");

        pnlMainWorkspace.add(pnlContentArea, BorderLayout.CENTER);

        add(pnlMainWorkspace, BorderLayout.CENTER);

        // Mở mục menu đầu tiên mà vai trò đó được phép truy cập
        String defaultTab = !menuItems.isEmpty() ? menuItems.get(0).getCardKey() : "BENH_NHAN";
        switchTab(defaultTab);

    }

    private JPanel createSidebarPanel() {

        JPanel sidebar = new JPanel(new BorderLayout());

        sidebar.setPreferredSize(new Dimension(256, 0));

        sidebar.setBackground(UITheme.SIDEBAR_BG);

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

        pnlUserFooter.setBackground(UITheme.SIDEBAR_HOVER);

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

        JButton btnDoiMK = UITheme.createOutlineIconButton(

                new AppIcon(AppIcon.Type.LOCK, 13, new Color(224, 242, 254)), "Đổi MK", new Color(100, 116, 139));

        btnDoiMK.setFont(UITheme.FONT_TINY);

        btnDoiMK.setBorder(new EmptyBorder(5, 8, 5, 8));

        btnDoiMK.addActionListener(e -> showDoiMatKhauDialog());

        JButton btnLogout = UITheme.createIconButton(new AppIcon(AppIcon.Type.CLOSE, 13, Color.WHITE),

                "Đăng xuất", new Color(185, 28, 28), Color.WHITE);

        btnLogout.setFont(UITheme.FONT_TINY);

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
     * 
     * Phân quyền menu khoa học:
     * 
     * Quản lý: Full quyền giám sát điều hành và cấu hình hệ thống
     * 
     * Các vị trí khác: Giới hạn theo đúng phân hệ nghiệp vụ
     * 
     */

    private void buildRoleBasedMenu(JPanel pnlMenuItems) {
        VaiTro vaiTro = getVaiTroHienTai();
        java.util.List<MucMenu> danhSach = MenuManager.getMenu(vaiTro);

        String danhMucHienTai = null;
        for (MucMenu muc : danhSach) {
            if (muc.getDanhMuc() != null && !muc.getDanhMuc().equals(danhMucHienTai)) {
                danhMucHienTai = muc.getDanhMuc();
                addCategoryHeader(pnlMenuItems, danhMucHienTai);
            }
            addMenuItem(pnlMenuItems, muc.getIconType(), muc.getTen(), muc.getCardKey());
        }
    }

    private void addCategoryHeader(JPanel parent, String title) {

        JLabel lbl = new JLabel(title);

        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));

        lbl.setForeground(UITheme.SIDEBAR_MUTED);

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
     * TOPBAR CHUẨN HÓA VỚI BREADCRUMB VÀ TRẠNG THÁI HỆ THỐNG
     */
    private JPanel createTopHeaderBar() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                new EmptyBorder(8, 20, 8, 20)));

        // 1. Bên trái: Breadcrumb hiển thị vị trí hiện tại
        JPanel pnlBreadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBreadcrumb.setOpaque(false);
        JLabel lblHomeIcon = new JLabel(new AppIcon(AppIcon.Type.PATIENT, 16, UITheme.PRIMARY));
        lblBreadcrumb = new JLabel("Trang chủ");
        lblBreadcrumb.setFont(UITheme.FONT_BOLD);
        lblBreadcrumb.setForeground(UITheme.TEXT_MAIN);
        pnlBreadcrumb.add(lblHomeIcon);
        pnlBreadcrumb.add(lblBreadcrumb);
        header.add(pnlBreadcrumb, BorderLayout.WEST);

        // 2. Bên phải: Trạng thái SQL Server & Đồng hồ hệ thống
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        pnlRight.setOpaque(false);

        boolean dbOk = connectDB.ConnectDB.isConnected() || connectDB.ConnectDB.getConnection() != null;
        JLabel lblDbStatus = UITheme.createBadge(
                dbOk ? "SQL Server: Đã kết nối" : "SQL Server: Kết nối thất bại",
                dbOk ? UITheme.SUCCESS_LIGHT : UITheme.DANGER_LIGHT,
                dbOk ? UITheme.SUCCESS : UITheme.DANGER);

        lblClock = new JLabel();
        lblClock.setFont(UITheme.FONT_SMALL);
        lblClock.setForeground(UITheme.TEXT_MUTED);

        Timer timer = new Timer(1000, e -> {
            lblClock.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss").format(new Date()));
        });
        timer.start();

        pnlRight.add(lblDbStatus);
        pnlRight.add(lblClock);

        header.add(pnlRight, BorderLayout.EAST);
        return header;
    }

    public boolean hasPermission(String cardKey) {
        if (cardKey == null)
            return false;
        return MenuManager.coQuyen(getVaiTroHienTai(), cardKey);
    }

    private boolean kiemTraQuyenTruyCap(String cardKey) {

        if (hasPermission(cardKey)) {

            return true;

        }

        JOptionPane.showMessageDialog(this,

                "Vị trí công tác của bạn ["

                        + (currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro() : "Nhân viên") + "]\n"

                        + "không được phân quyền truy cập trực tiếp vào phân hệ này!",

                "Giới hạn quyền truy cập", JOptionPane.WARNING_MESSAGE);

        return false;

    }

    public void syncSidebarOnly(String cardKey) {

        if (cardKey == null)
            return;

        for (SidebarMenuItem item : menuItems) {

            boolean active = item.getCardKey().equals(cardKey);

            item.setActive(active);

            if (active) {

                lblBreadcrumb.setText("Trang chủ  /  " + item.getTitle());

            }

        }

    }

    public PanelKhamBenh_BacSi getPnlKhamDieuTri() {
        return pnlKhamDieuTri;
    }

    public PanelBenhNhan_DungChung getPnlBenhNhan() {
        return pnlBenhNhan;
    }

    public JPanel getPnlContentArea() {
        return pnlContentArea;
    }

    public void switchTab(String cardKey) {

        if (cardKey == null)
            return;

        String param = null;

        if (cardKey.contains(":")) {

            String[] parts = cardKey.split(":", 2);

            cardKey = parts[0];

            param = parts[1];

        }

        String viewKey = cardKey;

        if (cardKey.startsWith("NHA_SI") || cardKey.equals("KHAM_DIEU_TRI")) {

            if (pnlKhamDieuTri != null) {

                pnlKhamDieuTri.showUseCase(cardKey);

                if (param != null) {

                    pnlKhamDieuTri.selectPatientByMaBN(param);

                }

            }

            viewKey = "NHA_SI";

        }

        cardLayout.show(pnlContentArea, viewKey);

        if ("BENH_NHAN".equals(cardKey) && param != null && pnlBenhNhan != null) {

            pnlBenhNhan.selectBenhNhan(param);

        }

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

            new DangNhap().setVisible(true);

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

        JButton btnLuu = UITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Lưu mật khẩu",

                UITheme.PRIMARY, Color.WHITE);

        JButton btnHuy = UITheme.createOutlineButton("Hủy", UITheme.TEXT_MUTED);

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
     * 
     * Nút Menu Sidebar tùy biến chuẩn Y Khoa, có AppIcon vector và vạch active sáng
     * 
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

            setIcon(new AppIcon(iconType, 16, UITheme.SIDEBAR_TEXT));

            setIconTextGap(10);

            setFont(new Font("Segoe UI", Font.BOLD, 13));

            setForeground(UITheme.SIDEBAR_TEXT);

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

            setForeground(active ? Color.WHITE : UITheme.SIDEBAR_TEXT);

            setIcon(new AppIcon(iconType, 16, active ? Color.WHITE : UITheme.SIDEBAR_TEXT));

            repaint();

        }

        @Override

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {

                g2.setColor(UITheme.SIDEBAR_ACTIVE);

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));

                // Vạch chỉ báo trắng nổi bật mép trái

                g2.setColor(Color.WHITE);

                g2.fill(new RoundRectangle2D.Double(2, 6, 4, getHeight() - 12, 4, 4));

            } else if (getModel().isRollover()) {

                g2.setColor(UITheme.SIDEBAR_HOVER);

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

            new TrangChu().setVisible(true);

        });

    }

}
