package gui;

import dao.ThuocDAO;
import dao.VatTuDAO;
import entity.Thuoc;
import entity.VatTu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Giao diện Quản lý Kho Dược & Vật tư Nha khoa - Thiết kế phẳng hiện đại
 * Tích hợp 2 phân hệ: Vật tư y tế & Thuốc kê đơn trực tiếp từ CSDL QLPHONGKHAM
 */
public class PanelKhoVatTuModern extends JPanel {

    private VatTuDAO vatTuDAO = new VatTuDAO();
    private ThuocDAO thuocDAO = new ThuocDAO();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    // TAB 1: VẬT TƯ
    private JTable tableVT;
    private DefaultTableModel modelVT;
    private JTextField txtMaVT, txtTenVT, txtDonViTinhVT, txtSoLuongVT, txtDinhMucVT, txtDonGiaVT, txtTimKiemVT;
    private JComboBox<String> cbLoaiVT, cbTrangThaiVT;
    private JButton btnThemVT, btnCapNhatVT, btnXoaVT, btnLamMoiVT, btnTimKiemVT, btnNhapXuatVT;

    // TAB 2: THUỐC
    private JTable tableThuoc;
    private DefaultTableModel modelThuoc;
    private JTextField txtMaThuoc, txtTenThuoc, txtHoatChat, txtDonViTinhThuoc, txtDonGiaThuoc, txtSoLuongThuoc, txtDinhMucThuoc, txtHSD, txtCachDung, txtTimKiemThuoc;
    private JComboBox<String> cbTrangThaiThuoc;
    private JButton btnThemThuoc, btnCapNhatThuoc, btnXoaThuoc, btnLamMoiThuoc, btnTimKiemThuoc, btnNhapXuatThuoc;

    public PanelKhoVatTuModern() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("KHO DƯỢC PHẨM & VẬT TƯ Y TẾ NHA KHOA");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Theo dõi tồn kho thuốc điều trị, trụ Implant, composite, kim tiêm & cảnh báo định mức tối thiểu");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. TABBED PANE CHÍNH
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernUITheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Vật Tư Tiêu Hao & Dụng Cụ Nha Khoa", new AppIcon(AppIcon.Type.INVENTORY, 16, ModernUITheme.PRIMARY), createTabVatTu());
        tabbedPane.addTab("Danh Mục Thuốc Kê Đơn & Dược Phẩm", new AppIcon(AppIcon.Type.SERVICE, 16, ModernUITheme.ACCENT), createTabThuoc());

        add(tabbedPane, BorderLayout.CENTER);

