package gui;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quản lý Tài Khoản & Phân Quyền Bảo Mật - Phong cách hiện đại
 */
public class PanelTaiKhoanModern extends JPanel {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private TaiKhoan currentUser;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtTenDangNhap, txtMatKhau, txtTimKiem;
    private JComboBox<String> cbNhanVien, cbTrangThai;
    private JButton btnThem, btnKhoa, btnDatLaiMK, btnLamMoi, btnTimKiem, btnXoaTK;

    public PanelTaiKhoanModern(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN TRUY CẬP");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Cấp tài khoản đăng nhập phần mềm cho bác sĩ, nhân viên, phân quyền và khóa bảo mật");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm tên đăng nhập hoặc tên nhân viên...");
        btnTimKiem = ModernUITheme.createButton("🔍 Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.62);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG TÀI KHOẢN
        String[] cols = { "Tên Đăng Nhập", "Bác Sĩ / Nhân Viên Sở Hữu", "Vai Trò Hệ Thống", "Trạng Thái Hoạt Động" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        splitPane.setTopComponent(scrollPane);

        // FORM CARD CẤP TÀI KHOẢN
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("🔐 THÔNG TIN TÀI KHOẢN NGƯỜI DÙNG");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 2, 20, 10));
        pnlFields.setOpaque(false);

        txtTenDangNhap = ModernUITheme.createTextField(15);
        txtMatKhau = ModernUITheme.createTextField(15);
        cbNhanVien = new JComboBox<>();
        cbTrangThai = new JComboBox<>(new String[] { "Hoạt động", "Bị khóa" });

        pnlFields.add(createFieldItem("Tên đăng nhập (*):", txtTenDangNhap));
        pnlFields.add(createFieldItem("Mật khẩu ban đầu (*):", txtMatKhau));
        pnlFields.add(createFieldItem("Gán cho nhân sự y tế (*):", cbNhanVien));
        pnlFields.add(createFieldItem("Trạng thái tài khoản:", cbTrangThai));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createButton("➕ Cấp tài khoản mới", ModernUITheme.PRIMARY, Color.WHITE);
        btnDatLaiMK = ModernUITheme.createButton("🔑 Đặt lại MK (123456)", ModernUITheme.ACCENT, Color.WHITE);
        btnKhoa = ModernUITheme.createButton("🔒 Khóa / Mở khóa", ModernUITheme.SECONDARY, Color.WHITE);
        btnXoaTK = ModernUITheme.createButton("🗑️ Xóa tài khoản", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXoaTK);
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

        btnThem.addActionListener(e -> xuLyThem());
        btnKhoa.addActionListener(e -> xuLyKhoa());
        btnDatLaiMK.addActionListener(e -> xuLyResetPass());
        btnXoaTK.addActionListener(e -> xuLyXoaTK());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTaiLai.addActionListener(e -> {
            txtTimKiem.setText("");
            taiDuLieu();
        });

