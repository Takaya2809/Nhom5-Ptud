package gui;

import dao.NhanVienDAO;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện Quản lý Đội ngũ Bác Sĩ & Nhân Sự Y Tế - Redesign chuẩn Y Khoa
 * - Sử dụng Date Picker trực quan cho Ngày Sinh và Ngày Vào Làm
 * - Ràng buộc ngày sinh không vượt quá ngày hiện tại (maxDate = today)
 * - Tích hợp AppIcon độc lập lỗi hiển thị trên Windows
 */
public class PanelNhanSuModern extends JPanel {

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaNV, txtHoTen, txtSDT, txtEmail, txtBangCap, txtTimKiem;
    private DatePickerField dpNgaySinh, dpNgayVaoLam;
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
        splitPane.setResizeWeight(0.58);
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

        JPanel pnlFormHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFormHeader.setOpaque(false);
        JLabel lblFormIcon = new JLabel(new AppIcon(AppIcon.Type.STAFF, 18, ModernUITheme.PRIMARY));
        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT BÁC SĨ / NHÂN VIÊN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormHeader.add(lblFormIcon);
        pnlFormHeader.add(lblFormTitle);
        pnlFormCard.add(pnlFormHeader, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 5, 12, 10));
        pnlFields.setOpaque(false);

        txtMaNV = ModernUITheme.createTextField(10);
        txtMaNV.setEditable(false);
        txtMaNV.setBackground(new Color(241, 245, 249));

        txtHoTen = ModernUITheme.createTextField(16);

        // Ngày sinh với DatePickerField & giới hạn không chọn tương lai
        dpNgaySinh = new DatePickerField(LocalDate.of(1990, 1, 1));
        dpNgaySinh.setMaxDate(LocalDate.now());

        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cbGioiTinh.setFont(ModernUITheme.FONT_REGULAR);

        txtSDT = ModernUITheme.createTextField(12);
        txtEmail = ModernUITheme.createTextField(16);

        cbChucDanh = new JComboBox<>(new String[]{
            "Bác sĩ Chuyên khoa RHM", "Bác sĩ Trưởng khoa", "Bác sĩ Chỉnh nha",
            "Y tá / Phụ tá", "Kỹ thuật viên nha khoa", "Lễ tân", "Thu ngân", "Quản lý"
        });
        cbChucDanh.setFont(ModernUITheme.FONT_REGULAR);

        txtBangCap = ModernUITheme.createTextField(18);
        txtBangCap.setText("BS. CKI ĐHYD TP.HCM");

        // Ngày vào làm với DatePickerField
        dpNgayVaoLam = new DatePickerField(LocalDate.now());

        cbTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Tạm nghỉ", "Đã nghỉ việc"});
        cbTrangThai.setFont(ModernUITheme.FONT_REGULAR);

        pnlFields.add(createFieldItem("Mã nhân viên:", txtMaNV));
        pnlFields.add(createFieldItem("Họ và tên (*):", txtHoTen));
        pnlFields.add(createFieldItem("Ngày sinh (*):", dpNgaySinh));
        pnlFields.add(createFieldItem("Giới tính:", cbGioiTinh));
        pnlFields.add(createFieldItem("Số điện thoại (*):", txtSDT));
        pnlFields.add(createFieldItem("Email liên hệ:", txtEmail));
        pnlFields.add(createFieldItem("Chức danh y tế:", cbChucDanh));
        pnlFields.add(createFieldItem("Bằng cấp / Chứng chỉ:", txtBangCap));
        pnlFields.add(createFieldItem("Ngày vào làm:", dpNgayVaoLam));
        pnlFields.add(createFieldItem("Trạng thái:", cbTrangThai));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Nút bấm thao tác với AppIcon chuyên nghiệp
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 15, Color.WHITE), "Thêm nhân sự", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Cập nhật hồ sơ", ModernUITheme.ACCENT, Color.WHITE);
        btnXoa = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 15, Color.WHITE), "Xóa / Nghỉ việc", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 15, ModernUITheme.TEXT_MUTED), "Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnThem);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Sự kiện chọn bảng
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
            if (table.getSelectedRow() == -1 && txtMaNV.getText().isEmpty()) {
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
            tableModel.addRow(new Object[]{
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

        // Nạp ngày sinh vào DatePickerField
        Object valNS = tableModel.getValueAt(row, 2);
        if (valNS != null && !valNS.toString().trim().isEmpty()) {
            try {
                dpNgaySinh.setDate(Date.valueOf(valNS.toString().trim()));
            } catch (Exception ignored) {
                dpNgaySinh.clear();
            }
        } else {
            dpNgaySinh.clear();
        }

        cbGioiTinh.setSelectedItem(
                tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Nam");
        txtSDT.setText(tableModel.getValueAt(row, 4).toString());
        txtEmail.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        cbChucDanh.setSelectedItem(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString()
                : "Bác sĩ Chuyên khoa RHM");
        txtBangCap.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");

        // Nạp ngày vào làm vào DatePickerField
        Object valNVL = tableModel.getValueAt(row, 8);
        if (valNVL != null && !valNVL.toString().trim().isEmpty()) {
            try {
                dpNgayVaoLam.setDate(Date.valueOf(valNVL.toString().trim()));
            } catch (Exception ignored) {
                dpNgayVaoLam.clear();
            }
        } else {
            dpNgayVaoLam.clear();
        }

        cbTrangThai.setSelectedItem(
                tableModel.getValueAt(row, 9) != null ? tableModel.getValueAt(row, 9).toString() : "Đang làm việc");
    }

    public void selectNhanVien(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return;
        String clean = maNV.trim();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (clean.equalsIgnoreCase(tableModel.getValueAt(i, 0).toString())) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                napFormTuBang(i);
                return;
            }
        }
    }

    private void xoaTrangForm() {
        String cd = cbChucDanh.getSelectedItem() != null ? cbChucDanh.getSelectedItem().toString() : "Nha sĩ";
        txtMaNV.setText(nhanVienDAO.sinhMaNVMoi(cd));
        txtHoTen.setText("");
        dpNgaySinh.setDate(LocalDate.of(1990, 1, 1));
        cbGioiTinh.setSelectedIndex(0);
        txtSDT.setText("");
        txtEmail.setText("");
        txtBangCap.setText("BS. Chuyên khoa Răng Hàm Mặt");
        dpNgayVaoLam.setDate(LocalDate.now());
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
        Date ngaySinh = dpNgaySinh.getDate();
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            email = maNV.toLowerCase() + "@nhakhoaabc.vn";
        }
        String bangCap = txtBangCap.getText().trim();
        Date ngayVaoLam = dpNgayVaoLam.getDate() != null ? dpNgayVaoLam.getDate() : new Date(System.currentTimeMillis());
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
        Date ngaySinh = dpNgaySinh.getDate();
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String cd = cbChucDanh.getSelectedItem() != null ? cbChucDanh.getSelectedItem().toString() : "Nha sĩ";
        String bangCap = txtBangCap.getText().trim();
        Date ngayVaoLam = dpNgayVaoLam.getDate() != null ? dpNgayVaoLam.getDate() : new Date(System.currentTimeMillis());
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

        if (!dpNgaySinh.isValidDate() || dpNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ hoặc chưa được chọn!\nVui lòng chọn ngày từ lịch (dd/MM/yyyy).", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dpNgaySinh.getLocalDate() != null && dpNgaySinh.getLocalDate().isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không được lớn hơn ngày hiện tại!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dpNgayVaoLam.getDate() != null && !dpNgayVaoLam.isValidDate()) {
            JOptionPane.showMessageDialog(this, "Ngày vào làm không hợp lệ!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return false;
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
