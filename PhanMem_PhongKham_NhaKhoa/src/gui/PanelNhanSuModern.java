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
    private JButton btnThem, btnCapNhat, btnLamMoi, btnTimKiem, btnXoa;

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
        btnXoa = ModernUITheme.createButton("🗑️ Xóa / Nghỉ việc", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

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

        cbChucDanh.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                String cd = cbChucDanh.getSelectedItem() != null ? cbChucDanh.getSelectedItem().toString() : "Nha sĩ";
                txtMaNV.setText(nhanVienDAO.sinhMaNVMoi(cd));
            }
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
        List<NhanVien> list = nhanVienDAO.getAllNhanVien();
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "",
                    nv.getGioiTinh() != null ? nv.getGioiTinh() : "Nam",
                    nv.getSoDienThoai(),
                    nv.getEmail() != null ? nv.getEmail() : "",
                    nv.getChucDanh() != null ? nv.getChucDanh() : "",
                    nv.getBangCapChungChi() != null ? nv.getBangCapChungChi() : "",
                    nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().toString() : "",
                    nv.getTrangThai() != null ? nv.getTrangThai() : "Đang làm việc"
            });
        }
        xoaTrangForm();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount())
            return;
        txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        txtNgaySinh.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        cbGioiTinh.setSelectedItem(
                tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Nam");
        txtSDT.setText(tableModel.getValueAt(row, 4).toString());
        txtEmail.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        cbChucDanh.setSelectedItem(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString()
                : "Bác sĩ Chuyên khoa RHM");
        txtBangCap.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");
        txtNgayVaoLam.setText(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "");
        cbTrangThai.setSelectedItem(
                tableModel.getValueAt(row, 9) != null ? tableModel.getValueAt(row, 9).toString() : "Đang làm việc");
    }

    private void xoaTrangForm() {
        String cd = cbChucDanh.getSelectedItem() != null ? cbChucDanh.getSelectedItem().toString() : "Nha sĩ";
        txtMaNV.setText(nhanVienDAO.sinhMaNVMoi(cd));
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
        if (!kiemTraHopLe())
            return;

        String cd = cbChucDanh.getSelectedItem() != null ? cbChucDanh.getSelectedItem().toString() : "Nha sĩ";
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            maNV = nhanVienDAO.sinhMaNVMoi(cd);
        }
        String hoTen = txtHoTen.getText().trim();
        Date ngaySinh = Date.valueOf(txtNgaySinh.getText().trim());
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            email = maNV.toLowerCase() + "@nhakhoaabc.vn";
        }
        String bangCap = txtBangCap.getText().trim();
        Date ngayVaoLam;
        try {
            ngayVaoLam = Date.valueOf(txtNgayVaoLam.getText().trim());
        } catch (Exception e) {
            ngayVaoLam = new Date(System.currentTimeMillis());
        }
        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString()
                : "Đang làm việc";

        NhanVien nv = new NhanVien(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, cd, bangCap, ngayVaoLam, trangThai);
        boolean thanhCong = nhanVienDAO.themNhanVien(nv);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Thêm nhân sự [" + maNV + " - " + hoTen + "] thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm nhân sự. Vui lòng kiểm tra lại SĐT hoặc Email trùng!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        int row = table.getSelectedRow();
        String maNV = txtMaNV.getText().trim();
        if (row < 0 && maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân sự trong danh sách để cập nhật!", "Nhắc nhở",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!kiemTraHopLe())
            return;

        String hoTen = txtHoTen.getText().trim();
        Date ngaySinh = Date.valueOf(txtNgaySinh.getText().trim());
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String cd = cbChucDanh.getSelectedItem() != null ? cbChucDanh.getSelectedItem().toString() : "Nha sĩ";
        String bangCap = txtBangCap.getText().trim();
        Date ngayVaoLam;
        try {
            ngayVaoLam = Date.valueOf(txtNgayVaoLam.getText().trim());
        } catch (Exception e) {
            ngayVaoLam = new Date(System.currentTimeMillis());
        }
        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString()
                : "Đang làm việc";

        NhanVien nv = new NhanVien(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, cd, bangCap, ngayVaoLam, trangThai);
        boolean thanhCong = nhanVienDAO.capNhatNhanVien(nv);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân sự [" + maNV + "] thành công!", "Thông báo",
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
        List<NhanVien> list = nhanVienDAO.timKiem(keyword);
        tableModel.setRowCount(0);
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "",
                    nv.getGioiTinh() != null ? nv.getGioiTinh() : "Nam",
                    nv.getSoDienThoai(),
                    nv.getEmail() != null ? nv.getEmail() : "",
                    nv.getChucDanh() != null ? nv.getChucDanh() : "",
                    nv.getBangCapChungChi() != null ? nv.getBangCapChungChi() : "",
                    nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().toString() : "",
                    nv.getTrangThai() != null ? nv.getTrangThai() : "Đang làm việc"
            });
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân sự nào khớp với từ khóa: \"" + keyword + "\"",
                    "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean kiemTraHopLe() {
        String hoTen = txtHoTen.getText().trim();
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ và tên nhân sự không được để trống!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return false;
        }
        String sdt = txtSDT.getText().trim();
        if (sdt.isEmpty() || !sdt.matches("0[0-9]{9,10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải từ 10-11 chữ số và bắt đầu bằng số 0!",
                    "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return false;
        }
        String nsStr = txtNgaySinh.getText().trim();
        if (!nsStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải theo định dạng yyyy-MM-dd!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return false;
        }
        try {
            Date.valueOf(nsStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return false;
        }
        String nvlStr = txtNgayVaoLam.getText().trim();
        if (!nvlStr.isEmpty()) {
            try {
                Date.valueOf(nvlStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày vào làm phải theo định dạng yyyy-MM-dd!", "Lỗi dữ liệu",
                        JOptionPane.ERROR_MESSAGE);
                txtNgayVaoLam.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void xuLyXoa() {
        int row = table.getSelectedRow();
        String maNV = txtMaNV.getText().trim();
        if (row < 0 && maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần xóa hoặc cho nghỉ việc!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (row >= 0) {
            maNV = tableModel.getValueAt(row, 0).toString();
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa hoặc cho nhân sự [" + maNV + "] nghỉ việc?\n(Nếu nhân sự đã có lịch trực hoặc hồ sơ khám liên kết, trạng thái sẽ tự động đổi sang 'Đã nghỉ việc')",
            "Xác nhận xóa / Nghỉ việc", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean kq = nhanVienDAO.xoaNhanVien(maNV);
            if (kq) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật/xóa nhân sự [" + maNV + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa hoặc cập nhật nhân sự này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
