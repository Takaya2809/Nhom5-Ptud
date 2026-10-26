package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Giao diện Quy Định, Chính Sách & Tiêu Chuẩn Vô Trùng Y Tế - Phong cách hiện
 * đại
 */
public class PanelQuyDinhModern extends JPanel {

    public PanelQuyDinhModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // HEADER
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("QUY CHẾ HOẠT ĐỘNG & TIÊU CHUẨN AN TOÀN Y TẾ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Quy trình kiểm soát nhiễm khuẩn, chính sách bảo hành dịch vụ và nội quy chuyên môn phòng khám");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        add(pnlHeader, BorderLayout.NORTH);

        // CENTER: 3 CARDS
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
                        "2. Mỗi bệnh nhân sử dụng một bộ tay khoan, mũi khoan và khay dụng cụ riêng biệt đã đóng gói ép nhiệt.\n\n"
                        +
                        "3. Khăn trải ngực, cốc súc miệng, găng tay y tế, kim tiêm, ống hút nước bọt chỉ sử dụng 1 lần duy nhất.\n\n"
                        +
                        "4. Khử khuẩn bề mặt ghế máy nha khoa bằng dung dịch diệt khuẩn chuyên dụng sau mỗi lượt khám.");
        ta1.setFont(ModernUITheme.FONT_REGULAR);
        ta1.setForeground(ModernUITheme.TEXT_REGULAR);
        ta1.setLineWrap(true);
        ta1.setWrapStyleWord(true);
        ta1.setEditable(false);
        ta1.setOpaque(false);
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
                "1. Cấy ghép Implant: Bảo hành trụ trọn đời với các dòng trụ Thụy Sĩ Straumann, bảo hành 10-15 năm với trụ Hàn Quốc.\n\n"
                        +
                        "2. Răng sứ thẩm mỹ: Cấp thẻ bảo hành điện tử chính hãng (Cercon, Zirconia, Emax) từ 7 đến 10 năm.\n\n"
                        +
                        "3. Niềng răng: Cam kết bảo đảm kết quả theo phác đồ điều trị đã ký kết, tái khám miễn phí định kỳ.\n\n"
                        +
                        "4. Trám răng & Điều trị tủy: Bảo hành miếng trám và phục hình trong vòng 06 tháng.");
        ta2.setFont(ModernUITheme.FONT_REGULAR);
        ta2.setForeground(ModernUITheme.TEXT_REGULAR);
        ta2.setLineWrap(true);
        ta2.setWrapStyleWord(true);
        ta2.setEditable(false);
        ta2.setOpaque(false);
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
                        "2. Trang phục: Áo blouse trắng chuẩn y tế, đeo thẻ tên nhân viên, găng tay và kính bảo hộ suốt ca điều trị.\n\n"
                        +
                        "3. Luôn giải thích cặn kẽ bệnh án, kế hoạch điều trị và chi phí cho bệnh nhân trước khi thực hiện thủ thuật.\n\n"
                        +
                        "4. Cập nhật hồ sơ bệnh án điện tử và đơn thuốc ngay sau khi kết thúc ca khám.");
        ta3.setFont(ModernUITheme.FONT_REGULAR);
        ta3.setForeground(ModernUITheme.TEXT_REGULAR);
        ta3.setLineWrap(true);
        ta3.setWrapStyleWord(true);
        ta3.setEditable(false);
        ta3.setOpaque(false);
        card3.add(new JScrollPane(ta3), BorderLayout.CENTER);
        pnlCards.add(card3);

        add(pnlCards, BorderLayout.CENTER);
    }
}
