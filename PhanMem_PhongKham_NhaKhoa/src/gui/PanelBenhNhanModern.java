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
import java.util.List;

/**
 * Giao diện Hồ sơ Bệnh nhân - Thiết kế phẳng hiện đại
 */
public class PanelBenhNhanModern extends JPanel {

    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaBN, txtHoTen, txtSDT, txtDiaChi, txtNgaySinh, txtTienSuBenh, txtDiemTichLuy, txtTimKiem;
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

        btnTimKiem = ModernUITheme.createButton("Tìm kiếm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE (Bảng phía trên, Form phía dưới)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.62);
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

        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT HỒ SƠ BỆNH NHÂN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

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
        txtNgaySinh = ModernUITheme.createTextField(10);
        txtNgaySinh.setToolTipText("Định dạng: yyyy-MM-dd");
        txtTienSuBenh = ModernUITheme.createTextField(25);
        txtTienSuBenh.setToolTipText("Dị ứng thuốc tê, cao huyết áp, tiểu đường...");

        cbLoaiKhach = new JComboBox<>(new String[]{"Khách mới", "Thường", "Thân thiết", "VIP"});
        txtDiemTichLuy = ModernUITheme.createTextField(8);
        txtDiemTichLuy.setText("0");

        pnlFields.add(createFieldItem("Mã bệnh nhân:", txtMaBN));
        pnlFields.add(createFieldItem("Họ và tên (*):", txtHoTen));
        pnlFields.add(createFieldItem("Số điện thoại (*):", txtSDT));
        pnlFields.add(createFieldItem("Giới tính:", cbGioiTinh));
        pnlFields.add(createFieldItem("Ngày sinh (yyyy-MM-dd):", txtNgaySinh));
        pnlFields.add(createFieldItem("Địa chỉ thường trú:", txtDiaChi));
        pnlFields.add(createFieldItem("Tiền sử bệnh lý / Dị ứng:", txtTienSuBenh));
        pnlFields.add(createFieldItem("Hạng thẻ thành viên:", cbLoaiKhach));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Nút bấm thao tác
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnXemLichSu = ModernUITheme.createButton("📋 Bệnh án & Lịch sử", ModernUITheme.ACCENT, Color.WHITE);
        btnDatHen = ModernUITheme.createButton("📅 Đặt lịch hẹn", ModernUITheme.PRIMARY_DARK, Color.WHITE);
        btnXoa = ModernUITheme.createButton("🗑️ Xóa hồ sơ", ModernUITheme.DANGER, Color.WHITE);
        btnThem = ModernUITheme.createButton("➕ Thêm mới hồ sơ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createButton("💾 Cập nhật", ModernUITheme.ACCENT, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới form", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnXemLichSu);
        pnlButtons.add(btnDatHen);
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
        txtNgaySinh.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        txtTienSuBenh.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
        cbLoaiKhach.setSelectedItem(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "Khách mới");
        txtDiemTichLuy.setText(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "0");
    }

