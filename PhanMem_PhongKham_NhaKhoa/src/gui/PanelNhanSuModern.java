package gui;

import dao.NhanVienDAO;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Giao diện Quản lý Đội ngũ Bác Sĩ & Nhân Sự Y Tế - Phong cách hiện đại
 */
public class PanelNhanSuModern extends JPanel {

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaNV, txtHoTen, txtNgaySinh, txtSDT, txtEmail, txtBangCap, txtNgayVaoLam, txtTimKiem;
    private JComboBox<String> cbGioiTinh, cbChucDanh, cbTrangThai;
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTimKiem;

    public PanelNhanSuModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐỘI NGŨ BÁC SĨ & NHÂN SỰ PHÒNG KHÁM");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý hồ sơ bác sĩ chuyên khoa RHM, bằng cấp, điều dưỡng phụ tá và lễ tân");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm mã, tên, chức danh bác sĩ...");
        btnTimKiem = ModernUITheme.createButton("🔍 Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu nhân sự:"));
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

        // BẢNG NHÂN SỰ
        String[] cols = {"Mã NV", "Họ Và Tên", "Ngày Sinh", "Giới Tính", "Số Điện Thoại", "Email", "Chức Danh", "Bằng Cấp / Chứng Chỉ", "Ngày Vào Làm", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        splitPane.setTopComponent(scrollPane);

        // FORM CARD NHẬP LIỆU
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("👨‍⚕️ THÔNG TIN CHI TIẾT BÁC SĨ / NHÂN VIÊN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 5, 12, 10));
        pnlFields.setOpaque(false);

        txtMaNV = ModernUITheme.createTextField(10);
        txtMaNV.setEditable(false);
        txtMaNV.setBackground(new Color(241, 245, 249));

        txtHoTen = ModernUITheme.createTextField(16);
        txtNgaySinh = ModernUITheme.createTextField(10);
        txtNgaySinh.setText("1988-05-12");
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        txtSDT = ModernUITheme.createTextField(12);
        txtEmail = ModernUITheme.createTextField(16);

        cbChucDanh = new JComboBox<>(new String[]{
            "Bác sĩ Chuyên khoa RHM", "Bác sĩ Trưởng khoa", "Bác sĩ Chỉnh nha",
            "Y tá / Phụ tá", "Kỹ thuật viên nha khoa", "Lễ tân", "Thu ngân", "Quản lý"
        });

        txtBangCap = ModernUITheme.createTextField(18);
        txtBangCap.setText("BS. CKI ĐHYD TP.HCM");

        txtNgayVaoLam = ModernUITheme.createTextField(10);
        txtNgayVaoLam.setText("2023-01-15");

        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});

        pnlFields.add(createFieldItem("Mã nhân viên:", txtMaNV));
        pnlFields.add(createFieldItem("Họ và tên (*):", txtHoTen));
        pnlFields.add(createFieldItem("Ngày sinh (yyyy-MM-dd):", txtNgaySinh));
        pnlFields.add(createFieldItem("Giới tính:", cbGioiTinh));
        pnlFields.add(createFieldItem("Số điện thoại (*):", txtSDT));
        pnlFields.add(createFieldItem("Email liên hệ:", txtEmail));
        pnlFields.add(createFieldItem("Chức danh y tế:", cbChucDanh));
        pnlFields.add(createFieldItem("Bằng cấp / Chứng chỉ:", txtBangCap));
        pnlFields.add(createFieldItem("Ngày vào làm:", txtNgayVaoLam));
        pnlFields.add(createFieldItem("Trạng thái:", cbTrangThai));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createButton("➕ Thêm nhân sự", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createButton("💾 Cập nhật hồ sơ", ModernUITheme.ACCENT, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);
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
        btnCapNhat.addActionListener(e -> xuLyCapNhat());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTaiLai.addActionListener(e -> taiDuLieu());

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

    private void taiDuLieu() {
        // TODO: Cài đặt tải danh sách nhân sự từ CSDL tại đây
        napDuLieuMau();
        xoaTrangForm();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"BS001", "BS. CKII Nguyễn Văn An", "1978-02-14", "Nam", "0901234567", "an.nguyen@nhakhoaabc.vn", "Bác sĩ Trưởng khoa", "BS. CKII Implant ĐHYD", "2020-03-01", "Đang làm việc"});
        tableModel.addRow(new Object[]{"BS002", "ThS. BS Lê Thị Thảo", "1985-09-21", "Nữ", "0902345678", "thao.le@nhakhoaabc.vn", "Bác sĩ Chỉnh nha", "ThS Chỉnh hình Răng Mặt", "2021-06-15", "Đang làm việc"});
        tableModel.addRow(new Object[]{"BS003", "BS. Trần Minh Trí", "1990-11-05", "Nam", "0903456789", "tri.tran@nhakhoaabc.vn", "Bác sĩ Chuyên khoa RHM", "BS Răng Hàm Mặt Chính quy", "2022-09-01", "Đang làm việc"});
        tableModel.addRow(new Object[]{"QL001", "Trần Thị Hoàng Lan", "1983-04-12", "Nữ", "0904567890", "lan.tran@nhakhoaabc.vn", "Quản lý", "ThS Quản lý Y tế", "2019-11-10", "Đang làm việc"});
        tableModel.addRow(new Object[]{"YT001", "Đỗ Thùy Trang", "1997-08-30", "Nữ", "0905678901", "trang.do@nhakhoaabc.vn", "Y tá / Phụ tá", "Cử nhân Điều dưỡng Nha khoa", "2023-02-20", "Đang làm việc"});
    }

    private void napFormTuBang(int row) {
        txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        txtNgaySinh.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        cbGioiTinh.setSelectedItem(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Nam");
        txtSDT.setText(tableModel.getValueAt(row, 4).toString());
        txtEmail.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        cbChucDanh.setSelectedItem(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "Bác sĩ Chuyên khoa RHM");
        txtBangCap.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");
        txtNgayVaoLam.setText(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "");
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 9) != null ? tableModel.getValueAt(row, 9).toString() : "Đang làm việc");
    }

    private void xoaTrangForm() {
        txtMaNV.setText("NV001");
        txtHoTen.setText("");
        txtNgaySinh.setText("1990-01-01");
        cbGioiTinh.setSelectedIndex(0);
        txtSDT.setText("");
        txtEmail.setText("");
        txtBangCap.setText("BS Răng Hàm Mặt");
        txtNgayVaoLam.setText("2024-01-01");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void xuLyThem() {
        // TODO: Cài đặt chức năng thêm nhân sự vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Thêm nhân sự đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyCapNhat() {
        // TODO: Cài đặt chức năng cập nhật hồ sơ nhân sự vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Cập nhật hồ sơ nhân sự đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        // TODO: Cài đặt chức năng tìm kiếm nhân sự trong CSDL tại đây
    }

    private boolean kiemTraHopLe() {
        // TODO: Cài đặt kiểm tra tính hợp lệ dữ liệu nhập tại đây
        return true;
    }
}
