package gui;

import dao.BenhNhanDAO;
import entity.BenhNhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Giao diện Hồ sơ Bệnh nhân - Thiết kế phẳng hiện đại
 */
public class PanelBenhNhanModern extends JPanel {

    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaBN, txtHoTen, txtSDT, txtDiaChi, txtNgaySinh, txtTienSuBenh, txtDiemTichLuy, txtTimKiem;
    private JComboBox<String> cbGioiTinh, cbLoaiKhach;
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTimKiem;
    private final boolean readOnly;

    public PanelBenhNhanModern() {
        this(false);
    }

    public PanelBenhNhanModern(boolean readOnly) {
        this.readOnly = readOnly;
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER & SEARCH
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel(readOnly ? "TRA CỨU BỆNH NHÂN" : "HỒ SƠ BỆNH NHÂN & THẺ THÀNH VIÊN");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(readOnly
                ? "Xem thông tin bệnh nhân và tiền sử bệnh lý phục vụ khám, điều trị"
                : "Quản lý thông tin hành chính, tiền sử bệnh lý và phân hạng khách hàng");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Nhập mã, tên hoặc SĐT bệnh nhân...");

        btnTimKiem = ModernUITheme.createButton("Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE (Bảng phía trên, Form phía dưới)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.62);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG DỮ LIỆU BỆNH NHÂN
        String[] cols = {"Mã BN", "Họ Tên", "Số Điện Thoại", "Địa Chỉ", "Giới Tính", "Ngày Sinh", "Tiền Sử Bệnh Lý", "Hạng Thẻ", "Điểm Tích Lũy"};
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

        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT HỒ SƠ BỆNH NHÂN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        // Grid fields
        JPanel pnlFields = new JPanel(new GridLayout(2, 4, 14, 10));
        pnlFields.setOpaque(false);

        txtMaBN = ModernUITheme.createTextField(10);
        txtMaBN.setEditable(false);
        txtMaBN.setBackground(new Color(241, 245, 249));

        txtHoTen = ModernUITheme.createTextField(18);
        txtSDT = ModernUITheme.createTextField(12);
        txtDiaChi = ModernUITheme.createTextField(20);
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        txtNgaySinh = ModernUITheme.createTextField(10);
        txtNgaySinh.setToolTipText("Định dạng: yyyy-MM-dd");
        txtTienSuBenh = ModernUITheme.createTextField(25);
        txtTienSuBenh.setToolTipText("Dị ứng thuốc tê, cao huyết áp, tiểu đường...");

        cbLoaiKhach = new JComboBox<>(new String[]{"Khách mới", "Thường", "Thân thiết", "VIP"});
        txtDiemTichLuy = ModernUITheme.createTextField(8);
        txtDiemTichLuy.setText("0");

        pnlFields.add(createFieldItem("Mã bệnh nhân:", txtMaBN));
        pnlFields.add(createFieldItem("Họ và tên (*):", txtHoTen));
        pnlFields.add(createFieldItem("Số điện thoại (*):", txtSDT));
        pnlFields.add(createFieldItem("Giới tính:", cbGioiTinh));
        pnlFields.add(createFieldItem("Ngày sinh (yyyy-MM-dd):", txtNgaySinh));
        pnlFields.add(createFieldItem("Địa chỉ thường trú:", txtDiaChi));
        pnlFields.add(createFieldItem("Tiền sử bệnh lý / Dị ứng:", txtTienSuBenh));
        pnlFields.add(createFieldItem("Hạng thẻ thành viên:", cbLoaiKhach));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Nút bấm thao tác
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createButton("Thêm mới hồ sơ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createButton("Cập nhật", ModernUITheme.ACCENT, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("Làm mới form", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnThem);
        if (readOnly) {
            btnThem.setVisible(false);
            btnCapNhat.setVisible(false);
            btnLamMoi.setVisible(false);
            txtHoTen.setEditable(false);
            txtSDT.setEditable(false);
            txtDiaChi.setEditable(false);
            txtNgaySinh.setEditable(false);
            txtTienSuBenh.setEditable(false);
            txtDiemTichLuy.setEditable(false);
            cbGioiTinh.setEnabled(false);
            cbLoaiKhach.setEnabled(false);
        }
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Sự kiện
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
        // TODO: Cài đặt tải danh sách bệnh nhân từ CSDL tại đây
        napDuLieuMau();
        xoaTrangForm();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"BN001", "Nguyễn Văn Tuấn", "0912345678", "Quận 1, TP.HCM", "Nam", "1988-05-12", "Không có", "VIP", 350});
        tableModel.addRow(new Object[]{"BN002", "Trần Mai Phương", "0987654321", "Bình Thạnh, TP.HCM", "Nữ", "1995-10-24", "Dị ứng Aspirin", "Thân thiết", 180});
        tableModel.addRow(new Object[]{"BN003", "Phạm Quốc Dũng", "0908112233", "Gò Vấp, TP.HCM", "Nam", "1990-03-15", "Tiền sử cao huyết áp", "Thường", 50});
        tableModel.addRow(new Object[]{"BN004", "Vũ Bích Ngọc", "0933445566", "Phú Nhuận, TP.HCM", "Nữ", "2000-08-09", "Không có", "VIP", 520});
        tableModel.addRow(new Object[]{"BN005", "Đặng Hoàng Nam", "0977889900", "Quận 3, TP.HCM", "Nam", "1992-12-01", "Dị ứng Penicillin", "Thường", 30});
    }

    private void napFormTuBang(int row) {
        txtMaBN.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        txtSDT.setText(tableModel.getValueAt(row, 2).toString());
        txtDiaChi.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        cbGioiTinh.setSelectedItem(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "Nam");
        txtNgaySinh.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        txtTienSuBenh.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
        cbLoaiKhach.setSelectedItem(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "Khách mới");
        txtDiemTichLuy.setText(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "0");
    }

    private void xoaTrangForm() {
        txtMaBN.setText("BN001");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        cbGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("1995-01-01");
        txtTienSuBenh.setText("");
        cbLoaiKhach.setSelectedIndex(0);
        txtDiemTichLuy.setText("0");
        table.clearSelection();
    }

    private void xuLyThem() {
        // TODO: Cài đặt chức năng thêm hồ sơ bệnh nhân vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Thêm bệnh nhân đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyCapNhat() {
        // TODO: Cài đặt chức năng cập nhật hồ sơ bệnh nhân vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Cập nhật bệnh nhân đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        // TODO: Cài đặt chức năng tìm kiếm hồ sơ bệnh nhân trong CSDL tại đây
    }

    private boolean kiemTraHopLe() {
        // TODO: Cài đặt kiểm tra tính hợp lệ dữ liệu nhập tại đây
        return true;
    }
}
