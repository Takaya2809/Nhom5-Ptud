package gui;

import dao.CaTrucDAO;
import dao.HoSoBenhAnDAO;
import dao.LichHenDAO;
import dao.NhanVienDAO;
import dao.ThongKeDAO;
import entity.HoSoBenhAn;
import entity.LichHen;
import entity.NhanVien;
import entity.PhanCongCaTruc;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Giao diện Dashboard Tổng quan y tế - Phòng Khám Nha Khoa ABC
 * Tích hợp Background.jpg cho Hero Banner trang nhã, KPI thời gian thực, điều phối hàng đợi khám chữa bệnh
 */
public class PanelDashboardModern extends JPanel {

    private Consumer<String> navigationCallback;
    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();

    private BufferedImage imgBackground;

    private JPanel pnlKPIContainer;
    private DefaultTableModel modelQueue;
    private JTable tableQueue;
    private List<LichHen> allAppointments = new ArrayList<>();
    private String currentFilter = "ALL";

    private JButton btnAll, btnWaiting, btnInChair, btnDone;
    private JPanel pnlDocList, pnlServList;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public PanelDashboardModern(Consumer<String> navCallback) {
        this.navigationCallback = navCallback;
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        loadBackgroundImage();

        // 1. HEADER WELCOME & QUICK ACTIONS (VỚI BACKGROUND.JPG)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. MAIN SCROLLABLE CONTENT
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setOpaque(false);

        // A. KPI Cards Section
        pnlKPIContainer = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlKPIContainer.setOpaque(false);
        pnlKPIContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        pnlKPIContainer.setPreferredSize(new Dimension(1000, 115));
        pnlBody.add(pnlKPIContainer);
        pnlBody.add(Box.createVerticalStrut(16));

        // B. Dual Panels Section (Queue Table + Doctor & Service Widget)
        pnlBody.add(createDualColumnsSection());

        JScrollPane scrollPane = new JScrollPane(pnlBody);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Load data initially from database
        refreshData();
    }

