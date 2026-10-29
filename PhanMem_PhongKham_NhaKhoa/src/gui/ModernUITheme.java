package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Design System hiện đại dành riêng cho Giao diện Quản lý Phòng Khám Nha Khoa ABC
 * Chuẩn màu sắc quốc tế:
 * - Primary: #2563EB (Xanh Hoàng Gia Y Tế)
 * - Primary Hover: #1D4ED8
 * - Background: #F8FAFC (Sạch sẽ, chuẩn phòng khám)
 * - Surface / Card: #FFFFFF
 * - Text Primary: #0F172A
 * - Text Secondary: #64748B
 * - Border: #E2E8F0
 * - Success: #16A34A
 * - Warning: #D97706
 * - Danger: #DC2626
 */
public class ModernUITheme {

    // --- BẢNG MÀU CHUẨN DESIGN SYSTEM ---
    public static final Color PRIMARY = new Color(37, 99, 235);       // #2563EB
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color PRIMARY_DARK = new Color(30, 64, 175);  // #1E40AF
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255);// #EFF6FF

    public static final Color BG_MAIN = new Color(248, 250, 252);     // #F8FAFC
    public static final Color CARD_BG = Color.WHITE;                  // #FFFFFF
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);// #E2E8F0

    public static final Color TEXT_MAIN = new Color(15, 23, 42);      // #0F172A
    public static final Color TEXT_REGULAR = new Color(51, 65, 85);   // #334155
    public static final Color TEXT_MUTED = new Color(100, 116, 139);  // #64748B
    public static final Color SECONDARY = new Color(100, 116, 139);   // #64748B

    public static final Color SUCCESS = new Color(22, 163, 74);       // #16A34A
    public static final Color SUCCESS_LIGHT = new Color(220, 252, 231);// #DCFCE7

    public static final Color WARNING = new Color(217, 119, 6);       // #D97706
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);// #FEF3C7

    public static final Color DANGER = new Color(220, 38, 38);        // #DC2626
    public static final Color DANGER_LIGHT = new Color(254, 226, 226); // #FEE2E2

    public static final Color ACCENT = new Color(13, 148, 136);       // #0D9488 (Teal Y Khoa)
    public static final Color ACCENT_LIGHT = new Color(204, 251, 241); // #CCFBF1

    // Sidebar
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);     // #0F172A
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);  // #1E293B
    public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);// #2563EB
    public static final Color SIDEBAR_TEXT = new Color(226, 232, 240);// #E2E8F0
    public static final Color SIDEBAR_MUTED = new Color(148, 163, 184);// #94A3B8

    // --- TYPOGRAPHY (SEGOE UI) ---
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 24);

    /**
     * Tạo Card Panel bo góc mềm mại, nền trắng, viền thanh lịch
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        return card;
    }

    /**
     * Nút bấm phong cách hiện đại với hiệu ứng Hover
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        return createIconButton(null, text, bg, fg);
    }

    /**
     * Nút bấm có Icon kết hợp Text, bo góc mềm mại
     */
    public static JButton createIconButton(Icon icon, String text, Color bg, Color fg) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color paintBg = bg;
                if (getModel().isPressed()) {
                    paintBg = paintBg.darker();
                } else if (getModel().isRollover()) {
                    if (bg.equals(PRIMARY)) {
                        paintBg = PRIMARY_HOVER;
                    } else {
                        paintBg = new Color(
                                Math.min(255, paintBg.getRed() + 15),
                                Math.min(255, paintBg.getGreen() + 15),
                                Math.min(255, paintBg.getBlue() + 15));
                    }
                }
                g2.setColor(paintBg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setIconTextGap(8);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Nút Outlined/Ghost button thanh lịch
     */
    public static JButton createOutlineButton(String text, Color accentColor) {
        return createOutlineIconButton(null, text, accentColor);
    }

    /**
     * Nút Outlined/Ghost button kèm Icon
     */
    public static JButton createOutlineIconButton(Icon icon, String text, Color accentColor) {
        JButton btn = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 25));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                }
                g2.setColor(accentColor);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(accentColor);
        btn.setIconTextGap(6);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * TextField hiện đại bo góc, chiều cao chuẩn 36-38px
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        return tf;
    }

    /**
     * Badge trạng thái hình viên thuốc (Pill Badge)
     */
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(FONT_SMALL);
        badge.setForeground(fg);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }

    /**
     * Định dạng JTable hiện đại phẳng, thanh lịch, sọc xen kẽ, chiều cao hàng 38px
     */
    public static void formatModernTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(38);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_MAIN);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_MAIN);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_LIGHT));

        // Renderer sọc xen kẽ
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 254));
                    c.setForeground(TEXT_REGULAR);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    /**
     * Tạo Thẻ KPI Metrics lớn hiển thị số liệu phòng khám (Dùng AppIcon, 0% lỗi hiển thị)
     */
    public static JPanel createKPICard(String title, String value, String subText, Color accentColor, Icon icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                // Vẽ dải màu nhấn ở mép trái thẻ
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, 5, getHeight(), 6, 6));

                // Viền mỏng
                g2.setColor(BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(12, 5));
        card.setBorder(new EmptyBorder(16, 20, 16, 16));

        // Left text: Tiêu đề + Giá trị to + Subtext
        JPanel pnlText = new JPanel();
        pnlText.setLayout(new BoxLayout(pnlText, BoxLayout.Y_AXIS));
        pnlText.setOpaque(false);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(FONT_TINY);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(FONT_METRIC);
        lblVal.setForeground(TEXT_MAIN);

        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(FONT_TINY);
        lblSub.setForeground(accentColor);

        pnlText.add(lblTitle);
        pnlText.add(Box.createVerticalStrut(4));
        pnlText.add(lblVal);
        pnlText.add(Box.createVerticalStrut(4));
        pnlText.add(lblSub);

        card.add(pnlText, BorderLayout.CENTER);

        if (icon != null) {
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setVerticalAlignment(SwingConstants.TOP);
            card.add(lblIcon, BorderLayout.EAST);
        }

        return card;
    }

    /**
     * Overload tương thích ngược nếu truyền string iconSymbol
     */
    public static JPanel createKPICard(String title, String value, String subText, Color accentColor, String iconSymbol) {
        Icon appIcon;
        if (iconSymbol != null && (iconSymbol.contains("💰") || iconSymbol.contains("VND") || iconSymbol.contains("DT"))) {
            appIcon = new AppIcon(AppIcon.Type.PAYMENT, 24, accentColor);
        } else if (iconSymbol != null && (iconSymbol.contains("🩺") || iconSymbol.contains("LK") || iconSymbol.contains("DV"))) {
            appIcon = new AppIcon(AppIcon.Type.SERVICE, 24, accentColor);
        } else if (iconSymbol != null && (iconSymbol.contains("👥") || iconSymbol.contains("BN"))) {
            appIcon = new AppIcon(AppIcon.Type.PATIENT, 24, accentColor);
        } else {
            appIcon = new AppIcon(AppIcon.Type.WARNING, 24, accentColor);
        }
        return createKPICard(title, value, subText, accentColor, appIcon);
    }
}
