package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Component Date Picker hiện đại thuần Java Swing.
 * - Định dạng chuẩn Việt Nam: dd/MM/yyyy
 * - Lịch chọn trực quan dạng popup gắn liền ô nhập
 * - Ràng buộc ngày linh hoạt (không chọn tương lai cho ngày sinh, v.v.)
 * - Chuyển đổi chính xác sang java.sql.Date không bị lỗi timezone
 */
public class DatePickerField extends JPanel {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField txtDate;
    private JButton btnCalendar;
    private JPopupMenu popupCalendar;

    private LocalDate selectedDate;
    private LocalDate displayMonth;
    private LocalDate maxDate;
    private LocalDate minDate;

    private JPanel pnlDaysGrid;
    private JLabel lblMonthYear;

    public DatePickerField() {
        this(null);
    }

    public DatePickerField(LocalDate defaultDate) {
        setLayout(new BorderLayout(4, 0));
        setOpaque(false);

        txtDate = new JTextField();
        txtDate.setFont(ModernUITheme.FONT_REGULAR);
        txtDate.setForeground(ModernUITheme.TEXT_MAIN);
        txtDate.setCaretColor(ModernUITheme.PRIMARY);
        txtDate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 8)));

        // Nút biểu tượng lịch
        btnCalendar = new JButton(new AppIcon(AppIcon.Type.CALENDAR, 16, ModernUITheme.PRIMARY));
        btnCalendar.setToolTipText("Mở lịch chọn ngày (dd/MM/yyyy)");
        btnCalendar.setFocusPainted(false);
        btnCalendar.setContentAreaFilled(false);
        btnCalendar.setBorder(new EmptyBorder(4, 6, 4, 6));
        btnCalendar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCalendar.addActionListener(e -> toggleCalendarPopup());

        // Validate khi người dùng gõ phím hoặc rời ô
        txtDate.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                parseInputText();
            }
        });

        txtDate.addActionListener(e -> parseInputText());

        add(txtDate, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);

        if (defaultDate != null) {
            setDate(defaultDate);
        } else {
            this.displayMonth = LocalDate.now();
        }

        buildCalendarPopup();
    }

    public void setMaxDate(LocalDate max) {
        this.maxDate = max;
    }

    public void setMinDate(LocalDate min) {
        this.minDate = min;
    }

    public LocalDate getLocalDate() {
        return selectedDate;
    }

    public Date getDate() {
        return selectedDate != null ? Date.valueOf(selectedDate) : null;
    }

    public void setDate(LocalDate date) {
        if (date != null) {
            if (maxDate != null && date.isAfter(maxDate)) {
                date = maxDate;
            }
            if (minDate != null && date.isBefore(minDate)) {
                date = minDate;
            }
            this.selectedDate = date;
            this.displayMonth = date;
            txtDate.setText(date.format(FORMATTER));
            txtDate.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 8)));
        } else {
            clear();
        }
    }

    public void setDate(Date sqlDate) {
        if (sqlDate != null) {
            setDate(sqlDate.toLocalDate());
        } else {
            clear();
        }
    }

    public void clear() {
        this.selectedDate = null;
        txtDate.setText("");
        txtDate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 8)));
    }

    public boolean isValidDate() {
        String text = txtDate.getText().trim();
        if (text.isEmpty()) {
            return true; // Cho phép rỗng nếu không bắt buộc
        }
        try {
            LocalDate d = LocalDate.parse(text, FORMATTER);
            if (maxDate != null && d.isAfter(maxDate)) return false;
            if (minDate != null && d.isBefore(minDate)) return false;
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private void parseInputText() {
        String text = txtDate.getText().trim();
        if (text.isEmpty()) {
            selectedDate = null;
            txtDate.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 8)));
            return;
        }

        // Hỗ trợ cả định dạng yyyy-MM-dd khi dán từ CSDL hoặc dd/MM/yyyy
        LocalDate parsed = null;
        try {
            if (text.contains("-")) {
                parsed = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                parsed = LocalDate.parse(text, FORMATTER);
            }
        } catch (Exception ex) {
            parsed = null;
        }

        if (parsed != null) {
            if (maxDate != null && parsed.isAfter(maxDate)) {
                JOptionPane.showMessageDialog(this,
                        "Ngày không được lớn hơn " + maxDate.format(FORMATTER) + "!",
                        "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
                setDate(selectedDate);
                return;
            }
            if (minDate != null && parsed.isBefore(minDate)) {
                JOptionPane.showMessageDialog(this,
                        "Ngày không được nhỏ hơn " + minDate.format(FORMATTER) + "!",
                        "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
                setDate(selectedDate);
                return;
            }
            setDate(parsed);
        } else {
            txtDate.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernUITheme.DANGER, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 8)));
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập ngày theo đúng định dạng ngày/tháng/năm (dd/MM/yyyy)!\nVí dụ: 15/01/1990",
                    "Định dạng ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void toggleCalendarPopup() {
        if (popupCalendar.isVisible()) {
            popupCalendar.setVisible(false);
        } else {
            if (selectedDate != null) {
                displayMonth = selectedDate;
            } else if (displayMonth == null) {
                displayMonth = LocalDate.now();
            }
            updateCalendarGrid();
            popupCalendar.show(this, 0, getHeight() + 2);
        }
    }

    private void buildCalendarPopup() {
        popupCalendar = new JPopupMenu();
        popupCalendar.setBackground(Color.WHITE);
        popupCalendar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1),
                new EmptyBorder(8, 10, 8, 10)));
        popupCalendar.setLayout(new BorderLayout(0, 8));

        // Header: <  Tháng MM/yyyy  >
        JPanel pnlHeader = new JPanel(new BorderLayout(6, 0));
        pnlHeader.setOpaque(false);

        JButton btnPrev = new JButton("<");
        btnPrev.setFont(ModernUITheme.FONT_BOLD);
        btnPrev.setForeground(ModernUITheme.TEXT_MUTED);
        btnPrev.setContentAreaFilled(false);
        btnPrev.setBorder(new EmptyBorder(3, 8, 3, 8));
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrev.addActionListener(e -> {
            displayMonth = displayMonth.minusMonths(1);
            updateCalendarGrid();
        });

        JButton btnNext = new JButton(">");
        btnNext.setFont(ModernUITheme.FONT_BOLD);
        btnNext.setForeground(ModernUITheme.TEXT_MUTED);
        btnNext.setContentAreaFilled(false);
        btnNext.setBorder(new EmptyBorder(3, 8, 3, 8));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> {
            displayMonth = displayMonth.plusMonths(1);
            updateCalendarGrid();
        });

        lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(ModernUITheme.FONT_BOLD);
        lblMonthYear.setForeground(ModernUITheme.PRIMARY_DARK);

        pnlHeader.add(btnPrev, BorderLayout.WEST);
        pnlHeader.add(lblMonthYear, BorderLayout.CENTER);
        pnlHeader.add(btnNext, BorderLayout.EAST);
        popupCalendar.add(pnlHeader, BorderLayout.NORTH);

        // Grid tiêu đề các thứ và các ô ngày
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 4));
        pnlCenter.setOpaque(false);

        JPanel pnlWeekDays = new JPanel(new GridLayout(1, 7, 2, 2));
        pnlWeekDays.setOpaque(false);
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(ModernUITheme.FONT_TINY);
            lbl.setForeground("CN".equals(d) ? ModernUITheme.DANGER : ModernUITheme.TEXT_MUTED);
            pnlWeekDays.add(lbl);
        }
        pnlCenter.add(pnlWeekDays, BorderLayout.NORTH);

        pnlDaysGrid = new JPanel(new GridLayout(6, 7, 2, 2));
        pnlDaysGrid.setOpaque(false);
        pnlDaysGrid.setPreferredSize(new Dimension(240, 160));
        pnlCenter.add(pnlDaysGrid, BorderLayout.CENTER);

        popupCalendar.add(pnlCenter, BorderLayout.CENTER);

        // Footer buttons: Hôm nay, Xóa, Đóng
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        pnlFooter.setOpaque(false);

        JButton btnToday = ModernUITheme.createOutlineButton("Hôm nay", ModernUITheme.PRIMARY);
        btnToday.setFont(ModernUITheme.FONT_TINY);
        btnToday.setBorder(new EmptyBorder(3, 6, 3, 6));
        btnToday.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            if (maxDate != null && today.isAfter(maxDate)) {
                setDate(maxDate);
            } else {
                setDate(today);
            }
            popupCalendar.setVisible(false);
        });

        JButton btnClear = ModernUITheme.createOutlineButton("Xóa", ModernUITheme.TEXT_MUTED);
        btnClear.setFont(ModernUITheme.FONT_TINY);
        btnClear.setBorder(new EmptyBorder(3, 6, 3, 6));
        btnClear.addActionListener(e -> {
            clear();
            popupCalendar.setVisible(false);
        });

        JButton btnClose = ModernUITheme.createOutlineButton("Đóng", ModernUITheme.TEXT_MUTED);
        btnClose.setFont(ModernUITheme.FONT_TINY);
        btnClose.setBorder(new EmptyBorder(3, 6, 3, 6));
        btnClose.addActionListener(e -> popupCalendar.setVisible(false));

        pnlFooter.add(btnToday);
        pnlFooter.add(btnClear);
        pnlFooter.add(btnClose);
        popupCalendar.add(pnlFooter, BorderLayout.SOUTH);
    }

    private void updateCalendarGrid() {
        if (displayMonth == null) displayMonth = LocalDate.now();

        lblMonthYear.setText(String.format("Tháng %02d / %d", displayMonth.getMonthValue(), displayMonth.getYear()));
        pnlDaysGrid.removeAll();

        YearMonth ym = YearMonth.of(displayMonth.getYear(), displayMonth.getMonth());
        LocalDate firstDay = ym.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // 1 = Thứ hai, 7 = Chủ nhật
        int lengthOfMonth = ym.lengthOfMonth();

        LocalDate today = LocalDate.now();

        // Ô trống đầu tháng
        for (int i = 1; i < dayOfWeek; i++) {
            JLabel empty = new JLabel("");
            pnlDaysGrid.add(empty);
        }

        // Các ngày trong tháng
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = ym.atDay(day);
            JButton btnDay = new JButton(String.valueOf(day));
            btnDay.setFont(ModernUITheme.FONT_TINY);
            btnDay.setFocusPainted(false);
            btnDay.setBorder(new EmptyBorder(2, 2, 2, 2));
            btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boolean isSelected = selectedDate != null && selectedDate.equals(date);
            boolean isToday = date.equals(today);
            boolean isDisabled = (maxDate != null && date.isAfter(maxDate)) || (minDate != null && date.isBefore(minDate));

            if (isDisabled) {
                btnDay.setEnabled(false);
                btnDay.setForeground(new Color(203, 213, 225));
                btnDay.setBackground(Color.WHITE);
            } else if (isSelected) {
                btnDay.setBackground(ModernUITheme.PRIMARY);
                btnDay.setForeground(Color.WHITE);
                btnDay.setFont(ModernUITheme.FONT_BOLD);
            } else if (isToday) {
                btnDay.setBackground(ModernUITheme.PRIMARY_LIGHT);
                btnDay.setForeground(ModernUITheme.PRIMARY_DARK);
                btnDay.setBorder(BorderFactory.createLineBorder(ModernUITheme.PRIMARY, 1));
            } else {
                btnDay.setBackground(Color.WHITE);
                btnDay.setForeground(ModernUITheme.TEXT_MAIN);
            }

            if (!isDisabled) {
                btnDay.addActionListener(e -> {
                    setDate(date);
                    popupCalendar.setVisible(false);
                });
            }

            pnlDaysGrid.add(btnDay);
        }

        // Bổ sung các ô còn lại để đủ 42 ô (6 tuần x 7 ngày)
        int filled = (dayOfWeek - 1) + lengthOfMonth;
        for (int i = filled; i < 42; i++) {
            JLabel empty = new JLabel("");
            pnlDaysGrid.add(empty);
        }

        pnlDaysGrid.revalidate();
        pnlDaysGrid.repaint();
    }
}

