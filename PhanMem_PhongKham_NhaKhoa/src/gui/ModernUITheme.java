package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Design System hiện đại dành riêng cho Giao diện Quản lý Phòng Khám Nha Khoa
 * Tone màu y tế quốc tế: Dark Slate Navy, Sky Blue, Medical Teal, Emerald,
 * Amber
 */
public class ModernUITheme {

    // --- BẢNG MÀU CHÍNH (COLOR SYSTEM) ---
    public static final Color SIDEBAR_BG = new Color(15, 23, 42); // Slate 900
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59); // Slate 800
    public static final Color SIDEBAR_ACTIVE = new Color(2, 132, 199); // Sky 600
    public static final Color SIDEBAR_TEXT = new Color(226, 232, 240);// Slate 200
    public static final Color SIDEBAR_MUTED = new Color(148, 163, 184);// Slate 400

    public static final Color PRIMARY = new Color(2, 132, 199); // Sky 600
    public static final Color PRIMARY_DARK = new Color(3, 105, 161); // Sky 700
    public static final Color PRIMARY_LIGHT = new Color(224, 242, 254);// Sky 100

    public static final Color ACCENT = new Color(13, 148, 136); // Teal 600
    public static final Color ACCENT_LIGHT = new Color(204, 251, 241);// Teal 100

    public static final Color SUCCESS = new Color(16, 185, 129); // Emerald 500
    public static final Color SUCCESS_LIGHT = new Color(209, 250, 229);// Emerald 100

    public static final Color WARNING = new Color(245, 158, 11); // Amber 500
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);// Amber 100

    public static final Color DANGER = new Color(239, 68, 68); // Rose/Red 500
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);// Rose 100

    public static final Color BG_MAIN = new Color(241, 245, 249);// Slate 100 (Canvas)
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);// Slate 200

    public static final Color TEXT_MAIN = new Color(15, 23, 42); // Slate 900
    public static final Color TEXT_REGULAR = new Color(51, 65, 85); // Slate 700
    public static final Color TEXT_MUTED = new Color(100, 116, 139);// Slate 500

    // --- HỆ THỐNG TYPOGRAPHY (SEGOE UI) ---
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 26);

    /**
     * Tạo Card Panel bo góc mềm mại, nền trắng tinh tế
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 18, 15, 18));
        return card;
    }

    /**
     * Nút bấm phong cách hiện đại với hiệu ứng Hover mượt mà
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color paintBg = bg;
                if (getModel().isPressed()) {
                    paintBg = paintBg.darker();
                } else if (getModel().isRollover()) {
                    paintBg = new Color(
                            Math.min(255, paintBg.getRed() + 20),
                            Math.min(255, paintBg.getGreen() + 20),
                            Math.min(255, paintBg.getBlue() + 20));
                }
                g2.setColor(paintBg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
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
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
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
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * TextField hiện đại bo góc, có viền nét thanh
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
     * Định dạng JTable hiện đại phẳng, thanh lịch, sọc xen kẽ, chiều cao 38px
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
     * Tạo Thẻ KPI Metrics lớn hiển thị số liệu phòng khám (Basic, không icon lỗi)
     */
    public static JPanel createKPICard(String title, String value, String subText, Color accentColor,
            String iconSymbol) {
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
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(new EmptyBorder(16, 20, 16, 16));

        // Tiêu đề + Giá trị to + Subtext
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

        return card;
    }
}
