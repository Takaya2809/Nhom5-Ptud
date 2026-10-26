package gui;

import dao.VatTuDAO;
import entity.VatTu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Giao diện Quản lý Kho Dược & Vật tư Nha khoa - Thiết kế phẳng hiện đại
 */
public class PanelKhoVatTuModern extends JPanel {

    private VatTuDAO vatTuDAO = new VatTuDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaVT, txtTenVT, txtDonViTinh, txtSoLuong, txtDinhMuc, txtDonGia, txtTimKiem;
    private JComboBox<String> cbLoaiVT, cbTrangThai;
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTimKiem;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PanelKhoVatTuModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("KHO DƯỢC PHẨM & VẬT TƯ TIÊU HAO Y TẾ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Theo dõi tồn kho thuốc tê, trụ Implant, composite, kim tiêm và kiểm soát định mức tối thiểu");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm mã, tên hoặc loại vật tư...");
        btnTimKiem = ModernUITheme.createButton("Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu kho:"));
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

        // BẢNG VẬT TƯ
        String[] cols = { "Mã Vật Tư", "Tên Thuốc / Vật Tư Y Tế", "Phân Loại", "Đơn Vị Tính", "Số Lượng Tồn",
                "Định Mức Tối Thiểu", "Đơn Giá", "Trạng Thái Kho" };
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

        // FORM CARD
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("NHẬP VẬT TƯ & CẬP NHẬT ĐỊNH MỨC KHO");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 4, 14, 10));
        pnlFields.setOpaque(false);

        txtMaVT = ModernUITheme.createTextField(10);
        txtMaVT.setEditable(false);
        txtMaVT.setBackground(new Color(241, 245, 249));

        txtTenVT = ModernUITheme.createTextField(18);
        cbLoaiVT = new JComboBox<>(new String[] { "Tiêu hao", "Dụng cụ", "Thiết bị" });
        txtDonViTinh = ModernUITheme.createTextField(10);
        txtSoLuong = ModernUITheme.createTextField(8);
        txtDinhMuc = ModernUITheme.createTextField(8);
        txtDinhMuc.setText("10");
        txtDonGia = ModernUITheme.createTextField(12);

        cbTrangThai = new JComboBox<>(new String[] { "Còn hàng", "Cảnh báo hết hàng", "Hết hàng", "Ngừng sử dụng" });

        pnlFields.add(createFieldItem("Mã vật tư:", txtMaVT));
        pnlFields.add(createFieldItem("Tên thuốc / vật tư (*):", txtTenVT));
        pnlFields.add(createFieldItem("Loại vật tư:", cbLoaiVT));
        pnlFields.add(createFieldItem("Đơn vị tính (*):", txtDonViTinh));
        pnlFields.add(createFieldItem("Số lượng tồn kho (*):", txtSoLuong));
        pnlFields.add(createFieldItem("Định mức tối thiểu:", txtDinhMuc));
        pnlFields.add(createFieldItem("Đơn giá (VNĐ):", txtDonGia));
        pnlFields.add(createFieldItem("Trạng thái kho:", cbTrangThai));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createButton("Thêm vào kho", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createButton("Cập nhật tồn kho", ModernUITheme.ACCENT, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("Làm mới", ModernUITheme.TEXT_MUTED);

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
        // TODO: Cài đặt tải danh sách kho vật tư từ CSDL tại đây
        napDuLieuMau();
        xoaTrangForm();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[] { "VT001", "Thuốc tê Septanest 1/100.000", "Tiêu hao", "Ống", 250, 50,
                df.format(25000), "Còn hàng" });
        tableModel.addRow(new Object[] { "VT002", "Trụ Implant Straumann SLA", "Tiêu hao", "Trụ", 18, 5,
                df.format(9500000), "Còn hàng" });
        tableModel.addRow(new Object[] { "VT003", "Composite đặc Filtek Z250 (3M)", "Tiêu hao", "Tuýp", 8, 10,
                df.format(420000), "Cảnh báo hết hàng" });
        tableModel.addRow(new Object[] { "VT004", "Găng tay cao su y tế tiệt trùng", "Tiêu hao", "Hộp", 50, 15,
                df.format(95000), "Còn hàng" });
        tableModel.addRow(new Object[] { "VT005", "Kim tiêm nha khoa 27G Short", "Tiêu hao", "Hộp", 12, 10,
                df.format(120000), "Còn hàng" });
    }

    private void napFormTuBang(int row) {
        txtMaVT.setText(tableModel.getValueAt(row, 0).toString());
        txtTenVT.setText(tableModel.getValueAt(row, 1).toString());
        cbLoaiVT.setSelectedItem(
                tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "Tiêu hao");
        txtDonViTinh.setText(tableModel.getValueAt(row, 3).toString());
        txtSoLuong.setText(tableModel.getValueAt(row, 4).toString());
        txtDinhMuc.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "10");
        String giaStr = tableModel.getValueAt(row, 6).toString().replaceAll("[^0-9]", "");
        txtDonGia.setText(giaStr);
        cbTrangThai.setSelectedItem(
                tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "Còn hàng");
    }

    private void xoaTrangForm() {
        txtMaVT.setText("VT001");
        txtTenVT.setText("");
        cbLoaiVT.setSelectedIndex(0);
        txtDonViTinh.setText("");
        txtSoLuong.setText("100");
        txtDinhMuc.setText("10");
        txtDonGia.setText("50000");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void xuLyThem() {
        // TODO: Cài đặt chức năng thêm vật tư vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Thêm vật tư vào kho đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyCapNhat() {
        // TODO: Cài đặt chức năng cập nhật tồn kho vật tư vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Cập nhật tồn kho đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        // TODO: Cài đặt chức năng tìm kiếm vật tư trong CSDL tại đây
    }

    private boolean kiemTraHopLe() {
        // TODO: Cài đặt kiểm tra tính hợp lệ dữ liệu nhập tại đây
        return true;
    }
}
