package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTheoDoiKhuKhuanModern extends JPanel {
    private JTable tblDungCu;
    private DefaultTableModel modelTable;
    private JTextField txtTimKiem;
    private JButton btnCapNhatTrangThai, btnTimKiem, btnTatCa;

    public PanelTheoDoiKhuKhuanModern() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==================== 1. PHẦN HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("THEO DÕI TRẠNG THÁI KHỬ KHUẨN DỤNG CỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 37, 41));

        JLabel lblSubTitle = new JLabel("Cập nhật và kiểm tra tiến trình khử khuẩn, xử lý dụng cụ y tế nhằm kiểm soát nhiễm khuẩn");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubTitle.setForeground(new Color(108, 117, 125));

        pnlTitleGroup.add(lblTitle);
        pnlTitleGroup.add(lblSubTitle);
        pnlHeader.add(pnlTitleGroup, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== 2. PHẦN CENTER (BẢNG & TÌM KIẾM) ====================
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        pnlCenter.setOpaque(false);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        pnlSearch.add(new JLabel("Tìm kiếm dụng cụ:"));
        txtTimKiem = new JTextField(20);
        pnlSearch.add(txtTimKiem);
        
        btnTimKiem = new JButton("Tìm");
        styleButton(btnTimKiem, new Color(0, 120, 215), new Color(0, 90, 160));
        pnlSearch.add(btnTimKiem);

        btnTatCa = new JButton("Tất cả");
        styleButton(btnTatCa, new Color(108, 117, 125), new Color(80, 88, 95));
        pnlSearch.add(btnTatCa);

        pnlCenter.add(pnlSearch, BorderLayout.NORTH);

        // Các cột hiển thị danh sách dụng cụ và trạng thái tiệt trùng
        String[] columnNames = {"Mã Dụng Cụ", "Tên Dụng Cụ", "Phân Loại", "Trạng Thái Khử Khuẩn", "Thời Gian Xử Lý"};
        modelTable = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDungCu = new JTable(modelTable);
        tblDungCu.setRowHeight(25);
        tblDungCu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Thêm dữ liệu mẫu kiểm tra giao diện
        loadMockData();

        JScrollPane scrollPane = new JScrollPane(tblDungCu);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // ==================== 3. PHẦN SOUTH (THAO TÁC CẬP NHẬT) ====================
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)), 
            " KIỂM SOÁT NHIỄM KHUẨN & TIỆT TRÙNG ", 
            0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(40, 167, 69)
        ));

        btnCapNhatTrangThai = new JButton("Cập nhật trạng thái khử khuẩn");
        styleButton(btnCapNhatTrangThai, new Color(40, 167, 69), new Color(30, 126, 52));
        pnlSouth.add(btnCapNhatTrangThai);

        add(pnlSouth, BorderLayout.SOUTH);

        // ==================== 4. SỰ KIỆN TƯƠNG TÁC ====================
        btnCapNhatTrangThai.addActionListener(e -> xuLyCapNhatTrangThai());
        
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(modelTable);
        tblDungCu.setRowSorter(sorter);

        btnTimKiem.addActionListener(e -> {
            String kw = txtTimKiem.getText().trim();
            if (kw.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(kw)));
            }
        });
        txtTimKiem.addActionListener(e -> btnTimKiem.doClick());
        btnTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            sorter.setRowFilter(null);
        });
    }

    private void styleButton(JButton btn, Color normalColor, Color pressedColor) {
        btn.setBackground(normalColor);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { btn.setBackground(pressedColor); }
            @Override
            public void mouseReleased(MouseEvent e) { btn.setBackground(normalColor); }
        });
    }

    private void loadMockData() {
        modelTable.addRow(new Object[]{"DC001", "Bộ kìm nhổ răng Inox", "Dụng cụ phẫu thuật", "Đã tiệt trùng (Sẵn sàng)", "01/11/2026 08:30"});
        modelTable.addRow(new Object[]{"DC002", "Gương nha khoa & Thám trâm", "Dụng cụ khám", "Đang khử khuẩn (Autoclave)", "01/11/2026 11:00"});
        modelTable.addRow(new Object[]{"DC003", "Tay khoan turbine tốc độ cao", "Thiết bị cầm tay", "Chưa khử khuẩn", "Chưa xử lý"});
    }

    private void xuLyCapNhatTrangThai() {
        int r = tblDungCu.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dụng cụ cần cập nhật trạng thái khử khuẩn!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenDC = tblDungCu.getValueAt(r, 1).toString();
        String[] options = {"Chưa khử khuẩn", "Đang khử khuẩn (Autoclave)", "Đã tiệt trùng (Sẵn sàng)"};
        
        String selectedStatus = (String) JOptionPane.showInputDialog(this,
                "Chọn trạng thái khử khuẩn mới cho dụng cụ [" + tenDC + "]:",
                "Cập nhật trạng thái (UC003)",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        if (selectedStatus != null) {
            tblDungCu.setValueAt(selectedStatus, r, 3);
            tblDungCu.setValueAt("01/11/2026 13:50", r, 4);
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái khử khuẩn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}