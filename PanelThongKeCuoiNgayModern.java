package gui;

import dao.VatTuDAO;
import entity.VatTu;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class PanelThongKeCuoiNgayModern extends JPanel {
    private JTable tblThongKe;
    private DefaultTableModel modelTable;
    private JButton btnChinhSuaSoLieu, btnXuatBaoCao, btnLamMoi, btnApDung;
    private DatePickerField dpNgayThongKe; // Thêm bộ lọc ngày
    private VatTuDAO vatTuDAO;
    private JLabel lblTongBN, lblTongVatTu;

    public PanelThongKeCuoiNgayModern() {
        vatTuDAO = new VatTuDAO();
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // ==================== 1. PHẦN HEADER & BỘ LỌC ====================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JPanel pnlTitleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlTitleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("THỐNG KÊ CUỐI NGÀY - VẬT TƯ TIÊU HAO & CA TRỰC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 37, 41));

        JLabel lblSubTitle = new JLabel("Tổng hợp số liệu bệnh nhân và vật tư tiêu hao thực tế từ kho theo ca trực trong ngày");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubTitle.setForeground(new Color(108, 117, 125));

        pnlTitleGroup.add(lblTitle);
        pnlTitleGroup.add(lblSubTitle);
        pnlTop.add(pnlTitleGroup, BorderLayout.NORTH);

        // Card bộ lọc ngày & tác vụ nhanh
        JPanel cardFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        cardFilter.setBackground(Color.WHITE);
        cardFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        dpNgayThongKe = new DatePickerField(LocalDate.now());
        btnApDung = new JButton("Xem số liệu");
        styleButton(btnApDung, new Color(0, 120, 215), new Color(0, 90, 160));
        
        btnApDung.addActionListener(e -> {
            loadDataFromDatabase();
            JOptionPane.showMessageDialog(this, "Đã cập nhật số liệu cho ngày: " + dpNgayThongKe.getDate(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        cardFilter.add(new JLabel("Chọn ngày thống kê:"));
        cardFilter.add(dpNgayThongKe);
        cardFilter.add(btnApDung);

        pnlTop.add(cardFilter, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // ==================== 2. PHẦN CENTER (BẢNG THỐNG KÊ) ====================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 8));
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
        tblThongKe.setRowHeight(32);
        tblThongKe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblThongKe.getTableHeader().setBackground(new Color(240, 243, 246));
        
        // Custom Renderer để tô màu trạng thái báo cáo
        tblThongKe.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String status = value != null ? value.toString() : "";
                    if (status.contains("Đã")) {
                        lbl.setForeground(new Color(16, 185, 129)); // Xanh lá
                    } else {
                        lbl.setForeground(new Color(245, 158, 11));  // Cam vàng
                    }
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return lbl;
            }
        });

        loadDataFromDatabase();

        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // ==================== 3. PHẦN SOUTH (CÁC NÚT THAO TÁC) ====================
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        btnLamMoi = new JButton("Làm mới dữ liệu");
        styleButton(btnLamMoi, new Color(108, 117, 125), new Color(80, 88, 95));
        
        btnChinhSuaSoLieu = new JButton("Chỉnh sửa số liệu ca trực");
        styleButton(btnChinhSuaSoLieu, new Color(255, 140, 0), new Color(200, 110, 0));
        
        btnXuatBaoCao = new JButton("In & Xuất Báo Cáo");
        styleButton(btnXuatBaoCao, new Color(0, 120, 215), new Color(0, 90, 160));

        pnlSouth.add(btnLamMoi);
        pnlSouth.add(btnChinhSuaSoLieu);
        pnlSouth.add(btnXuatBaoCao);

        add(pnlSouth, BorderLayout.SOUTH);

        // ==================== 4. XỬ LÝ SỰ KIỆN ====================
        btnChinhSuaSoLieu.addActionListener(e -> xuLyChinhSuaNhieuVatTu());
        
        btnXuatBaoCao.addActionListener(e -> xuLyInVaXuatBaoCao());
        
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
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

        // Thêm dữ liệu mẫu các ca trực chuẩn
        modelTable.addRow(new Object[]{"Ca Sáng", "08:00 - 12:00", 12, summaryVatTu, "Đã chỉnh sửa"});
        modelTable.addRow(new Object[]{"Ca Chiều", "13:30 - 17:30", 15, summaryVatTu, "Chưa chỉnh sửa"});
        modelTable.addRow(new Object[]{"Ca Tối", "18:00 - 21:00", 8, summaryVatTu, "Chưa chỉnh sửa"});
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

        String newBN = JOptionPane.showInputDialog(this, "Nhập số lượng bệnh nhân khám mới cho [" + caTruc + "]:", currentBN);
        if (newBN == null) return;

        List<VatTu> listVT = vatTuDAO.getAllVatTu();
        if (listVT == null || listVT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy danh sách vật tư trong kho!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
                "Chỉnh Sửa Vật Tư Tiêu Hao Theo Ca", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        StringBuilder sbSummary = new StringBuilder();
        int countSelected = 0;

        for (int i = 0; i < modelChonVatTu.getRowCount(); i++) {
            Boolean isChecked = (Boolean) modelChonVatTu.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                String tenVT = modelChonVatTu.getValueAt(i, 1).toString();
                String slVT = modelChonVatTu.getValueAt(i, 2).toString().trim();
                if (slVT.isEmpty()) slVT = "1";
                
                sbSummary.append(tenVT).append(" (SL: ").append(slVT).append("), ");
                countSelected++;
            }
        }

        String finalVatTuSummary = countSelected > 0 ? sbSummary.substring(0, sbSummary.length() - 2) : "Không phát sinh";

        String[] reportOptions = {"Chưa chỉnh sửa", "Đang soạn thảo", "Đã chỉnh sửa", "Đã in báo cáo"};
        String newTrangThai = (String) JOptionPane.showInputDialog(
                this, "Chọn trạng thái báo cáo cho [" + caTruc + "]:",
                "Chỉnh sửa Trạng Thái", JOptionPane.QUESTION_MESSAGE, null, reportOptions, currentTrangThai
        );
        if (newTrangThai == null) return;

        try {
            int parsedBN = Integer.parseInt(newBN.trim());
            tblThongKe.setValueAt(parsedBN, r, 2);
            tblThongKe.setValueAt(finalVatTuSummary, r, 3);
            tblThongKe.setValueAt(newTrangThai, r, 4);

            JOptionPane.showMessageDialog(this, "Cập nhật dữ liệu ca trực thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng bệnh nhân phải là số nguyên hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyInVaXuatBaoCao() {
        String[] options = {"In Báo Cáo", "Xuất File CSV / Excel", "Hủy"};
        int choice = JOptionPane.showOptionDialog(this, 
                "Bạn muốn thực hiện thao tác nào với báo cáo cuối ngày?", 
                "Tùy Chọn Báo Cáo", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, options, options[0]);

        if (choice == 0) {
            // In trực tiếp
            try {
                boolean complete = tblThongKe.print(JTable.PrintMode.FIT_WIDTH,
                        new java.text.MessageFormat("Báo Cáo Tổng Kết Cuối Ngày - Nha Khoa ABC (Ngày: " + dpNgayThongKe.getDate() + ")"),
                        new java.text.MessageFormat("Trang - {0}"));
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Đã gửi lệnh in báo cáo thành công!", "In ấn", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể in báo cáo: " + ex.getMessage(), "Lỗi In Ấn", JOptionPane.ERROR_MESSAGE);
            }
        } else if (choice == 1) {
            // Xuất CSV chuẩn tiếng Việt UTF-8 BOM
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Lưu Báo Cáo Cuối Ngày");
            chooser.setSelectedFile(new File("BaoCaoCuoiNgay_" + dpNgayThongKe.getDate() + ".csv"));

            int userSelect = chooser.showSaveDialog(this);
            if (userSelect == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }

                try (FileOutputStream fos = new FileOutputStream(file);
                     OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                     java.io.BufferedWriter writer = new java.io.BufferedWriter(osw)) {

                    fos.write(0xEF);
                    fos.write(0xBB);
                    fos.write(0xBF);

                    writer.write("PHÒNG KHÁM NHA KHOA ABC - BÁO CÁO THỐNG KÊ CUỐI NGÀY\n");
                    writer.write("Ngày thống kê: " + dpNgayThongKe.getDate() + "\n\n");
                    
                    writer.write("CA TRỰC,THỜI GIAN,SL BỆNH NHÂN,VẬT TƯ TIÊU HAO,TRẠNG THÁI\n");
                    for (int i = 0; i < modelTable.getRowCount(); i++) {
                        writer.write(String.format("\"%s\",\"%s\",%s,\"%s\",\"%s\"\n",
                                modelTable.getValueAt(i, 0),
                                modelTable.getValueAt(i, 1),
                                modelTable.getValueAt(i, 2),
                                modelTable.getValueAt(i, 3),
                                modelTable.getValueAt(i, 4)
                        ));
                    }

                    JOptionPane.showMessageDialog(this, "Xuất file CSV thành công!\nĐường dẫn: " + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}