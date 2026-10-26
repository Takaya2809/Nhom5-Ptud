package gui;

import dao.DichVuDAO;
import entity.DichVu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Giao diện Dịch vụ & Bảng giá thủ thuật Nha khoa - Phong cách hiện đại
 */
public class PanelDichVuModern extends JPanel {

    private DichVuDAO dichVuDAO = new DichVuDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaDV, txtTenDV, txtDonGia, txtThoiGian, txtMoTa, txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTimKiem;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PanelDichVuModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("DANH MỤC DỊCH VỤ & BẢNG GIÁ ĐIỀU TRỊ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý danh mục kỹ thuật nha khoa, giá dịch vụ niêm yết và thời gian thực hiện");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm mã hoặc tên dịch vụ...");
        btnTimKiem = ModernUITheme.createButton("Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG DỊCH VỤ
        String[] cols = {"Mã Dịch Vụ", "Tên Dịch Vụ Nha Khoa", "Đơn Giá Niêm Yết", "Thời Gian (Phút)", "Trạng Thái", "Mô Tả Kỹ Thuật"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        splitPane.setTopComponent(scrollPane);

        // FORM CARD
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT DỊCH VỤ NHA KHOA");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 3, 16, 10));
        pnlFields.setOpaque(false);

        txtMaDV = ModernUITheme.createTextField(10);
        txtMaDV.setEditable(false);
        txtMaDV.setBackground(new Color(241, 245, 249));

        txtTenDV = ModernUITheme.createTextField(20);
        txtDonGia = ModernUITheme.createTextField(12);
        txtThoiGian = ModernUITheme.createTextField(8);
        txtThoiGian.setText("45");
        cbTrangThai = new JComboBox<>(new String[]{"Đang cung cấp", "Tạm ngừng cung cấp"});
        txtMoTa = ModernUITheme.createTextField(25);

        pnlFields.add(createFieldItem("Mã dịch vụ:", txtMaDV));
        pnlFields.add(createFieldItem("Tên dịch vụ nha khoa (*):", txtTenDV));
        pnlFields.add(createFieldItem("Đơn giá niêm yết (VNĐ) (*):", txtDonGia));
        pnlFields.add(createFieldItem("Thời gian dự kiến (phút):", txtThoiGian));
        pnlFields.add(createFieldItem("Trạng thái cung cấp:", cbTrangThai));
        pnlFields.add(createFieldItem("Mô tả kỹ thuật điều trị:", txtMoTa));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createButton("Thêm dịch vụ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createButton("Cập nhật giá", ModernUITheme.ACCENT, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnThem);
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
        // TODO: Cài đặt tải danh mục dịch vụ từ CSDL tại đây
        napDuLieuMau();
        xoaTrangForm();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"DV001", "Cấy ghép Implant Straumann Thụy Sĩ", df.format(22000000), 60, "Đang cung cấp", "Trụ Titanium cao cấp tích hợp xương nhanh"});
        tableModel.addRow(new Object[]{"DV002", "Niềng răng mắc cài kim loại tự buộc", df.format(30000000), 90, "Đang cung cấp", "Thời gian điều trị 18-24 tháng"});
        tableModel.addRow(new Object[]{"DV003", "Bọc răng toàn sứ Cercon HT", df.format(4500000), 45, "Đang cung cấp", "Độ cứng 1200 MPa, bảo hành chính hãng 10 năm"});
        tableModel.addRow(new Object[]{"DV004", "Nhổ răng khôn mọc lệch bằng máy Piezotome", df.format(2000000), 30, "Đang cung cấp", "Nhổ bằng sóng siêu âm không đau, liền thương nhanh"});
        tableModel.addRow(new Object[]{"DV005", "Trám răng thẩm mỹ Composite 3M", df.format(400000), 30, "Đang cung cấp", "Vật liệu Composite trùng màu men răng"});
        tableModel.addRow(new Object[]{"DV006", "Tẩy trắng răng công nghệ Laser Whitening", df.format(1800000), 45, "Đang cung cấp", "Trắng bật 2-3 tone chỉ sau 45 phút"});
        tableModel.addRow(new Object[]{"DV007", "Lấy cao răng siêu âm & Đánh bóng", df.format(200000), 20, "Đang cung cấp", "Làm sạch mảng bám không ê buốt"});
    }

    private void napFormTuBang(int row) {
        txtMaDV.setText(tableModel.getValueAt(row, 0).toString());
        txtTenDV.setText(tableModel.getValueAt(row, 1).toString());
        String dgStr = tableModel.getValueAt(row, 2).toString().replaceAll("[^0-9]", "");
        txtDonGia.setText(dgStr);
        txtThoiGian.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "30");
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "Đang cung cấp");
        txtMoTa.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
    }

    private void xoaTrangForm() {
        txtMaDV.setText("DV001");
        txtTenDV.setText("");
        txtDonGia.setText("");
        txtThoiGian.setText("45");
        cbTrangThai.setSelectedIndex(0);
        txtMoTa.setText("");
        table.clearSelection();
    }

    private void xuLyThem() {
        // TODO: Cài đặt chức năng thêm dịch vụ mới vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Thêm dịch vụ đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyCapNhat() {
        // TODO: Cài đặt chức năng cập nhật thông tin dịch vụ vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Cập nhật dịch vụ đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        // TODO: Cài đặt chức năng tìm kiếm dịch vụ trong CSDL tại đây
    }

    private boolean kiemTraHopLe() {
        // TODO: Cài đặt kiểm tra tính hợp lệ dữ liệu nhập tại đây
        return true;
    }
}
