package gui;

import dao.BenhNhanDAO;
import dao.DichVuDAO;
import dao.HoaDonDAO;
import dao.NhanVienDAO;
import dao.UuDaiDAO;
import entity.BenhNhan;
import entity.DichVu;
import entity.HoaDonKham;
import entity.NhanVien;
import entity.UuDai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Giao diện Lập Phiếu Khám & Thanh Toán Dịch Vụ Nha Khoa - Phong cách hiện đại
 */
public class PanelThanhToanModern extends JPanel {

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private DichVuDAO dichVuDAO = new DichVuDAO();
    private UuDaiDAO uuDaiDAO = new UuDaiDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> cbBenhNhan, cbBacSi, cbDichVu, cbUuDai, cbPhuongThuc;
    private JLabel lblTongTien, lblGiamGia, lblThanhTien;
    private JButton btnThanhToan, btnInHoaDon, btnLamMoi, btnTimKiem, btnHuyHoaDon;
    private JTextField txtTimKiem;

    private double currentDonGia = 0;
    private double currentPhanTramGiam = 0;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PanelThanhToanModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("LẬP PHIẾU KHÁM & THU NGÂN ĐIỀU TRỊ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Thu tiền dịch vụ, tự động áp dụng mã ưu đãi & chương trình tri ân thành viên");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Search bar
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlSearch.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(16);
        txtTimKiem.setToolTipText("Tra cứu mã HĐ, tên bệnh nhân, bác sĩ...");
        btnTimKiem = ModernUITheme.createButton("Tra cứu", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

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

        // BẢNG HÓA ĐƠN GẦN ĐÂY
        String[] cols = {"Mã HĐ", "Bệnh Nhân", "Bác Sĩ Điều Trị", "Dịch Vụ Khám", "Ngày Khám", "Thành Tiền", "Phương Thức", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        splitPane.setTopComponent(scrollPane);

        // FORM CARD LẬP HÓA ĐƠN
        JPanel pnlBillingCard = ModernUITheme.createCardPanel();
        pnlBillingCard.setLayout(new BorderLayout(15, 12));

        JLabel lblFormTitle = new JLabel("CHI TIẾT PHIẾU KHÁM & THANH TOÁN TIỀN");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlBillingCard.add(lblFormTitle, BorderLayout.NORTH);

        // Center: Selection fields
        JPanel pnlFields = new JPanel(new GridLayout(2, 3, 16, 10));
        pnlFields.setOpaque(false);

        cbBenhNhan = new JComboBox<>();
        cbBacSi = new JComboBox<>();
        cbDichVu = new JComboBox<>();
        cbUuDai = new JComboBox<>();
        cbPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản QR", "Quẹt thẻ POS", "Ví điện tử MoMo"});

        pnlFields.add(createFieldItem("Bệnh nhân tiếp nhận (*):", cbBenhNhan));
        pnlFields.add(createFieldItem("Bác sĩ điều trị (*):", cbBacSi));
        pnlFields.add(createFieldItem("Dịch vụ thực hiện (*):", cbDichVu));
        pnlFields.add(createFieldItem("Chương trình ưu đãi:", cbUuDai));
        pnlFields.add(createFieldItem("Hình thức thanh toán:", cbPhuongThuc));

        pnlBillingCard.add(pnlFields, BorderLayout.CENTER);

        // Bottom: Total summary & Action buttons
        JPanel pnlSummaryAndBtns = new JPanel(new BorderLayout(20, 0));
        pnlSummaryAndBtns.setOpaque(false);
        pnlSummaryAndBtns.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlSummary.setOpaque(false);

        lblTongTien = new JLabel("Nguyên giá: 0 VNĐ");
        lblTongTien.setFont(ModernUITheme.FONT_REGULAR);
        lblTongTien.setForeground(ModernUITheme.TEXT_MUTED);

        lblGiamGia = new JLabel("Giảm: 0 VNĐ (0%)");
        lblGiamGia.setFont(ModernUITheme.FONT_REGULAR);
        lblGiamGia.setForeground(ModernUITheme.ACCENT);

        lblThanhTien = new JLabel("TỔNG THANH TOÁN: 0 VNĐ");
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblThanhTien.setForeground(ModernUITheme.SUCCESS);

        pnlSummary.add(lblTongTien);
        pnlSummary.add(lblGiamGia);
        pnlSummary.add(lblThanhTien);
        pnlSummaryAndBtns.add(pnlSummary, BorderLayout.WEST);

        // Action Buttons
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        btnLamMoi = ModernUITheme.createOutlineButton("Làm mới", ModernUITheme.TEXT_MUTED);
        btnHuyHoaDon = ModernUITheme.createButton("❌ Hủy hóa đơn", ModernUITheme.DANGER, Color.WHITE);
        btnInHoaDon = ModernUITheme.createButton("In phiếu khám", ModernUITheme.ACCENT, Color.WHITE);
        btnThanhToan = ModernUITheme.createButton("Xác nhận thanh toán", ModernUITheme.SUCCESS, Color.WHITE);

        pnlActions.add(btnLamMoi);
        pnlActions.add(btnHuyHoaDon);
        pnlActions.add(btnInHoaDon);
        pnlActions.add(btnThanhToan);
        pnlSummaryAndBtns.add(pnlActions, BorderLayout.EAST);

        pnlBillingCard.add(pnlSummaryAndBtns, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlBillingCard);
        add(splitPane, BorderLayout.CENTER);

        // Listeners for auto price calculation
        cbDichVu.addActionListener(e -> capNhatGia());
        cbUuDai.addActionListener(e -> capNhatGia());

        btnThanhToan.addActionListener(e -> xuLyThanhToan());
        btnInHoaDon.addActionListener(e -> inPhieuKham());
        btnHuyHoaDon.addActionListener(e -> xuLyHuyHoaDon());
        btnLamMoi.addActionListener(e -> xoaTrang());
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        btnTaiLai.addActionListener(e -> {
            txtTimKiem.setText("");
            taiDuLieu();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                napFormTuBang(table.getSelectedRow());
            }
        });

        napDuLieuCombobox();
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

    private void napDuLieuCombobox() {
        cbBenhNhan.removeAllItems();
        cbBacSi.removeAllItems();
        cbDichVu.removeAllItems();
        cbUuDai.removeAllItems();

        List<BenhNhan> dsBN = benhNhanDAO.getAllBenhNhan();
        for (BenhNhan bn : dsBN) {
            cbBenhNhan.addItem(bn.getMaBN() + " - " + bn.getHoTen());
        }

        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();
        for (NhanVien nv : dsNV) {
            String cd = nv.getChucDanh() != null ? nv.getChucDanh() : "";
            if (cd.contains("Nha sĩ") || cd.contains("Bác sĩ") || nv.getMaNV().startsWith("BS")) {
                cbBacSi.addItem(nv.getMaNV() + " - " + nv.getHoTen());
            }
        }
        if (cbBacSi.getItemCount() == 0) {
            for (NhanVien nv : dsNV) {
                cbBacSi.addItem(nv.getMaNV() + " - " + nv.getHoTen());
            }
        }

        List<DichVu> dsDV = dichVuDAO.getAllDichVu();
        for (DichVu dv : dsDV) {
            cbDichVu.addItem(dv.getMaDichVu() + " - " + dv.getTenDichVu() + " (" + df.format(dv.getGiaNiemYet()) + ")");
        }

        cbUuDai.addItem("Không áp dụng ưu đãi (0%)");
        List<UuDai> dsUD = uuDaiDAO.getUuDaiDangApDung();
        for (UuDai ud : dsUD) {
            cbUuDai.addItem(ud.getMaUuDai() + " - " + ud.getTenUuDai() + " (Giảm " + (int)ud.getPhanTramGiam() + "%)");
        }

        capNhatGia();
    }

    private void capNhatGia() {
        currentDonGia = 0;
        currentPhanTramGiam = 0;

        if (cbDichVu.getSelectedItem() != null) {
            String item = cbDichVu.getSelectedItem().toString();
            String maDV = item.split(" - ")[0].trim();
            DichVu dv = dichVuDAO.getDichVuByMa(maDV);
            if (dv != null) {
                currentDonGia = dv.getGiaNiemYet();
            } else {
                if (item.contains("Implant")) currentDonGia = 22000000;
                else if (item.contains("Niềng")) currentDonGia = 30000000;
                else if (item.contains("Cercon")) currentDonGia = 4500000;
                else if (item.contains("Nhổ")) currentDonGia = 2000000;
                else if (item.contains("Trám")) currentDonGia = 400000;
                else currentDonGia = 500000;
            }
        }

        if (cbUuDai.getSelectedItem() != null) {
            String udStr = cbUuDai.getSelectedItem().toString();
            if (udStr.contains("%")) {
                try {
                    int pIdx = udStr.lastIndexOf("%");
                    int start = pIdx - 1;
                    while (start >= 0 && Character.isDigit(udStr.charAt(start))) {
                        start--;
                    }
                    currentPhanTramGiam = Double.parseDouble(udStr.substring(start + 1, pIdx).trim());
                } catch (Exception ignored) {}
            }
        }

        double giam = currentDonGia * (currentPhanTramGiam / 100.0);
        double thanhTien = Math.max(0, currentDonGia - giam);

        lblTongTien.setText("Nguyên giá: " + df.format(currentDonGia));
        lblGiamGia.setText("Giảm: " + df.format(giam) + " (" + (int)currentPhanTramGiam + "%)");
        lblThanhTien.setText("TỔNG THANH TOÁN: " + df.format(thanhTien));
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        List<HoaDonKham> list = hoaDonDAO.getAllHoaDon();
        for (HoaDonKham hd : list) {
            String ngayStr = hd.getNgayLap() != null ? sdf.format(hd.getNgayLap()) : "";
            tableModel.addRow(new Object[]{
                "HD" + String.format("%03d", hd.getMaHD()),
                hd.getTenBN() != null ? hd.getTenBN() : hd.getMaBN(),
                hd.getTenNV() != null ? hd.getTenNV() : hd.getMaNV(),
                hd.getTenDichVu() != null ? hd.getTenDichVu() : hd.getMaDichVu(),
                ngayStr,
                df.format(hd.getThanhTien()),
                hd.getPhuongThucTT() != null ? hd.getPhuongThucTT() : "Tiền mặt",
                hd.getTrangThai() != null ? hd.getTrangThai() : "Đã thanh toán"
            });
        }
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        String tenBN = tableModel.getValueAt(row, 1).toString();
        for (int i = 0; i < cbBenhNhan.getItemCount(); i++) {
            if (cbBenhNhan.getItemAt(i).contains(tenBN)) {
                cbBenhNhan.setSelectedIndex(i);
                break;
            }
        }
        String tenBS = tableModel.getValueAt(row, 2).toString();
        for (int i = 0; i < cbBacSi.getItemCount(); i++) {
            if (cbBacSi.getItemAt(i).contains(tenBS)) {
                cbBacSi.setSelectedIndex(i);
                break;
            }
        }
        String tenDV = tableModel.getValueAt(row, 3).toString();
        for (int i = 0; i < cbDichVu.getItemCount(); i++) {
            if (cbDichVu.getItemAt(i).contains(tenDV)) {
                cbDichVu.setSelectedIndex(i);
                break;
            }
        }
        String pt = tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "Tiền mặt";
        cbPhuongThuc.setSelectedItem(pt);
        capNhatGia();
    }

    private void xuLyThanhToan() {
        if (cbBenhNhan.getSelectedItem() == null || cbBacSi.getSelectedItem() == null || cbDichVu.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Bệnh nhân, Bác sĩ điều trị và Dịch vụ khám!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemBN = cbBenhNhan.getSelectedItem().toString();
        String maBN = itemBN.split(" - ")[0].trim();
        String tenBN = itemBN.contains(" - ") ? itemBN.substring(itemBN.indexOf(" - ") + 3).trim() : maBN;

        String itemBS = cbBacSi.getSelectedItem().toString();
        String maBS = itemBS.split(" - ")[0].trim();
        String tenBS = itemBS.contains(" - ") ? itemBS.substring(itemBS.indexOf(" - ") + 3).trim() : maBS;

        String itemDV = cbDichVu.getSelectedItem().toString();
        String maDV = itemDV.split(" - ")[0].trim();
        String tenDV = itemDV.contains(" - ") ? itemDV.substring(itemDV.indexOf(" - ") + 3).trim() : maDV;
        if (tenDV.contains(" (")) {
            tenDV = tenDV.substring(0, tenDV.indexOf(" (")).trim();
        }

        capNhatGia();
        double giam = currentDonGia * (currentPhanTramGiam / 100.0);
        double thanhTien = Math.max(0, currentDonGia - giam);
        String pt = cbPhuongThuc.getSelectedItem() != null ? cbPhuongThuc.getSelectedItem().toString() : "Tiền mặt";

        HoaDonKham hd = new HoaDonKham(0, new Timestamp(System.currentTimeMillis()), maBN, maBS, maDV, currentDonGia, giam, thanhTien, pt, "Đã thanh toán");
        hd.setTenBN(tenBN);
        hd.setTenNV(tenBS);
        hd.setTenDichVu(tenDV);

        boolean thanhCong = hoaDonDAO.themHoaDon(hd);
        if (thanhCong) {
            String maHDStr = "HD" + String.format("%03d", hd.getMaHD());
            
            // Tích điểm thưởng cho bệnh nhân trong CSDL (cứ 10.000 VNĐ = 1 điểm)
            int diemCong = (int) (thanhTien / 10000);
            if (diemCong > 0) {
                benhNhanDAO.congDiemTichLuy(maBN, diemCong);
            }
            // Ghi nhật ký vào CSDL
            new dao.ThongKeDAO().ghiLog("THANH_TOAN", "Thanh toán thành công " + maHDStr + " cho BN " + tenBN + " (" + maBN + "), dịch vụ " + tenDV + ", số tiền " + df.format(thanhTien), maBS);

            JOptionPane.showMessageDialog(this, "Thanh toán thành công hóa đơn [" + maHDStr + "] với số tiền: " + df.format(thanhTien) + "\n(Đã cộng +" + diemCong + " điểm tích lũy thành viên cho bệnh nhân)", "Thanh toán thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
            table.setRowSelectionInterval(0, 0);
            int opt = JOptionPane.showConfirmDialog(this, "Bạn có muốn in phiếu thu hóa đơn ngay không?", "In hóa đơn", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                inPhieuKham();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyHoaDon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn trong danh sách để hủy!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maHDStr = tableModel.getValueAt(row, 0).toString().replaceAll("[^0-9]", "");
        int maHD = Integer.parseInt(maHDStr);
        String trangThai = tableModel.getValueAt(row, 7).toString();
        if ("Đã hủy".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được hủy trước đó!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int cf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy hóa đơn [HD" + String.format("%03d", maHD) + "] trong CSDL?", "Xác nhận hủy hóa đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (cf == JOptionPane.YES_OPTION) {
            boolean ok = hoaDonDAO.huyHoaDon(maHD);
            if (ok) {
                new dao.ThongKeDAO().ghiLog("HUY_HOA_DON", "Hủy hóa đơn HD" + String.format("%03d", maHD) + " bởi thu ngân", "TN001");
                JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn HD" + String.format("%03d", maHD) + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void inPhieuKham() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hóa đơn trong bảng để in phiếu!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maHD = tableModel.getValueAt(row, 0).toString();
        String bn = tableModel.getValueAt(row, 1).toString();
        String bs = tableModel.getValueAt(row, 2).toString();
        String dv = tableModel.getValueAt(row, 3).toString();
        String ngay = tableModel.getValueAt(row, 4).toString();
        String tt = tableModel.getValueAt(row, 5).toString();
        String pt = tableModel.getValueAt(row, 6).toString();

        String receipt =
            "====================================================\n" +
            "            PHÒNG KHÁM NHA KHOA ABC                 \n" +
            "        PHIẾU THU TIỀN ĐIỀU TRỊ NHA KHOA            \n" +
            "====================================================\n" +
            "Mã hóa đơn:       " + maHD + "\n" +
            "Ngày khám:        " + ngay + "\n" +
            "Họ tên bệnh nhân: " + bn + "\n" +
            "Bác sĩ điều trị:  " + bs + "\n" +
            "Dịch vụ nha khoa: " + dv + "\n" +
            "Hình thức TT:     " + pt + "\n" +
            "----------------------------------------------------\n" +
            "TỔNG THANH TOÁN:  " + tt + "\n" +
            "Trạng thái:       ĐÃ HOÀN TẤT THANH TOÁN\n" +
            "====================================================\n" +
            "      Chúc Quý khách luôn có nụ cười rạng rỡ!       \n" +
            "    Hotline cấp cứu nha khoa 24/7: 1900 6868        \n" +
            "====================================================";

        JTextArea ta = new JTextArea(receipt);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setEditable(false);
        ta.setBackground(new Color(250, 250, 250));
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Bản in Phiếu Thu Nha Khoa", JOptionPane.PLAIN_MESSAGE);
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieu();
            return;
        }
        keyword = keyword.trim();
        List<HoaDonKham> list = hoaDonDAO.timKiem(keyword);
        tableModel.setRowCount(0);
        for (HoaDonKham hd : list) {
            String ngayStr = hd.getNgayLap() != null ? sdf.format(hd.getNgayLap()) : "";
            tableModel.addRow(new Object[]{
                "HD" + String.format("%03d", hd.getMaHD()),
                hd.getTenBN() != null ? hd.getTenBN() : hd.getMaBN(),
                hd.getTenNV() != null ? hd.getTenNV() : hd.getMaNV(),
                hd.getTenDichVu() != null ? hd.getTenDichVu() : hd.getMaDichVu(),
                ngayStr,
                df.format(hd.getThanhTien()),
                hd.getPhuongThucTT() != null ? hd.getPhuongThucTT() : "Tiền mặt",
                hd.getTrangThai() != null ? hd.getTrangThai() : "Đã thanh toán"
            });
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào khớp với từ khóa: \"" + keyword + "\"", "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xoaTrang() {
        if (cbBenhNhan.getItemCount() > 0) cbBenhNhan.setSelectedIndex(0);
        if (cbBacSi.getItemCount() > 0) cbBacSi.setSelectedIndex(0);
        if (cbDichVu.getItemCount() > 0) cbDichVu.setSelectedIndex(0);
        if (cbUuDai.getItemCount() > 0) cbUuDai.setSelectedIndex(0);
        cbPhuongThuc.setSelectedIndex(0);
        table.clearSelection();
        capNhatGia();
    }
}
