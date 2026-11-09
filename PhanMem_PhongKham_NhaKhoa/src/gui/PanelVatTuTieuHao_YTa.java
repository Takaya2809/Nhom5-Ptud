package gui;

import dao.VatTuDAO;
import entity.VatTu;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelVatTuTieuHao_YTa extends JPanel {
    private JTable tblVatTu;
    private DefaultTableModel modelTable;
    private JTextField txtTimKiem;
    private JButton btnCapNhatKho, btnTimKiem, btnTatCa;
    private VatTuDAO vatTuDAO;

    public PanelVatTuTieuHao_YTa() {
        vatTuDAO = new VatTuDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==================== 1. PHẦN HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ VẬT TƯ TIÊU HAO & CẬP NHẬT KHO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 37, 41));

        JLabel lblSubTitle = new JLabel("Tra cứu tồn kho và điều chỉnh số lượng vật tư nhanh chóng qua thao tác chọn dòng");
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

        pnlSearch.add(new JLabel("Tìm kiếm vật tư:"));
        txtTimKiem = new JTextField(20);
        pnlSearch.add(txtTimKiem);
        
        btnTimKiem = new JButton("Tìm");
        styleButton(btnTimKiem, new Color(0, 120, 215), new Color(0, 90, 160));
        pnlSearch.add(btnTimKiem);

        btnTatCa = new JButton("Tất cả");
        styleButton(btnTatCa, new Color(108, 117, 125), new Color(80, 88, 95));
        pnlSearch.add(btnTatCa);

        pnlCenter.add(pnlSearch, BorderLayout.NORTH);

        String[] columnNames = {"Mã Vật Tư", "Tên Vật Tư", "Đơn Vị Tính", "Số Lượng Tồn Kho"};
        modelTable = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblVatTu = new JTable(modelTable);
        tblVatTu.setRowHeight(25);
        tblVatTu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        loadDataToTable();

        JScrollPane scrollPane = new JScrollPane(tblVatTu);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // ==================== 3. PHẦN SOUTH (NÚT CẬP NHẬT KHO) ====================
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)), 
            " THAO TÁC NGHIỆP VỤ KHO ", 
            0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 120, 215)
        ));

        btnCapNhatKho = new JButton("Cập nhật kho");
        styleButton(btnCapNhatKho, new Color(40, 167, 69), new Color(30, 126, 52));
        pnlSouth.add(btnCapNhatKho);

        add(pnlSouth, BorderLayout.SOUTH);

        // ==================== 4. SỰ KIỆN TƯƠNG TÁC ====================
        btnCapNhatKho.addActionListener(e -> xuLyCapNhatKho());
        
        btnTatCa.addActionListener(e -> { txtTimKiem.setText(""); loadDataToTable(); });
        
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                loadDataToTable();
                return;
            }
            modelTable.setRowCount(0);
            List<VatTu> list = vatTuDAO.getAllVatTu();
            for (VatTu vt : list) {
                if (vt.getMaVatTu().toLowerCase().contains(keyword) || vt.getTenVatTu().toLowerCase().contains(keyword)) {
                    modelTable.addRow(new Object[]{vt.getMaVatTu(), vt.getTenVatTu(), vt.getDonViTinh(), vt.getSoLuongTon()});
                }
            }
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
            public void mousePressed(MouseEvent e) {
                btn.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(normalColor);
            }
        });
    }

    private void loadDataToTable() {
        modelTable.setRowCount(0);
        List<VatTu> list = vatTuDAO.getAllVatTu();
        for (VatTu vt : list) {
            modelTable.addRow(new Object[]{
                vt.getMaVatTu(),
                vt.getTenVatTu(),
                vt.getDonViTinh(),
                vt.getSoLuongTon()
            });
        }
    }

    // Hàm xử lý Cập nhật kho bằng JOptionPane y hệt code mẫu của bạn bè bạn
    private void xuLyCapNhatKho() {
        int r = tblVatTu.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vật tư cần cập nhật kho!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ma = tblVatTu.getValueAt(r, 0).toString();
        String ten = tblVatTu.getValueAt(r, 1).toString();
        String curTon = tblVatTu.getValueAt(r, 3).toString();

        String input = JOptionPane.showInputDialog(this,
            "Nhập số lượng thay đổi cho vật tư [" + ten + "] (Hiện tồn: " + curTon + "):\n(Số dương: Nhập thêm, Số âm: Xuất/Tiêu hao)",
            "Cập nhật tồn kho vật tư", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int delta = Integer.parseInt(input.trim());
                if (vatTuDAO.nhapXuatKho(ma, delta)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng tồn kho của [" + ma + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật tồn kho thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}