        taiDuLieuVT();
        taiDuLieuThuoc();
    }

    // ==========================================
    // TAB 1: VẬT TƯ TIÊU HAO
    // ==========================================
    private JPanel createTabVatTu() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        // Sub-header search
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlSearch.setOpaque(false);
        txtTimKiemVT = ModernUITheme.createTextField(18);
        txtTimKiemVT.setToolTipText("Tìm mã, tên hoặc loại vật tư...");
        btnTimKiemVT = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 15, Color.WHITE), "Tra cứu", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLaiVT = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tìm vật tư:"));
        pnlSearch.add(txtTimKiemVT);
        pnlSearch.add(btnTimKiemVT);
        pnlSearch.add(btnTaiLaiVT);
        pnl.add(pnlSearch, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.62);
        split.setDividerSize(6);
        split.setOpaque(false);

        // Bảng
        String[] cols = { "Mã VT", "Tên Thuốc / Vật Tư", "Phân Loại", "Đơn Vị Tính", "Số Lượng Tồn", "Định Mức", "Đơn Giá", "Trạng Thái Kho" };
        modelVT = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableVT = new JTable(modelVT);
        ModernUITheme.formatModernTable(tableVT);
        JScrollPane sp = new JScrollPane(tableVT);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        split.setTopComponent(sp);

        // Form Card
        JPanel card = ModernUITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        JLabel lblForm = new JLabel("CHI TIẾT VẬT TƯ NHA KHOA");
        lblForm.setFont(ModernUITheme.FONT_TITLE);
        lblForm.setForeground(ModernUITheme.PRIMARY_DARK);
        card.add(lblForm, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(2, 4, 12, 8));
        fields.setOpaque(false);

        txtMaVT = ModernUITheme.createTextField(10);
        txtMaVT.setEditable(false);
        txtTenVT = ModernUITheme.createTextField(16);
        cbLoaiVT = new JComboBox<>(new String[] { "Tiêu hao", "Dụng cụ", "Thiết bị" });
        txtDonViTinhVT = ModernUITheme.createTextField(8);
        txtSoLuongVT = ModernUITheme.createTextField(8);
        txtDinhMucVT = ModernUITheme.createTextField(8);
        txtDonGiaVT = ModernUITheme.createTextField(12);
        cbTrangThaiVT = new JComboBox<>(new String[] { "Còn hàng", "Cảnh báo hết hàng", "Hết hàng", "Ngừng sử dụng" });

        fields.add(createField("Mã vật tư:", txtMaVT));
        fields.add(createField("Tên vật tư (*):", txtTenVT));
        fields.add(createField("Phân loại:", cbLoaiVT));
        fields.add(createField("Đơn vị tính (*):", txtDonViTinhVT));
        fields.add(createField("Số lượng tồn (*):", txtSoLuongVT));
        fields.add(createField("Định mức tối thiểu:", txtDinhMucVT));
        fields.add(createField("Đơn giá (VNĐ):", txtDonGiaVT));
        fields.add(createField("Trạng thái:", cbTrangThaiVT));
        card.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btnLamMoiVT = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Làm mới", ModernUITheme.TEXT_MUTED);
        btnNhapXuatVT = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.INVENTORY, 14, Color.WHITE), "Nhập/Xuất kho", ModernUITheme.ACCENT, Color.WHITE);
        btnXoaVT = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 14, Color.WHITE), "Xóa VT", ModernUITheme.DANGER, Color.WHITE);
        btnCapNhatVT = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Cập nhật", ModernUITheme.ACCENT, Color.WHITE);
        btnThemVT = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 14, Color.WHITE), "Thêm vật tư", ModernUITheme.PRIMARY, Color.WHITE);

        btns.add(btnLamMoiVT);
        btns.add(btnNhapXuatVT);
        btns.add(btnXoaVT);
        btns.add(btnCapNhatVT);
        btns.add(btnThemVT);
        card.add(btns, BorderLayout.SOUTH);

        split.setBottomComponent(card);
        pnl.add(split, BorderLayout.CENTER);

        // Events
        tableVT.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableVT.getSelectedRow() != -1) {
                napFormVT(tableVT.getSelectedRow());
            }
        });

        btnThemVT.addActionListener(e -> xuLyThemVT());
        btnCapNhatVT.addActionListener(e -> xuLyCapNhatVT());
        btnXoaVT.addActionListener(e -> xuLyXoaVT());
        btnNhapXuatVT.addActionListener(e -> xuLyNhapXuatVT());
        btnLamMoiVT.addActionListener(e -> xoaTrangVT());
        btnTimKiemVT.addActionListener(e -> xuLyTimKiemVT());
        txtTimKiemVT.addActionListener(e -> xuLyTimKiemVT());
        btnTaiLaiVT.addActionListener(e -> {
            txtTimKiemVT.setText("");
            taiDuLieuVT();
        });

        return pnl;
    }

    private void taiDuLieuVT() {
        modelVT.setRowCount(0);
        List<VatTu> list = vatTuDAO.getAllVatTu();
        for (VatTu vt : list) {
            modelVT.addRow(new Object[] {
                vt.getMaVatTu(),
                vt.getTenVatTu(),
                vt.getLoaiVatTu() != null ? vt.getLoaiVatTu() : "Tiêu hao",
                vt.getDonViTinh() != null ? vt.getDonViTinh() : "Hộp",
                vt.getSoLuongTon(),
                vt.getDinhMucToiThieu(),
                df.format(vt.getDonGia()),
                vt.getTrangThai() != null ? vt.getTrangThai() : (vt.isDuoiDinhMuc() ? "Cảnh báo hết hàng" : "Còn hàng")
            });
        }
        xoaTrangVT();
    }

    private void napFormVT(int row) {
        if (row < 0 || row >= modelVT.getRowCount()) return;
        txtMaVT.setText(modelVT.getValueAt(row, 0).toString());
        txtTenVT.setText(modelVT.getValueAt(row, 1).toString());
        cbLoaiVT.setSelectedItem(modelVT.getValueAt(row, 2) != null ? modelVT.getValueAt(row, 2).toString() : "Tiêu hao");
        txtDonViTinhVT.setText(modelVT.getValueAt(row, 3).toString());
        txtSoLuongVT.setText(modelVT.getValueAt(row, 4).toString());
        txtDinhMucVT.setText(modelVT.getValueAt(row, 5) != null ? modelVT.getValueAt(row, 5).toString() : "10");
        String giaStr = modelVT.getValueAt(row, 6).toString().replaceAll("[^0-9]", "");
        txtDonGiaVT.setText(giaStr);
        cbTrangThaiVT.setSelectedItem(modelVT.getValueAt(row, 7) != null ? modelVT.getValueAt(row, 7).toString() : "Còn hàng");
    }

    private void xoaTrangVT() {
        txtMaVT.setText(vatTuDAO.sinhMaVTMoi());
        txtTenVT.setText("");
        cbLoaiVT.setSelectedIndex(0);
        txtDonViTinhVT.setText("");
        txtSoLuongVT.setText("50");
        txtDinhMucVT.setText("10");
        txtDonGiaVT.setText("50000");
        cbTrangThaiVT.setSelectedIndex(0);
        tableVT.clearSelection();
    }

    private void xuLyThemVT() {
        String ten = txtTenVT.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên vật tư không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String dvt = txtDonViTinhVT.getText().trim();
        if (dvt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int sl = 0;
        int dm = 10;
        double gia = 0;
        try {
            sl = Integer.parseInt(txtSoLuongVT.getText().trim());
            dm = Integer.parseInt(txtDinhMucVT.getText().trim());
            gia = Double.parseDouble(txtDonGiaVT.getText().trim().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng, định mức hoặc đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ma = txtMaVT.getText().trim();
        if (ma.isEmpty()) ma = vatTuDAO.sinhMaVTMoi();
        String loai = cbLoaiVT.getSelectedItem() != null ? cbLoaiVT.getSelectedItem().toString() : "Tiêu hao";
        String tt = cbTrangThaiVT.getSelectedItem() != null ? cbTrangThaiVT.getSelectedItem().toString() : "Còn hàng";

        VatTu vt = new VatTu(ma, ten, loai, dvt, sl, dm, gia, tt);
        if (vatTuDAO.themVatTu(vt)) {
            JOptionPane.showMessageDialog(this, "Thêm vật tư [" + ma + " - " + ten + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuVT();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm vật tư!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhatVT() {
        int r = tableVT.getSelectedRow();
        String ma = txtMaVT.getText().trim();
        if (r < 0 && (ma.isEmpty() || ma.equals(vatTuDAO.sinhMaVTMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 vật tư để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ten = txtTenVT.getText().trim();
        String dvt = txtDonViTinhVT.getText().trim();
        int sl = Integer.parseInt(txtSoLuongVT.getText().trim());
        int dm = Integer.parseInt(txtDinhMucVT.getText().trim());
        double gia = Double.parseDouble(txtDonGiaVT.getText().trim().replaceAll("[^0-9]", ""));
        String loai = cbLoaiVT.getSelectedItem() != null ? cbLoaiVT.getSelectedItem().toString() : "Tiêu hao";
        String tt = cbTrangThaiVT.getSelectedItem() != null ? cbTrangThaiVT.getSelectedItem().toString() : "Còn hàng";

        VatTu vt = new VatTu(ma, ten, loai, dvt, sl, dm, gia, tt);
        if (vatTuDAO.capNhatVatTu(vt)) {
            JOptionPane.showMessageDialog(this, "Cập nhật vật tư [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuVT();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoaVT() {
        int r = tableVT.getSelectedRow();
        String ma = txtMaVT.getText().trim();
        if (r < 0 && (ma.isEmpty() || ma.equals(vatTuDAO.sinhMaVTMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 vật tư cần xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (r >= 0) ma = modelVT.getValueAt(r, 0).toString();

        int cf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa vật tư [" + ma + "] khỏi hệ thống?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            if (vatTuDAO.xoaVatTu(ma)) {
                JOptionPane.showMessageDialog(this, "Đã xóa vật tư [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuVT();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa vật tư này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyNhapXuatVT() {
        int r = tableVT.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vật tư cần nhập/xuất kho!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = modelVT.getValueAt(r, 0).toString();
        String ten = modelVT.getValueAt(r, 1).toString();
        String curTon = modelVT.getValueAt(r, 4).toString();

        String input = JOptionPane.showInputDialog(this, 
            "Nhập số lượng thay đổi cho [" + ten + "] (Hiện tồn: " + curTon + "):\n(Số dương: Nhập thêm kho, Số âm: Xuất kho sử dụng)", 
            "Điều chỉnh số lượng tồn kho", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int delta = Integer.parseInt(input.trim());
                if (vatTuDAO.nhapXuatKho(ma, delta)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng tồn kho của [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieuVT();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật tồn kho thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyTimKiemVT() {
        String kw = txtTimKiemVT.getText().trim();
        if (kw.isEmpty()) {
            taiDuLieuVT();
            return;
        }
        List<VatTu> list = vatTuDAO.timKiem(kw);
        modelVT.setRowCount(0);
        for (VatTu vt : list) {
            modelVT.addRow(new Object[] {
                vt.getMaVatTu(),
                vt.getTenVatTu(),
                vt.getLoaiVatTu() != null ? vt.getLoaiVatTu() : "Tiêu hao",
                vt.getDonViTinh() != null ? vt.getDonViTinh() : "Hộp",
                vt.getSoLuongTon(),
                vt.getDinhMucToiThieu(),
                df.format(vt.getDonGia()),
                vt.getTrangThai() != null ? vt.getTrangThai() : (vt.isDuoiDinhMuc() ? "Cảnh báo hết hàng" : "Còn hàng")
            });
        }
    }

    // ==========================================
    // TAB 2: THUỐC ĐIỀU TRỊ & DƯỢC PHẨM
    // ==========================================
    private JPanel createTabThuoc() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        // Sub-header search
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlSearch.setOpaque(false);
        txtTimKiemThuoc = ModernUITheme.createTextField(18);
        txtTimKiemThuoc.setToolTipText("Tìm mã, tên thuốc, hoạt chất...");
        btnTimKiemThuoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 15, Color.WHITE), "Tra cứu", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLaiThuoc = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tìm thuốc:"));
        pnlSearch.add(txtTimKiemThuoc);
        pnlSearch.add(btnTimKiemThuoc);
        pnlSearch.add(btnTaiLaiThuoc);
        pnl.add(pnlSearch, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.62);
        split.setDividerSize(6);
        split.setOpaque(false);

        // Bảng
        String[] cols = { "Mã Thuốc", "Tên Thuốc Biệt Dược", "Hoạt Chất", "ĐVT", "Đơn Giá", "Tồn Kho", "Định Mức", "Hạn Sử Dụng", "Cách Dùng Mặc Định", "Trạng Thái" };
        modelThuoc = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableThuoc = new JTable(modelThuoc);
        ModernUITheme.formatModernTable(tableThuoc);
        JScrollPane sp = new JScrollPane(tableThuoc);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        split.setTopComponent(sp);

        // Form Card
        JPanel card = ModernUITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        JLabel lblForm = new JLabel("HỒ SƠ DƯỢC PHẨM & QUY CÁCH KÊ ĐƠN");
        lblForm.setFont(ModernUITheme.FONT_TITLE);
        lblForm.setForeground(ModernUITheme.PRIMARY_DARK);
        card.add(lblForm, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(2, 5, 12, 8));
        fields.setOpaque(false);

        txtMaThuoc = ModernUITheme.createTextField(10);
        txtMaThuoc.setEditable(false);
        txtTenThuoc = ModernUITheme.createTextField(16);
        txtHoatChat = ModernUITheme.createTextField(14);
        txtDonViTinhThuoc = ModernUITheme.createTextField(8);
        txtDonGiaThuoc = ModernUITheme.createTextField(10);
        txtSoLuongThuoc = ModernUITheme.createTextField(8);
        txtDinhMucThuoc = ModernUITheme.createTextField(8);
        txtHSD = ModernUITheme.createTextField(10);
        txtHSD.setText("2027-12-31");
        txtCachDung = ModernUITheme.createTextField(20);
        cbTrangThaiThuoc = new JComboBox<>(new String[] { "Đang kinh doanh", "Cảnh báo hết hàng", "Hết hàng", "Ngừng kinh doanh" });

        fields.add(createField("Mã thuốc:", txtMaThuoc));
        fields.add(createField("Tên thuốc (*):", txtTenThuoc));
        fields.add(createField("Hoạt chất chính:", txtHoatChat));
        fields.add(createField("Đơn vị tính (*):", txtDonViTinhThuoc));
        fields.add(createField("Đơn giá bán (VNĐ):", txtDonGiaThuoc));
        fields.add(createField("Số lượng tồn (*):", txtSoLuongThuoc));
        fields.add(createField("Định mức tối thiểu:", txtDinhMucThuoc));
        fields.add(createField("Hạn dùng (yyyy-MM-dd):", txtHSD));
        fields.add(createField("Cách dùng mặc định:", txtCachDung));
        fields.add(createField("Trạng thái:", cbTrangThaiThuoc));
        card.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btnLamMoiThuoc = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Làm mới", ModernUITheme.TEXT_MUTED);
        btnNhapXuatThuoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.INVENTORY, 14, Color.WHITE), "Nhập/Xuất kho", ModernUITheme.ACCENT, Color.WHITE);
        btnXoaThuoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 14, Color.WHITE), "Xóa thuốc", ModernUITheme.DANGER, Color.WHITE);
        btnCapNhatThuoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Cập nhật", ModernUITheme.ACCENT, Color.WHITE);
        btnThemThuoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 14, Color.WHITE), "Thêm thuốc", ModernUITheme.PRIMARY, Color.WHITE);

        btns.add(btnLamMoiThuoc);
        btns.add(btnNhapXuatThuoc);
        btns.add(btnXoaThuoc);
        btns.add(btnCapNhatThuoc);
        btns.add(btnThemThuoc);
        card.add(btns, BorderLayout.SOUTH);

        split.setBottomComponent(card);
        pnl.add(split, BorderLayout.CENTER);

        // Events
        tableThuoc.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableThuoc.getSelectedRow() != -1) {
                napFormThuoc(tableThuoc.getSelectedRow());
            }
        });

        btnThemThuoc.addActionListener(e -> xuLyThemThuoc());
        btnCapNhatThuoc.addActionListener(e -> xuLyCapNhatThuoc());
        btnXoaThuoc.addActionListener(e -> xuLyXoaThuoc());
        btnNhapXuatThuoc.addActionListener(e -> xuLyNhapXuatThuoc());
        btnLamMoiThuoc.addActionListener(e -> xoaTrangThuoc());
        btnTimKiemThuoc.addActionListener(e -> xuLyTimKiemThuoc());
        txtTimKiemThuoc.addActionListener(e -> xuLyTimKiemThuoc());
        btnTaiLaiThuoc.addActionListener(e -> {
            txtTimKiemThuoc.setText("");
            taiDuLieuThuoc();
        });

        return pnl;
    }

    private void taiDuLieuThuoc() {
        modelThuoc.setRowCount(0);
        List<Thuoc> list = thuocDAO.getAllThuoc();
        for (Thuoc th : list) {
            modelThuoc.addRow(new Object[] {
                th.getMaThuoc(),
                th.getTenThuoc(),
                th.getHoatChat() != null ? th.getHoatChat() : "",
                th.getDonViTinh() != null ? th.getDonViTinh() : "Viên",
                df.format(th.getDonGia()),
                th.getSoLuongTon(),
                th.getDinhMucToiThieu(),
                th.getHanSuDung() != null ? th.getHanSuDung().toString() : "",
                th.getCachDungMacDinh() != null ? th.getCachDungMacDinh() : "",
                th.getTrangThai() != null ? th.getTrangThai() : "Đang kinh doanh"
            });
        }
        xoaTrangThuoc();
    }

    private void napFormThuoc(int row) {
        if (row < 0 || row >= modelThuoc.getRowCount()) return;
        txtMaThuoc.setText(modelThuoc.getValueAt(row, 0).toString());
        txtTenThuoc.setText(modelThuoc.getValueAt(row, 1).toString());
        txtHoatChat.setText(modelThuoc.getValueAt(row, 2) != null ? modelThuoc.getValueAt(row, 2).toString() : "");
        txtDonViTinhThuoc.setText(modelThuoc.getValueAt(row, 3).toString());
        String giaStr = modelThuoc.getValueAt(row, 4).toString().replaceAll("[^0-9]", "");
        txtDonGiaThuoc.setText(giaStr);
        txtSoLuongThuoc.setText(modelThuoc.getValueAt(row, 5).toString());
        txtDinhMucThuoc.setText(modelThuoc.getValueAt(row, 6) != null ? modelThuoc.getValueAt(row, 6).toString() : "20");
        txtHSD.setText(modelThuoc.getValueAt(row, 7) != null ? modelThuoc.getValueAt(row, 7).toString() : "2027-12-31");
        txtCachDung.setText(modelThuoc.getValueAt(row, 8) != null ? modelThuoc.getValueAt(row, 8).toString() : "");
        cbTrangThaiThuoc.setSelectedItem(modelThuoc.getValueAt(row, 9) != null ? modelThuoc.getValueAt(row, 9).toString() : "Đang kinh doanh");
    }

    private void xoaTrangThuoc() {
        txtMaThuoc.setText(thuocDAO.sinhMaThuocMoi());
        txtTenThuoc.setText("");
        txtHoatChat.setText("");
        txtDonViTinhThuoc.setText("Viên");
        txtDonGiaThuoc.setText("5000");
        txtSoLuongThuoc.setText("100");
        txtDinhMucThuoc.setText("20");
        txtHSD.setText("2027-12-31");
        txtCachDung.setText("Uống 1 viên x 2 lần/ngày sau ăn");
        cbTrangThaiThuoc.setSelectedIndex(0);
        tableThuoc.clearSelection();
    }

    private void xuLyThemThuoc() {
        String ten = txtTenThuoc.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên thuốc không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String dvt = txtDonViTinhThuoc.getText().trim();
        if (dvt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn vị tính không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int sl = 0;
        int dm = 20;
        double gia = 0;
        Date hsd;
        try {
            sl = Integer.parseInt(txtSoLuongThuoc.getText().trim());
            dm = Integer.parseInt(txtDinhMucThuoc.getText().trim());
            gia = Double.parseDouble(txtDonGiaThuoc.getText().trim().replaceAll("[^0-9]", ""));
            hsd = Date.valueOf(txtHSD.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng, định mức, đơn giá hoặc hạn sử dụng (yyyy-MM-dd) không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ma = txtMaThuoc.getText().trim();
        if (ma.isEmpty()) ma = thuocDAO.sinhMaThuocMoi();
        String hoatChat = txtHoatChat.getText().trim();
        String cachDung = txtCachDung.getText().trim();
        String tt = cbTrangThaiThuoc.getSelectedItem() != null ? cbTrangThaiThuoc.getSelectedItem().toString() : "Đang kinh doanh";

        Thuoc th = new Thuoc(ma, ten, hoatChat, dvt, gia, sl, dm, hsd, cachDung, tt);
        if (thuocDAO.themThuoc(th)) {
            JOptionPane.showMessageDialog(this, "Thêm thuốc [" + ma + " - " + ten + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuThuoc();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm thuốc vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhatThuoc() {
        int r = tableThuoc.getSelectedRow();
        String ma = txtMaThuoc.getText().trim();
        if (r < 0 && (ma.isEmpty() || ma.equals(thuocDAO.sinhMaThuocMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thuốc trong bảng để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ten = txtTenThuoc.getText().trim();
        String dvt = txtDonViTinhThuoc.getText().trim();
        int sl = Integer.parseInt(txtSoLuongThuoc.getText().trim());
        int dm = Integer.parseInt(txtDinhMucThuoc.getText().trim());
        double gia = Double.parseDouble(txtDonGiaThuoc.getText().trim().replaceAll("[^0-9]", ""));
        Date hsd = Date.valueOf(txtHSD.getText().trim());
        String hoatChat = txtHoatChat.getText().trim();
        String cachDung = txtCachDung.getText().trim();
        String tt = cbTrangThaiThuoc.getSelectedItem() != null ? cbTrangThaiThuoc.getSelectedItem().toString() : "Đang kinh doanh";

        Thuoc th = new Thuoc(ma, ten, hoatChat, dvt, gia, sl, dm, hsd, cachDung, tt);
        if (thuocDAO.capNhatThuoc(th)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thuốc [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuThuoc();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoaThuoc() {
        int r = tableThuoc.getSelectedRow();
        String ma = txtMaThuoc.getText().trim();
        if (r < 0 && (ma.isEmpty() || ma.equals(thuocDAO.sinhMaThuocMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (r >= 0) ma = modelThuoc.getValueAt(r, 0).toString();

        int cf = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thuốc [" + ma + "] khỏi hệ thống?\n(Nếu thuốc đã từng kê đơn trong bệnh án, trạng thái sẽ đổi sang 'Ngừng kinh doanh')", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            if (thuocDAO.xoaThuoc(ma)) {
                JOptionPane.showMessageDialog(this, "Đã xóa hoặc cập nhật trạng thái thuốc [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuThuoc();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa thuốc này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyNhapXuatThuoc() {
        int r = tableThuoc.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần nhập/xuất kho!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = modelThuoc.getValueAt(r, 0).toString();
        String ten = modelThuoc.getValueAt(r, 1).toString();
        String curTon = modelThuoc.getValueAt(r, 5).toString();

        String input = JOptionPane.showInputDialog(this, 
            "Nhập số lượng thay đổi cho thuốc [" + ten + "] (Hiện tồn: " + curTon + "):\n(Số dương: Nhập thêm, Số âm: Xuất kho)", 
            "Điều chỉnh tồn kho thuốc", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int delta = Integer.parseInt(input.trim());
                if (thuocDAO.nhapXuatKho(ma, delta)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng tồn kho của [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieuThuoc();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật tồn kho thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyTimKiemThuoc() {
        String kw = txtTimKiemThuoc.getText().trim();
        if (kw.isEmpty()) {
            taiDuLieuThuoc();
            return;
        }
        List<Thuoc> list = thuocDAO.timKiem(kw);
        modelThuoc.setRowCount(0);
        for (Thuoc th : list) {
            modelThuoc.addRow(new Object[] {
                th.getMaThuoc(),
                th.getTenThuoc(),
                th.getHoatChat() != null ? th.getHoatChat() : "",
                th.getDonViTinh() != null ? th.getDonViTinh() : "Viên",
                df.format(th.getDonGia()),
                th.getSoLuongTon(),
                th.getDinhMucToiThieu(),
                th.getHanSuDung() != null ? th.getHanSuDung().toString() : "",
                th.getCachDungMacDinh() != null ? th.getCachDungMacDinh() : "",
                th.getTrangThai() != null ? th.getTrangThai() : "Đang kinh doanh"
            });
        }
    }

    private JPanel createField(String labelText, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(ModernUITheme.FONT_BOLD);
        lbl.setForeground(ModernUITheme.TEXT_REGULAR);
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }
}
