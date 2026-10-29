package gui;

import dao.QuyDinhDAO;
import entity.QuyDinh;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giao diện Cài Đặt Hệ Thống & Quy Định Phòng Khám - Redesign Chuẩn Y Khoa
 * Phân tách rõ ràng giữa:
 * 1. Tham số cấu hình vận hành hệ thống (CSDL QuyDinhHeThong)
 * 2. Tiêu chuẩn y tế, kiểm soát nhiễm khuẩn, chính sách bảo hành và nội quy
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
        JLabel lblTitle = new JLabel("CÀI ĐẶT THAM SỐ VẬN HÀNH & QUY CHẾ PHÒNG KHÁM");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Quản lý cấu hình tham số vận hành hệ thống phòng khám (CSDL) & Bộ quy chuẩn chuyên môn y tế");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. TABBED PANE PHÂN TÁCH NGHIỆP VỤ RÕ RÀNG
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernUITheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Cấu Hình Tham Số Vận Hành", new AppIcon(AppIcon.Type.SETTINGS, 16, ModernUITheme.PRIMARY), createTabThamSo());
        tabbedPane.addTab("Kiểm Soát Nhiễm Khuẩn", new AppIcon(AppIcon.Type.STAFF, 16, ModernUITheme.PRIMARY), createTabNhiemKhuan());
        tabbedPane.addTab("Chính Sách Bảo Hành", new AppIcon(AppIcon.Type.SERVICE, 16, ModernUITheme.SUCCESS), createTabBaoHanh());
        tabbedPane.addTab("Nội Quy & Tiêu Chuẩn Y Đức", new AppIcon(AppIcon.Type.HISTORY, 16, ModernUITheme.WARNING), createTabNoiQuy());

        add(tabbedPane, BorderLayout.CENTER);

        taiDuLieu();
    }

    /**
     * TAB 1: CẤU HÌNH THAM SỐ HỆ THỐNG TỪ CSDL
     */
    private JPanel createTabThamSo() {
        JPanel pnlMain = new JPanel(new BorderLayout(0, 14));
        pnlMain.setBackground(ModernUITheme.BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Split: Bảng danh sách phía trên, form chi tiết phía dưới
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // BẢNG THAM SỐ
        String[] cols = { "Mã Quy Định", "Tên Tham Số / Quy Định", "Giá Trị Cấu Hình", "Kiểu Dữ Liệu", "Mô Tả Áp Dụng", "Ngày Cập Nhật", "Người Cập Nhật" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        splitPane.setTopComponent(sp);

        // FORM CẬP NHẬT GIÁ TRỊ THAM SỐ
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 10));

        JPanel pnlFormHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFormHeader.setOpaque(false);
        JLabel lblIco = new JLabel(new AppIcon(AppIcon.Type.SETTINGS, 16, ModernUITheme.PRIMARY));
        JLabel lblTitle = new JLabel("CHỈNH SỬA THAM SỐ HỆ THỐNG ĐƯỢC CHỌN");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormHeader.add(lblIco);
        pnlFormHeader.add(lblTitle);
        pnlFormCard.add(pnlFormHeader, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(1, 5, 12, 0));
        pnlFields.setOpaque(false);

        txtMaQD = ModernUITheme.createTextField(10);
        txtMaQD.setEditable(false);
        txtMaQD.setBackground(new Color(241, 245, 249));

        txtTenQD = ModernUITheme.createTextField(15);
        txtTenQD.setEditable(false);
        txtTenQD.setBackground(new Color(241, 245, 249));

        txtGiaTri = ModernUITheme.createTextField(8);
        txtKieuDL = ModernUITheme.createTextField(6);
        txtKieuDL.setEditable(false);
        txtKieuDL.setBackground(new Color(241, 245, 249));

        txtMoTa = ModernUITheme.createTextField(20);
        txtMoTa.setEditable(false);
        txtMoTa.setBackground(new Color(241, 245, 249));

        pnlFields.add(createField("Mã quy định:", txtMaQD));
        pnlFields.add(createField("Tên tham số:", txtTenQD));
        pnlFields.add(createField("Giá trị mới (*):", txtGiaTri));
        pnlFields.add(createField("Kiểu dữ liệu:", txtKieuDL));
        pnlFields.add(createField("Mô tả áp dụng:", txtMoTa));
        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tải lại CSDL", ModernUITheme.TEXT_MUTED);
        btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Lưu cấu hình vào CSDL", ModernUITheme.PRIMARY, Color.WHITE);
        pnlBtns.add(btnLamMoi);
        pnlBtns.add(btnLuu);
        pnlFormCard.add(pnlBtns, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        pnlMain.add(splitPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                napFormTuBang(table.getSelectedRow());
            }
        });

        btnLuu.addActionListener(e -> xuLyCapNhat());
        btnLamMoi.addActionListener(e -> taiDuLieu());

        return pnlMain;
    }

    /**
     * TAB 2: TIÊU CHUẨN KIỂM SOÁT NHIỄM KHUẨN
     */
    private JPanel createTabNhiemKhuan() {
        JPanel card = ModernUITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 16));

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlHeader.setOpaque(false);
        JLabel lblIco = new JLabel(new AppIcon(AppIcon.Type.STAFF, 20, ModernUITheme.PRIMARY));
        JLabel lblTitle = new JLabel("TIÊU CHUẨN KIỂM SOÁT NHIỄM KHUẨN & VÔ TRÙNG NHA KHOA ĐẠT CHUẨN SỞ Y TẾ");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlHeader.add(lblIco);
        pnlHeader.add(lblTitle);
        card.add(pnlHeader, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(
                "1. QUY TRÌNH TIỆT TRÙNG KHÉP KÍN 100%:\n" +
                "   - Toàn bộ dụng cụ nha khoa kim loại (kìm, nạy, tay khoan, mũi khoan) bắt buộc phải qua 4 bước:\n" +
                "     Phân loại -> Ngâm khử khuẩn bằng dung dịch Aniosyme -> Rửa siêu âm -> Đóng gói ép nhiệt túi vô trùng -> Hấp sấy Autoclave Class B Châu Âu ở 134°C trong 45 phút.\n\n" +
                "2. NGUYÊN TẮC 'MỖI BỆNH NHÂN MỘT BỘ DỤNG CỤ RIÊNG BIỆT':\n" +
                "   - Túi dụng cụ chỉ được phép xé bao bì trước mặt bệnh nhân ngay khi bắt đầu ca điều trị.\n" +
                "   - Khăn trải ngực, cốc súc miệng, găng tay y tế, kim tiêm, đầu hút phẫu thuật và ống hút nước bọt chỉ sử dụng 1 lần duy nhất rồi tiêu hủy theo quy định rác thải y tế.\n\n" +
                "3. KHỬ KHUẨN GHẾ MÁY & BỀ MẶT PHÒNG KHÁM:\n" +
                "   - Khử khuẩn toàn bộ bề mặt mâm ghế, đèn chiếu, bồn nhổ bằng dung dịch cồn y tế / xịt diệt khuẩn chuyên dụng sau mỗi lượt khám.\n" +
                "   - Cuối ngày thực hiện chiếu đèn cực tím (UV) khử khuẩn không khí toàn phòng khám 60 phút.\n\n" +
                "4. TRANG BỊ BẢO HỘ CÁ NHÂN (PPE):\n" +
                "   - Bác sĩ và điều dưỡng phụ tá bắt buộc phải đeo kính bảo hộ/kính chắn giọt bắn, khẩu trang y tế và găng tay suốt quá trình điều trị.");
        formatTextArea(ta);
        card.add(new JScrollPane(ta), BorderLayout.CENTER);
        return card;
    }

    /**
     * TAB 3: CHÍNH SÁCH BẢO HÀNH DỊCH VỤ
     */
    private JPanel createTabBaoHanh() {
        JPanel card = ModernUITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 16));

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlHeader.setOpaque(false);
        JLabel lblIco = new JLabel(new AppIcon(AppIcon.Type.SERVICE, 20, ModernUITheme.SUCCESS));
        JLabel lblTitle = new JLabel("CHÍNH SÁCH BẢO HÀNH ĐIỆN TỬ & CAM KẾT CHẤT LƯỢNG ĐIỀU TRỊ");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.SUCCESS);
        pnlHeader.add(lblIco);
        pnlHeader.add(lblTitle);
        card.add(pnlHeader, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(
                "1. CẤY GHÉP IMPLANT:\n" +
                "   - Bảo hành trọn đời đối với các dòng trụ cao cấp Straumann (Thụy Sĩ), Nobel Biocare (Mỹ).\n" +
                "   - Bảo hành 10 - 15 năm đối với các dòng trụ Osstem (Hàn Quốc), Dentium (Mỹ).\n" +
                "   - Điều kiện: Tái khám định kỳ 6 tháng/lần tại phòng khám và tuân thủ hướng dẫn vệ sinh răng miệng.\n\n" +
                "2. RĂNG SỨ THẨM MỸ & DÁN SỨ VENEER:\n" +
                "   - Cấp thẻ bảo hành chính hãng có mã quét QR điện tử tra cứu nguồn gốc phôi sứ:\n" +
                "     + Sứ Cercon HT, Zirconia Katana: Bảo hành 10 năm.\n" +
                "     + Mặt dán sứ Emax Press: Bảo hành 07 năm.\n" +
                "   - Bảo hành miễn phí các lỗi kỹ thuật: nứt, vỡ, sứt mẻ trong quá trình ăn nhai bình thường.\n\n" +
                "3. NIỀNG RĂNG - CHỈNH NHA:\n" +
                "   - Ký kết hợp đồng cam kết hiệu quả phác đồ trước khi gắn mắc cài / khay trong suốt.\n" +
                "   - Miễn phí toàn bộ khí cụ duy trì sau khi tháo niềng và miễn phí tái khám trong 1 năm đầu.\n\n" +
                "4. TRÁM RĂNG & ĐIỀU TRỊ NỘI NHA (TỦY):\n" +
                "   - Bảo hành 06 tháng cho miếng trám thẩm mỹ Composite và phục hình tái tạo sau điều trị tủy.");
        formatTextArea(ta);
        card.add(new JScrollPane(ta), BorderLayout.CENTER);
        return card;
    }

    /**
     * TAB 4: NỘI QUY CHUYÊN MÔN & TIÊU CHUẨN Y ĐỨC
     */
    private JPanel createTabNoiQuy() {
        JPanel card = ModernUITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 16));

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlHeader.setOpaque(false);
        JLabel lblIco = new JLabel(new AppIcon(AppIcon.Type.HISTORY, 20, ModernUITheme.WARNING));
        JLabel lblTitle = new JLabel("QUY CHẾ LÀM VIỆC NỘI BỘ, TRÁCH NHIỆM CHUYÊN MÔN & CHUẨN MỰC GIAO TIẾP");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.WARNING);
        pnlHeader.add(lblIco);
        pnlHeader.add(lblTitle);
        card.add(pnlHeader, BorderLayout.NORTH);

        JTextArea ta = new JTextArea(
                "1. THỜI GIAN LÀM VIỆC & CHUẨN BỊ CA KHÁM:\n" +
                "   - Bác sĩ và Điều dưỡng có mặt trước giờ bắt đầu ca khám 15 phút để kiểm tra hệ thống khí nén, máy hút và dụng cụ.\n" +
                "   - Trang phục chuẩn mực: Áo blouse trắng là phẳng, đeo bảng tên nhân viên, tóc gọn gàng.\n\n" +
                "2. NGUYÊN TẮC TƯ VẤN VÀ TÔN TRỌNG BỆNH NHÂN:\n" +
                "   - Luôn giải thích rõ ràng, trung thực tình trạng răng miệng, các phương án điều trị khả thi và báo giá chi tiết trước khi can thiệp thủ thuật.\n" +
                "   - Không tự ý phát sinh chi phí hoặc thực hiện dịch vụ khi chưa có sự đồng ý của bệnh nhân.\n\n" +
                "3. TRÁCH NHIỆM HỒ SƠ BỆNH ÁN ĐIỆN TỬ:\n" +
                "   - Bắt buộc cập nhật đầy đủ chẩn đoán, phim X-quang, hướng điều trị và đơn thuốc vào phần mềm ngay sau khi kết thúc ca khám.\n" +
                "   - Tuyệt đối bảo mật thông tin cá nhân và dữ liệu bệnh án của khách hàng theo quy định pháp luật.");
        formatTextArea(ta);
        card.add(new JScrollPane(ta), BorderLayout.CENTER);
        return card;
    }

    private void formatTextArea(JTextArea ta) {
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setForeground(ModernUITheme.TEXT_REGULAR);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setBorder(new EmptyBorder(8, 12, 8, 12));
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tham số trong bảng để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String giaTriMoi = txtGiaTri.getText().trim();
        if (giaTriMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá trị cấu hình không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtGiaTri.requestFocus();
            return;
        }

        String kieuDL = txtKieuDL.getText().trim();
        if ("INT".equalsIgnoreCase(kieuDL)) {
            try {
                int val = Integer.parseInt(giaTriMoi);
                if (val < 0) {
                    JOptionPane.showMessageDialog(this, "Giá trị phải là số nguyên không âm!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Kiểu dữ liệu của tham số này là INT (Số nguyên). Vui lòng nhập số hợp lệ!", "Lỗi kiểu dữ liệu", JOptionPane.ERROR_MESSAGE);
                txtGiaTri.requestFocus();
                return;
            }
        } else if ("FLOAT".equalsIgnoreCase(kieuDL) || "DOUBLE".equalsIgnoreCase(kieuDL)) {
            try {
                Double.parseDouble(giaTriMoi);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Kiểu dữ liệu của tham số này là Số thực (FLOAT/DOUBLE). Vui lòng nhập số hợp lệ!", "Lỗi kiểu dữ liệu", JOptionPane.ERROR_MESSAGE);
                txtGiaTri.requestFocus();
                return;
            }
        }

        int cf = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn cập nhật giá trị tham số [" + txtTenQD.getText() + "] thành [" + giaTriMoi + "] vào CSDL?", 
            "Xác nhận cập nhật CSDL", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (cf == JOptionPane.YES_OPTION) {
            boolean ok = quyDinhDAO.capNhatGiaTri(maQD, giaTriMoi, "QL001");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật tham số vận hành vào CSDL thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật vào CSDL thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