        napComboboxNV();
        taiDuLieu();
    }

    private JPanel createFieldItem(String labelText, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(ModernUITheme.FONT_BOLD);
        lbl.setForeground(ModernUITheme.TEXT_REGULAR);
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private void napComboboxNV() {
        cbNhanVien.removeAllItems();
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();
        for (NhanVien nv : dsNV) {
            cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTen() + " (" + nv.getChucDanh() + ")");
        }
        if (cbNhanVien.getItemCount() == 0) {
            cbNhanVien.addItem("QL001 - Trần Thị Hoàng Lan (Quản lý)");
            cbNhanVien.addItem("BS001 - BS.CKI Nguyễn Minh Tuấn (Nha sĩ)");
        }
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<TaiKhoan> list = taiKhoanDAO.getAllTaiKhoan();
        for (TaiKhoan tk : list) {
            tableModel.addRow(new Object[] {
                tk.getTenDangNhap(),
                tk.getHoTenNV() != null ? tk.getHoTenNV() : tk.getMaNV(),
                tk.getTenVaiTro() != null ? tk.getTenVaiTro() : "Nhân viên",
                tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động"
            });
        }
        xoaTrangForm();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        txtTenDangNhap.setText(tableModel.getValueAt(row, 0).toString());
        txtTenDangNhap.setEditable(false);
        txtMatKhau.setText("••••••");
        String tenNV = tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "";
        for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
            if (cbNhanVien.getItemAt(i).contains(tenNV)) {
                cbNhanVien.setSelectedIndex(i);
                break;
            }
        }
        String tt = tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Hoạt động";
        cbTrangThai.setSelectedItem(tt);
        if ("Bị khóa".equalsIgnoreCase(tt)) {
            btnKhoa.setText("🔓 Mở khóa tài khoản");
            btnKhoa.setBackground(ModernUITheme.SUCCESS);
        } else {
            btnKhoa.setText("🔒 Khóa tài khoản");
            btnKhoa.setBackground(ModernUITheme.DANGER);
        }
    }

    private void xoaTrangForm() {
        txtTenDangNhap.setText("");
        txtTenDangNhap.setEditable(true);
        txtMatKhau.setText("123456");
        if (cbNhanVien.getItemCount() > 0)
            cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        btnKhoa.setText("🔒 Khóa tài khoản");
        btnKhoa.setBackground(ModernUITheme.DANGER);
        table.clearSelection();
    }

    private void xuLyThem() {
        String username = txtTenDangNhap.getText().trim();
        String password = txtMatKhau.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.requestFocus();
            return;
        }
        if (cbNhanVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên được cấp tài khoản!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemNV = cbNhanVien.getSelectedItem().toString();
        String maNV = itemNV.split(" - ")[0].trim();
        String hoTen = "";
        if (itemNV.contains(" - ")) {
            hoTen = itemNV.substring(itemNV.indexOf(" - ") + 3);
            if (hoTen.contains(" (")) {
                hoTen = hoTen.substring(0, hoTen.indexOf(" (")).trim();
            }
        }

        String maVaiTro = "VT_BS";
        String tenVaiTro = "Nha sĩ";
        String itemLower = itemNV.toLowerCase();
        if (itemLower.contains("quản lý") || maNV.startsWith("QL")) {
            maVaiTro = "VT_QL";
            tenVaiTro = "Quản lý";
        } else if (itemLower.contains("lễ tân") || maNV.startsWith("LT")) {
            maVaiTro = "VT_LT";
            tenVaiTro = "Lễ tân";
        } else if (itemLower.contains("thu ngân") || maNV.startsWith("TN")) {
            maVaiTro = "VT_TN";
            tenVaiTro = "Thu ngân";
        } else if (itemLower.contains("y tá") || itemLower.contains("điều dưỡng") || maNV.startsWith("YT")) {
            maVaiTro = "VT_YT";
            tenVaiTro = "Y tá";
        }

        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "Hoạt động";
        TaiKhoan tk = new TaiKhoan(username, password, null, trangThai, 0, maNV, maVaiTro);
        tk.setHoTenNV(hoTen);
        tk.setTenVaiTro(tenVaiTro);

        boolean thanhCong = taiKhoanDAO.themTaiKhoan(tk);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Cấp tài khoản [" + username + "] cho " + hoTen + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tạo tài khoản. Tên đăng nhập có thể đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyKhoa() {
        int row = table.getSelectedRow();
        String username = txtTenDangNhap.getText().trim();
        if (row < 0 && username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản trong danh sách!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Không thể khóa tài khoản quản trị tối cao (admin)!", "Cảnh báo bảo mật", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trangThaiHienTai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "Hoạt động";
        boolean khoa = "Hoạt động".equalsIgnoreCase(trangThaiHienTai);
        boolean thanhCong;
        if (khoa) {
            thanhCong = taiKhoanDAO.khoaTaiKhoan(username);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đã khóa tài khoản [" + username + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            thanhCong = taiKhoanDAO.moKhoaTaiKhoan(username);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đã mở khóa tài khoản [" + username + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        taiDuLieu();
    }

    private void xuLyResetPass() {
        int row = table.getSelectedRow();
        String username = txtTenDangNhap.getText().trim();
        if (row < 0 && username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần đặt lại mật khẩu!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đặt lại mật khẩu của tài khoản [" + username + "] về mặc định: 123456?", "Xác nhận đặt lại", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean thanhCong = taiKhoanDAO.capLaiMatKhau(username, "123456");
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đã đặt lại mật khẩu tài khoản [" + username + "] về mặc định: 123456!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            boolean match = (tk.getTenDangNhap() != null && tk.getTenDangNhap().toLowerCase().contains(keyword)) ||
                            (tk.getHoTenNV() != null && tk.getHoTenNV().toLowerCase().contains(keyword)) ||
                            (tk.getMaNV() != null && tk.getMaNV().toLowerCase().contains(keyword)) ||
                            (tk.getTenVaiTro() != null && tk.getTenVaiTro().toLowerCase().contains(keyword)) ||
                            (tk.getTrangThai() != null && tk.getTrangThai().toLowerCase().contains(keyword));
            if (match) {
                tableModel.addRow(new Object[] {
                    tk.getTenDangNhap(),
                    tk.getHoTenNV() != null ? tk.getHoTenNV() : tk.getMaNV(),
                    tk.getTenVaiTro() != null ? tk.getTenVaiTro() : "Nhân viên",
                    tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động"
                });
            }
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản nào khớp với từ khóa: \"" + keyword + "\"", "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyXoaTK() {
        int row = table.getSelectedRow();
        String username = txtTenDangNhap.getText().trim();
        if (row < 0 && username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trong bảng để xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (row >= 0) username = tableModel.getValueAt(row, 0).toString();

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Không được phép xóa tài khoản quản trị hệ thống (admin)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản đăng nhập [" + username + "] khỏi hệ thống?", "Xác nhận xóa tài khoản", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (cf == JOptionPane.YES_OPTION) {
            boolean ok = taiKhoanDAO.xoaTaiKhoan(username);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa tài khoản [" + username + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
