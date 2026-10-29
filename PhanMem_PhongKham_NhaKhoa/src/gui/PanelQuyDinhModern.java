package gui;

import dao.QuyDinhDAO;
import entity.QuyDinh;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Quy Định, Chính Sách & Tiêu Chuẩn Vô Trùng Y Tế - Phong cách hiện đại
 * Tích hợp Bảng Quy Định Hệ Thống lưu trực tiếp vào CSDL QLPHONGKHAM
 */
public class PanelQuyDinhModern extends JPanel {

    private QuyDinhDAO quyDinhDAO = new QuyDinhDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaQD, txtTenQD, txtGiaTri, txtKieuDL, txtMoTa;
    private JButton btnLuu, btnLamMoi;

    public PanelQuyDinhModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // 1. HEADER
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("QUY CHẾ HOẠT ĐỘNG, THIẾT LẬP THAM SỐ & TIÊU CHUẨN AN TOÀN Y TẾ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Cấu hình các tham số vận hành hệ thống phòng khám (CSDL) & Quy chuẩn vô trùng đạt chuẩn Sở Y Tế");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE: Phía trên là bảng CSDL QuyDinhHeThong, Phía dưới là các Card nghiệp vụ
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);

        // TOP: Quản lý tham số hệ thống từ CSDL
        JPanel pnlTopCard = ModernUITheme.createCardPanel();
        pnlTopCard.setLayout(new BorderLayout(10, 10));

        JLabel lblTopTitle = new JLabel("⚙️ CẤU HÌNH THAM SỐ VẬN HÀNH HỆ THỐNG (CSDL QLPHONGKHAM)");
        lblTopTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTopTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlTopCard.add(lblTopTitle, BorderLayout.NORTH);

        String[] cols = { "Mã Quy Định", "Tên Tham Số / Quy Định", "Giá Trị Cấu Hình", "Kiểu Dữ Liệu", "Mô Tả Áp Dụng", "Ngày Cập Nhật", "Người Cập Nhật" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0, 160));
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnlTopCard.add(sp, BorderLayout.CENTER);

        // Form chỉnh sửa giá trị quy định
        JPanel pnlForm = new JPanel(new BorderLayout(10, 8));
        pnlForm.setOpaque(false);

        JPanel pnlFields = new JPanel(new GridLayout(1, 5, 10, 0));
        pnlFields.setOpaque(false);

        txtMaQD = ModernUITheme.createTextField(10);
        txtMaQD.setEditable(false);
        txtTenQD = ModernUITheme.createTextField(15);
        txtTenQD.setEditable(false);
        txtGiaTri = ModernUITheme.createTextField(8);
        txtKieuDL = ModernUITheme.createTextField(6);
        txtKieuDL.setEditable(false);
        txtMoTa = ModernUITheme.createTextField(20);
        txtMoTa.setEditable(false);

        pnlFields.add(createField("Mã quy định:", txtMaQD));
        pnlFields.add(createField("Tên tham số:", txtTenQD));
        pnlFields.add(createField("Giá trị mới (*):", txtGiaTri));
        pnlFields.add(createField("Kiểu DL:", txtKieuDL));
        pnlFields.add(createField("Mô tả áp dụng:", txtMoTa));
        pnlForm.add(pnlFields, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);
        btnLamMoi = ModernUITheme.createOutlineButton("🔄 Tải lại CSDL", ModernUITheme.TEXT_MUTED);
        btnLuu = ModernUITheme.createButton("💾 Cập nhật giá trị vào CSDL", ModernUITheme.PRIMARY, Color.WHITE);
        pnlBtns.add(btnLamMoi);
        pnlBtns.add(btnLuu);
        pnlForm.add(pnlBtns, BorderLayout.SOUTH);

        pnlTopCard.add(pnlForm, BorderLayout.SOUTH);
        splitPane.setTopComponent(pnlTopCard);

        // BOTTOM: 3 CARDS TIÊU CHUẨN Y KHOA
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 16, 0));
        pnlCards.setOpaque(false);

        // Card 1: Quy trình vô trùng
        JPanel card1 = ModernUITheme.createCardPanel();
        card1.setLayout(new BorderLayout(0, 10));
        JLabel lblC1 = new JLabel("🛡️ KIỂM SOÁT NHIỄM KHUẨN");
        lblC1.setFont(ModernUITheme.FONT_TITLE);
        lblC1.setForeground(ModernUITheme.PRIMARY);
        card1.add(lblC1, BorderLayout.NORTH);

        JTextArea ta1 = new JTextArea(
                "1. Tiệt trùng dụng cụ 100% bằng lò hấp Autoclave Class B tiêu chuẩn Châu Âu.\n\n" +
                "2. Mỗi bệnh nhân sử dụng một bộ tay khoan, mũi khoan và khay dụng cụ riêng biệt đã đóng gói ép nhiệt.\n\n" +
                "3. Khăn trải ngực, cốc súc miệng, găng tay y tế, kim tiêm, ống hút nước bọt chỉ sử dụng 1 lần duy nhất.\n\n" +
                "4. Khử khuẩn bề mặt ghế máy nha khoa bằng dung dịch diệt khuẩn chuyên dụng sau mỗi lượt khám.");
        formatTextArea(ta1);
        card1.add(new JScrollPane(ta1), BorderLayout.CENTER);
        pnlCards.add(card1);

        // Card 2: Chính sách bảo hành
        JPanel card2 = ModernUITheme.createCardPanel();
        card2.setLayout(new BorderLayout(0, 10));
        JLabel lblC2 = new JLabel("🏆 CHÍNH SÁCH BẢO HÀNH");
        lblC2.setFont(ModernUITheme.FONT_TITLE);
        lblC2.setForeground(ModernUITheme.SUCCESS);
        card2.add(lblC2, BorderLayout.NORTH);

        JTextArea ta2 = new JTextArea(
                "1. Cấy ghép Implant: Bảo hành trụ trọn đời với các dòng trụ Thụy Sĩ Straumann, bảo hành 10-15 năm với trụ Hàn Quốc.\n\n" +
                "2. Răng sứ thẩm mỹ: Cấp thẻ bảo hành điện tử chính hãng (Cercon, Zirconia, Emax) từ 7 đến 10 năm.\n\n" +
                "3. Niềng răng: Cam kết bảo đảm kết quả theo phác đồ điều trị đã ký kết, tái khám miễn phí định kỳ.\n\n" +
                "4. Trám răng & Điều trị tủy: Bảo hành miếng trám và phục hình trong vòng 06 tháng.");
        formatTextArea(ta2);
        card2.add(new JScrollPane(ta2), BorderLayout.CENTER);
        pnlCards.add(card2);

        // Card 3: Quy định nhân sự
        JPanel card3 = ModernUITheme.createCardPanel();
        card3.setLayout(new BorderLayout(0, 10));
        JLabel lblC3 = new JLabel("📜 NỘI QUY CHUYÊN MÔN");
        lblC3.setFont(ModernUITheme.FONT_TITLE);
        lblC3.setForeground(ModernUITheme.WARNING);
        card3.add(lblC3, BorderLayout.NORTH);

        JTextArea ta3 = new JTextArea(
                "1. Bác sĩ và Điều dưỡng có mặt trước giờ làm việc 15 phút để chuẩn bị phòng khám và vật tư.\n\n" +
                "2. Trang phục: Áo blouse trắng chuẩn y tế, đeo thẻ tên nhân viên, găng tay và kính bảo hộ suốt ca điều trị.\n\n" +
                "3. Luôn giải thích cặn kẽ bệnh án, kế hoạch điều trị và chi phí cho bệnh nhân trước khi thực hiện thủ thuật.\n\n" +
                "4. Cập nhật hồ sơ bệnh án điện tử và đơn thuốc ngay sau khi kết thúc ca khám.");
        formatTextArea(ta3);
        card3.add(new JScrollPane(ta3), BorderLayout.CENTER);
        pnlCards.add(card3);

        splitPane.setBottomComponent(pnlCards);
        add(splitPane, BorderLayout.CENTER);

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                napFormTuBang(table.getSelectedRow());
            }
        });

        btnLuu.addActionListener(e -> xuLyCapNhat());
        btnLamMoi.addActionListener(e -> taiDuLieu());

        taiDuLieu();
    }

    private void formatTextArea(JTextArea ta) {
        ta.setFont(ModernUITheme.FONT_REGULAR);
        ta.setForeground(ModernUITheme.TEXT_REGULAR);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
    }

    private JPanel createField(String labelText, JComponent comp) {
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
        List<QuyDinh> list = quyDinhDAO.getAllQuyDinh();
        for (QuyDinh qd : list) {
            tableModel.addRow(new Object[] {
                qd.getMaQuyDinh(),
                qd.getTenQuyDinh(),
                qd.getGiaTri(),
                qd.getKieuDuLieu(),
                qd.getMoTa(),
                qd.getNgayCapNhat() != null ? qd.getNgayCapNhat().toString() : "",
                qd.getMaNVCapNhat() != null ? qd.getMaNVCapNhat() : "Hệ thống"
            });
        }
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            napFormTuBang(0);
        }
    }

    private void napFormTuBang(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        txtMaQD.setText(tableModel.getValueAt(row, 0).toString());
        txtTenQD.setText(tableModel.getValueAt(row, 1).toString());
        txtGiaTri.setText(tableModel.getValueAt(row, 2).toString());
        txtKieuDL.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "INT");
        txtMoTa.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
    }

    private void xuLyCapNhat() {
        String maQD = txtMaQD.getText().trim();
        if (maQD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 quy định trong bảng để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String giaTriMoi = txtGiaTri.getText().trim();
        if (giaTriMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá trị quy định không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtGiaTri.requestFocus();
            return;
        }

        int cf = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn cập nhật giá trị của [" + txtTenQD.getText() + "] thành [" + giaTriMoi + "] vào CSDL?", 
            "Xác nhận cập nhật CSDL", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            boolean ok = quyDinhDAO.capNhatGiaTri(maQD, giaTriMoi, "QL001");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật quy định hệ thống vào CSDL thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật vào CSDL thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
