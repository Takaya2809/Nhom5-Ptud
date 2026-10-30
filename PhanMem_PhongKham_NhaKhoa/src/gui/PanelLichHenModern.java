package gui;

import dao.LichHenDAO;
import entity.LichHen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

/** Reception appointment scheduling and status management. */
public class PanelLichHenModern extends JPanel {
    private final LichHenDAO dao = new LichHenDAO();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{
            "Mã lịch", "Ngày", "Giờ", "Mã BN", "Bệnh nhân", "Điện thoại", "Nha sĩ", "Lý do", "Trạng thái"
    }, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
    private final JTable table = new JTable(model);
    private final JComboBox<Choice> cbBenhNhan = new JComboBox<>();
    private final JComboBox<Choice> cbNhaSi = new JComboBox<>();
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Đã lên lịch", "Đã xác nhận", "Đang khám", "Đã hoàn thành", "Đã hủy"});
    private final JTextField txtNgay = ModernUITheme.createTextField(10);
    private final JTextField txtGio = ModernUITheme.createTextField(6);
    private final JTextField txtLyDo = ModernUITheme.createTextField(24);
    private final JTextField txtGhiChu = ModernUITheme.createTextField(24);
    private final JTextField txtTuKhoa = ModernUITheme.createTextField(18);
    private final JTextField txtLocNgay = ModernUITheme.createTextField(10);
    private final JTextField txtMaLich = ModernUITheme.createTextField(10);

