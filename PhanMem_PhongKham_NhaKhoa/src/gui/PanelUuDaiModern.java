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
    private JButton btnThem, btnCapNhat, btnDoiTrangThai, btnLamMoi, btnTimKiem, btnXoa;

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
        btnXoa = ModernUITheme.createButton("🗑️ Xóa ưu đãi", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXoa);
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
        btnXoa.addActionListener(e -> xuLyXoa());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTaiLai.addActionListener(e -> {
            txtTimKiem.setText("");
            taiDuLieu();
        });

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
        tableModel.setRowCount(0);
        List<UuDai> list = uuDaiDAO.getAllUuDai();
        for (UuDai ud : list) {
            tableModel.addRow(new Object[]{
                ud.getMaUuDai(),
                ud.getTenUuDai(),
                (int)ud.getPhanTramGiam() + "%",
                ud.getNgayBD() != null ? ud.getNgayBD().toString() : "",
                ud.getNgayKT() != null ? ud.getNgayKT().toString() : "",
                ud.getDieuKien() != null ? ud.getDieuKien() : "",
                ud.getTrangThai() != null ? ud.getTrangThai() : "Đang áp dụng"
            });
        }
        xoaTrangForm();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
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
        txtMaUD.setText(uuDaiDAO.sinhMaUuDaiMoi());
        txtTenUD.setText("");
        txtPhanTramGiam.setText("10");
        txtNgayBD.setText("2026-10-01");
        txtNgayKT.setText("2026-12-31");
        txtDieuKien.setText("Áp dụng khách hàng điều trị");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void xuLyThem() {
        if (!kiemTraHopLe()) return;

        String maUD = txtMaUD.getText().trim();
        if (maUD.isEmpty()) {
            maUD = uuDaiDAO.sinhMaUuDaiMoi();
        }
        String tenUD = txtTenUD.getText().trim();
        double pt = Double.parseDouble(txtPhanTramGiam.getText().trim());
        Date nb = Date.valueOf(txtNgayBD.getText().trim());
        Date nk = Date.valueOf(txtNgayKT.getText().trim());
        String dk = txtDieuKien.getText().trim();
        String tt = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "Đang áp dụng";

        UuDai ud = new UuDai(maUD, tenUD, pt, dk, nb, nk, tt);
        boolean thanhCong = uuDaiDAO.themUuDai(ud);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Thêm chính sách ưu đãi [" + maUD + " - " + tenUD + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm ưu đãi thất bại. Mã có thể đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        int row = table.getSelectedRow();
        String maUD = txtMaUD.getText().trim();
        if (row < 0 && (maUD.isEmpty() || maUD.equals(uuDaiDAO.sinhMaUuDaiMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 ưu đãi trong danh sách để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!kiemTraHopLe()) return;

        String tenUD = txtTenUD.getText().trim();
        double pt = Double.parseDouble(txtPhanTramGiam.getText().trim());
        Date nb = Date.valueOf(txtNgayBD.getText().trim());
        Date nk = Date.valueOf(txtNgayKT.getText().trim());
        String dk = txtDieuKien.getText().trim();
        String tt = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "Đang áp dụng";

        UuDai ud = new UuDai(maUD, tenUD, pt, dk, nb, nk, tt);
        boolean thanhCong = uuDaiDAO.capNhatUuDai(ud);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Cập nhật ưu đãi [" + maUD + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyDoiTrangThai() {
        int row = table.getSelectedRow();
        String maUD = txtMaUD.getText().trim();
        if (row < 0 && maUD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 ưu đãi trong bảng để bật/tắt trạng thái!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ttHienTai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : "Đang áp dụng";
        String ttMoi = "Đang áp dụng".equalsIgnoreCase(ttHienTai) ? "Hết hạn" : "Đang áp dụng";
        boolean thanhCong = uuDaiDAO.doiTrangThai(maUD, ttMoi);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái ưu đãi [" + maUD + "] sang: " + ttMoi, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieu();
            return;
        }
        keyword = keyword.trim();
        List<UuDai> list = uuDaiDAO.timKiemUuDai(keyword);
        tableModel.setRowCount(0);
        for (UuDai ud : list) {
            tableModel.addRow(new Object[]{
                ud.getMaUuDai(),
                ud.getTenUuDai(),
                (int)ud.getPhanTramGiam() + "%",
                ud.getNgayBD() != null ? ud.getNgayBD().toString() : "",
                ud.getNgayKT() != null ? ud.getNgayKT().toString() : "",
                ud.getDieuKien() != null ? ud.getDieuKien() : "",
                ud.getTrangThai() != null ? ud.getTrangThai() : "Đang áp dụng"
            });
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chương trình ưu đãi nào khớp với từ khóa: \"" + keyword + "\"", "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean kiemTraHopLe() {
        String tenUD = txtTenUD.getText().trim();
        if (tenUD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên chương trình ưu đãi không được để trống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtTenUD.requestFocus();
            return false;
        }
        String ptStr = txtPhanTramGiam.getText().trim();
        try {
            double pt = Double.parseDouble(ptStr);
            if (pt <= 0 || pt > 100) {
                JOptionPane.showMessageDialog(this, "Phần trăm giảm giá phải nằm trong khoảng từ 1% đến 100%!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                txtPhanTramGiam.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Phần trăm giảm giá không hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtPhanTramGiam.requestFocus();
            return false;
        }
        String nbStr = txtNgayBD.getText().trim();
        String nkStr = txtNgayKT.getText().trim();
        if (!nbStr.matches("\\d{4}-\\d{2}-\\d{2}") || !nkStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu và ngày kết thúc phải theo định dạng yyyy-MM-dd!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Date nb = Date.valueOf(nbStr);
            Date nk = Date.valueOf(nkStr);
            if (nk.before(nb)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc không được trước ngày bắt đầu!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                txtNgayKT.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày tháng không hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void xuLyXoa() {
        int row = table.getSelectedRow();
        String maUD = txtMaUD.getText().trim();
        if (row < 0 && (maUD.isEmpty() || maUD.equals(uuDaiDAO.sinhMaUDMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 chương trình ưu đãi trong bảng để xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (row >= 0) maUD = tableModel.getValueAt(row, 0).toString();

        int cf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa mã ưu đãi [" + maUD + "] khỏi hệ thống CSDL?", "Xác nhận xóa ưu đãi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (cf == JOptionPane.YES_OPTION) {
            boolean ok = uuDaiDAO.xoaUuDai(maUD);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa ưu đãi [" + maUD + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa ưu đãi này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
