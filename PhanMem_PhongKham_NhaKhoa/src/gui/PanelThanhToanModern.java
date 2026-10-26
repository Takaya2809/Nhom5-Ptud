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
    private JButton btnThanhToan, btnInHoaDon, btnLamMoi;

    private double currentDonGia = 0;
    private double currentPhanTramGiam = 0;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

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

        JButton btnReload = ModernUITheme.createOutlineButton("🔄 Tải lại dữ liệu", ModernUITheme.TEXT_MUTED);
        btnReload.addActionListener(e -> taiDuLieu());
        pnlHeader.add(btnReload, BorderLayout.EAST);

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

        JLabel lblFormTitle = new JLabel("💳 CHI TIẾT PHIẾU KHÁM & THANH TOÁN TIỀN");
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

        // Summary box
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        pnlSummary.setOpaque(false);

        lblTongTien = new JLabel("Nguyên giá: 0 VNĐ");
        lblTongTien.setFont(ModernUITheme.FONT_BOLD);
        lblTongTien.setForeground(ModernUITheme.TEXT_MUTED);

        lblGiamGia = new JLabel("Giảm: 0 VNĐ (0%)");
        lblGiamGia.setFont(ModernUITheme.FONT_BOLD);
        lblGiamGia.setForeground(ModernUITheme.DANGER);

        lblThanhTien = new JLabel("TỔNG THANH TOÁN: 0 VNĐ");
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblThanhTien.setForeground(ModernUITheme.SUCCESS);

        pnlSummary.add(lblTongTien);
        pnlSummary.add(lblGiamGia);
        pnlSummary.add(lblThanhTien);
        pnlSummaryAndBtns.add(pnlSummary, BorderLayout.WEST);

        // Buttons
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        btnThanhToan = ModernUITheme.createButton("✅ Xác nhận thanh toán", ModernUITheme.SUCCESS, Color.WHITE);
        btnInHoaDon = ModernUITheme.createButton("🖨️ In phiếu thu", ModernUITheme.PRIMARY, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

        pnlActions.add(btnLamMoi);
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
        btnLamMoi.addActionListener(e -> xoaTrang());

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

        cbBenhNhan.addItem("BN001 - Nguyễn Văn Tuấn");
        cbBenhNhan.addItem("BN002 - Trần Mai Phương");
        cbBenhNhan.addItem("BN003 - Phạm Quốc Dũng");

        cbBacSi.addItem("NV001 - BS. CKII Nguyễn Văn An");
        cbBacSi.addItem("NV002 - ThS. BS Lê Thị Thảo");
        cbBacSi.addItem("NV003 - BS. Trần Minh Trí");

        cbDichVu.addItem("DV001 - Cấy ghép Implant (22.000.000 VNĐ)");
        cbDichVu.addItem("DV002 - Niềng răng mắc cài (30.000.000 VNĐ)");
        cbDichVu.addItem("DV003 - Bọc răng toàn sứ Cercon (4.500.000 VNĐ)");
        cbDichVu.addItem("DV004 - Nhổ răng khôn Piezotome (2.000.000 VNĐ)");
        cbDichVu.addItem("DV005 - Trám răng Composite (400.000 VNĐ)");

        cbUuDai.addItem("Không áp dụng ưu đãi (0%)");
        cbUuDai.addItem("UD01 - Khai xuân rạng rỡ (Giảm 10%)");
        cbUuDai.addItem("UD02 - Khách hàng VIP tri ân (Giảm 15%)");

        capNhatGia();
    }

    private void capNhatGia() {
        currentDonGia = 0;
        currentPhanTramGiam = 0;

        if (cbDichVu.getSelectedItem() != null) {
            String item = cbDichVu.getSelectedItem().toString();
            if (item.contains("Implant")) currentDonGia = 22000000;
            else if (item.contains("Niềng")) currentDonGia = 30000000;
            else if (item.contains("Cercon")) currentDonGia = 4500000;
            else if (item.contains("Nhổ")) currentDonGia = 2000000;
            else if (item.contains("Trám")) currentDonGia = 400000;
            else currentDonGia = 500000;
        }

        if (cbUuDai.getSelectedItem() != null) {
            String ud = cbUuDai.getSelectedItem().toString();
            if (ud.contains("10%")) currentPhanTramGiam = 10;
            else if (ud.contains("15%")) currentPhanTramGiam = 15;
            else if (ud.contains("20%")) currentPhanTramGiam = 20;
        }

        double giam = currentDonGia * (currentPhanTramGiam / 100.0);
        double thanhTien = Math.max(0, currentDonGia - giam);

        lblTongTien.setText("Nguyên giá: " + df.format(currentDonGia));
        lblGiamGia.setText("Giảm: " + df.format(giam) + " (" + (int)currentPhanTramGiam + "%)");
        lblThanhTien.setText("TỔNG THANH TOÁN: " + df.format(thanhTien));
    }

    private void taiDuLieu() {
        // TODO: Cài đặt tải danh sách hóa đơn từ CSDL tại đây
        napDuLieuMau();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"HD001", "Nguyễn Văn Tuấn", "BS. CKII Nguyễn Văn An", "Cấy ghép Implant", "12/10/2026", df.format(19800000), "Chuyển khoản QR", "Đã thanh toán"});
        tableModel.addRow(new Object[]{"HD002", "Trần Mai Phương", "BS. Lê Thị Thảo", "Niềng răng mắc cài", "12/10/2026", df.format(27000000), "Quẹt thẻ POS", "Đã thanh toán"});
        tableModel.addRow(new Object[]{"HD003", "Phạm Quốc Dũng", "ThS. BS Trần Minh Trí", "Nhổ răng khôn Piezotome", "12/10/2026", df.format(2000000), "Tiền mặt", "Đã thanh toán"});
        tableModel.addRow(new Object[]{"HD004", "Vũ Bích Ngọc", "BS. Lê Thị Thảo", "Bọc răng sứ Cercon", "11/10/2026", df.format(4050000), "Chuyển khoản QR", "Đã thanh toán"});
    }

    private void xuLyThanhToan() {
        // TODO: Cài đặt chức năng thanh toán và lưu hóa đơn vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Thanh toán hóa đơn đã sẵn sàng để cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        String tt = tableModel.getValueAt(row, 5).toString();

        String receipt =
            "=========================================\n" +
            "       PHÒNG KHÁM NHA KHOA ABC           \n" +
            "     HÓA ĐƠN ĐIỀU TRỊ VÀ PHIẾU THU       \n" +
            "=========================================\n" +
            "Số phiếu: " + maHD + "\n" +
            "Bệnh nhân: " + bn + "\n" +
            "Bác sĩ điều trị: " + bs + "\n" +
            "Dịch vụ: " + dv + "\n" +
            "Tổng thanh toán: " + tt + "\n" +
            "Trạng thái: Đã hoàn tất thanh toán\n" +
            "=========================================\n" +
            "  Cảm ơn Quý khách! Chúc nụ cười rạng rỡ!\n" +
            "=========================================";

        JTextArea ta = new JTextArea(receipt);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Bản in Phiếu Thu Nha Khoa", JOptionPane.PLAIN_MESSAGE);
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
