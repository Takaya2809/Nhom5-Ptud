package gui;

import dao.BenhNhanDAO;
import dao.HoSoBenhAnDAO;
import dao.HoaDonDAO;
import dao.LichHenDAO;
import dao.NhanVienDAO;
import dao.ThongKeDAO;
import dao.ThuocDAO;
import entity.BenhNhan;
import entity.HoSoBenhAn;
import entity.HoaDonKham;
import entity.LichHen;
import entity.NhanVien;
import entity.Thuoc;
import entity.TrangThaiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện Hồ sơ Bệnh nhân & Khách hàng Y Khoa Hoàn Thiện
 * Tích hợp tự động tính hạng thẻ từ điểm tích lũy, bắt buộc ngày sinh, kiểm tra trùng SĐT,
 * Dialog Đặt hẹn chống trùng giờ/quá khứ, Dialog Bệnh án & Lịch sử JTabbedPane 4 tab,
 * Kê đơn thuốc tự trừ tồn kho, cảnh báo dị ứng, soft delete và phân quyền vai trò.
 */
public class PanelBenhNhanModern extends JPanel {

    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ThuocDAO thuocDAO = new ThuocDAO();
    // ThongKeDAO.ghiLog() là static, không cần instance

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaBN, txtHoTen, txtSDT, txtDiaChi, txtTienSuBenh, txtDiemTichLuy, txtTimKiem;
    private JLabel lblTuoiHienThi;
    private DatePickerField dpNgaySinh;
    private JComboBox<String> cbGioiTinh, cbLoaiKhach, cbFilterHangThe, cbFilterGioiTinh;
    private JButton btnThem, btnCapNhat, btnXoa, btnXemLichSu, btnDatHen, btnLamMoi, btnTimKiem;

    private boolean isEditMode = false;
    private boolean isFormDirty = false;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PanelBenhNhanModern() {
        setLayout(new BorderLayout(0, 14));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER & SEARCH BAR
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("HỒ SƠ BỆNH NHÂN & QUẢN LÝ ĐIỀU TRỊ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý thông tin hành chính, tiền sử bệnh lý, hạng thẻ tích lũy và hồ sơ bệnh án điện tử");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar & Lọc nhanh
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);

        cbFilterHangThe = new JComboBox<>(new String[]{"Tất cả hạng thẻ", "Khách mới", "Thường", "Thân thiết", "VIP"});
        cbFilterGioiTinh = new JComboBox<>(new String[]{"Tất cả giới tính", "Nam", "Nữ"});

        txtTimKiem = ModernUITheme.createTextField(15);
        txtTimKiem.setToolTipText("Nhập mã, tên hoặc SĐT bệnh nhân...");

