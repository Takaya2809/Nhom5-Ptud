package gui;

import dao.UuDaiDAO;
import entity.UuDai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Giao diện Chính Sách Ưu Đãi & Khuyến Mại Nha Khoa - Phong cách hiện đại
 */
public class PanelUuDaiModern extends JPanel {

    private UuDaiDAO uuDaiDAO = new UuDaiDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaUD, txtTenUD, txtPhanTramGiam, txtNgayBD, txtNgayKT, txtDieuKien, txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JButton btnThem, btnCapNhat, btnDoiTrangThai, btnLamMoi, btnTimKiem;

    public PanelUuDaiModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("CHÍNH SÁCH ƯU ĐÃI & KHUYẾN MẠI NHA KHOA");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý voucher giảm giá, chính sách tri ân khách hàng thân thiết, khách hàng VIP");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm mã hoặc tên chương trình ưu đãi...");
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

        // BẢNG ƯU ĐÃI
        String[] cols = {"Mã Ưu Đãi", "Tên Chương Trình Khuyến Mại", "Mức Giảm (%)", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Điều Kiện Áp Dụng", "Trạng Thái"};
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

        JLabel lblFormTitle = new JLabel("🎁 THIẾT LẬP CHÍNH SÁCH ƯU ĐÃI DỊCH VỤ");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 4, 14, 10));
        pnlFields.setOpaque(false);

        txtMaUD = ModernUITheme.createTextField(10);
        txtMaUD.setEditable(false);
        txtMaUD.setBackground(new Color(241, 245, 249));

        txtTenUD = ModernUITheme.createTextField(18);
        txtPhanTramGiam = ModernUITheme.createTextField(8);
        txtPhanTramGiam.setToolTipText("Từ 1 đến 100%");
        txtNgayBD = ModernUITheme.createTextField(10);
        txtNgayBD.setText("2026-10-01");
        txtNgayKT = ModernUITheme.createTextField(10);
        txtNgayKT.setText("2026-12-31");
        txtDieuKien = ModernUITheme.createTextField(22);
        txtDieuKien.setText("Hóa đơn từ 2.000.000 VNĐ");
        cbTrangThai = new JComboBox<>(new String[]{"Đang áp dụng", "Hết hạn", "Tạm dừng"});

        pnlFields.add(createFieldItem("Mã ưu đãi:", txtMaUD));
        pnlFields.add(createFieldItem("Tên chương trình ưu đãi (*):", txtTenUD));
        pnlFields.add(createFieldItem("% Giảm giá (*):", txtPhanTramGiam));
        pnlFields.add(createFieldItem("Ngày bắt đầu (yyyy-MM-dd):", txtNgayBD));
        pnlFields.add(createFieldItem("Ngày kết thúc (yyyy-MM-dd):", txtNgayKT));
        pnlFields.add(createFieldItem("Điều kiện áp dụng:", txtDieuKien));
        pnlFields.add(createFieldItem("Trạng thái:", cbTrangThai));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createButton("➕ Thêm ưu đãi", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createButton("💾 Cập nhật", ModernUITheme.ACCENT, Color.WHITE);
        btnDoiTrangThai = ModernUITheme.createButton("🔄 Bật / Tắt trạng thái", ModernUITheme.WARNING, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnDoiTrangThai);
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
        btnDoiTrangThai.addActionListener(e -> xuLyDoiTrangThai());
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
        // TODO: Cài đặt tải danh sách chính sách ưu đãi từ CSDL tại đây
        napDuLieuMau();
        xoaTrangForm();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"UD001", "Chào Hè Nụ Cười Xinh", "15%", "2026-06-01", "2026-08-31", "Áp dụng niềng răng & bọc sứ", "Đang áp dụng"});
        tableModel.addRow(new Object[]{"UD002", "Tri Ân Khách Hàng Thân Thiết", "10%", "2026-01-01", "2026-12-31", "Áp dụng khách có thẻ VIP", "Đang áp dụng"});
        tableModel.addRow(new Object[]{"UD003", "Trợ Giá Trồng Răng Implant", "20%", "2026-09-01", "2026-10-31", "Cho bệnh nhân từ 50 tuổi", "Đang áp dụng"});
        tableModel.addRow(new Object[]{"UD004", "Tẩy Trắng Răng Đón Tết", "25%", "2026-01-01", "2026-02-15", "Tất cả khách hàng", "Hết hạn"});
    }

    private void napFormTuBang(int row) {
        txtMaUD.setText(tableModel.getValueAt(row, 0).toString());
        txtTenUD.setText(tableModel.getValueAt(row, 1).toString());
        String pt = tableModel.getValueAt(row, 2).toString().replace("%", "").trim();
        txtPhanTramGiam.setText(pt);
        txtNgayBD.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtNgayKT.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        txtDieuKien.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "Đang áp dụng");
    }

    private void xoaTrangForm() {
        txtMaUD.setText("UD001");
        txtTenUD.setText("");
        txtPhanTramGiam.setText("10");
        txtNgayBD.setText("2026-10-01");
        txtNgayKT.setText("2026-12-31");
        txtDieuKien.setText("Hóa đơn từ 1.000.000 VNĐ");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void xuLyThem() {
        // TODO: Cài đặt chức năng thêm chính sách ưu đãi vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Thêm ưu đãi đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyCapNhat() {
        // TODO: Cài đặt chức năng cập nhật chính sách ưu đãi trong CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Cập nhật ưu đãi đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyDoiTrangThai() {
        // TODO: Cài đặt chức năng thay đổi trạng thái ưu đãi trong CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Bật/Tắt trạng thái ưu đãi đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        // TODO: Cài đặt chức năng tra cứu ưu đãi tại đây
    }

    private boolean kiemTraHopLe() {
        // TODO: Cài đặt kiểm tra tính hợp lệ dữ liệu nhập tại đây
        return true;
    }
}
