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
    private JButton btnThem, btnKhoa, btnDatLaiMK, btnLamMoi, btnTimKiem;

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
        btnKhoa = ModernUITheme.createButton("🔒 Khóa / Mở khóa", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

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

        btnThem.addActionListener(e -> xuLyThem());
        btnKhoa.addActionListener(e -> xuLyKhoa());
        btnDatLaiMK.addActionListener(e -> xuLyResetPass());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTaiLai.addActionListener(e -> taiDuLieu());

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
        cbNhanVien.addItem("NV001 - BS. CKII Nguyễn Văn An");
        cbNhanVien.addItem("NV002 - ThS. BS Lê Thị Thảo");
        cbNhanVien.addItem("NV003 - BS. Trần Minh Trí");
        cbNhanVien.addItem("NV004 - Trần Thị Hoàng Lan");
    }

    private void taiDuLieu() {
        // TODO: Cài đặt tải danh sách tài khoản từ CSDL tại đây
        napDuLieuMau();
        xoaTrangForm();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[] { "admin", "Trần Thị Hoàng Lan", "Quản lý", "Hoạt động" });
        tableModel.addRow(new Object[] { "bs.an", "BS. CKII Nguyễn Văn An", "Bác sĩ", "Hoạt động" });
        tableModel.addRow(new Object[] { "bs.thao", "ThS. BS Lê Thị Thảo", "Bác sĩ", "Hoạt động" });
        tableModel.addRow(new Object[] { "bs.tri", "BS. Trần Minh Trí", "Bác sĩ", "Hoạt động" });
        tableModel.addRow(new Object[] { "letan01", "Đỗ Thùy Trang", "Lễ tân", "Hoạt động" });
    }

    private void napFormTuBang(int row) {
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
        cbTrangThai.setSelectedItem(
                tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Hoạt động");
    }

    private void xoaTrangForm() {
        txtTenDangNhap.setText("");
        txtTenDangNhap.setEditable(true);
        txtMatKhau.setText("123456");
        if (cbNhanVien.getItemCount() > 0)
            cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void xuLyThem() {
        // TODO: Cài đặt chức năng cấp tài khoản mới vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Cấp tài khoản đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyKhoa() {
        // TODO: Cài đặt chức năng khóa / mở khóa tài khoản trong CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Khóa/Mở khóa tài khoản đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyResetPass() {
        // TODO: Cài đặt chức năng đặt lại mật khẩu trong CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Đặt lại mật khẩu đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        // TODO: Cài đặt chức năng tra cứu tài khoản tại đây
    }
}
