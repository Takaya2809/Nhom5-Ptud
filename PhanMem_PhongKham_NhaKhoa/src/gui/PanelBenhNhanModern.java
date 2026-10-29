package gui;

import dao.BenhNhanDAO;
import dao.HoSoBenhAnDAO;
import dao.LichHenDAO;
import dao.NhanVienDAO;
import entity.BenhNhan;
import entity.HoSoBenhAn;
import entity.LichHen;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện Hồ sơ Bệnh nhân & Khách hàng - Chuẩn Y Khoa Hiện Đại
 * Tích hợp DatePickerField chọn ngày sinh trực quan, AppIcon độc lập hệ điều hành
 */
public class PanelBenhNhanModern extends JPanel {

    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaBN, txtHoTen, txtSDT, txtDiaChi, txtTienSuBenh, txtDiemTichLuy, txtTimKiem;
    private DatePickerField dpNgaySinh;
    private JComboBox<String> cbGioiTinh, cbLoaiKhach;
    private JButton btnThem, btnCapNhat, btnXoa, btnXemLichSu, btnDatHen, btnLamMoi, btnTimKiem;

    public PanelBenhNhanModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER & SEARCH
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("HỒ SƠ BỆNH NHÂN & THẺ THÀNH VIÊN");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý thông tin hành chính, tiền sử bệnh lý và phân hạng khách hàng");
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

