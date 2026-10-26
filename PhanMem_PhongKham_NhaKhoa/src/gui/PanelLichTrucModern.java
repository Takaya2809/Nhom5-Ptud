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
    private JButton btnPhanCong, btnHuyPhanCong, btnLamMoi, btnLoc;

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
        btnLoc = ModernUITheme.createButton("🔍 Tra cứu", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);

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

        JLabel lblFormTitle = new JLabel("📅 PHÂN CÔNG BÁC SĨ VÀO CA TRỰC PHÒNG KHÁM");
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

        btnPhanCong = ModernUITheme.createButton("➕ Xác nhận phân công", ModernUITheme.PRIMARY, Color.WHITE);
        btnHuyPhanCong = ModernUITheme.createButton("❌ Hủy phân công", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Làm mới", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnHuyPhanCong);
        pnlButtons.add(btnPhanCong);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Events
        btnPhanCong.addActionListener(e -> xuLyPhanCong());
        btnHuyPhanCong.addActionListener(e -> xuLyHuy());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnLoc.addActionListener(e -> xuLyLoc());
        btnTaiLai.addActionListener(e -> taiDuLieu());

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

        // Danh sách mẫu hiển thị giao diện
        cbNhanVien.addItem("NV001 - BS. CKII Nguyễn Văn An (Bác sĩ)");
        cbNhanVien.addItem("NV002 - ThS. BS Lê Thị Thảo (Bác sĩ)");
        cbNhanVien.addItem("NV003 - BS. Trần Minh Trí (Bác sĩ)");
        cbNhanVien.addItem("NV004 - Y tá Đỗ Thùy Trang (Điều dưỡng)");

        cbCaTruc.addItem("CA01 - Ca Sáng (08:00 - 12:00)");
        cbCaTruc.addItem("CA02 - Ca Chiều (13:30 - 17:30)");
        cbCaTruc.addItem("CA03 - Ca Tối (18:00 - 21:00)");
    }

    private void taiDuLieu() {
        // TODO: Cài đặt tải danh sách phân công ca trực từ CSDL tại đây
        napDuLieuMau();
    }

    private void napDuLieuMau() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[] { "PC001", "BS. CKII Nguyễn Văn An", "Ca Sáng (08:00 - 12:00)", "Bác sĩ chính",
                "Đã đến", "Phòng Cấy ghép Implant Ghế 01" });
        tableModel.addRow(new Object[] { "PC002", "ThS. BS Lê Thị Thảo", "Ca Sáng (08:00 - 12:00)", "Bác sĩ chính",
                "Đã đến", "Phòng Chỉnh nha Niềng răng Ghế 02" });
        tableModel.addRow(new Object[] { "PC003", "BS. Trần Minh Trí", "Ca Chiều (13:30 - 17:30)", "Bác sĩ chính",
                "Chưa đến", "Phòng Tiểu phẫu Nhổ răng Ghế 03" });
        tableModel.addRow(new Object[] { "PC004", "Y tá Đỗ Thùy Trang", "Ca Sáng (08:00 - 12:00)", "Phụ tá ghế",
                "Đã đến", "Phụ tá ghế nha khoa & Khử trùng" });
        tableModel.addRow(new Object[] { "PC005", "BS. CKII Nguyễn Văn An", "Ca Tối (18:00 - 21:00)", "Bác sĩ chính",
                "Chưa đến", "Khám ngoài giờ chuyên gia" });
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
        // TODO: Cài đặt chức năng phân công ca trực vào CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Phân công ca trực đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyHuy() {
        // TODO: Cài đặt chức năng hủy phân công ca trực trong CSDL tại đây
        JOptionPane.showMessageDialog(this, "Chức năng Hủy phân công đã sẵn sàng để cài đặt!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyLoc() {
        // TODO: Cài đặt chức năng lọc danh sách ca trực tại đây
    }
}
