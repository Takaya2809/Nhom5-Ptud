package gui;

import dao.CaTrucDAO;
import dao.NhanVienDAO;
import entity.CaTruc;
import entity.NhanVien;
import entity.PhanCongCaTruc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Giao diện Lịch Trực & Phân Công Ca Khám Bác Sĩ - Phong cách hiện đại
 */
public class PanelLichTrucModern extends JPanel {

    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> cbNhanVien, cbCaTruc, cbVaiTro;
    private JTextField txtGhiChu, txtTimKiem;
    private JButton btnPhanCong, btnHuyPhanCong, btnLamMoi, btnLoc, btnDiemDanh, btnThemCa;

    public PanelLichTrucModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH TRỰC & PHÂN CÔNG CA KHÁM BÁC SĨ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Điều phối bác sĩ chuyên khoa, y tá phụ tá và kỹ thuật viên ghế nha theo ca làm việc");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(16);
        txtTimKiem.setToolTipText("Tìm bác sĩ, ca trực...");
        btnLoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 14, Color.WHITE), "Tra cứu", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 13, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);

        pnlSearch.add(new JLabel("Tra cứu:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnLoc);
        pnlSearch.add(btnTaiLai);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.62);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG PHÂN CÔNG
        String[] cols = { "Mã PC", "Bác Sĩ / Y Tá", "Ca Khám", "Vai Trò Trong Ca", "Điểm Danh", "Ghi Chú Phân Công" };
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

        // FORM CARD PHÂN CÔNG
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("PHÂN CÔNG BÁC SĨ VÀO CA TRỰC PHÒNG KHÁM");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 2, 20, 10));
        pnlFields.setOpaque(false);

        cbNhanVien = new JComboBox<>();
        cbCaTruc = new JComboBox<>();
        cbVaiTro = new JComboBox<>(new String[] { "Bác sĩ chính", "Phụ tá ghế", "Điều dưỡng", "Lễ tân" });
        txtGhiChu = ModernUITheme.createTextField(25);
        txtGhiChu.setText("Phụ trách khám tổng quát & cấy ghép");

        pnlFields.add(createFieldItem("Bác sĩ / Nhân sự y tế (*):", cbNhanVien));
        pnlFields.add(createFieldItem("Ca trực nha khoa (*):", cbCaTruc));
        pnlFields.add(createFieldItem("Vai trò phụ trách trong ca:", cbVaiTro));
        pnlFields.add(createFieldItem("Ghi chú vị trí ghế / nhiệm vụ:", txtGhiChu));

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThemCa = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.CLOCK, 14, ModernUITheme.PRIMARY), "Tạo ca trực mới", ModernUITheme.PRIMARY);
        btnDiemDanh = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Điểm danh", ModernUITheme.ACCENT, Color.WHITE);
        btnHuyPhanCong = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CLOSE, 14, Color.WHITE), "Hủy phân công", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Làm mới", ModernUITheme.TEXT_MUTED);
        btnPhanCong = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 14, Color.WHITE), "Xác nhận phân công", ModernUITheme.PRIMARY, Color.WHITE);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnThemCa);
        pnlButtons.add(btnDiemDanh);
        pnlButtons.add(btnHuyPhanCong);
        pnlButtons.add(btnPhanCong);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                napFormTuBang(table.getSelectedRow());
            }
        });

        btnPhanCong.addActionListener(e -> xuLyPhanCong());
        btnHuyPhanCong.addActionListener(e -> xuLyHuy());
        btnDiemDanh.addActionListener(e -> xuLyDiemDanh());
        btnThemCa.addActionListener(e -> xuLyThemCa());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnLoc.addActionListener(e -> xuLyLoc());
        txtTimKiem.addActionListener(e -> xuLyLoc());
        btnTaiLai.addActionListener(e -> {
            txtTimKiem.setText("");
            taiDuLieu();
        });

        napCombobox();
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

    private void napCombobox() {
        cbNhanVien.removeAllItems();
        cbCaTruc.removeAllItems();

        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();
        for (NhanVien nv : dsNV) {
            cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTen() + " (" + nv.getChucDanh() + ")");
        }

        List<CaTruc> dsCa = caTrucDAO.getAllCaTruc();
        for (CaTruc ct : dsCa) {
            String gio = "";
            if (ct.getGioBatDau() != null && ct.getGioKetThuc() != null) {
                gio = " (" + ct.getGioBatDau().toString().substring(0, 5) + " - " + ct.getGioKetThuc().toString().substring(0, 5) + ")";
            }
            cbCaTruc.addItem(ct.getMaCa() + " - " + ct.getTenCa() + gio);
        }

        if (cbCaTruc.getItemCount() == 0) {
            cbCaTruc.addItem("CA_20261023_SANG - Ca Sáng (08:00 - 12:00)");
            cbCaTruc.addItem("CA_20261023_CHIEU - Ca Chiều (13:30 - 17:30)");
            cbCaTruc.addItem("CA_20261023_TOI - Ca Tối (18:00 - 21:00)");
        }
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<PhanCongCaTruc> list = caTrucDAO.getAllPhanCong();
        for (PhanCongCaTruc pc : list) {
            tableModel.addRow(new Object[] {
                "PC" + String.format("%03d", pc.getMaPhanCong()),
                pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV(),
                pc.getTenCa() != null ? pc.getTenCa() : pc.getMaCa(),
                pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "Bác sĩ chính",
                pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : "Chưa đến",
                pc.getGhiChu() != null ? pc.getGhiChu() : ""
            });
        }
        xoaTrangForm();
    }

    public void selectPhanCong(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        String clean = keyword.trim().toLowerCase();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object val = tableModel.getValueAt(i, col);
                if (val != null && val.toString().toLowerCase().contains(clean)) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    napFormTuBang(i);
                    return;
                }
            }
        }
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        String tenNV = tableModel.getValueAt(row, 1).toString();
        for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
            if (cbNhanVien.getItemAt(i).contains(tenNV)) {
                cbNhanVien.setSelectedIndex(i);
                break;
            }
        }
        String tenCa = tableModel.getValueAt(row, 2).toString();
        for (int i = 0; i < cbCaTruc.getItemCount(); i++) {
            if (cbCaTruc.getItemAt(i).contains(tenCa) || tenCa.contains(cbCaTruc.getItemAt(i).split(" - ")[0])) {
                cbCaTruc.setSelectedIndex(i);
                break;
            }
        }
        String vt = tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Bác sĩ chính";
        cbVaiTro.setSelectedItem(vt);
        txtGhiChu.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
    }

    private void xoaTrangForm() {
        if (cbNhanVien.getItemCount() > 0)
            cbNhanVien.setSelectedIndex(0);
        if (cbCaTruc.getItemCount() > 0)
            cbCaTruc.setSelectedIndex(0);
        cbVaiTro.setSelectedIndex(0);
        txtGhiChu.setText("");
        table.clearSelection();
    }

    private void xuLyPhanCong() {
        if (cbNhanVien.getSelectedItem() == null || cbCaTruc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên và ca trực!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemNV = cbNhanVien.getSelectedItem().toString();
        String maNV = itemNV.split(" - ")[0].trim();
        String hoTenNV = itemNV.contains(" - ") ? itemNV.substring(itemNV.indexOf(" - ") + 3).trim() : maNV;
        if (hoTenNV.contains(" (")) {
            hoTenNV = hoTenNV.substring(0, hoTenNV.indexOf(" (")).trim();
        }

        String itemCa = cbCaTruc.getSelectedItem().toString();
        String maCa = itemCa.split(" - ")[0].trim();
        String tenCa = itemCa.contains(" - ") ? itemCa.substring(itemCa.indexOf(" - ") + 3).trim() : maCa;

        String vaiTro = cbVaiTro.getSelectedItem() != null ? cbVaiTro.getSelectedItem().toString() : "Bác sĩ chính";
        String ghiChu = txtGhiChu.getText().trim();

        PhanCongCaTruc pc = new PhanCongCaTruc(0, maNV, maCa, vaiTro, "Chưa đến", ghiChu);
        pc.setHoTenNV(hoTenNV);
        pc.setTenCa(tenCa);

        boolean thanhCong = caTrucDAO.themPhanCong(pc);
        if (thanhCong) {
            JOptionPane.showMessageDialog(this, "Phân công [" + hoTenNV + "] vào [" + tenCa + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Phân công thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuy() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lượt phân công trong bảng để hủy!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maPCStr = tableModel.getValueAt(row, 0).toString().replaceAll("[^0-9]", "");
        int maPC = Integer.parseInt(maPCStr);
        String tenNV = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy phân công ca trực của [" + tenNV + "]?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean thanhCong = caTrucDAO.xoaPhanCong(maPC);
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Đã hủy phân công ca trực thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy phân công thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyLoc() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieu();
            return;
        }
        keyword = keyword.trim().toLowerCase();
        List<PhanCongCaTruc> all = caTrucDAO.getAllPhanCong();
        tableModel.setRowCount(0);
        for (PhanCongCaTruc pc : all) {
            boolean match = (pc.getHoTenNV() != null && pc.getHoTenNV().toLowerCase().contains(keyword)) ||
                            (pc.getMaNV() != null && pc.getMaNV().toLowerCase().contains(keyword)) ||
                            (pc.getTenCa() != null && pc.getTenCa().toLowerCase().contains(keyword)) ||
                            (pc.getMaCa() != null && pc.getMaCa().toLowerCase().contains(keyword)) ||
                            (pc.getVaiTroTrongCa() != null && pc.getVaiTroTrongCa().toLowerCase().contains(keyword)) ||
                            (pc.getGhiChu() != null && pc.getGhiChu().toLowerCase().contains(keyword));
            if (match) {
                tableModel.addRow(new Object[] {
                    "PC" + String.format("%03d", pc.getMaPhanCong()),
                    pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV(),
                    pc.getTenCa() != null ? pc.getTenCa() : pc.getMaCa(),
                    pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "Bác sĩ chính",
                    pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : "Chưa đến",
                    pc.getGhiChu() != null ? pc.getGhiChu() : ""
                });
            }
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lượt phân công nào khớp với từ khóa: \"" + keyword + "\"", "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyDiemDanh() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lượt phân công trong bảng để điểm danh!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int maPC = Integer.parseInt(tableModel.getValueAt(r, 0).toString().replaceAll("[^0-9]", ""));
        String tenNV = tableModel.getValueAt(r, 1).toString();
        String currentTT = tableModel.getValueAt(r, 4).toString();

        String[] options = {"Đã đến trực", "Đang trực", "Vắng mặt", "Đổi ca", "Hoàn thành ca"};
        Object selected = JOptionPane.showInputDialog(this, 
            "Chọn trạng thái điểm danh cho nhân sự [" + tenNV + "]:", 
            "Điểm danh ca trực phòng khám", 
            JOptionPane.QUESTION_MESSAGE, null, options, currentTT);

        if (selected != null) {
            boolean ok = caTrucDAO.diemDanh(maPC, selected.toString());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm danh cho [" + tenNV + "] thành: " + selected, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyThemCa() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo Ca Trực Nha Khoa Mới", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));

        JPanel pnlContent = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlContent.setBorder(new EmptyBorder(15, 20, 15, 20));

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdfDate.format(new java.util.Date());
        String defaultMaCa = "CA_" + todayStr.replace("-", "") + "_SANG";

        JTextField txtDlgMa = ModernUITheme.createTextField(14);
        txtDlgMa.setText(defaultMaCa);
        JComboBox<String> cbDlgTen = new JComboBox<>(new String[]{"Ca Sáng (08:00 - 12:00)", "Ca Chiều (13:30 - 17:30)", "Ca Tối (18:00 - 21:00)", "Ca Cấp Cứu Ngoài Giờ"});
        JTextField txtDlgNgay = ModernUITheme.createTextField(10);
        txtDlgNgay.setText(todayStr);
        JTextField txtDlgGioBD = ModernUITheme.createTextField(8);
        txtDlgGioBD.setText("08:00:00");
        JTextField txtDlgGioKT = ModernUITheme.createTextField(8);
        txtDlgGioKT.setText("12:00:00");
        JTextField txtDlgMaxBN = ModernUITheme.createTextField(6);
        txtDlgMaxBN.setText("15");

        cbDlgTen.addActionListener(e -> {
            int idx = cbDlgTen.getSelectedIndex();
            String prefix = "CA_" + txtDlgNgay.getText().trim().replace("-", "") + "_";
            if (idx == 0) {
                txtDlgMa.setText(prefix + "SANG");
                txtDlgGioBD.setText("08:00:00");
                txtDlgGioKT.setText("12:00:00");
            } else if (idx == 1) {
                txtDlgMa.setText(prefix + "CHIEU");
                txtDlgGioBD.setText("13:30:00");
                txtDlgGioKT.setText("17:30:00");
            } else if (idx == 2) {
                txtDlgMa.setText(prefix + "TOI");
                txtDlgGioBD.setText("18:00:00");
                txtDlgGioKT.setText("21:00:00");
            } else {
                txtDlgMa.setText(prefix + "CAPCUU");
                txtDlgGioBD.setText("21:00:00");
                txtDlgGioKT.setText("23:59:00");
            }
        });

        pnlContent.add(new JLabel("Mã ca trực (*):"));
        pnlContent.add(txtDlgMa);
        pnlContent.add(new JLabel("Tên ca:"));
        pnlContent.add(cbDlgTen);
        pnlContent.add(new JLabel("Ngày trực (yyyy-MM-dd):"));
        pnlContent.add(txtDlgNgay);
        pnlContent.add(new JLabel("Giờ bắt đầu:"));
        pnlContent.add(txtDlgGioBD);
        pnlContent.add(new JLabel("Giờ kết thúc:"));
        pnlContent.add(txtDlgGioKT);
        pnlContent.add(new JLabel("Số BN tối đa tiếp nhận:"));
        pnlContent.add(txtDlgMaxBN);

        dlg.add(pnlContent, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);
        JButton btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Lưu Ca Trực Vào DB", ModernUITheme.PRIMARY, Color.WHITE);
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        btnHuy.addActionListener(ev -> dlg.dispose());
        btnLuu.addActionListener(ev -> {
            try {
                String ma = txtDlgMa.getText().trim();
                String ten = cbDlgTen.getSelectedItem().toString();
                Date ngay = Date.valueOf(txtDlgNgay.getText().trim());
                Time bd = Time.valueOf(txtDlgGioBD.getText().trim());
                Time kt = Time.valueOf(txtDlgGioKT.getText().trim());
                int maxBN = Integer.parseInt(txtDlgMaxBN.getText().trim());

                CaTruc ct = new CaTruc(ma, ten, ngay, bd, kt, 1, 1, maxBN, "Đã lên lịch");
                boolean ok = caTrucDAO.themCaTruc(ct);
                if (ok) {
                    JOptionPane.showMessageDialog(dlg, "Đã thêm ca trực [" + ma + "] vào cơ sở dữ liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    napCombobox();
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Không thể thêm ca trực. Có thể mã ca đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Định dạng ngày (yyyy-MM-dd), giờ (HH:mm:ss) hoặc số lượng không hợp lệ!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }
}