        btnTimKiem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 15, Color.WHITE), "Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE (Bảng phía trên, Form phía dưới)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.58);
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

        JPanel pnlFormHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFormHeader.setOpaque(false);
        JLabel lblFormIcon = new JLabel(new AppIcon(AppIcon.Type.PATIENT, 18, ModernUITheme.PRIMARY));
        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT HỒ SƠ BỆNH NHÂN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormHeader.add(lblFormIcon);
        pnlFormHeader.add(lblFormTitle);
        pnlFormCard.add(pnlFormHeader, BorderLayout.NORTH);

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
        cbGioiTinh.setFont(ModernUITheme.FONT_REGULAR);

        // DatePickerField cho ngày sinh
        dpNgaySinh = new DatePickerField(LocalDate.of(1995, 1, 1));
        dpNgaySinh.setMaxDate(LocalDate.now());

        txtTienSuBenh = ModernUITheme.createTextField(25);
        txtTienSuBenh.setToolTipText("Dị ứng thuốc tê, cao huyết áp, tiểu đường...");

        cbLoaiKhach = new JComboBox<>(new String[]{"Khách mới", "Thường", "Thân thiết", "VIP"});
        cbLoaiKhach.setFont(ModernUITheme.FONT_REGULAR);
        txtDiemTichLuy = ModernUITheme.createTextField(8);
        txtDiemTichLuy.setText("0");

        pnlFields.add(createFieldItem("Mã bệnh nhân:", txtMaBN));
        pnlFields.add(createFieldItem("Họ và tên (*):", txtHoTen));
        pnlFields.add(createFieldItem("Số điện thoại (*):", txtSDT));
        pnlFields.add(createFieldItem("Giới tính:", cbGioiTinh));
        pnlFields.add(createFieldItem("Ngày sinh (*):", dpNgaySinh));
        pnlFields.add(createFieldItem("Địa chỉ thường trú:", txtDiaChi));
        pnlFields.add(createFieldItem("Tiền sử bệnh lý / Dị ứng:", txtTienSuBenh));
        pnlFields.add(createFieldItem("Hạng thẻ thành viên:", cbLoaiKhach));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Nút bấm thao tác với AppIcon
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnXemLichSu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.HISTORY, 15, Color.WHITE), "Bệnh án & Lịch sử", ModernUITheme.ACCENT, Color.WHITE);
        btnDatHen = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CALENDAR, 15, Color.WHITE), "Đặt lịch hẹn", ModernUITheme.PRIMARY_DARK, Color.WHITE);
        btnXoa = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 15, Color.WHITE), "Xóa hồ sơ", ModernUITheme.DANGER, Color.WHITE);
        btnThem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 15, Color.WHITE), "Thêm mới hồ sơ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Cập nhật", ModernUITheme.ACCENT, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 15, ModernUITheme.TEXT_MUTED), "Làm mới form", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnXemLichSu);
        pnlButtons.add(btnDatHen);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnThem);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Sự kiện click bảng
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                napFormTuBang(table.getSelectedRow());
            }
        });

        btnThem.addActionListener(e -> xuLyThem());
        btnCapNhat.addActionListener(e -> xuLyCapNhat());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnXemLichSu.addActionListener(e -> xuLyXemLichSu());
        btnDatHen.addActionListener(e -> xuLyDatHen());
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
        List<BenhNhan> list = benhNhanDAO.getAllBenhNhan();
        for (BenhNhan bn : list) {
            tableModel.addRow(new Object[]{
                bn.getMaBN(),
                bn.getHoTen(),
                bn.getSoDienThoai(),
                bn.getDiaChi() != null ? bn.getDiaChi() : "",
                bn.getGioiTinh() != null ? bn.getGioiTinh() : "Nam",
                bn.getNgaySinh() != null ? bn.getNgaySinh().toString() : "",
                bn.getTienSuBenh() != null ? bn.getTienSuBenh() : "",
                bn.getLoaiKhach() != null ? bn.getLoaiKhach() : "Khách mới",
                bn.getDiemTichLuy()
            });
        }
        xoaTrangForm();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        txtMaBN.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        txtSDT.setText(tableModel.getValueAt(row, 2).toString());
        txtDiaChi.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        cbGioiTinh.setSelectedItem(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "Nam");

        Object valNS = tableModel.getValueAt(row, 5);
        if (valNS != null && !valNS.toString().trim().isEmpty()) {
            try {
                dpNgaySinh.setDate(Date.valueOf(valNS.toString().trim()));
            } catch (Exception ignored) {
                dpNgaySinh.clear();
            }
        } else {
            dpNgaySinh.clear();
        }

        txtTienSuBenh.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
        cbLoaiKhach.setSelectedItem(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "Khách mới");
        txtDiemTichLuy.setText(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "0");
    }

    public void selectBenhNhan(String maBN) {
        if (maBN == null || maBN.trim().isEmpty()) return;
        String clean = maBN.trim();
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
        txtMaBN.setText(benhNhanDAO.sinhMaBNMoi());
        txtHoTen.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        cbGioiTinh.setSelectedIndex(0);
        dpNgaySinh.setDate(LocalDate.of(1995, 1, 1));
        txtTienSuBenh.setText("");
        cbLoaiKhach.setSelectedIndex(0);
        txtDiemTichLuy.setText("0");
        table.clearSelection();
    }

    private void xuLyThem() {
        if (!kiemTraHopLe()) return;

        String maBN = txtMaBN.getText().trim();
        if (maBN.isEmpty()) {
            maBN = benhNhanDAO.sinhMaBNMoi();
        }
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        Date ngaySinh = dpNgaySinh.getDate();
        if (ngaySinh == null) {
            ngaySinh = Date.valueOf("1995-01-01");
        }
        String tienSu = txtTienSuBenh.getText().trim();
        String loaiKhach = cbLoaiKhach.getSelectedItem() != null ? cbLoaiKhach.getSelectedItem().toString() : "Khách mới";
        int diem = 0;
        try {
            diem = Integer.parseInt(txtDiemTichLuy.getText().trim());
        } catch (Exception ignored) {}

        BenhNhan bn = new BenhNhan(maBN, hoTen, sdt, diaChi, gioiTinh, ngaySinh, tienSu, loaiKhach, diem);
        boolean thanhCong = benhNhanDAO.themBenhNhan(bn);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Thêm hồ sơ bệnh nhân [" + maBN + " - " + hoTen + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm bệnh nhân. Vui lòng kiểm tra lại số điện thoại hoặc mã bệnh nhân trùng lặp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        int row = table.getSelectedRow();
        String maBN = txtMaBN.getText().trim();
        if (row < 0 && (maBN.isEmpty() || maBN.equals(benhNhanDAO.sinhMaBNMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!kiemTraHopLe()) return;

        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        Date ngaySinh = dpNgaySinh.getDate();
        if (ngaySinh == null) {
            ngaySinh = Date.valueOf("1995-01-01");
        }
        String tienSu = txtTienSuBenh.getText().trim();
        String loaiKhach = cbLoaiKhach.getSelectedItem() != null ? cbLoaiKhach.getSelectedItem().toString() : "Khách mới";
        int diem = 0;
        try {
            diem = Integer.parseInt(txtDiemTichLuy.getText().trim());
        } catch (Exception ignored) {}

        BenhNhan bn = new BenhNhan(maBN, hoTen, sdt, diaChi, gioiTinh, ngaySinh, tienSu, loaiKhach, diem);
        boolean thanhCong = benhNhanDAO.capNhatBenhNhan(bn);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ bệnh nhân [" + maBN + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoa() {
        int row = table.getSelectedRow();
        String maBN = txtMaBN.getText().trim();
        if (row < 0 && maBN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách để xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hoTen = txtHoTen.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa hồ sơ bệnh nhân [" + maBN + " - " + hoTen + "] khỏi hệ thống?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = benhNhanDAO.xoaBenhNhan(maBN);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa hoặc cập nhật ngừng theo dõi hồ sơ bệnh nhân [" + maBN + "]!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa hồ sơ bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyXemLichSu() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để xem bệnh án và lịch sử khám!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maBN = tableModel.getValueAt(row, 0).toString();
        String hoTen = tableModel.getValueAt(row, 1).toString();

        List<HoSoBenhAn> listHSBA = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Hồ Sơ Bệnh Án Điện Tử - " + hoTen + " (" + maBN + ")", true);
        dlg.setSize(750, 450);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(12, 16, 0, 16));
        JLabel lblHeader = new JLabel("LỊCH SỬ KHÁM CHỮA BỆNH & BỆNH ÁN: " + hoTen.toUpperCase());
        lblHeader.setFont(ModernUITheme.FONT_TITLE);
        lblHeader.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlHeader.add(lblHeader, BorderLayout.WEST);
        dlg.add(pnlHeader, BorderLayout.NORTH);

        String[] colsHS = {"Mã BA", "Ngày Khám", "Bác Sĩ Điều Trị", "Chẩn Đoán Bệnh", "Kết Quả / Hướng Điều Trị", "Ghi Chú"};
        DefaultTableModel modelHS = new DefaultTableModel(colsHS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (HoSoBenhAn hs : listHSBA) {
            modelHS.addRow(new Object[]{
                hs.getMaHoSo(),
                hs.getNgayKham() != null ? hs.getNgayKham().toString() : "",
                hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi(),
                hs.getChanDoan(),
                hs.getKetQuaDieuTri(),
                hs.getGhiChu()
            });
        }
        JTable tblHS = new JTable(modelHS);
        ModernUITheme.formatModernTable(tblHS);
        JScrollPane sp = new JScrollPane(tblHS);
        sp.setBorder(new EmptyBorder(10, 16, 10, 16));
        dlg.add(sp, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        JButton btnDong = ModernUITheme.createButton("Đóng", ModernUITheme.PRIMARY, Color.WHITE);
        btnDong.addActionListener(e -> dlg.dispose());
        pnlBtn.add(btnDong);
        dlg.add(pnlBtn, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void xuLyDatHen() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để lập lịch hẹn khám!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maBN = tableModel.getValueAt(row, 0).toString();
        String hoTen = tableModel.getValueAt(row, 1).toString();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đặt Lịch Hẹn Khám - " + hoTen, true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 10, 12));
        pnlForm.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField txtMaLH = ModernUITheme.createTextField(10);
        txtMaLH.setText(lichHenDAO.sinhMaLichHenMoi());
        txtMaLH.setEditable(false);

        JTextField txtBN = ModernUITheme.createTextField(15);
        txtBN.setText(hoTen + " (" + maBN + ")");
        txtBN.setEditable(false);

        List<NhanVien> listBS = nhanVienDAO.getDanhSachBacSi();
        JComboBox<String> cbBS = new JComboBox<>();
        for (NhanVien bs : listBS) {
            cbBS.addItem(bs.getMaNV() + " - " + bs.getHoTen());
        }

        DatePickerField dpHen = new DatePickerField(LocalDate.now().plusDays(1));
        JTextField txtGio = ModernUITheme.createTextField(10);
        txtGio.setText("09:00:00");

        JTextField txtLyDo = ModernUITheme.createTextField(20);
        txtLyDo.setText("Khám răng định kỳ & Lấy cao răng");

        pnlForm.add(new JLabel("Mã lịch hẹn:"));
        pnlForm.add(txtMaLH);
        pnlForm.add(new JLabel("Bệnh nhân:"));
        pnlForm.add(txtBN);
        pnlForm.add(new JLabel("Bác sĩ phụ trách:"));
        pnlForm.add(cbBS);
        pnlForm.add(new JLabel("Ngày hẹn:"));
        pnlForm.add(dpHen);
        pnlForm.add(new JLabel("Giờ hẹn (HH:mm:ss):"));
        pnlForm.add(txtGio);
        pnlForm.add(new JLabel("Lý do khám:"));
        pnlForm.add(txtLyDo);

        dlg.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        JButton btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Lưu lịch hẹn", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);

        btnLuu.addActionListener(e -> {
            String maLH = txtMaLH.getText().trim();
            String bsItem = cbBS.getSelectedItem() != null ? cbBS.getSelectedItem().toString() : "";
            String maBS = bsItem.contains(" - ") ? bsItem.split(" - ")[0].trim() : "BS001";
            Date d = dpHen.getDate();
            if (d == null) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày hẹn hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Time t = Time.valueOf(txtGio.getText().trim());
                String lyDo = txtLyDo.getText().trim();

                LichHen lh = new LichHen(maLH, maBN, maBS, d, t, lyDo, "Đặt hẹn tại quầy tiếp đón", "Đã xác nhận");
                boolean ok = lichHenDAO.themLichHen(lh);
                if (ok) {
                    JOptionPane.showMessageDialog(dlg, "Đã đặt lịch hẹn [" + maLH + "] cho bệnh nhân [" + hoTen + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Không thể lưu lịch hẹn vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Giờ hẹn không đúng định dạng HH:mm:ss (ví dụ: 09:30:00)!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dlg.dispose());
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieu();
            return;
        }
        keyword = keyword.trim();
        List<BenhNhan> list = benhNhanDAO.timKiem(keyword);
        tableModel.setRowCount(0);
        for (BenhNhan bn : list) {
            tableModel.addRow(new Object[]{
                bn.getMaBN(),
                bn.getHoTen(),
                bn.getSoDienThoai(),
                bn.getDiaChi() != null ? bn.getDiaChi() : "",
                bn.getGioiTinh() != null ? bn.getGioiTinh() : "Nam",
                bn.getNgaySinh() != null ? bn.getNgaySinh().toString() : "",
                bn.getTienSuBenh() != null ? bn.getTienSuBenh() : "",
                bn.getLoaiKhach() != null ? bn.getLoaiKhach() : "Khách mới",
                bn.getDiemTichLuy()
            });
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ bệnh nhân nào khớp với từ khóa: \"" + keyword + "\"", "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean kiemTraHopLe() {
        String hoTen = txtHoTen.getText().trim();
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ và tên bệnh nhân không được để trống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return false;
        }
        String sdt = txtSDT.getText().trim();
        if (sdt.isEmpty() || !sdt.matches("0[0-9]{9,10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải từ 10-11 chữ số và bắt đầu bằng số 0!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return false;
        }

        if (!dpNgaySinh.isValidDate() || dpNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ hoặc chưa được chọn!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dpNgaySinh.getLocalDate() != null && dpNgaySinh.getLocalDate().isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không được lớn hơn ngày hiện tại!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String diemStr = txtDiemTichLuy.getText().trim();
        if (!diemStr.isEmpty() && !diemStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Điểm tích lũy phải là số nguyên không âm!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtDiemTichLuy.requestFocus();
            return false;
        }
        return true;
    }
}