        btnTimKiem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 15, Color.WHITE), "Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Lọc thẻ:"));
        pnlSearch.add(cbFilterHangThe);
        pnlSearch.add(new JLabel("Giới tính:"));
        pnlSearch.add(cbFilterGioiTinh);
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG DỮ LIỆU BỆNH NHÂN (Hiển thị cả Tuổi & Cảnh báo tiền sử)
        String[] cols = {"Mã BN", "Họ Tên", "Tuổi", "SĐT", "Địa Chỉ", "Giới Tính", "Ngày Sinh", "Tiền Sử / Dị Ứng", "Hạng Thẻ", "Điểm Tích Lũy", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 2 || col == 9) return Integer.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);
        table.setAutoCreateRowSorter(true);

        // Renderer cảnh báo tiền sử dị ứng
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = value != null ? value.toString().trim() : "";
                if (!isSelected) {
                    if (!val.isEmpty() && !val.equalsIgnoreCase("Không") && !val.equalsIgnoreCase("Không có")) {
                        lbl.setForeground(ModernUITheme.DANGER);
                        lbl.setFont(ModernUITheme.FONT_BOLD);
                    } else {
                        lbl.setForeground(ModernUITheme.TEXT_MUTED);
                        lbl.setFont(ModernUITheme.FONT_REGULAR);
                    }
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        splitPane.setTopComponent(scrollPane);

        // FORM CARD NHẬP LIỆU
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JPanel pnlFormHeader = new JPanel(new BorderLayout());
        pnlFormHeader.setOpaque(false);

        JPanel pnlFormHeaderLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFormHeaderLeft.setOpaque(false);
        JLabel lblFormIcon = new JLabel(new AppIcon(AppIcon.Type.PATIENT, 18, ModernUITheme.PRIMARY));
        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT HỒ SƠ BỆNH NHÂN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormHeaderLeft.add(lblFormIcon);
        pnlFormHeaderLeft.add(lblFormTitle);
        pnlFormHeader.add(pnlFormHeaderLeft, BorderLayout.WEST);

        lblTuoiHienThi = new JLabel("Tuổi: -- ");
        lblTuoiHienThi.setFont(ModernUITheme.FONT_BOLD);
        lblTuoiHienThi.setForeground(ModernUITheme.ACCENT);
        pnlFormHeader.add(lblTuoiHienThi, BorderLayout.EAST);
        pnlFormCard.add(pnlFormHeader, BorderLayout.NORTH);

        // Grid fields: 2 hàng x 5 cột (Đã thêm txtDiemTichLuy vào layout!)
        JPanel pnlFields = new JPanel(new GridLayout(2, 5, 12, 10));
        pnlFields.setOpaque(false);

        txtMaBN = ModernUITheme.createTextField(10);
        txtMaBN.setEditable(false);
        txtMaBN.setBackground(new Color(241, 245, 249));

        txtHoTen = ModernUITheme.createTextField(16);
        txtSDT = ModernUITheme.createTextField(12);
        txtDiaChi = ModernUITheme.createTextField(18);
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});

        dpNgaySinh = new DatePickerField();
        dpNgaySinh.setMaxDate(LocalDate.now());

        txtTienSuBenh = ModernUITheme.createTextField(20);
        txtTienSuBenh.setToolTipText("Dị ứng kháng sinh, Penicillin, tim mạch, tiểu đường...");

        txtDiemTichLuy = ModernUITheme.createTextField(8);
        txtDiemTichLuy.setText("0");

        cbLoaiKhach = new JComboBox<>(new String[]{"Khách mới", "Thường", "Thân thiết", "VIP"});
        // Chỉ Quản lý mới được chỉnh tay hạng thẻ
        cbLoaiKhach.setEnabled(SessionManager.isQuanLy());

        // Lắng nghe điểm tích lũy để tự động tính hạng thẻ
        txtDiemTichLuy.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { capNhatHangTheTuDong(); isFormDirty = true; }
            @Override public void removeUpdate(DocumentEvent e) { capNhatHangTheTuDong(); isFormDirty = true; }
            @Override public void changedUpdate(DocumentEvent e) { capNhatHangTheTuDong(); isFormDirty = true; }
        });

        // Lắng nghe ngày sinh để cập nhật hiển thị tuổi
        dpNgaySinh.addPropertyChangeListener("date", evt -> capNhatTuoiHienThi());

        DocumentListener dirtyListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { isFormDirty = true; }
            @Override public void removeUpdate(DocumentEvent e) { isFormDirty = true; }
            @Override public void changedUpdate(DocumentEvent e) { isFormDirty = true; }
        };
        txtHoTen.getDocument().addDocumentListener(dirtyListener);
        txtSDT.getDocument().addDocumentListener(dirtyListener);
        txtDiaChi.getDocument().addDocumentListener(dirtyListener);
        txtTienSuBenh.getDocument().addDocumentListener(dirtyListener);

        // Hàng 1
        pnlFields.add(createFieldItem("Mã bệnh nhân:", txtMaBN));
        pnlFields.add(createFieldItem("Họ và tên (*):", txtHoTen));
        pnlFields.add(createFieldItem("Số điện thoại (*):", txtSDT));
        pnlFields.add(createFieldItem("Giới tính:", cbGioiTinh));
        pnlFields.add(createFieldItem("Ngày sinh (*):", dpNgaySinh));

        // Hàng 2
        pnlFields.add(createFieldItem("Địa chỉ thường trú:", txtDiaChi));
        pnlFields.add(createFieldItem("Tiền sử bệnh lý / Dị ứng:", txtTienSuBenh));
        pnlFields.add(createFieldItem("Điểm tích lũy:", txtDiemTichLuy));
        pnlFields.add(createFieldItem("Hạng thẻ (Tự động):", cbLoaiKhach));
        pnlFields.add(createFieldItem("Thao tác form:", btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Làm mới form", ModernUITheme.TEXT_MUTED)));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Nút bấm thao tác
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnXemLichSu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.HISTORY, 15, Color.WHITE), "Bệnh án & Lịch sử", ModernUITheme.ACCENT, Color.WHITE);
        btnDatHen = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CALENDAR, 15, Color.WHITE), "Đặt lịch hẹn", ModernUITheme.PRIMARY_DARK, Color.WHITE);
        btnXoa = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 15, Color.WHITE), "Xóa / Ngừng theo dõi", ModernUITheme.DANGER, Color.WHITE);
        btnThem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 15, Color.WHITE), "Thêm mới hồ sơ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Cập nhật", ModernUITheme.SUCCESS, Color.WHITE);

        pnlButtons.add(btnXemLichSu);
        pnlButtons.add(btnDatHen);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnThem);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Sự kiện Table chọn dòng (có kiểm tra dirty check)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                if (isFormDirty) {
                    int ans = JOptionPane.showConfirmDialog(this,
                            "Form đang có thông tin chưa lưu. Bạn có muốn bỏ qua để chọn bệnh nhân khác?",
                            "Xác nhận thay đổi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (ans != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                napFormTuBang(table.getSelectedRow());
            }
        });

        // Sự kiện các nút
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
            cbFilterHangThe.setSelectedIndex(0);
            cbFilterGioiTinh.setSelectedIndex(0);
            taiDuLieu();
        });

        cbFilterHangThe.addActionListener(e -> xuLyTimKiem());
        cbFilterGioiTinh.addActionListener(e -> xuLyTimKiem());

        // Phân quyền theo vai trò đăng nhập
        apDungPhanQuyen();

        taiDuLieu();
    }

    private void apDungPhanQuyen() {
        // Nếu là Thu ngân: Chỉ được xem, không thêm/sửa/xóa hồ sơ bệnh nhân
        if (SessionManager.isThuNgan()) {
            btnThem.setEnabled(false);
            btnCapNhat.setEnabled(false);
            btnXoa.setEnabled(false);
            btnDatHen.setEnabled(false);
        }
    }

    private JPanel createFieldItem(String labelText, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(ModernUITheme.FONT_TINY);
        lbl.setForeground(ModernUITheme.TEXT_MUTED);
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }

    private void capNhatHangTheTuDong() {
        if (!SessionManager.isQuanLy()) {
            try {
                int diem = Integer.parseInt(txtDiemTichLuy.getText().trim());
                if (diem >= 1000) {
                    cbLoaiKhach.setSelectedItem(TrangThaiConstants.THE_VIP);
                } else if (diem >= 500) {
                    cbLoaiKhach.setSelectedItem(TrangThaiConstants.THE_THAN_THIET);
                } else if (diem >= 100) {
                    cbLoaiKhach.setSelectedItem(TrangThaiConstants.THE_THUONG);
                } else {
                    cbLoaiKhach.setSelectedItem(TrangThaiConstants.THE_KHACH_MOI);
                }
            } catch (Exception ignored) {}
        }
    }

    private void capNhatTuoiHienThi() {
        if (dpNgaySinh.getLocalDate() != null) {
            int tuoi = Period.between(dpNgaySinh.getLocalDate(), LocalDate.now()).getYears();
            lblTuoiHienThi.setText("Tuổi: " + (tuoi >= 0 ? tuoi : 0) + " tuổi");
        } else {
            lblTuoiHienThi.setText("Tuổi: -- ");
        }
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        SwingWorker<List<BenhNhan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BenhNhan> doInBackground() {
                return benhNhanDAO.getAllBenhNhan();
            }

            @Override
            protected void done() {
                try {
                    List<BenhNhan> list = get();
                    for (BenhNhan bn : list) {
                        int tuoi = 0;
                        if (bn.getNgaySinh() != null) {
                            tuoi = Period.between(bn.getNgaySinh().toLocalDate(), LocalDate.now()).getYears();
                        }
                        tableModel.addRow(new Object[]{
                                bn.getMaBN(),
                                bn.getHoTen(),
                                tuoi,
                                bn.getSoDienThoai(),
                                bn.getDiaChi() != null ? bn.getDiaChi() : "",
                                bn.getGioiTinh() != null ? bn.getGioiTinh() : "Nam",
                                bn.getNgaySinh() != null ? bn.getNgaySinh().toString() : "",
                                bn.getTienSuBenh() != null ? bn.getTienSuBenh() : "",
                                bn.getLoaiKhach() != null ? bn.getLoaiKhach() : "Khách mới",
                                bn.getDiemTichLuy(),
                                bn.getTrangThai() != null ? bn.getTrangThai() : "Đang theo dõi"
                        });
                    }
                    xoaTrangForm();
                } catch (Exception ex) {
                    System.err.println("Lỗi nạp bệnh nhân: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        isEditMode = true;

        txtMaBN.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        txtSDT.setText(tableModel.getValueAt(row, 3).toString());
        txtDiaChi.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        cbGioiTinh.setSelectedItem(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "Nam");

        Object valNS = tableModel.getValueAt(row, 6);
        if (valNS != null && !valNS.toString().trim().isEmpty()) {
            try {
                dpNgaySinh.setDate(Date.valueOf(valNS.toString().trim()));
            } catch (Exception ignored) {
                dpNgaySinh.clear();
            }
        } else {
            dpNgaySinh.clear();
        }

        txtTienSuBenh.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");
        txtDiemTichLuy.setText(tableModel.getValueAt(row, 9) != null ? tableModel.getValueAt(row, 9).toString() : "0");
        cbLoaiKhach.setSelectedItem(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "Khách mới");

        capNhatTuoiHienThi();
        isFormDirty = false;
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
        isEditMode = false;
        txtMaBN.setText(benhNhanDAO.sinhMaBNMoi());
        txtHoTen.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        cbGioiTinh.setSelectedIndex(0);
        dpNgaySinh.clear(); // KHÔNG gán mặc định 1995-01-01
        txtTienSuBenh.setText("");
        txtDiemTichLuy.setText("0");
        cbLoaiKhach.setSelectedIndex(0);
        lblTuoiHienThi.setText("Tuổi: -- ");
        table.clearSelection();
        isFormDirty = false;
    }

    // =========================================================================
    // THÊM / CẬP NHẬT / XÓA
    // =========================================================================

    private void xuLyThem() {
        if (isEditMode) {
            JOptionPane.showMessageDialog(this,
                    "Bạn đang chọn một bệnh nhân có sẵn trên bảng. Vui lòng nhấn 'Làm mới form' trước khi thêm mới!",
                    "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraHopLe()) return;

        String sdt = txtSDT.getText().trim();
        String daTrung = benhNhanDAO.kiemTraTrungSoDienThoai(sdt, null);
        if (daTrung != null) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại [" + sdt + "] đã được đăng ký bởi bệnh nhân:\n[" + daTrung + "]\nVui lòng kiểm tra lại số điện thoại!",
                    "Trùng lặp số điện thoại", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        String maBN = txtMaBN.getText().trim();
        if (maBN.isEmpty()) {
            maBN = benhNhanDAO.sinhMaBNMoi();
        }
        String hoTen = txtHoTen.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        Date ngaySinh = dpNgaySinh.getDate();
        String tienSu = txtTienSuBenh.getText().trim();
        String loaiKhach = cbLoaiKhach.getSelectedItem() != null ? cbLoaiKhach.getSelectedItem().toString() : "Khách mới";
        int diem = 0;
        try { diem = Integer.parseInt(txtDiemTichLuy.getText().trim()); } catch (Exception ignored) {}

        BenhNhan bn = new BenhNhan(maBN, hoTen, sdt, diaChi, gioiTinh, ngaySinh, tienSu, loaiKhach, diem, TrangThaiConstants.BN_DANG_THEO_DOI);
        boolean ok = benhNhanDAO.themBenhNhan(bn);
        if (ok) {
            ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "THÊM BỆNH NHÂN", "Thêm bệnh nhân mới: [" + maBN + " - " + hoTen + "]");
            JOptionPane.showMessageDialog(this, "Thêm hồ sơ bệnh nhân [" + maBN + " - " + hoTen + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm bệnh nhân vào CSDL. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        if (!isEditMode) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân từ bảng trước khi bấm cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraHopLe()) return;

        String maBN = txtMaBN.getText().trim();
        String sdt = txtSDT.getText().trim();
        String daTrung = benhNhanDAO.kiemTraTrungSoDienThoai(sdt, maBN);
        if (daTrung != null) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại [" + sdt + "] đã bị trùng với bệnh nhân:\n[" + daTrung + "]\nVui lòng kiểm tra lại!",
                    "Trùng lặp số điện thoại", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        String hoTen = txtHoTen.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String gioiTinh = cbGioiTinh.getSelectedItem() != null ? cbGioiTinh.getSelectedItem().toString() : "Nam";
        Date ngaySinh = dpNgaySinh.getDate();
        String tienSu = txtTienSuBenh.getText().trim();
        String loaiKhach = cbLoaiKhach.getSelectedItem() != null ? cbLoaiKhach.getSelectedItem().toString() : "Khách mới";
        int diem = 0;
        try { diem = Integer.parseInt(txtDiemTichLuy.getText().trim()); } catch (Exception ignored) {}

        BenhNhan bn = new BenhNhan(maBN, hoTen, sdt, diaChi, gioiTinh, ngaySinh, tienSu, loaiKhach, diem, TrangThaiConstants.BN_DANG_THEO_DOI);
        boolean ok = benhNhanDAO.capNhatBenhNhan(bn);
        if (ok) {
            ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "CẬP NHẬT BỆNH NHÂN", "Cập nhật bệnh nhân: [" + maBN + " - " + hoTen + "]");
            JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ bệnh nhân [" + maBN + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng kiểm tra lại kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoa() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách để xóa hoặc ngừng theo dõi!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maBN = txtMaBN.getText().trim();
        String hoTen = txtHoTen.getText().trim();

        boolean coDuLieu = benhNhanDAO.coDuLieuLienQuan(maBN);
        String thongBao = coDuLieu
                ? "Bệnh nhân [" + maBN + " - " + hoTen + "] đã từng phát sinh Lịch hẹn / Bệnh án / Hóa đơn.\n" +
                  "Để đảm bảo toàn vẹn dữ liệu y tế, hệ thống sẽ chuyển trạng thái sang 'Ngừng theo dõi' (Soft delete).\nBạn có đồng ý không?"
                : "Bạn có chắc chắn muốn xóa vĩnh viễn hồ sơ bệnh nhân [" + maBN + " - " + hoTen + "]?";

        int confirm = JOptionPane.showConfirmDialog(this, thongBao, "Xác nhận thao tác", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = benhNhanDAO.xoaBenhNhan(maBN);
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "XÓA/NGỪNG THEO DÕI", "Xóa/Ngừng theo dõi bệnh nhân: [" + maBN + " - " + hoTen + "]");
                JOptionPane.showMessageDialog(this, "Thao tác thành công trên hồ sơ bệnh nhân [" + maBN + "]!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa hồ sơ bệnh nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // DIALOG ĐẶT LỊCH HẸN: KHUNG GIỜ COMBOBOX, CHẶN TRÙNG, CHẶN QUÁ KHỨ
    // =========================================================================

    private void xuLyDatHen() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để đặt lịch hẹn khám!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maBN = txtMaBN.getText().trim();
        String hoTen = txtHoTen.getText().trim();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đặt Lịch Hẹn Khám Bệnh - " + hoTen, true);
        dlg.setSize(480, 460);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 12, 12));
        pnlForm.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField txtDlgMaLH = ModernUITheme.createTextField(14);
        txtDlgMaLH.setText(lichHenDAO.sinhMaLichHenMoi());
        txtDlgMaLH.setEditable(false);

        JTextField txtDlgBN = ModernUITheme.createTextField(14);
        txtDlgBN.setText(maBN + " - " + hoTen);
        txtDlgBN.setEditable(false);

        JComboBox<String> cbBS = new JComboBox<>();
        List<NhanVien> listBS = nhanVienDAO.getDanhSachBacSi();
        for (NhanVien bs : listBS) {
            cbBS.addItem(bs.getMaNV() + " - " + bs.getHoTen() + " (" + bs.getChucDanh() + ")");
        }

        DatePickerField dpHen = new DatePickerField(LocalDate.now());
        dpHen.setMinDate(LocalDate.now()); // Chặn chọn ngày quá khứ

        // Khung giờ hẹn chuẩn y khoa 30 phút
        String[] khungGio = {
                "08:00:00", "08:30:00", "09:00:00", "09:30:00", "10:00:00", "10:30:00", "11:00:00", "11:30:00",
                "13:30:00", "14:00:00", "14:30:00", "15:00:00", "15:30:00", "16:00:00", "16:30:00", "17:00:00",
                "18:00:00", "18:30:00", "19:00:00", "19:30:00", "20:00:00"
        };
        JComboBox<String> cbKhungGio = new JComboBox<>(khungGio);

        JTextField txtLyDo = ModernUITheme.createTextField(20);
        txtLyDo.setText("Khám nha khoa định kỳ & kiểm tra răng");

        pnlForm.add(new JLabel("Mã lịch hẹn:"));
        pnlForm.add(txtDlgMaLH);
        pnlForm.add(new JLabel("Bệnh nhân:"));
        pnlForm.add(txtDlgBN);
        pnlForm.add(new JLabel("Bác sĩ phụ trách (*):"));
        pnlForm.add(cbBS);
        pnlForm.add(new JLabel("Ngày hẹn khám (*):"));
        pnlForm.add(dpHen);
        pnlForm.add(new JLabel("Khung giờ hẹn (*):"));
        pnlForm.add(cbKhungGio);
        pnlForm.add(new JLabel("Lý do khám / Nội dung:"));
        pnlForm.add(txtLyDo);

        dlg.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        JButton btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Xác nhận đặt lịch", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = ModernUITheme.createOutlineButton("Đóng", ModernUITheme.TEXT_MUTED);

        btnLuu.addActionListener(e -> {
            Date d = dpHen.getDate();
            if (d == null) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày hẹn hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dpHen.getLocalDate().isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(dlg, "Không được đặt lịch hẹn trong quá khứ!", "Lỗi ngày hẹn", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String bsItem = cbBS.getSelectedItem() != null ? cbBS.getSelectedItem().toString() : "";
            if (bsItem.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Chưa chọn bác sĩ phụ trách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String maBS = bsItem.split(" - ")[0].trim();
            Time t = Time.valueOf(cbKhungGio.getSelectedItem().toString());

            // Chặn đặt trùng lịch của bác sĩ
            boolean biTrung = lichHenDAO.kiemTraTrungLichBacSi(maBS, d, t, null);
            if (biTrung) {
                JOptionPane.showMessageDialog(dlg,
                        "Bác sĩ [" + bsItem + "] đã có lịch khám vào khung giờ " + t.toString().substring(0, 5) + " ngày " + d + "!\nVui lòng chọn khung giờ hoặc bác sĩ khác.",
                        "Trùng lịch khám", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maLH = txtDlgMaLH.getText().trim();
            String lyDo = txtLyDo.getText().trim();

            LichHen lh = new LichHen(maLH, maBN, maBS, d, t, lyDo, "Đặt hẹn tại quầy tiếp tân", TrangThaiConstants.LH_DA_XAC_NHAN);
            boolean ok = lichHenDAO.themLichHen(lh);
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "ĐẶT LỊCH HẸN", "Đặt lịch hẹn [" + maLH + "] cho bệnh nhân [" + hoTen + "]");
                JOptionPane.showMessageDialog(dlg, "Đã đặt lịch hẹn [" + maLH + "] cho bệnh nhân [" + hoTen + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Không thể lưu lịch hẹn vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dlg.dispose());
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // =========================================================================
    // DIALOG BỆNH ÁN & LỊCH SỬ KHÁM CHỮA BỆNH (JTABBEDPANE 4 TAB)
    // =========================================================================

    private void xuLyXemLichSu() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để xem bệnh án và lịch sử khám!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maBN = txtMaBN.getText().trim();
        String hoTen = txtHoTen.getText().trim();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Hồ Sơ Y Khoa Toàn Diện: " + hoTen + " (" + maBN + ")", true);
        dlg.setSize(950, 580);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        // Header dialog
        JPanel pnlHead = new JPanel(new BorderLayout());
        pnlHead.setBorder(new EmptyBorder(14, 18, 6, 18));
        JLabel lblH = new JLabel("HỒ SƠ BỆNH ÁN & LỊCH SỬ Y KHOA: " + hoTen.toUpperCase());
        lblH.setFont(ModernUITheme.FONT_HERO);
        lblH.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlHead.add(lblH, BorderLayout.WEST);

        JLabel lblBadgeQuyen = ModernUITheme.createBadge(
                SessionManager.isNhaSi() || SessionManager.isQuanLy() ? "Quyền: Tạo/Sửa Bệnh Án & Kê Đơn" : "Quyền: Chỉ Xem",
                SessionManager.isNhaSi() || SessionManager.isQuanLy() ? ModernUITheme.PRIMARY_LIGHT : new Color(241, 245, 249),
                SessionManager.isNhaSi() || SessionManager.isQuanLy() ? ModernUITheme.PRIMARY : ModernUITheme.TEXT_MUTED
        );
        pnlHead.add(lblBadgeQuyen, BorderLayout.EAST);
        dlg.add(pnlHead, BorderLayout.NORTH);

        // JTabbedPane 4 tab
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ModernUITheme.FONT_TITLE);

        // Tab 1: Hồ sơ bệnh án
        tabs.addTab("  1. Bệnh Án Điện Tử  ", createSubTabBenhAn(dlg, maBN, hoTen));

        // Tab 2: Lịch hẹn khám
        tabs.addTab("  2. Lịch Hẹn Khám  ", createSubTabLichHen(maBN));

        // Tab 3: Hóa đơn đã thanh toán
        tabs.addTab("  3. Hóa Đơn Khám  ", createSubTabHoaDon(maBN));

        // Tab 4: Đơn thuốc & Kê đơn
        tabs.addTab("  4. Đơn Thuốc & Kê Đơn  ", createSubTabDonThuoc(dlg, maBN, hoTen));

        dlg.add(tabs, BorderLayout.CENTER);

        JPanel pnlClose = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        JButton btnDong = ModernUITheme.createOutlineButton("Đóng", ModernUITheme.TEXT_MUTED);
        btnDong.addActionListener(e -> dlg.dispose());
        pnlClose.add(btnDong);
        dlg.add(pnlClose, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel createSubTabBenhAn(JDialog parentDlg, String maBN, String hoTen) {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(10, 14, 10, 14));

        String[] cols = {"Mã Bệnh Án", "Ngày Khám", "Bác Sĩ Điều Trị", "Triệu Chứng", "Chẩn Đoán", "Kết Quả Điều Trị", "Lời Dặn", "Trạng Thái"};
        DefaultTableModel modelBA = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableBA = new JTable(modelBA);
        ModernUITheme.formatModernTable(tableBA);

        List<HoSoBenhAn> list = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);
        for (HoSoBenhAn hs : list) {
            modelBA.addRow(new Object[]{
                    hs.getMaHoSo(),
                    hs.getNgayKham() != null ? hs.getNgayKham().toString().substring(0, 16) : "",
                    hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi(),
                    hs.getTrieuChung(),
                    hs.getChanDoan(),
                    hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "",
                    hs.getGhiChu() != null ? hs.getGhiChu() : "",
                    hs.getTrangThai()
            });
        }

        JScrollPane sp = new JScrollPane(tableBA);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(sp, BorderLayout.CENTER);

        // Nút thêm / sửa bệnh án (Chỉ Quản lý & Nha sĩ được phép click)
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnThemBA = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 14, Color.WHITE), "Tạo Bệnh Án Mới", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnSuaBA = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Sửa Bệnh Án", ModernUITheme.ACCENT, Color.WHITE);

        boolean coQuyen = SessionManager.isQuanLy() || SessionManager.isNhaSi();
        btnThemBA.setEnabled(coQuyen);
        btnSuaBA.setEnabled(coQuyen);

        btnThemBA.addActionListener(e -> moDialogTaoSuaBenhAn(parentDlg, maBN, null, () -> {
            // Refresh lại bảng bệnh án
            modelBA.setRowCount(0);
            List<HoSoBenhAn> refreshed = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);
            for (HoSoBenhAn hs : refreshed) {
                modelBA.addRow(new Object[]{
                        hs.getMaHoSo(),
                        hs.getNgayKham() != null ? hs.getNgayKham().toString().substring(0, 16) : "",
                        hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi(),
                        hs.getTrieuChung(),
                        hs.getChanDoan(),
                        hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "",
                        hs.getGhiChu() != null ? hs.getGhiChu() : "",
                        hs.getTrangThai()
                });
            }
        }));

        btnSuaBA.addActionListener(e -> {
            int sel = tableBA.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(parentDlg, "Vui lòng chọn 1 bệnh án trong bảng để sửa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maBA = modelBA.getValueAt(sel, 0).toString();
            HoSoBenhAn target = null;
            for (HoSoBenhAn h : list) {
                if (h.getMaHoSo().equalsIgnoreCase(maBA)) { target = h; break; }
            }
            if (target != null) {
                moDialogTaoSuaBenhAn(parentDlg, maBN, target, () -> {
                    modelBA.setRowCount(0);
                    List<HoSoBenhAn> refreshed = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);
                    for (HoSoBenhAn hs : refreshed) {
                        modelBA.addRow(new Object[]{
                                hs.getMaHoSo(),
                                hs.getNgayKham() != null ? hs.getNgayKham().toString().substring(0, 16) : "",
                                hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi(),
                                hs.getTrieuChung(),
                                hs.getChanDoan(),
                                hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "",
                                hs.getGhiChu() != null ? hs.getGhiChu() : "",
                                hs.getTrangThai()
                        });
                    }
                });
            }
        });

        pnlActions.add(btnSuaBA);
        pnlActions.add(btnThemBA);
        pnl.add(pnlActions, BorderLayout.SOUTH);
        return pnl;
    }

    private void moDialogTaoSuaBenhAn(JDialog parent, String maBN, HoSoBenhAn hsCu, Runnable onSuccess) {
        boolean isEdit = hsCu != null;
        JDialog dlg = new JDialog(parent, (isEdit ? "Cập Nhật Hồ Sơ Bệnh Án" : "Tạo Mới Hồ Sơ Bệnh Án Nha Khoa"), true);
        dlg.setSize(520, 520);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(new BorderLayout(14, 14));

        JPanel pnl = new JPanel(new GridLayout(6, 2, 10, 10));
        pnl.setBorder(new EmptyBorder(16, 20, 10, 20));

        JTextField txtMa = ModernUITheme.createTextField(12);
        txtMa.setText(isEdit ? hsCu.getMaHoSo() : hoSoBenhAnDAO.sinhMaHoSoMoi());
        txtMa.setEditable(false);

        JComboBox<String> cbBS = new JComboBox<>();
        List<NhanVien> dsBS = nhanVienDAO.getDanhSachBacSi();
        for (NhanVien nv : dsBS) {
            cbBS.addItem(nv.getMaNV() + " - " + nv.getHoTen());
            if (isEdit && nv.getMaNV().equalsIgnoreCase(hsCu.getMaNhaSi())) {
                cbBS.setSelectedItem(nv.getMaNV() + " - " + nv.getHoTen());
            }
        }

        JTextField txtTrieuChung = ModernUITheme.createTextField(25);
        txtTrieuChung.setText(isEdit ? hsCu.getTrieuChung() : "Đau nhức vùng răng hàm, ê buốt khi ăn đồ lạnh");

        JTextField txtChanDoan = ModernUITheme.createTextField(25);
        txtChanDoan.setText(isEdit ? hsCu.getChanDoan() : "Viêm tủy cấp R46, sâu răng mặt nhai");

        JTextField txtKetQua = ModernUITheme.createTextField(25);
        txtKetQua.setText(isEdit ? hsCu.getKetQuaDieuTri() : "Mở tủy làm sạch ống tủy, đặt thuốc diệt tủy");

        JTextField txtGhiChu = ModernUITheme.createTextField(25);
        txtGhiChu.setText(isEdit ? hsCu.getGhiChu() : "Tái khám sau 3 ngày để hàn bít ống tủy");

        pnl.add(new JLabel("Mã bệnh án:"));
        pnl.add(txtMa);
        pnl.add(new JLabel("Bác sĩ điều trị:"));
        pnl.add(cbBS);
        pnl.add(new JLabel("Triệu chứng lâm sàng (*):"));
        pnl.add(txtTrieuChung);
        pnl.add(new JLabel("Chẩn đoán y khoa (*):"));
        pnl.add(txtChanDoan);
        pnl.add(new JLabel("Kết quả điều trị:"));
        pnl.add(txtKetQua);
        pnl.add(new JLabel("Lời dặn / Ghi chú:"));
        pnl.add(txtGhiChu);

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Lưu Bệnh Án", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);

        btnLuu.addActionListener(e -> {
            String tc = txtTrieuChung.getText().trim();
            String cd = txtChanDoan.getText().trim();
            if (tc.isEmpty() || cd.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Triệu chứng và chẩn đoán không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String bsItem = cbBS.getSelectedItem() != null ? cbBS.getSelectedItem().toString() : "";
            String maBS = bsItem.split(" - ")[0].trim();

            if (isEdit) {
                hsCu.setMaNhaSi(maBS);
                hsCu.setTrieuChung(tc);
                hsCu.setChanDoan(cd);
                hsCu.setKetQuaDieuTri(txtKetQua.getText().trim());
                hsCu.setGhiChu(txtGhiChu.getText().trim());
                boolean ok = hoSoBenhAnDAO.capNhatHoSoBenhAn(hsCu);
                if (ok) {
                    ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "SỬA BỆNH ÁN", "Cập nhật bệnh án [" + hsCu.getMaHoSo() + "]");
                    JOptionPane.showMessageDialog(dlg, "Cập nhật bệnh án thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dlg.dispose();
                }
            } else {
                HoSoBenhAn hsMoi = new HoSoBenhAn(
                        txtMa.getText().trim(),
                        maBN,
                        maBS,
                        null,
                        new Timestamp(System.currentTimeMillis()),
                        tc,
                        cd,
                        txtKetQua.getText().trim(),
                        txtGhiChu.getText().trim(),
                        TrangThaiConstants.BA_HOAN_THANH
                );
                boolean ok = hoSoBenhAnDAO.themHoSoBenhAn(hsMoi);
                if (ok) {
                    ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "TẠO BỆNH ÁN", "Tạo bệnh án mới [" + hsMoi.getMaHoSo() + "] cho bệnh nhân [" + maBN + "]");
                    JOptionPane.showMessageDialog(dlg, "Tạo bệnh án mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    if (onSuccess != null) onSuccess.run();
                    dlg.dispose();
                }
            }
        });

        btnHuy.addActionListener(e -> dlg.dispose());
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private JPanel createSubTabLichHen(String maBN) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(new EmptyBorder(10, 14, 10, 14));

        String[] cols = {"Mã Hẹn", "Ngày Hẹn", "Giờ Hẹn", "Bác Sĩ", "Nội Dung Khám", "Trạng Thái", "Ghi Chú"};
        DefaultTableModel modelLH = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableLH = new JTable(modelLH);
        ModernUITheme.formatModernTable(tableLH);

        List<LichHen> all = lichHenDAO.getAllLichHen();
        for (LichHen lh : all) {
            if (lh.getMaBN() != null && lh.getMaBN().equalsIgnoreCase(maBN)) {
                modelLH.addRow(new Object[]{
                        lh.getMaLichHen(),
                        lh.getNgayHen() != null ? lh.getNgayHen().toString() : "",
                        lh.getGioHen() != null ? lh.getGioHen().toString().substring(0, 5) : "",
                        lh.getTenNhaSi() != null ? lh.getTenNhaSi() : lh.getMaNhaSi(),
                        lh.getLyDoKham(),
                        lh.getTrangThai(),
                        lh.getGhiChu() != null ? lh.getGhiChu() : ""
                });
            }
        }

        JScrollPane sp = new JScrollPane(tableLH);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(sp, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createSubTabHoaDon(String maBN) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(new EmptyBorder(10, 14, 10, 14));

        String[] cols = {"Mã HD", "Ngày Lập", "Dịch Vụ Khám", "Tổng Tiền", "Giảm Giá", "Thành Tiền", "Phương Thức", "Trạng Thái"};
        DefaultTableModel modelHD = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableHD = new JTable(modelHD);
        ModernUITheme.formatModernTable(tableHD);

        List<HoaDonKham> all = hoaDonDAO.getAllHoaDon();
        for (HoaDonKham hd : all) {
            if (hd.getMaBN() != null && hd.getMaBN().equalsIgnoreCase(maBN)) {
                modelHD.addRow(new Object[]{
                        "HD" + String.format("%03d", hd.getMaHD()),
                        hd.getNgayLap() != null ? hd.getNgayLap().toString().substring(0, 16) : "",
                        hd.getTenDichVu() != null ? hd.getTenDichVu() : hd.getMaDichVu(),
                        df.format(hd.getTongTien()),
                        df.format(hd.getGiamGia()),
                        df.format(hd.getThanhTien()),
                        hd.getPhuongThucTT(),
                        hd.getTrangThai()
                });
            }
        }

        JScrollPane sp = new JScrollPane(tableHD);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(sp, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createSubTabDonThuoc(JDialog parent, String maBN, String hoTen) {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(10, 14, 10, 14));

        String[] cols = {"Tên Thuốc", "Hoạt Chất", "Đơn Vị", "Số Lượng Kê", "Liều Dùng & Cách Dùng"};
        DefaultTableModel modelDT = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableDT = new JTable(modelDT);
        ModernUITheme.formatModernTable(tableDT);

        // Mẫu đơn thuốc hiện có
        modelDT.addRow(new Object[]{"Amoxicillin 500mg", "Amoxicillin Trihydrate", "Viên", 14, "Uống 1 viên x 2 lần/ngày sau ăn"});
        modelDT.addRow(new Object[]{"Ibuprofen 400mg", "Ibuprofen", "Viên", 10, "Uống 1 viên khi đau sau khi ăn no"});
        modelDT.addRow(new Object[]{"Nước súc miệng Chlorhexidine 0.12%", "Chlorhexidine", "Chai", 1, "Súc miệng 15ml x 3 lần/ngày"});

        JScrollPane sp = new JScrollPane(tableDT);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(sp, BorderLayout.CENTER);

        // Nút kê thêm thuốc (Tự động trừ tồn kho)
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnKeDon = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 14, Color.WHITE), "Kê Thêm Thuốc & Trừ Tồn Kho", ModernUITheme.PRIMARY, Color.WHITE);

        boolean coQuyen = SessionManager.isQuanLy() || SessionManager.isNhaSi();
        btnKeDon.setEnabled(coQuyen);

        btnKeDon.addActionListener(e -> {
            moDialogKeDonThuoc(parent, (tenTh, hoatChat, dvt, sl, cachDung) -> {
                modelDT.addRow(new Object[]{tenTh, hoatChat, dvt, sl, cachDung});
            });
        });

        pnlAction.add(btnKeDon);
        pnl.add(pnlAction, BorderLayout.SOUTH);
        return pnl;
    }

    private interface CallbackKeDon {
        void onKeDon(String tenThuoc, String hoatChat, String dvt, int soLuong, String cachDung);
    }

    private void moDialogKeDonThuoc(JDialog parent, CallbackKeDon callback) {
        JDialog dlg = new JDialog(parent, "Kê Đơn Thuốc Y Khoa - Tự Động Trừ Tồn Kho", true);
        dlg.setSize(480, 380);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(new BorderLayout(14, 14));

        JPanel pnl = new JPanel(new GridLayout(4, 2, 10, 12));
        pnl.setBorder(new EmptyBorder(16, 20, 10, 20));

        JComboBox<String> cbThuoc = new JComboBox<>();
        List<Thuoc> dsThuoc = thuocDAO.getAllThuoc();
        for (Thuoc t : dsThuoc) {
            cbThuoc.addItem(t.getMaThuoc() + " - " + t.getTenThuoc() + " (Tồn: " + t.getSoLuongTon() + " " + t.getDonViTinh() + ")");
        }

        JSpinner spSoLuong = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        JTextField txtCachDung = ModernUITheme.createTextField(20);
        txtCachDung.setText("Uống 1 viên x 2 lần/ngày sau khi ăn no");

        pnl.add(new JLabel("Chọn thuốc trong kho (*):"));
        pnl.add(cbThuoc);
        pnl.add(new JLabel("Số lượng kê (*):"));
        pnl.add(spSoLuong);
        pnl.add(new JLabel("Liều dùng & Hướng dẫn:"));
        pnl.add(txtCachDung);

        dlg.add(pnl, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnXacNhan = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Xác nhận kê & Xuất kho", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);

        btnXacNhan.addActionListener(e -> {
            int idx = cbThuoc.getSelectedIndex();
            if (idx < 0 || idx >= dsThuoc.size()) return;
            Thuoc th = dsThuoc.get(idx);
            int sl = (Integer) spSoLuong.getValue();

            if (sl > th.getSoLuongTon()) {
                JOptionPane.showMessageDialog(dlg, "Số lượng kê (" + sl + ") vượt quá số lượng tồn kho thực tế (" + th.getSoLuongTon() + ")!", "Lỗi tồn kho", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tự động trừ tồn kho
            boolean ok = thuocDAO.nhapXuatKho(th.getMaThuoc(), -sl);
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "KÊ ĐƠN THUỐC", "Kê đơn xuất kho thuốc [" + th.getTenThuoc() + "] số lượng: " + sl);
                JOptionPane.showMessageDialog(dlg, "Đã kê đơn và trừ " + sl + " " + th.getDonViTinh() + " thuốc [" + th.getTenThuoc() + "] trong kho!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (callback != null) {
                    callback.onKeDon(th.getTenThuoc(), th.getHoatChat(), th.getDonViTinh(), sl, txtCachDung.getText().trim());
                }
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Không thể cập nhật tồn kho thuốc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnHuy.addActionListener(e -> dlg.dispose());
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnXacNhan);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // =========================================================================
    // TÌM KIẾM & KIỂM TRA HỢP LỆ
    // =========================================================================

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText() != null ? txtTimKiem.getText().trim() : "";
        String filterHang = cbFilterHangThe.getSelectedItem() != null ? cbFilterHangThe.getSelectedItem().toString() : "Tất cả hạng thẻ";
        String filterGT = cbFilterGioiTinh.getSelectedItem() != null ? cbFilterGioiTinh.getSelectedItem().toString() : "Tất cả giới tính";

        List<BenhNhan> all = benhNhanDAO.timKiem(keyword);
        tableModel.setRowCount(0);

        for (BenhNhan bn : all) {
            boolean matchHang = filterHang.startsWith("Tất cả") || (bn.getLoaiKhach() != null && bn.getLoaiKhach().equalsIgnoreCase(filterHang));
            boolean matchGT = filterGT.startsWith("Tất cả") || (bn.getGioiTinh() != null && bn.getGioiTinh().equalsIgnoreCase(filterGT));

            if (matchHang && matchGT) {
                int tuoi = 0;
                if (bn.getNgaySinh() != null) {
                    tuoi = Period.between(bn.getNgaySinh().toLocalDate(), LocalDate.now()).getYears();
                }
                tableModel.addRow(new Object[]{
                        bn.getMaBN(),
                        bn.getHoTen(),
                        tuoi,
                        bn.getSoDienThoai(),
                        bn.getDiaChi() != null ? bn.getDiaChi() : "",
                        bn.getGioiTinh() != null ? bn.getGioiTinh() : "Nam",
                        bn.getNgaySinh() != null ? bn.getNgaySinh().toString() : "",
                        bn.getTienSuBenh() != null ? bn.getTienSuBenh() : "",
                        bn.getLoaiKhach() != null ? bn.getLoaiKhach() : "Khách mới",
                        bn.getDiemTichLuy(),
                        bn.getTrangThai() != null ? bn.getTrangThai() : "Đang theo dõi"
                });
            }
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

        // BẮT BUỘC NGÀY SINH - Không gán mặc định ảo
        if (!dpNgaySinh.isValidDate() || dpNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Ngày sinh là trường bắt buộc. Vui lòng chọn ngày sinh hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
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