    public PanelLichHenModern() {
        setLayout(new BorderLayout(12, 12)); setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(18, 20, 20, 20));
        JLabel title = new JLabel("LỊCH HẸN KHÁM"); title.setFont(ModernUITheme.FONT_HERO); title.setForeground(ModernUITheme.TEXT_MAIN);
        JLabel subtitle = new JLabel("Tạo lịch, tra cứu và cập nhật trạng thái các cuộc hẹn.");
        subtitle.setFont(ModernUITheme.FONT_SMALL); subtitle.setForeground(ModernUITheme.TEXT_MUTED);
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 3)); heading.setOpaque(false); heading.add(title); heading.add(subtitle);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0)); filters.setOpaque(false);
        txtLocNgay.setToolTipText("Ngày lọc yyyy-MM-dd; để trống để xem tất cả");
        txtTuKhoa.setToolTipText("Mã lịch, mã bệnh nhân, tên hoặc số điện thoại");
        JButton search = ModernUITheme.createButton("Tìm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton today = ModernUITheme.createOutlineButton("Hôm nay", ModernUITheme.TEXT_MUTED);
        JButton all = ModernUITheme.createOutlineButton("Tất cả", ModernUITheme.TEXT_MUTED);
        filters.add(new JLabel("Ngày:")); filters.add(txtLocNgay); filters.add(txtTuKhoa); filters.add(search); filters.add(today); filters.add(all);
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false); top.add(heading, BorderLayout.WEST); top.add(filters, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        ModernUITheme.formatModernTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);

        JPanel form = ModernUITheme.createCardPanel(); form.setLayout(new BorderLayout(10, 10));
        JLabel formTitle = new JLabel("TẠO LỊCH HẸN / CẬP NHẬT TRẠNG THÁI"); formTitle.setFont(ModernUITheme.FONT_TITLE); formTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        form.add(formTitle, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(2, 4, 10, 8)); fields.setOpaque(false);
        txtMaLich.setEditable(false); txtMaLich.setBackground(new Color(241,245,249));
        txtNgay.setToolTipText("yyyy-MM-dd"); txtGio.setToolTipText("HH:mm");
        fields.add(field("Mã lịch", txtMaLich)); fields.add(field("Bệnh nhân", cbBenhNhan));
        fields.add(field("Nha sĩ (không bắt buộc)", cbNhaSi)); fields.add(field("Ngày (yyyy-MM-dd)", txtNgay));
        fields.add(field("Giờ (HH:mm)", txtGio)); fields.add(field("Lý do khám", txtLyDo));
        fields.add(field("Ghi chú", txtGhiChu)); fields.add(field("Trạng thái", cbTrangThai));
        form.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); actions.setOpaque(false);
        JButton create = ModernUITheme.createButton("Tạo lịch hẹn", ModernUITheme.PRIMARY, Color.WHITE);
        JButton update = ModernUITheme.createButton("Cập nhật trạng thái", ModernUITheme.ACCENT, Color.WHITE);
        JButton cancel = ModernUITheme.createOutlineButton("Hủy lịch", ModernUITheme.DANGER);
        JButton clear = ModernUITheme.createOutlineButton("Làm mới", ModernUITheme.TEXT_MUTED);
        actions.add(clear); actions.add(cancel); actions.add(update); actions.add(create); form.add(actions, BorderLayout.SOUTH);
        add(form, BorderLayout.SOUTH);

        search.addActionListener(e -> search()); today.addActionListener(e -> loadToday()); all.addActionListener(e -> loadAll());
        create.addActionListener(e -> createAppointment()); update.addActionListener(e -> updateStatus(false));
        cancel.addActionListener(e -> updateStatus(true)); clear.addActionListener(e -> resetForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                txtMaLich.setText(String.valueOf(model.getValueAt(row, 0)));
                cbTrangThai.setSelectedItem(model.getValueAt(row, 8));
            }
        });
        loadOptions(); loadAll(); resetForm();
    }

    private JPanel field(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 4)); p.setOpaque(false);
        JLabel l = new JLabel(label); l.setFont(ModernUITheme.FONT_BOLD); l.setForeground(ModernUITheme.TEXT_REGULAR);
        p.add(l, BorderLayout.NORTH); p.add(input, BorderLayout.CENTER); return p;
    }

    private void loadOptions() {
        cbBenhNhan.removeAllItems();
        for (String[] row : dao.getBenhNhanOptions()) cbBenhNhan.addItem(new Choice(row[0], row[1] + " - " + row[2]));
        if (dao.getLastError() != null) { showError(dao.getLastError()); return; }
        cbNhaSi.removeAllItems(); cbNhaSi.addItem(new Choice("", "Chưa phân công"));
        for (String[] row : dao.getNhaSiOptions()) cbNhaSi.addItem(new Choice(row[0], row[1]));
        if (dao.getLastError() != null) showError(dao.getLastError());
    }

    private void loadAll() { showRows(dao.getAllLichHen()); }

    private void loadToday() {
        txtLocNgay.setText(LocalDate.now().toString());
        showRows(dao.getLichHenHomNay());
    }

    private void search() {
        Date date = null;
        String raw = txtLocNgay.getText().trim();
        if (!raw.isEmpty()) {
            try { date = Date.valueOf(raw); }
            catch (IllegalArgumentException e) { showError("Ngày lọc cần đúng định dạng yyyy-MM-dd."); return; }
        }
        showRows(dao.timKiem(txtTuKhoa.getText(), date));
    }

    public int timKiemTuKhoa(String keyword) {
        txtTuKhoa.setText(keyword == null ? "" : keyword.trim());
        txtLocNgay.setText("");
        List<LichHen> rows = dao.timKiem(txtTuKhoa.getText(), null);
        model.setRowCount(0);
        for (LichHen lh : rows) model.addRow(new Object[]{lh.getMaLichHen(), lh.getNgayHen(), lh.getGioHen(), lh.getMaBN(),
                lh.getTenBenhNhan(), lh.getSdtBenhNhan(), lh.getTenNhaSi() == null ? "Chưa phân công" : lh.getTenNhaSi(),
                lh.getLyDoKham(), lh.getTrangThai()});
        if (dao.getLastError() != null) { showError(dao.getLastError()); return -1; }
        return rows.size();
    }

    private void showRows(List<LichHen> rows) {
        model.setRowCount(0);
        for (LichHen lh : rows) model.addRow(new Object[]{lh.getMaLichHen(), lh.getNgayHen(), lh.getGioHen(), lh.getMaBN(),
                lh.getTenBenhNhan(), lh.getSdtBenhNhan(), lh.getTenNhaSi() == null ? "Chưa phân công" : lh.getTenNhaSi(),
                lh.getLyDoKham(), lh.getTrangThai()});
        if (dao.getLastError() != null) showError(dao.getLastError());
    }

    private void createAppointment() {
        if (cbBenhNhan.getSelectedItem() == null) { showError("Cần có ít nhất một bệnh nhân trước khi đặt lịch."); return; }
        try {
            Date date = Date.valueOf(txtNgay.getText().trim());
            String timeText = txtGio.getText().trim();
            Time time = Time.valueOf(timeText.length() == 5 ? timeText + ":00" : timeText);
            if (date.before(Date.valueOf(LocalDate.now()))) { showError("Không thể tạo lịch trong quá khứ."); return; }
            if (txtLyDo.getText().trim().isEmpty()) { showError("Vui lòng nhập lý do khám."); return; }
            String id = dao.sinhMaLichHenMoi();
            if (id == null) { showError(dao.getLastError()); return; }
            Choice patient = (Choice) cbBenhNhan.getSelectedItem();
            Choice dentist = (Choice) cbNhaSi.getSelectedItem();
            LichHen lh = new LichHen(id, patient.id, dentist == null ? null : dentist.id, date, time,
                    txtLyDo.getText().trim(), txtGhiChu.getText().trim(), "Đã lên lịch");
            if (!dao.themLichHen(lh)) { showError(dao.getLastError()); return; }
            loadAll(); resetForm(); JOptionPane.showMessageDialog(this, "Đã tạo lịch hẹn " + id + ".");
        } catch (IllegalArgumentException e) { showError("Ngày cần theo yyyy-MM-dd, giờ theo HH:mm."); }
    }

    private void updateStatus(boolean cancel) {
        String id = txtMaLich.getText().trim();
        if (id.isEmpty()) { showError("Chọn lịch hẹn cần cập nhật trong bảng."); return; }
        String status = cancel ? "Đã hủy" : String.valueOf(cbTrangThai.getSelectedItem());
        if (!dao.capNhatTrangThai(id, status)) { showError(dao.getLastError()); return; }
        loadAll(); resetForm();
    }

    private void resetForm() {
        String id = dao.sinhMaLichHenMoi(); txtMaLich.setText(id == null ? "" : id);
        txtNgay.setText(LocalDate.now().toString()); txtGio.setText("09:00"); txtLyDo.setText(""); txtGhiChu.setText("");
        cbTrangThai.setSelectedItem("Đã lên lịch"); table.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message == null ? "Có lỗi khi xử lý lịch hẹn." : message,
                "Lịch hẹn", JOptionPane.WARNING_MESSAGE);
    }

    private static class Choice {
        final String id; final String text;
        Choice(String id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text; }
    }
}
