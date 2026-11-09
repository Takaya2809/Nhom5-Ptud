package gui;

import connectDB.PasswordUtils;
import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.VaiTro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quản lý Tài Khoản & Phân Quyền Bảo Mật - Chuẩn hóa y tế
 * - Định danh đăng nhập trực tiếp bằng MÃ NHÂN VIÊN (maNV), loại bỏ tên đăng
 * nhập dư thừa.
 * - Mật khẩu được mã hóa băm SHA-256 an toàn, không hiển thị plain-text.
 * - Mật khẩu khởi tạo / reset mặc định là "123".
 * - Loại bỏ hoàn toàn nút Xóa tài khoản để bảo toàn dữ liệu người dùng.
 */
public class PanelTaiKhoan_QuanLy extends JPanel {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private TaiKhoan currentUser;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaNV, txtHoTen, txtChucDanh, txtMatKhau, txtTimKiem;
    private JComboBox<String> cbNhanVien, cbTrangThai;
    private JButton btnThem, btnKhoa, btnDatLaiMK, btnLamMoi, btnTimKiem;

    public PanelTaiKhoan_QuanLy(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN TRUY CẬP");
        lblTitle.setFont(UITheme.FONT_HERO);
        lblTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Cấp tài khoản bằng Mã nhân viên, mã hóa mật khẩu SHA-256, phân quyền tự động và khóa bảo mật");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = UITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm mã hoặc họ tên nhân viên...");
        btnTimKiem = UITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 15, Color.WHITE), "Tìm kiếm",
                UITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = UITheme.createOutlineIconButton(
                new AppIcon(AppIcon.Type.REFRESH, 14, UITheme.TEXT_MUTED), "Tất cả", UITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.60);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG TÀI KHOẢN
        String[] cols = { "Mã Nhân Viên", "Họ Và Tên Sở Hữu", "Chức Danh Y Tế", "Vai Trò Hệ Thống",
                "Mật Khẩu (Bảo mật)", "Trạng Thái Hoạt Động" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UITheme.formatTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        splitPane.setTopComponent(scrollPane);

        // FORM CARD CẤP TÀI KHOẢN
        JPanel pnlFormCard = UITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("THIẾT LẬP TÀI KHOẢN ĐĂNG NHẬP THEO MÃ NHÂN VIÊN");
        lblFormTitle.setFont(UITheme.FONT_TITLE);
        lblFormTitle.setForeground(UITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 3, 16, 10));
        pnlFields.setOpaque(false);

        txtMaNV = UITheme.createTextField(12);
        txtMaNV.setToolTipText("Nhập chính xác mã nhân viên cần cấp tài khoản (ví dụ: BS001, LT001...)");

        cbNhanVien = new JComboBox<>();
        cbNhanVien.setFont(UITheme.FONT_REGULAR);

        txtHoTen = UITheme.createTextField(15);
        txtHoTen.setEditable(false);
        txtHoTen.setBackground(new Color(241, 245, 249));

        txtChucDanh = UITheme.createTextField(15);
        txtChucDanh.setEditable(false);
        txtChucDanh.setBackground(new Color(241, 245, 249));

        txtMatKhau = UITheme.createTextField(15);
        txtMatKhau.setText("123");
        txtMatKhau.setEditable(false);
        txtMatKhau.setBackground(new Color(241, 245, 249));

        cbTrangThai = new JComboBox<>(new String[] { "Hoạt động", "Bị khóa" });
        cbTrangThai.setFont(UITheme.FONT_REGULAR);

        pnlFields.add(createFieldItem("Chọn nhanh từ danh sách nhân sự:", cbNhanVien));
        pnlFields.add(createFieldItem("Mã nhân viên (*):", txtMaNV));
        pnlFields.add(createFieldItem("Họ và tên nhân viên:", txtHoTen));
        pnlFields.add(createFieldItem("Chức danh y tế:", txtChucDanh));
        pnlFields.add(createFieldItem("Mật khẩu ban đầu:", txtMatKhau));
        pnlFields.add(createFieldItem("Trạng thái tài khoản:", cbTrangThai));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons (Loại bỏ hoàn toàn nút Xóa tài khoản)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = UITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 15, Color.WHITE), "Cấp tài khoản mới",
                UITheme.PRIMARY, Color.WHITE);
        btnDatLaiMK = UITheme.createIconButton(new AppIcon(AppIcon.Type.LOCK, 15, Color.WHITE), "Đặt lại MK về '123'",
                UITheme.ACCENT, Color.WHITE);
        btnKhoa = UITheme.createIconButton(new AppIcon(AppIcon.Type.ACCOUNT, 15, Color.WHITE), "Khóa / Mở khóa",
                UITheme.SECONDARY, Color.WHITE);
        btnLamMoi = UITheme.createOutlineIconButton(
                new AppIcon(AppIcon.Type.REFRESH, 15, UITheme.TEXT_MUTED), "Làm mới", UITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnDatLaiMK);
        pnlButtons.add(btnKhoa);
        pnlButtons.add(btnThem);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Listeners
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                napFormTuBang(table.getSelectedRow());
            }
        });

        // Khi chọn nhân viên từ ComboBox, tự động nạp mã, họ tên, chức danh
        cbNhanVien.addActionListener(e -> {
            if (cbNhanVien.getSelectedItem() != null && table.getSelectedRow() == -1) {
                String item = cbNhanVien.getSelectedItem().toString();
                if (item.contains(" - ")) {
                    String m = item.split(" - ")[0].trim();
                    txtMaNV.setText(m);
                    capNhatThongTinTheoMa(m);
                }
            }
        });

        // Khi người dùng gõ mã nhân viên vào txtMaNV
        txtMaNV.addActionListener(e -> {
            capNhatThongTinTheoMa(txtMaNV.getText().trim());
        });

        btnThem.addActionListener(e -> xuLyThem());
        btnKhoa.addActionListener(e -> xuLyKhoa());
        btnDatLaiMK.addActionListener(e -> xuLyResetPass());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTaiLai.addActionListener(e -> {
            txtTimKiem.setText("");
            taiDuLieu();
        });

        // Tự động tải lại danh sách tài khoản mỗi khi chuyển sang màn hình Quản Trị Tài
        // Khoản
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                napComboboxNV();
                taiDuLieu();
            }
        });

        napComboboxNV();
        taiDuLieu();
    }

    private JPanel createFieldItem(String labelText, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_REGULAR);
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private void capNhatThongTinTheoMa(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            txtHoTen.setText("");
            txtChucDanh.setText("");
            return;
        }
        NhanVien nv = nhanVienDAO.getNhanVienByMa(maNV.trim());
        if (nv != null) {
            txtHoTen.setText(nv.getHoTen());
            txtChucDanh.setText(nv.getChucDanh() != null ? nv.getChucDanh() : "");
        } else {
            txtHoTen.setText("(Chưa có trong hồ sơ)");
            txtChucDanh.setText("");
        }
    }

    private void napComboboxNV() {
        cbNhanVien.removeAllItems();
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();
        List<TaiKhoan> dsTK = taiKhoanDAO.getAllTaiKhoan();
        for (NhanVien nv : dsNV) {
            String trangThaiTK = "Chưa kích hoạt";
            boolean daCoTK = false;
            for (TaiKhoan tk : dsTK) {
                if (nv.getMaNV().equalsIgnoreCase(tk.getMaNV()) || nv.getMaNV().equalsIgnoreCase(tk.getTenDangNhap())) {
                    daCoTK = true;
                    trangThaiTK = tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động";
                    break;
                }
            }
            String suffix;
            if (!daCoTK || "Chưa kích hoạt".equalsIgnoreCase(trangThaiTK)) {
                suffix = " [TK chưa kích hoạt]";
            } else if ("Bị khóa".equalsIgnoreCase(trangThaiTK)) {
                suffix = " [TK bị khóa]";
            } else {
                suffix = " [Đã kích hoạt]";
            }
            cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTen() + " (" + nv.getChucDanh() + ")" + suffix);
        }
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<TaiKhoan> list = taiKhoanDAO.getAllTaiKhoan();
        for (TaiKhoan tk : list) {
            String maNV = tk.getMaNV() != null ? tk.getMaNV() : tk.getTenDangNhap();
            NhanVien nv = nhanVienDAO.getNhanVienByMa(maNV);
            String hoTen = nv != null ? nv.getHoTen() : (tk.getHoTenNV() != null ? tk.getHoTenNV() : "");
            String chucDanh = nv != null ? nv.getChucDanh() : "";
            tableModel.addRow(new Object[] {
                    maNV,
                    hoTen,
                    chucDanh,
                    tk.getTenVaiTro() != null ? tk.getTenVaiTro() : "Nhân viên",
                    "••••••••",
                    tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động"
            });
        }
        xoaTrangForm();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount())
            return;
        String maNV = tableModel.getValueAt(row, 0).toString();
        txtMaNV.setText(maNV);
        txtMaNV.setEditable(false);
        txtHoTen.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
        txtChucDanh.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtMatKhau.setText("••••••••");

        for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
            if (cbNhanVien.getItemAt(i).startsWith(maNV + " -")) {
                cbNhanVien.setSelectedIndex(i);
                break;
            }
        }
        String tt = tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "Hoạt động";
        cbTrangThai.setSelectedItem(tt);
        if ("Bị khóa".equalsIgnoreCase(tt)) {
            btnKhoa.setText("Mở khóa tài khoản");
            btnKhoa.setBackground(UITheme.SUCCESS);
        } else {
            btnKhoa.setText("Khóa tài khoản");
            btnKhoa.setBackground(UITheme.DANGER);
        }
    }

    private void xoaTrangForm() {
        table.clearSelection();
        txtMaNV.setText("");
        txtMaNV.setEditable(true);
        txtHoTen.setText("");
        txtChucDanh.setText("");
        txtMatKhau.setText("123");
        if (cbNhanVien.getItemCount() > 0) {
            // Ưu tiên chọn nhân viên chưa có tài khoản
            boolean found = false;
            for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
                if (cbNhanVien.getItemAt(i).contains("[TK chưa kích hoạt]")) {
                    cbNhanVien.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found)
                cbNhanVien.setSelectedIndex(0);
        }
        cbTrangThai.setSelectedIndex(0);
        btnKhoa.setText("Khóa tài khoản");
        btnKhoa.setBackground(UITheme.DANGER);
    }

    private void xuLyThem() {
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập hoặc chọn Mã nhân viên cần cấp tài khoản!",
                    "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            txtMaNV.requestFocus();
            return;
        }

        // 1. Kiểm tra mã nhân viên có tồn tại trong bảng Nhân Viên không
        NhanVien nv = nhanVienDAO.getNhanVienByMa(maNV);
        if (nv == null) {
            JOptionPane.showMessageDialog(this,
                    "Mã nhân viên [" + maNV + "] không tồn tại trong hệ thống!\n" +
                            "Vui lòng vào mục 'Bác Sĩ & Nhân Sự' để thêm hồ sơ nhân sự trước khi cấp tài khoản.",
                    "Lỗi xác thực nhân sự", JOptionPane.ERROR_MESSAGE);
            txtMaNV.requestFocus();
            return;
        }

        // 2. Kiểm tra xem tài khoản đã tồn tại hay đang ở trạng thái 'Chưa kích hoạt'
        boolean daCoTK = taiKhoanDAO.daCoTaiKhoan(maNV);
        if (daCoTK) {
            // Kiểm tra trạng thái hiện tại của tài khoản
            List<TaiKhoan> allTK = taiKhoanDAO.getAllTaiKhoan();
            TaiKhoan tkHienTai = null;
            for (TaiKhoan t : allTK) {
                if (maNV.equalsIgnoreCase(t.getMaNV()) || maNV.equalsIgnoreCase(t.getTenDangNhap())) {
                    tkHienTai = t;
                    break;
                }
            }
            if (tkHienTai != null && "Chưa kích hoạt".equalsIgnoreCase(tkHienTai.getTrangThai())) {
                // KÍCH HOẠT VÀ CẤP TÀI KHOẢN MỚI
                boolean ok = taiKhoanDAO.doiTrangThai(maNV, "Hoạt động");
                taiKhoanDAO.datLaiMatKhau(maNV);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Cấp và kích hoạt tài khoản thành công cho nhân sự mới!\n\n" +
                            "• Mã đăng nhập: " + maNV + "\n" +
                            "• Nhân viên: " + nv.getHoTen() + " (" + nv.getChucDanh() + ")\n" +
                            "• Mật khẩu khởi tạo: 123 (Đã băm SHA-256)\n" +
                            "• Trạng thái: Hoạt động\n\n" +
                            "* Người dùng sẽ được yêu cầu đổi mật khẩu mới trong lần đăng nhập đầu tiên.",
                            "Cấp tài khoản thành công", JOptionPane.INFORMATION_MESSAGE);
                    napComboboxNV();
                    taiDuLieu();
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Tài khoản của nhân viên [" + maNV + " - " + nv.getHoTen() + "] đã hoạt động rồi!\n" +
                        "Bạn có thể sử dụng nút 'Đặt lại MK' hoặc 'Khóa / Mở khóa' để quản lý.",
                        "Tài khoản đã hoạt động", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // 3. Tự động xác định vai trò hệ thống theo chức danh
        String maVaiTro = "VT_BS";
        String tenVaiTro = "Nha sĩ";
        String cdLower = (nv.getChucDanh() != null ? nv.getChucDanh().toLowerCase() : "");
        String maNVUpper = maNV.toUpperCase();
        if (cdLower.contains("quản lý") || maNVUpper.startsWith("QL")) {
            maVaiTro = "VT_QL";
            tenVaiTro = "Quản lý";
        } else if (cdLower.contains("lễ tân") || maNVUpper.startsWith("LT")) {
            maVaiTro = "VT_LT";
            tenVaiTro = "Lễ tân";
        } else if (cdLower.contains("thu ngân") || maNVUpper.startsWith("TN")) {
            maVaiTro = "VT_TN";
            tenVaiTro = "Thu ngân";
        } else if (cdLower.contains("y tá") || cdLower.contains("điều dưỡng") || cdLower.contains("phụ tá")
                || maNVUpper.startsWith("YT")) {
            maVaiTro = "VT_YT";
            tenVaiTro = "Y tá";
        }

        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString()
                : "Hoạt động";

        // Mật khẩu khởi tạo mặc định là "123" và được tự động băm SHA-256
        TaiKhoan tk = new TaiKhoan(maNV, "123", null, trangThai, 0, maNV, maVaiTro);
        tk.setHoTenNV(nv.getHoTen());
        tk.setTenVaiTro(tenVaiTro);

        boolean thanhCong = taiKhoanDAO.themTaiKhoan(tk);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this,
                    "Cấp tài khoản mới thành công!\n\n" +
                            "• Mã đăng nhập: " + maNV + "\n" +
                            "• Nhân viên sở hữu: " + nv.getHoTen() + " (" + nv.getChucDanh() + ")\n" +
                            "• Vai trò: " + tenVaiTro + "\n" +
                            "• Mật khẩu khởi tạo: 123 (Đã băm SHA-256)\n\n" +
                            "* Lưu ý: Nhân viên sẽ được yêu cầu đổi mật khẩu ngay trong lần đăng nhập đầu tiên.",
                    "Cấp tài khoản thành công", JOptionPane.INFORMATION_MESSAGE);
            napComboboxNV();
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tạo tài khoản. Vui lòng kiểm tra lại kết nối CSDL!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyKhoa() {
        int row = table.getSelectedRow();
        String maNV = txtMaNV.getText().trim();
        if (row < 0 && maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản trong danh sách!", "Nhắc nhở",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ngăn chặn tự khóa chính mình
        if (currentUser != null && maNV.equalsIgnoreCase(currentUser.getMaNV())) {
            JOptionPane.showMessageDialog(this, "Bạn không thể tự khóa tài khoản của chính mình!",
                    "Cảnh báo bảo mật", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trangThaiHienTai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString()
                : "Hoạt động";
        boolean khoa = "Hoạt động".equalsIgnoreCase(trangThaiHienTai);
        boolean thanhCong;
        if (khoa) {
            thanhCong = taiKhoanDAO.khoaTaiKhoan(maNV);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this,
                        "Đã KHÓA tài khoản [" + maNV + "] thành công!\nNgười dùng sẽ không thể đăng nhập vào hệ thống.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            thanhCong = taiKhoanDAO.moKhoaTaiKhoan(maNV);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đã MỞ KHÓA tài khoản [" + maNV + "] thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        taiDuLieu();
    }

    private void xuLyResetPass() {
        int row = table.getSelectedRow();
        String maNV = txtMaNV.getText().trim();
        if (row < 0 && maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần đặt lại mật khẩu!", "Nhắc nhở",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đặt lại mật khẩu của nhân viên [" + maNV + "] về mặc định: '123'?\n" +
                        "Mật khẩu sẽ được mã hóa băm SHA-256 và nhân viên phải đổi mật khẩu khi đăng nhập lần tới.",
                "Xác nhận đặt lại mật khẩu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean thanhCong = taiKhoanDAO.datLaiMatKhau(maNV);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this,
                        "Đã đặt lại mật khẩu của tài khoản [" + maNV + "] về mặc định: 123!\n" +
                                "Hệ thống sẽ chuyển hướng bắt buộc đổi mật khẩu khi nhân viên đăng nhập.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thất bại. Vui lòng thử lại!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieu();
            return;
        }
        keyword = keyword.trim().toLowerCase();
        List<TaiKhoan> all = taiKhoanDAO.getAllTaiKhoan();
        tableModel.setRowCount(0);
        for (TaiKhoan tk : all) {
            String maNV = tk.getMaNV() != null ? tk.getMaNV() : tk.getTenDangNhap();
            NhanVien nv = nhanVienDAO.getNhanVienByMa(maNV);
            String hoTen = nv != null ? nv.getHoTen() : (tk.getHoTenNV() != null ? tk.getHoTenNV() : "");
            String chucDanh = nv != null ? nv.getChucDanh() : "";
            boolean match = (maNV != null && maNV.toLowerCase().contains(keyword)) ||
                    (hoTen != null && hoTen.toLowerCase().contains(keyword)) ||
                    (chucDanh != null && chucDanh.toLowerCase().contains(keyword)) ||
                    (tk.getTenVaiTro() != null && tk.getTenVaiTro().toLowerCase().contains(keyword)) ||
                    (tk.getTrangThai() != null && tk.getTrangThai().toLowerCase().contains(keyword));
            if (match) {
                tableModel.addRow(new Object[] {
                        maNV,
                        hoTen,
                        chucDanh,
                        tk.getTenVaiTro() != null ? tk.getTenVaiTro() : "Nhân viên",
                        "•••••••• (SHA-256)",
                        tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động"
                });
            }
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản nào khớp với từ khóa: \"" + keyword + "\"",
                    "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
