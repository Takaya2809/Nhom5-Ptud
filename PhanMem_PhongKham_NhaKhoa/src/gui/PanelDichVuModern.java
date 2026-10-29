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
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTimKiem, btnXoa;
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
        btnTimKiem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 15, Color.WHITE), "Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);

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
        String[] cols = { "Mã Dịch Vụ", "Tên Dịch Vụ Nha Khoa", "Đơn Giá Niêm Yết", "Thời Gian (Phút)", "Trạng Thái",
                "Mô Tả Kỹ Thuật" };
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
        cbTrangThai = new JComboBox<>(new String[] { "Đang cung cấp", "Tạm ngừng cung cấp" });
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

        btnThem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 15, Color.WHITE), "Thêm dịch vụ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Cập nhật giá", ModernUITheme.ACCENT, Color.WHITE);
        btnXoa = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 15, Color.WHITE), "Ngừng dịch vụ", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 15, ModernUITheme.TEXT_MUTED), "Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXoa);
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
        List<DichVu> list = dichVuDAO.getAllDichVu();
        for (DichVu dv : list) {
            tableModel.addRow(new Object[] {
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    df.format(dv.getGiaNiemYet()),
                    dv.getThoiGianDuKien(),
                    dv.getTrangThai() != null ? dv.getTrangThai() : "Đang cung cấp",
                    dv.getMoTa() != null ? dv.getMoTa() : ""
            });
        }
        xoaTrangForm();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount())
            return;
        txtMaDV.setText(tableModel.getValueAt(row, 0).toString());
        txtTenDV.setText(tableModel.getValueAt(row, 1).toString());
        String dgStr = tableModel.getValueAt(row, 2).toString().replaceAll("[^0-9]", "");
        txtDonGia.setText(dgStr);
        txtThoiGian.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "30");
        cbTrangThai.setSelectedItem(
                tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "Đang cung cấp");
        txtMoTa.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
    }

    private void xoaTrangForm() {
        txtMaDV.setText(dichVuDAO.sinhMaDVMoi());
        txtTenDV.setText("");
        txtDonGia.setText("");
        txtThoiGian.setText("45");
        cbTrangThai.setSelectedIndex(0);
        txtMoTa.setText("");
        table.clearSelection();
    }

    private void xuLyThem() {
        if (!kiemTraHopLe())
            return;

        String maDV = txtMaDV.getText().trim();
        if (maDV.isEmpty()) {
            maDV = dichVuDAO.sinhMaDVMoi();
        }
        String tenDV = txtTenDV.getText().trim();
        double donGia = Double.parseDouble(txtDonGia.getText().trim().replaceAll("[^0-9]", ""));
        int thoiGian = 30;
        try {
            thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
        } catch (Exception ignored) {
        }
        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString()
                : "Đang cung cấp";
        String moTa = txtMoTa.getText().trim();

        DichVu dv = new DichVu(maDV, tenDV, donGia, thoiGian, trangThai, moTa);
        boolean thanhCong = dichVuDAO.themDichVu(dv);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ [" + maDV + " - " + tenDV + "] thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm dịch vụ. Tên dịch vụ có thể đã tồn tại!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        int row = table.getSelectedRow();
        String maDV = txtMaDV.getText().trim();
        if (row < 0 && (maDV.isEmpty() || maDV.equals(dichVuDAO.sinhMaDVMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dịch vụ trong danh sách để cập nhật!", "Nhắc nhở",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!kiemTraHopLe())
            return;

        String tenDV = txtTenDV.getText().trim();
        double donGia = Double.parseDouble(txtDonGia.getText().trim().replaceAll("[^0-9]", ""));
        int thoiGian = 30;
        try {
            thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
        } catch (Exception ignored) {
        }
        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString()
                : "Đang cung cấp";
        String moTa = txtMoTa.getText().trim();

        DichVu dv = new DichVu(maDV, tenDV, donGia, thoiGian, trangThai, moTa);
        boolean thanhCong = dichVuDAO.capNhatDichVu(dv);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ [" + maDV + "] thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieu();
            return;
        }
        keyword = keyword.trim();
        List<DichVu> list = dichVuDAO.timKiem(keyword);
        tableModel.setRowCount(0);
        for (DichVu dv : list) {
            tableModel.addRow(new Object[] {
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    df.format(dv.getGiaNiemYet()),
                    dv.getThoiGianDuKien(),
                    dv.getTrangThai() != null ? dv.getTrangThai() : "Đang cung cấp",
                    dv.getMoTa() != null ? dv.getMoTa() : ""
            });
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dịch vụ nào khớp với từ khóa: \"" + keyword + "\"",
                    "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean kiemTraHopLe() {
        String tenDV = txtTenDV.getText().trim();
        if (tenDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên dịch vụ không được để trống!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            txtTenDV.requestFocus();
            return false;
        }
        String dgStr = txtDonGia.getText().trim().replaceAll("[^0-9]", "");
        if (dgStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn giá niêm yết không được để trống!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return false;
        }
        try {
            double dg = Double.parseDouble(dgStr);
            if (dg < 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải là số dương!", "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
                txtDonGia.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return false;
        }
        String tgStr = txtThoiGian.getText().trim();
        if (!tgStr.isEmpty() && !tgStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Thời gian dự kiến phải là số phút nguyên dương!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            txtThoiGian.requestFocus();
            return false;
        }
        return true;
    }

    private void xuLyXoa() {
        int row = table.getSelectedRow();
        String maDV = txtMaDV.getText().trim();
        if (row < 0 && (maDV.isEmpty() || maDV.equals(dichVuDAO.sinhMaDVMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dịch vụ cần xóa hoặc tạm ngừng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (row >= 0) {
            maDV = tableModel.getValueAt(row, 0).toString();
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa hoặc tạm ngừng dịch vụ [" + maDV + "]?\n(Nếu dịch vụ đã từng được xuất hóa đơn, hệ thống sẽ tự động cập nhật trạng thái thành 'Tạm ngừng cung cấp')",
            "Xác nhận xóa / Ngừng dịch vụ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean kq = dichVuDAO.xoaDichVu(maDV);
            if (kq) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật/xóa dịch vụ [" + maDV + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa dịch vụ này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