    private void xoaTrangForm() {
        txtMaBN.setText(benhNhanDAO.sinhMaBNMoi());
        txtHoTen.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        cbGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("1995-01-01");
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
        Date ngaySinh;
        try {
            ngaySinh = Date.valueOf(txtNgaySinh.getText().trim());
        } catch (Exception e) {
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
        Date ngaySinh;
        try {
            ngaySinh = Date.valueOf(txtNgaySinh.getText().trim());
        } catch (Exception e) {
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
        String maBN = txtMaBN.getText().trim();
        if (row < 0 && maBN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để xem lịch sử điều trị & lịch hẹn!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hoTen = txtHoTen.getText().trim();
        List<HoSoBenhAn> dsHoSo = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("          HỒ SƠ BỆNH ÁN & LỊCH SỬ KHÁM BỆNH - CSDL PHÒNG KHÁM           \n");
        sb.append("========================================================================\n");
        sb.append("Bệnh nhân: ").append(hoTen).append(" (Mã: ").append(maBN).append(")\n");
        sb.append("Số điện thoại: ").append(txtSDT.getText().trim()).append(" | Hạng thẻ: ").append(cbLoaiKhach.getSelectedItem()).append("\n");
        sb.append("Tiền sử bệnh lý: ").append(txtTienSuBenh.getText().trim().isEmpty() ? "Bình thường" : txtTienSuBenh.getText().trim()).append("\n");
        sb.append("------------------------------------------------------------------------\n\n");

        sb.append("--- [1] DANH SÁCH BỆNH ÁN ĐÃ ĐIỀU TRỊ (" + dsHoSo.size() + " ca) ---\n");
        if (dsHoSo.isEmpty()) {
            sb.append("  (Chưa có bệnh án lưu trữ)\n");
        } else {
            for (HoSoBenhAn hs : dsHoSo) {
                sb.append("• Mã BA: ").append(hs.getMaHoSo()).append(" | Ngày khám: ").append(hs.getNgayKham() != null ? hs.getNgayKham().toString() : "").append("\n");
                sb.append("  Bác sĩ: ").append(hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi()).append("\n");
                sb.append("  Triệu chứng: ").append(hs.getTrieuChung()).append("\n");
                sb.append("  Chẩn đoán:   ").append(hs.getChanDoan()).append("\n");
                sb.append("  Kết quả:     ").append(hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "Tốt").append("\n");
                sb.append("  Trạng thái:  ").append(hs.getTrangThai()).append("\n");
                sb.append("  --------------------------------------------------------------------\n");
            }
        }

        sb.append("\n--- [2] LỊCH HẸN KHÁM CỦA BỆNH NHÂN ---\n");
        List<LichHen> allLH = lichHenDAO.getAllLichHen();
        int cntLH = 0;
        for (LichHen lh : allLH) {
            if (maBN.equalsIgnoreCase(lh.getMaBN())) {
                sb.append("• Mã LH: ").append(lh.getMaLichHen())
                  .append(" | Ngày: ").append(lh.getNgayHen() != null ? lh.getNgayHen().toString() : "")
                  .append(" ").append(lh.getGioHen() != null ? lh.getGioHen().toString() : "")
                  .append(" | BS: ").append(lh.getTenNhaSi() != null ? lh.getTenNhaSi() : "")
                  .append(" | Nội dung: ").append(lh.getLyDoKham())
                  .append(" [").append(lh.getTrangThai()).append("]\n");
                cntLH++;
            }
        }
        if (cntLH == 0) {
            sb.append("  (Chưa có lịch hẹn nào)\n");
        }

        JTextArea ta = new JTextArea(sb.toString(), 20, 60);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setCaretPosition(0);

        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Lịch sử điều trị bệnh nhân: " + hoTen, JOptionPane.PLAIN_MESSAGE);
    }

    private void xuLyDatHen() {
        int row = table.getSelectedRow();
        String maBN = txtMaBN.getText().trim();
        if (row < 0 && maBN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để đặt lịch hẹn!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hoTen = txtHoTen.getText().trim();

        JPanel pnl = new JPanel(new GridLayout(4, 2, 10, 8));
        JTextField txtNgay = ModernUITheme.createTextField(10);
        txtNgay.setText(new Date(System.currentTimeMillis() + 86400000L).toString());
        JTextField txtGio = ModernUITheme.createTextField(8);
        txtGio.setText("09:00:00");

        JComboBox<String> cbBS = new JComboBox<>();
        List<NhanVien> listNV = nhanVienDAO.getAllNhanVien();
        for (NhanVien nv : listNV) {
            if (nv.getChucDanh() != null && (nv.getChucDanh().contains("Bác sĩ") || nv.getChucDanh().contains("Nha sĩ"))) {
                cbBS.addItem(nv.getMaNV() + " - " + nv.getHoTen());
            }
        }
        if (cbBS.getItemCount() == 0 && !listNV.isEmpty()) {
            for (NhanVien nv : listNV) {
                cbBS.addItem(nv.getMaNV() + " - " + nv.getHoTen());
            }
        }

        JTextField txtLyDo = ModernUITheme.createTextField(20);
        txtLyDo.setText("Khám tổng quát và tư vấn nha khoa");

        pnl.add(new JLabel("Bệnh nhân:"));
        pnl.add(new JLabel(hoTen + " (" + maBN + ")"));
        pnl.add(new JLabel("Bác sĩ phụ trách:"));
        pnl.add(cbBS);
        pnl.add(new JLabel("Ngày hẹn (yyyy-MM-dd):"));
        pnl.add(txtNgay);
        pnl.add(new JLabel("Giờ hẹn (HH:mm:ss):"));
        pnl.add(txtGio);
        pnl.add(new JLabel("Lý do khám / Dịch vụ:"));
        pnl.add(txtLyDo);

        int opt = JOptionPane.showConfirmDialog(this, pnl, "Đặt lịch hẹn khám cho: " + hoTen, JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Date d = Date.valueOf(txtNgay.getText().trim());
                Time t = Time.valueOf(txtGio.getText().trim());
                String maBS = cbBS.getSelectedItem() != null ? cbBS.getSelectedItem().toString().split(" - ")[0].trim() : "BS001";
                String lyDo = txtLyDo.getText().trim();
                String maLH = lichHenDAO.sinhMaLichHenMoi();

                LichHen lh = new LichHen(maLH, maBN, maBS, d, t, lyDo, "Đặt hẹn tại quầy tiếp đón", "Đã xác nhận");
                boolean ok = lichHenDAO.themLichHen(lh);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Đã đặt lịch hẹn [" + maLH + "] cho bệnh nhân [" + hoTen + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể lưu lịch hẹn vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày hoặc giờ hẹn không đúng định dạng (yyyy-MM-dd và HH:mm:ss)!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        String ngaySinhStr = txtNgaySinh.getText().trim();
        if (!ngaySinhStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải theo định dạng yyyy-MM-dd (ví dụ: 1995-05-20)!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return false;
        }
        try {
            Date.valueOf(ngaySinhStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
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
