package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Bộ Icon Vector thuần Java Swing chuẩn Medical / ERP System.
 * Tự vẽ bằng Graphics2D chất lượng cao (Anti-aliasing), độc lập DPI và hệ điều hành,
 * triệt tiêu hoàn toàn lỗi font, thiếu icon hoặc ký tự ô vuông (□) trên Windows.
 */
public class AppIcon implements Icon {

    public enum Type {
        DASHBOARD,
        PATIENT,
        SERVICE,
        CALENDAR,
        PAYMENT,
        DISCOUNT,
        INVENTORY,
        STAFF,
        ACCOUNT,
        SETTINGS,
        REPORT,
        SEARCH,
        PLUS,
        SAVE,
        DELETE,
        REFRESH,
        HISTORY,
        LOCK,
        CLOCK,
        CHECK,
        CLOSE,
        INFO,
        WARNING,
        PRINT,
        FILTER,
        DOLLAR,
        TOOTH,
        USER
    }

    private final Type type;
    private final int size;
    private final Color color;

    public AppIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    public AppIcon(Type type, int size) {
        this(type, size, ModernUITheme.SIDEBAR_TEXT);
    }

    public AppIcon(Type type) {
        this(type, 16, ModernUITheme.SIDEBAR_TEXT);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);

        // Chuẩn hóa tỷ lệ vẽ về khung 16x16
        double scale = size / 16.0;
        g2.scale(scale, scale);

        Color drawColor = color;
        if (drawColor == null && c != null) {
            drawColor = c.getForeground();
        }
        if (drawColor == null) {
            drawColor = Color.BLACK;
        }
        g2.setColor(drawColor);

        float strokeWidth = 1.4f;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case DASHBOARD:
                g2.fill(new RoundRectangle2D.Float(1, 1, 6, 6, 2, 2));
                g2.fill(new RoundRectangle2D.Float(9, 1, 6, 6, 2, 2));
                g2.fill(new RoundRectangle2D.Float(1, 9, 6, 6, 2, 2));
                g2.draw(new RoundRectangle2D.Float(9, 9, 6, 6, 2, 2));
                break;

            case PATIENT:
            case USER:
                g2.fill(new Ellipse2D.Float(5.5f, 1.5f, 5, 5));
                g2.draw(new Arc2D.Float(2f, 8f, 12f, 9f, 0, 180, Arc2D.OPEN));
                break;

            case SERVICE:
            case TOOTH:
                Path2D tooth = new Path2D.Float();
                tooth.moveTo(8, 2.5);
                tooth.curveTo(4.5, 1.5, 2, 4.5, 2.5, 8);
                tooth.curveTo(2.8, 10.5, 4.5, 14.5, 6, 14.5);
                tooth.curveTo(7, 14.5, 7.2, 10.5, 8, 10.5);
                tooth.curveTo(8.8, 10.5, 9, 14.5, 10, 14.5);
                tooth.curveTo(11.5, 14.5, 13.2, 10.5, 13.5, 8);
                tooth.curveTo(14, 4.5, 11.5, 1.5, 8, 2.5);
                tooth.closePath();
                g2.draw(tooth);
                break;

            case CALENDAR:
                g2.draw(new RoundRectangle2D.Float(1.5f, 3f, 13f, 11.5f, 2, 2));
                g2.draw(new Line2D.Float(1.5f, 6.5f, 14.5f, 6.5f));
                g2.draw(new Line2D.Float(4.5f, 1.5f, 4.5f, 4f));
                g2.draw(new Line2D.Float(11.5f, 1.5f, 11.5f, 4f));
                g2.fillRect(4, 9, 2, 2);
                g2.fillRect(7, 9, 2, 2);
                g2.fillRect(10, 9, 2, 2);
                break;

            case PAYMENT:
            case DOLLAR:
                g2.draw(new RoundRectangle2D.Float(1.5f, 3.5f, 13f, 9f, 2, 2));
                g2.draw(new Line2D.Float(1.5f, 6.5f, 14.5f, 6.5f));
                g2.fillRect(3, 9, 3, 2);
                break;

            case DISCOUNT:
                Path2D tag = new Path2D.Float();
                tag.moveTo(2, 8);
                tag.lineTo(8, 2);
                tag.lineTo(14, 2);
                tag.lineTo(14, 8);
                tag.lineTo(8, 14);
                tag.closePath();
                g2.draw(tag);
                g2.fill(new Ellipse2D.Float(10.5f, 3.5f, 2f, 2f));
                break;

            case INVENTORY:
                g2.draw(new RoundRectangle2D.Float(2f, 3.5f, 12f, 10f, 2, 2));
                g2.draw(new Line2D.Float(2f, 6.5f, 14f, 6.5f));
                g2.draw(new Line2D.Float(8f, 6.5f, 8f, 10.5f));
                break;

            case STAFF:
                g2.fill(new Ellipse2D.Float(5.5f, 1.5f, 5, 5));
                g2.draw(new Arc2D.Float(2f, 8f, 12f, 9f, 0, 180, Arc2D.OPEN));
                g2.setColor(new Color(239, 68, 68));
                g2.drawLine(12, 11, 14, 11);
                g2.drawLine(13, 10, 13, 12);
                break;

