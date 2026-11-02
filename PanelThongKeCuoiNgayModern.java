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

public class PanelThongKeCuoiNgayModern extends JPanel {
    private JTable tblThongKe;
    private DefaultTableModel modelTable;
    private JButton btnChinhSuaSoLieu, btnXuatBaoCao, btnLamMoi;
    private VatTuDAO vatTuDAO;

    public PanelThongKeCuoiNgayModern() {
        vatTuDAO = new VatTuDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==================== 1. PHẦN HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("THỐNG KÊ CUỐI NGÀY - VẬT TƯ TIÊU HAO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 37, 41));

        JLabel lblSubTitle = new JLabel("Tổng hợp số liệu bệnh nhân và vật tư tiêu hao thực tế từ kho theo ca trực");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubTitle.setForeground(new Color(108, 117, 125));

        pnlTitleGroup.add(lblTitle);
        pnlTitleGroup.add(lblSubTitle);
        pnlHeader.add(pnlTitleGroup, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== 2. PHẦN CENTER (BẢNG THỐNG KÊ) ====================
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        pnlCenter.setOpaque(false);

        String[] columnNames = {
            "Ca Trực", 
            "Thời Gian", 
            "SL Bệnh Nhân", 
            "Tổng Hợp Vật Tư Tiêu Hao (Kho)", 
            "Trạng Thái Báo Cáo"
        };
        
        modelTable = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblThongKe = new JTable(modelTable);
        tblThongKe.setRowHeight(28);
        tblThongKe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        loadDataFromDatabase();

        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // ==================== 3. PHẦN SOUTH (CÁC NÚT THAO TÁC) ====================
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)), 
            " TỔNG KẾT & CHỈNH SỬA SỐ LIỆU ", 
            0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 120, 215)
        ));

        btnLamMoi = new JButton("Làm mới dữ liệu");
        styleButton(btnLamMoi, new Color(108, 117, 125), new Color(80, 88, 95));
        pnlSouth.add(btnLamMoi);

        btnChinhSuaSoLieu = new JButton("Chỉnh sửa số liệu");
        styleButton(btnChinhSuaSoLieu, new Color(255, 140, 0), new Color(200, 110, 0));
        pnlSouth.add(btnChinhSuaSoLieu);

        btnXuatBaoCao = new JButton("Gửi Báo Cáo Cuối Ngày");
        styleButton(btnXuatBaoCao, new Color(0, 120, 215), new Color(0, 90, 160));
        pnlSouth.add(btnXuatBaoCao);

        add(pnlSouth, BorderLayout.SOUTH);

        // ==================== 4. XỬ LÝ SỰ KIỆN ====================
        btnChinhSuaSoLieu.addActionListener(e -> xuLyChinhSuaNhieuVatTu());
        
        btnXuatBaoCao.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã gửi báo cáo tổng kết cuối ngày thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnLamMoi.addActionListener(e -> {
            loadDataFromDatabase();
            JOptionPane.showMessageDialog(this, "Đã đồng bộ lại dữ liệu vật tư mới nhất từ kho!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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

    private void loadDataFromDatabase() {
        modelTable.setRowCount(0);
        
        List<VatTu> listVT = vatTuDAO.getAllVatTu();
        StringBuilder sbVatTu = new StringBuilder();
        if (listVT != null) {
            int count = 0;
            for (VatTu vt : listVT) {
                if (vt.getLoaiVatTu() != null && vt.getLoaiVatTu().equalsIgnoreCase("Tiêu hao")) {
                    sbVatTu.append(vt.getTenVatTu()).append(" (SL: 1), ");
                    count++;
                    if (count >= 2) break;
                }
            }
        }
        String summaryVatTu = sbVatTu.length() > 0 ? sbVatTu.substring(0, sbVatTu.length() - 2) : "Không phát sinh";

        // Thêm Ca Sáng (08:00 - 12:00), Ca Chiều, và Ca Tối (18:00 - 21:00)
        modelTable.addRow(new Object[]{"Ca Sáng", "08:00 - 12:00", "12", summaryVatTu, "Đã chỉnh sửa"});
        modelTable.addRow(new Object[]{"Ca Chiều", "13:30 - 17:30", "15", summaryVatTu, "Chưa gửi"});
        modelTable.addRow(new Object[]{"Ca Tối", "18:00 - 21:00", "8", summaryVatTu, "Chưa gửi"});
    }

    private void xuLyChinhSuaNhieuVatTu() {
        int r = tblThongKe.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng ca trực cần chỉnh sửa số liệu!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String caTruc = tblThongKe.getValueAt(r, 0).toString();
        String currentBN = tblThongKe.getValueAt(r, 2).toString();
        String currentTrangThai = tblThongKe.getValueAt(r, 4).toString();

        // 1. Nhập số lượng bệnh nhân
        String newBN = JOptionPane.showInputDialog(this, "Nhập số lượng bệnh nhân khám mới cho [" + caTruc + "]:", currentBN);
        if (newBN == null) return;

        // 2. Lấy danh sách vật tư từ Database
        List<VatTu> listVT = vatTuDAO.getAllVatTu();
        if (listVT == null || listVT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy danh sách vật tư trong kho!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo bảng chọn nhiều vật tư
        String[] columns = {"Chọn", "Tên Vật Tư Tiêu Hao", "Số Lượng Sử Dụng"};
        DefaultTableModel modelChonVatTu = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 2;
            }
        };

        for (VatTu vt : listVT) {
            if (vt.getLoaiVatTu() != null && vt.getLoaiVatTu().equalsIgnoreCase("Tiêu hao")) {
                modelChonVatTu.addRow(new Object[]{false, vt.getTenVatTu(), "1"});
            }
        }

        JTable tblChon = new JTable(modelChonVatTu);
        tblChon.setRowHeight(26);
        tblChon.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(tblChon);
        scrollPane.setPreferredSize(new Dimension(500, 250));

        JPanel panelDialog = new JPanel(new BorderLayout(5, 5));
        panelDialog.add(new JLabel("Tích chọn vật tư và sửa số lượng tiêu hao tương ứng trong [" + caTruc + "]:"), BorderLayout.NORTH);
        panelDialog.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panelDialog, 
                "Chọn Nhiều Vật Tư Tiêu Hao", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        StringBuilder sbSummary = new StringBuilder();
        int countSelected = 0;

        for (int i = 0; i < modelChonVatTu.getRowCount(); i++) {
            Boolean isChecked = (Boolean) modelChonVatTu.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                String tenVT = modelChonVatTu.getValueAt(i, 1).toString();
                String slVT = modelChonVatTu.getValueAt(i, 2).toString().trim();
                
                if (slVT.isEmpty()) {
                    slVT = "1";
                }
                
                sbSummary.append(tenVT).append(" (SL: ").append(slVT).append("), ");
                countSelected++;
            }
        }

        String finalVatTuSummary = countSelected > 0 ? sbSummary.substring(0, sbSummary.length() - 2) : "Không phát sinh";

        // 3. Chọn trạng thái báo cáo
        String[] reportOptions = {"Chưa gửi", "Đang soạn thảo", "Đã chỉnh sửa", "Đã gửi quản lý"};
        String newTrangThai = (String) JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái báo cáo cho [" + caTruc + "]:",
                "Chỉnh sửa Trạng Thái Báo Cáo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                reportOptions,
                currentTrangThai
        );
        if (newTrangThai == null) return;

        try {
            Integer.parseInt(newBN.trim());

            tblThongKe.setValueAt(newBN.trim(), r, 2);
            tblThongKe.setValueAt(finalVatTuSummary, r, 3);
            tblThongKe.setValueAt(newTrangThai, r, 4);

            JOptionPane.showMessageDialog(this, "Cập nhật danh sách vật tư và số liệu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng bệnh nhân phải là số nguyên hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }
}