    private void loadBackgroundImage() {
        String[] paths = {
            "PhanMem_PhongKham_NhaKhoa/images/Background.jpg",
            "images/Background.jpg",
            "../images/Background.jpg"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    imgBackground = ImageIO.read(f);
                    break;
                } catch (Exception ignored) {}
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel pnlHero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                // Bo góc cho Hero Banner
                RoundRectangle2D roundedRect = new RoundRectangle2D.Double(0, 0, w, h, 14, 14);
                g2.setClip(roundedRect);

                if (imgBackground != null) {
                    double imgW = imgBackground.getWidth();
                    double imgH = imgBackground.getHeight();
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(imgBackground, x, y, drawW, drawH, null);

                    // Lớp phủ Gradient chuẩn y tế tạo độ sắc nét tuyệt đối cho văn bản
                    GradientPaint grad = new GradientPaint(
                            0, 0, new Color(15, 23, 42, 225),
                            w, 0, new Color(30, 58, 138, 235));
                    g2.setPaint(grad);
                    g2.fillRect(0, 0, w, h);
                } else {
                    GradientPaint grad = new GradientPaint(
                            0, 0, ModernUITheme.SIDEBAR_BG,
                            w, h, ModernUITheme.PRIMARY_DARK);
                    g2.setPaint(grad);
                    g2.fillRect(0, 0, w, h);
                }

                g2.setClip(null);
                g2.setColor(ModernUITheme.BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 14, 14));
                g2.dispose();
            }
        };

        pnlHero.setOpaque(false);
        pnlHero.setLayout(new BorderLayout(15, 0));
        pnlHero.setBorder(new EmptyBorder(18, 24, 18, 24));
        pnlHero.setPreferredSize(new Dimension(0, 96));

        JPanel pnlTitles = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlTitles.setOpaque(false);

        JLabel lblTitle = new JLabel("TRUNG TÂM ĐIỀU HÀNH PHÒNG KHÁM NHA KHOA ABC");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(
                "Giám sát chỉ số khám chữa bệnh, doanh thu và điều phối hàng đợi bệnh nhân thời gian thực từ CSDL");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 242, 254));

        pnlTitles.add(lblTitle);
        pnlTitles.add(lblSub);
        pnlHero.add(pnlTitles, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        JButton btnNewPatient = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PATIENT, 15, Color.WHITE), "Tiếp đón bệnh nhân", ModernUITheme.PRIMARY, Color.WHITE);
        btnNewPatient.addActionListener(e -> {
            if (navigationCallback != null)
                navigationCallback.accept("BENH_NHAN");
        });

        JButton btnNewInvoice = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PAYMENT, 15, Color.WHITE), "Lập hóa đơn khám", ModernUITheme.ACCENT, Color.WHITE);
        btnNewInvoice.addActionListener(e -> {
            if (navigationCallback != null)
                navigationCallback.accept("THANH_TOAN");
        });

        JButton btnRefresh = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, Color.WHITE), "Cập nhật CSDL", Color.WHITE);
        btnRefresh.addActionListener(e -> refreshData());

        pnlActions.add(btnRefresh);
        pnlActions.add(btnNewPatient);
        pnlActions.add(btnNewInvoice);

        pnlHero.add(pnlActions, BorderLayout.EAST);
        return pnlHero;
    }

    private JPanel createDualColumnsSection() {
        JPanel pnlDual = new JPanel(new BorderLayout(16, 0));
        pnlDual.setOpaque(false);

        // LEFT COLUMN: BẢNG ĐIỀU PHỐI HÀNG ĐỢI BỆNH NHÂN (65% width)
        JPanel pnlLeftCard = ModernUITheme.createCardPanel();
        pnlLeftCard.setLayout(new BorderLayout(0, 12));

        // Header bảng
        JPanel pnlQueueHeader = new JPanel(new BorderLayout());
        pnlQueueHeader.setOpaque(false);

        JLabel lblQueueTitle = new JLabel("HÀNG ĐỢI ĐIỀU TRỊ TRONG NGÀY");
        lblQueueTitle.setFont(ModernUITheme.FONT_TITLE);
        lblQueueTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlFilters.setOpaque(false);

        btnAll = ModernUITheme.createButton("Tất cả", ModernUITheme.PRIMARY, Color.WHITE);
        btnWaiting = ModernUITheme.createOutlineButton("Chờ khám", ModernUITheme.WARNING);
        btnInChair = ModernUITheme.createOutlineButton("Đang khám", ModernUITheme.PRIMARY);
        btnDone = ModernUITheme.createOutlineButton("Đã hoàn tất", ModernUITheme.SUCCESS);

        btnAll.addActionListener(e -> { currentFilter = "ALL"; renderQueueTable(); });
        btnWaiting.addActionListener(e -> { currentFilter = "WAITING"; renderQueueTable(); });
        btnInChair.addActionListener(e -> { currentFilter = "IN_CHAIR"; renderQueueTable(); });
        btnDone.addActionListener(e -> { currentFilter = "DONE"; renderQueueTable(); });

        pnlFilters.add(btnAll);
        pnlFilters.add(btnWaiting);
        pnlFilters.add(btnInChair);
        pnlFilters.add(btnDone);

        pnlQueueHeader.add(lblQueueTitle, BorderLayout.WEST);
        pnlQueueHeader.add(pnlFilters, BorderLayout.EAST);
        pnlLeftCard.add(pnlQueueHeader, BorderLayout.NORTH);

        // Table
        String[] cols = { "Mã Lịch Hẹn", "Mã BN", "Họ và Tên", "Bác sĩ phụ trách", "Nội dung khám", "Giờ hẹn", "Trạng thái" };
        modelQueue = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableQueue = new JTable(modelQueue);
        ModernUITheme.formatModernTable(tableQueue);

        JScrollPane scrollTable = new JScrollPane(tableQueue);
        scrollTable.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        scrollTable.setPreferredSize(new Dimension(650, 320));
        pnlLeftCard.add(scrollTable, BorderLayout.CENTER);

        // Queue Control Bar (Bottom of Left Card)
        JPanel pnlQueueBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlQueueBottom.setOpaque(false);
        JButton btnMoiVao = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Mời vào khám", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHoanTat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Xác nhận hoàn tất", ModernUITheme.SUCCESS, Color.WHITE);
        JButton btnChiTiet = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.HISTORY, 14, ModernUITheme.TEXT_MUTED), "Xem bệnh án chi tiết", ModernUITheme.TEXT_MUTED);

        btnMoiVao.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                String maLH = modelQueue.getValueAt(row, 0).toString();
                String tenBN = modelQueue.getValueAt(row, 2).toString();
                boolean ok = lichHenDAO.capNhatTrangThai(maLH, "Đang khám");
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Đã gọi bệnh nhân [" + tenBN + "] vào ghế khám (Cập nhật CSDL thành công)!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách hàng đợi!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnHoanTat.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                String maLH = modelQueue.getValueAt(row, 0).toString();
                String tenBN = modelQueue.getValueAt(row, 2).toString();
                boolean ok = lichHenDAO.capNhatTrangThai(maLH, "Đã hoàn tất");
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Bệnh nhân [" + tenBN + "] đã hoàn thành điều trị (Cập nhật CSDL thành công)!", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách hàng đợi!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnChiTiet.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                String maBN = modelQueue.getValueAt(row, 1).toString();
                String tenBN = modelQueue.getValueAt(row, 2).toString();
                hienThiBenhAnChiTiet(maBN, tenBN);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân để xem bệnh án!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlQueueBottom.add(btnChiTiet);
        pnlQueueBottom.add(btnMoiVao);
        pnlQueueBottom.add(btnHoanTat);
        pnlLeftCard.add(pnlQueueBottom, BorderLayout.SOUTH);

        pnlDual.add(pnlLeftCard, BorderLayout.CENTER);

        // RIGHT COLUMN: BÁC SĨ TRỰC & DỊCH VỤ NHA KHOA NỔI BẬT (35% width)
        JPanel pnlRightColumn = new JPanel();
        pnlRightColumn.setLayout(new BoxLayout(pnlRightColumn, BoxLayout.Y_AXIS));
        pnlRightColumn.setOpaque(false);
        pnlRightColumn.setPreferredSize(new Dimension(360, 0));

        // Widget 1: Bác sĩ trực
        JPanel cardDoctors = ModernUITheme.createCardPanel();
        cardDoctors.setLayout(new BorderLayout(0, 10));

        JPanel pnlDocHeader = new JPanel(new BorderLayout());
        pnlDocHeader.setOpaque(false);
        JLabel lblDocTitle = new JLabel("BÁC SĨ ĐANG TRỰC CA");
        lblDocTitle.setFont(ModernUITheme.FONT_TITLE);
        lblDocTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblDocBadge = ModernUITheme.createBadge("Hôm nay", ModernUITheme.PRIMARY_LIGHT, ModernUITheme.PRIMARY);
        pnlDocHeader.add(lblDocTitle, BorderLayout.WEST);
        pnlDocHeader.add(lblDocBadge, BorderLayout.EAST);
        cardDoctors.add(pnlDocHeader, BorderLayout.NORTH);

        pnlDocList = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlDocList.setOpaque(false);
        cardDoctors.add(pnlDocList, BorderLayout.CENTER);

        pnlRightColumn.add(cardDoctors);
        pnlRightColumn.add(Box.createVerticalStrut(16));

        // Widget 2: Dịch vụ điều trị phổ biến
        JPanel cardServices = ModernUITheme.createCardPanel();
        cardServices.setLayout(new BorderLayout(0, 10));

        JLabel lblServTitle = new JLabel("DỊCH VỤ NHA KHOA TRỌNG ĐIỂM");
        lblServTitle.setFont(ModernUITheme.FONT_TITLE);
        lblServTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardServices.add(lblServTitle, BorderLayout.NORTH);

        pnlServList = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlServList.setOpaque(false);
        cardServices.add(pnlServList, BorderLayout.CENTER);

        pnlRightColumn.add(cardServices);

        pnlDual.add(pnlRightColumn, BorderLayout.EAST);
        return pnlDual;
    }

    private void renderQueueTable() {
        modelQueue.setRowCount(0);
        int cntWaiting = 0, cntInChair = 0, cntDone = 0;

        for (LichHen lh : allAppointments) {
            String tt = lh.getTrangThai() != null ? lh.getTrangThai() : "Chờ khám";
            String ttLower = tt.toLowerCase();

            if (ttLower.contains("chờ") || ttLower.contains("xác nhận") || ttLower.contains("lên lịch")) {
                cntWaiting++;
            } else if (ttLower.contains("khám") || ttLower.contains("trị")) {
                cntInChair++;
            } else if (ttLower.contains("hoàn") || ttLower.contains("xong")) {
                cntDone++;
            }

            boolean show = false;
            if ("ALL".equals(currentFilter)) {
                show = true;
            } else if ("WAITING".equals(currentFilter)) {
                show = ttLower.contains("chờ") || ttLower.contains("xác nhận") || ttLower.contains("lên lịch");
            } else if ("IN_CHAIR".equals(currentFilter)) {
                show = ttLower.contains("khám") || ttLower.contains("trị");
            } else if ("DONE".equals(currentFilter)) {
                show = ttLower.contains("hoàn") || ttLower.contains("xong");
            }

            if (show) {
                String gio = lh.getGioHen() != null ? lh.getGioHen().toString().substring(0, 5) : "08:30";
                modelQueue.addRow(new Object[] {
                        lh.getMaLichHen(),
                        lh.getMaBN(),
                        lh.getTenBenhNhan() != null ? lh.getTenBenhNhan() : lh.getMaBN(),
                        lh.getTenNhaSi() != null ? lh.getTenNhaSi() : "BS phụ trách",
                        lh.getLyDoKham() != null ? lh.getLyDoKham() : "Khám tổng quát",
                        gio,
                        tt
                });
            }
        }

        btnAll.setText("Tất cả (" + allAppointments.size() + ")");
        btnWaiting.setText("Chờ khám (" + cntWaiting + ")");
        btnInChair.setText("Đang khám (" + cntInChair + ")");
        btnDone.setText("Đã hoàn tất (" + cntDone + ")");
    }

    private void hienThiBenhAnChiTiet(String maBN, String tenBN) {
        List<HoSoBenhAn> dsHoSo = hoSoBenhAnDAO.getHoSoByBenhNhan(maBN);
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                  HỒ SƠ BỆNH ÁN ĐIỆN TỬ NHA KHOA                        \n");
        sb.append("========================================================================\n");
        sb.append("Mã bệnh nhân: ").append(maBN).append(" | Họ và tên: ").append(tenBN).append("\n");
        sb.append("Tổng số ca/lần điều trị đã lưu trong CSDL: ").append(dsHoSo.size()).append("\n");
        sb.append("------------------------------------------------------------------------\n\n");

        if (dsHoSo.isEmpty()) {
            sb.append("Chưa có hồ sơ bệnh án nào được ghi nhận cho bệnh nhân này.\n");
            sb.append("(Bác sĩ có thể tạo bệnh án mới khi hoàn tất ca khám)\n");
        } else {
            for (int i = 0; i < dsHoSo.size(); i++) {
                HoSoBenhAn hs = dsHoSo.get(i);
                sb.append("[").append(i + 1).append("] Mã bệnh án: ").append(hs.getMaHoSo()).append("\n");
                sb.append("    - Ngày khám: ").append(hs.getNgayKham() != null ? hs.getNgayKham().toString() : "Hôm nay").append("\n");
                sb.append("    - Bác sĩ điều trị: ").append(hs.getTenNhaSi() != null ? hs.getTenNhaSi() : hs.getMaNhaSi()).append("\n");
                sb.append("    - Triệu chứng lâm sàng: ").append(hs.getTrieuChung()).append("\n");
                sb.append("    - Chẩn đoán y khoa: ").append(hs.getChanDoan()).append("\n");
                sb.append("    - Kết quả điều trị: ").append(hs.getKetQuaDieuTri() != null ? hs.getKetQuaDieuTri() : "Tốt").append("\n");
                sb.append("    - Lời dặn / Ghi chú: ").append(hs.getGhiChu() != null ? hs.getGhiChu() : "Tái khám theo hẹn").append("\n");
                sb.append("    - Trạng thái hồ sơ: ").append(hs.getTrangThai()).append("\n");
                sb.append("------------------------------------------------------------------------\n");
            }
        }

        JTextArea ta = new JTextArea(sb.toString(), 18, 55);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setCaretPosition(0);

        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Bệnh án điện tử: " + tenBN + " (" + maBN + ")", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel createDoctorItem(String name, String spec, String status, Color statusColor) {
        JPanel pnl = new JPanel(new BorderLayout(8, 2));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlInfo.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(ModernUITheme.FONT_BOLD);
        lblName.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSpec = new JLabel(spec);
        lblSpec.setFont(ModernUITheme.FONT_TINY);
        lblSpec.setForeground(ModernUITheme.TEXT_MUTED);

        pnlInfo.add(lblName);
        pnlInfo.add(lblSpec);

        JLabel lblBadge = ModernUITheme.createBadge(status,
                new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30), statusColor);

        pnl.add(pnlInfo, BorderLayout.CENTER);
        pnl.add(lblBadge, BorderLayout.EAST);
        return pnl;
    }

    private JPanel createServiceProgressItem(String title, String price, int percent, Color barColor) {
        JPanel pnl = new JPanel(new BorderLayout(5, 4));
        pnl.setOpaque(false);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(ModernUITheme.FONT_BOLD);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(ModernUITheme.FONT_TINY);
        lblPrice.setForeground(ModernUITheme.TEXT_MUTED);

        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(lblPrice, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(percent);
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(100, 6));

        pnl.add(pnlTop, BorderLayout.NORTH);
        pnl.add(bar, BorderLayout.SOUTH);
        return pnl;
    }

    public void refreshData() {
        // 1. Cập nhật số liệu KPI từ CSDL
        pnlKPIContainer.removeAll();
        double tongDT = thongKeDAO.getTongDoanhThu();
        int tongLK = thongKeDAO.getTongLuotKham();
        int tongBN = thongKeDAO.getTongBenhNhan();
        int canhBaoVT = thongKeDAO.getSoVatTuCanhBao();

        pnlKPIContainer.add(ModernUITheme.createKPICard("LƯỢT KHÁM HÔM NAY", tongLK + " ca", "Tổng ca điều trị trong CSDL",
                ModernUITheme.PRIMARY, new AppIcon(AppIcon.Type.SERVICE, 24, ModernUITheme.PRIMARY)));
        pnlKPIContainer.add(ModernUITheme.createKPICard("DOANH THU ĐÃ THU", df.format(tongDT), "Tổng doanh thu thực tế",
                ModernUITheme.SUCCESS, new AppIcon(AppIcon.Type.PAYMENT, 24, ModernUITheme.SUCCESS)));
        pnlKPIContainer.add(ModernUITheme.createKPICard("HỒ SƠ BỆNH NHÂN", tongBN + " người", "Bệnh nhân đã đăng ký",
                ModernUITheme.ACCENT, new AppIcon(AppIcon.Type.PATIENT, 24, ModernUITheme.ACCENT)));
        pnlKPIContainer.add(ModernUITheme.createKPICard("CẢNH BÁO KHO DƯỢC", canhBaoVT + " mặt hàng", "Cần bổ sung vật tư/thuốc",
                ModernUITheme.DANGER, new AppIcon(AppIcon.Type.WARNING, 24, ModernUITheme.DANGER)));
        pnlKPIContainer.revalidate();
        pnlKPIContainer.repaint();

        // 2. Tải hàng đợi lịch hẹn từ CSDL
        allAppointments = lichHenDAO.getAllLichHen();
        renderQueueTable();

        // 3. Tải danh sách bác sĩ trực từ CSDL
        pnlDocList.removeAll();
        List<PhanCongCaTruc> dsPhanCong = caTrucDAO.getAllPhanCong();
        int docCount = 0;
        for (PhanCongCaTruc pc : dsPhanCong) {
            String vt = pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "";
            if (vt.contains("Bác sĩ") || vt.contains("Nha sĩ") || pc.getMaNV().startsWith("BS") || pc.getMaNV().startsWith("NV")) {
                pnlDocList.add(createDoctorItem(
                        pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV(),
                        pc.getTenCa() != null ? pc.getTenCa() : "Ca trực",
                        "Đã điểm danh".equals(pc.getTrangThaiDiemDanh()) ? "Đang trực" : "Chưa đến",
                        "Đã điểm danh".equals(pc.getTrangThaiDiemDanh()) ? ModernUITheme.SUCCESS : ModernUITheme.WARNING));
                docCount++;
                if (docCount >= 3) break;
            }
        }
        if (docCount == 0) {
            pnlDocList.add(createDoctorItem("BS. CKII Nguyễn Văn An", "Ca Sáng (07:30 - 11:30)", "Đang trực", ModernUITheme.SUCCESS));
            pnlDocList.add(createDoctorItem("ThS. BS Lê Thị Thảo", "Ca Chiều (13:30 - 17:30)", "Chưa đến", ModernUITheme.WARNING));
            pnlDocList.add(createDoctorItem("BS. Trần Minh Trí", "Ca Tối (17:30 - 20:30)", "Chưa đến", ModernUITheme.WARNING));
        }
        pnlDocList.revalidate();
        pnlDocList.repaint();

        // 4. Dịch vụ nha khoa phổ biến
        pnlServList.removeAll();
        pnlServList.add(createServiceProgressItem("Cấy ghép Implant Straumann", "22.000.000 VNĐ", 85, ModernUITheme.PRIMARY));
        pnlServList.add(createServiceProgressItem("Niềng răng mắc cài tự buộc", "30.000.000 VNĐ", 68, ModernUITheme.ACCENT));
        pnlServList.add(createServiceProgressItem("Bọc răng toàn sứ Cercon", "4.500.000 VNĐ", 92, ModernUITheme.SUCCESS));
        pnlServList.revalidate();
        pnlServList.repaint();
    }
}