            case ACCOUNT:
                Path2D shield = new Path2D.Float();
                shield.moveTo(8, 1.5);
                shield.lineTo(13.5, 3.5);
                shield.lineTo(13.5, 8.5);
                shield.curveTo(13.5, 12, 8, 14.5, 8, 14.5);
                shield.curveTo(8, 14.5, 2.5, 12, 2.5, 8.5);
                shield.lineTo(2.5, 3.5);
                shield.closePath();
                g2.draw(shield);
                g2.fill(new Ellipse2D.Float(7f, 6.5f, 2f, 2f));
                g2.draw(new Line2D.Float(8f, 8.5f, 8f, 10.5f));
                break;

            case SETTINGS:
                g2.draw(new Ellipse2D.Float(5, 5, 6, 6));
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4.0;
                    float x1 = (float) (8 + 3.5 * Math.cos(angle));
                    float y1 = (float) (8 + 3.5 * Math.sin(angle));
                    float x2 = (float) (8 + 6.5 * Math.cos(angle));
                    float y2 = (float) (8 + 6.5 * Math.sin(angle));
                    g2.draw(new Line2D.Float(x1, y1, x2, y2));
                }
                break;

            case REPORT:
                g2.drawLine(2, 14, 14, 14);
                g2.drawLine(2, 2, 2, 14);
                g2.fillRect(4, 8, 2, 6);
                g2.fillRect(7, 4, 2, 10);
                g2.fillRect(10, 6, 2, 8);
                break;

            case SEARCH:
                g2.draw(new Ellipse2D.Float(2.5f, 2.5f, 7.5f, 7.5f));
                g2.draw(new Line2D.Float(8.5f, 8.5f, 13.5f, 13.5f));
                break;

            case PLUS:
                g2.drawLine(8, 3, 8, 13);
                g2.drawLine(3, 8, 13, 8);
                break;

            case SAVE:
                g2.draw(new RoundRectangle2D.Float(2, 2, 12, 12, 2, 2));
                g2.fillRect(4, 2, 8, 4);
                g2.fillRect(4, 9, 8, 5);
                break;

            case DELETE:
                g2.drawLine(2, 4, 14, 4);
                g2.drawLine(6, 2, 10, 2);
                Path2D bin = new Path2D.Float();
                bin.moveTo(3.5, 4);
                bin.lineTo(4.5, 14);
                bin.lineTo(11.5, 14);
                bin.lineTo(12.5, 4);
                g2.draw(bin);
                g2.draw(new Line2D.Float(6.5f, 6.5f, 6.5f, 11.5f));
                g2.draw(new Line2D.Float(9.5f, 6.5f, 9.5f, 11.5f));
                break;

            case REFRESH:
                g2.draw(new Arc2D.Float(2.5f, 2.5f, 11f, 11f, 45, 270, Arc2D.OPEN));
                g2.fill(new Polygon(new int[]{10, 14, 14}, new int[]{2, 2, 6}, 3));
                break;

            case HISTORY:
                g2.draw(new RoundRectangle2D.Float(3, 1.5f, 10, 13, 2, 2));
                g2.draw(new Line2D.Float(5.5f, 5, 10.5f, 5));
                g2.draw(new Line2D.Float(5.5f, 8, 10.5f, 8));
                g2.draw(new Line2D.Float(5.5f, 11, 8.5f, 11));
                break;

            case LOCK:
                g2.draw(new RoundRectangle2D.Float(3, 6.5f, 10, 7.5f, 2, 2));
                g2.draw(new Arc2D.Float(5, 2, 6, 7, 0, 180, Arc2D.OPEN));
                g2.fill(new Ellipse2D.Float(7.2f, 9, 1.6f, 1.6f));
                break;

            case CLOCK:
                g2.draw(new Ellipse2D.Float(2, 2, 12, 12));
                g2.draw(new Line2D.Float(8, 4.5f, 8, 8));
                g2.drawLine(8, 8, 11, 8);
                break;

            case CHECK:
                g2.draw(new Line2D.Float(3, 8.5f, 6.5f, 12));
                g2.draw(new Line2D.Float(6.5f, 12, 13, 4));
                break;

            case CLOSE:
                g2.draw(new Line2D.Float(3.5f, 3.5f, 12.5f, 12.5f));
                g2.draw(new Line2D.Float(12.5f, 3.5f, 3.5f, 12.5f));
                break;

            case PRINT:
                g2.draw(new RoundRectangle2D.Float(2, 5, 12, 7, 2, 2));
                g2.draw(new Rectangle2D.Float(4.5f, 1.5f, 7, 3.5f));
                g2.draw(new Rectangle2D.Float(4.5f, 9, 7, 4.5f));
                break;

            case FILTER:
                Path2D filter = new Path2D.Float();
                filter.moveTo(2, 3);
                filter.lineTo(14, 3);
                filter.lineTo(9.5, 8.5);
                filter.lineTo(9.5, 13);
                filter.lineTo(6.5, 11.5);
                filter.lineTo(6.5, 8.5);
                filter.closePath();
                g2.draw(filter);
                break;

            case INFO:
                g2.draw(new Ellipse2D.Float(2, 2, 12, 12));
                g2.fill(new Ellipse2D.Float(7.2f, 4.5f, 1.6f, 1.6f));
                g2.draw(new Line2D.Float(8, 7.5f, 8, 11));
                break;

            case WARNING:
                Path2D warn = new Path2D.Float();
                warn.moveTo(8, 2);
                warn.lineTo(14.5, 13.5);
                warn.lineTo(1.5, 13.5);
                warn.closePath();
                g2.draw(warn);
                g2.draw(new Line2D.Float(8, 6, 8, 9.5f));
                g2.fill(new Ellipse2D.Float(7.3f, 11, 1.4f, 1.4f));
                break;
        }

        g2.dispose();
    }
}
