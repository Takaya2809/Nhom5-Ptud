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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Giao diện Dashboard Tổng quan y tế - Phòng Khám Nha Khoa ABC
 * Kết nối 100% CSDL SQL Server: KPI thời gian thực, điều phối hàng đợi khám chữa bệnh, bác sĩ trực
 */
public class PanelDashboardModern extends JPanel {

    private Consumer<String> navigationCallback;
    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private LichHenDAO lichHenDAO = new LichHenDAO();
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private HoSoBenhAnDAO hoSoBenhAnDAO = new HoSoBenhAnDAO();

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

        // 1. HEADER WELCOME & QUICK ACTIONS
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

    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitles = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlTitles.setOpaque(false);

        JLabel lblTitle = new JLabel("TỔNG QUAN ĐIỀU HÀNH PHÒNG KHÁM");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel(
                "Giám sát chỉ số khám chữa bệnh, doanh thu và điều phối hàng đợi bệnh nhân thời gian thực từ CSDL");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);

        pnlTitles.add(lblTitle);
        pnlTitles.add(lblSub);
        pnlHeader.add(pnlTitles, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        JButton btnNewPatient = ModernUITheme.createButton("Tiếp đón bệnh nhân", ModernUITheme.PRIMARY, Color.WHITE);
        btnNewPatient.addActionListener(e -> {
            if (navigationCallback != null)
                navigationCallback.accept("BENH_NHAN");
        });

        JButton btnNewInvoice = ModernUITheme.createButton("Lập hóa đơn khám", ModernUITheme.ACCENT, Color.WHITE);
        btnNewInvoice.addActionListener(e -> {
            if (navigationCallback != null)
                navigationCallback.accept("THANH_TOAN");
        });

        JButton btnRefresh = ModernUITheme.createOutlineButton("Cập nhật CSDL", ModernUITheme.TEXT_MUTED);
        btnRefresh.addActionListener(e -> refreshData());

        pnlActions.add(btnRefresh);
        pnlActions.add(btnNewPatient);
        pnlActions.add(btnNewInvoice);

        pnlHeader.add(pnlActions, BorderLayout.EAST);
        return pnlHeader;
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
        JButton btnMoiVao = ModernUITheme.createButton("Mời vào khám", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnHoanTat = ModernUITheme.createButton("Xác nhận hoàn tất", ModernUITheme.SUCCESS, Color.WHITE);
        JButton btnChiTiet = ModernUITheme.createOutlineButton("Xem bệnh án chi tiết", ModernUITheme.TEXT_MUTED);

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
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái trong CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong hàng đợi!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnHoanTat.addActionListener(e -> {
            int row = tableQueue.getSelectedRow();
            if (row >= 0) {
                String maLH = modelQueue.getValueAt(row, 0).toString();
                String tenBN = modelQueue.getValueAt(row, 2).toString();
                boolean ok = lichHenDAO.capNhatTrangThai(maLH, "Hoàn thành");
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Ca điều trị của [" + tenBN + "] đã hoàn tất. Chuyển thông tin sang bộ phận Thu ngân!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái trong CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân cần hoàn tất!", "Nhắc nhở",
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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bệnh nhân trong danh sách để xem bệnh án!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlQueueBottom.add(btnChiTiet);
        pnlQueueBottom.add(btnMoiVao);
        pnlQueueBottom.add(btnHoanTat);
        pnlLeftCard.add(pnlQueueBottom, BorderLayout.SOUTH);

        pnlDual.add(pnlLeftCard, BorderLayout.CENTER);

        // RIGHT COLUMN: 2 TIỆN ÍCH (Bác sĩ trực & Top dịch vụ) (35% width)
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(new Dimension(360, 400));

        // Widget 1: Bác sĩ trực hôm nay
        JPanel cardDoctors = ModernUITheme.createCardPanel();
        cardDoctors.setLayout(new BorderLayout(0, 10));

        JLabel lblDocTitle = new JLabel("BÁC SĨ TRỰC HÔM NAY");
        lblDocTitle.setFont(ModernUITheme.FONT_TITLE);
        lblDocTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardDoctors.add(lblDocTitle, BorderLayout.NORTH);

        pnlDocList = new JPanel(new GridLayout(0, 1, 0, 8));
        pnlDocList.setOpaque(false);
        cardDoctors.add(pnlDocList, BorderLayout.CENTER);
        pnlRight.add(cardDoctors);
        pnlRight.add(Box.createVerticalStrut(16));

        // Widget 2: Dịch vụ nổi bật
        JPanel cardServices = ModernUITheme.createCardPanel();
        cardServices.setLayout(new BorderLayout(0, 10));

        JLabel lblServTitle = new JLabel("TOP DỊCH VỤ CHỈ ĐỊNH CAO");
        lblServTitle.setFont(ModernUITheme.FONT_TITLE);
        lblServTitle.setForeground(ModernUITheme.TEXT_MAIN);
        cardServices.add(lblServTitle, BorderLayout.NORTH);

        pnlServList = new JPanel(new GridLayout(0, 1, 0, 8));
        pnlServList.setOpaque(false);
        cardServices.add(pnlServList, BorderLayout.CENTER);
        pnlRight.add(cardServices);

        pnlDual.add(pnlRight, BorderLayout.EAST);

        return pnlDual;
    }

    private void renderQueueTable() {
        modelQueue.setRowCount(0);
        int cntWaiting = 0;
        int cntInChair = 0;
        int cntDone = 0;

        for (LichHen lh : allAppointments) {
            String tt = lh.getTrangThai() != null ? lh.getTrangThai() : "Đã lên lịch";
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
                ModernUITheme.PRIMARY, "🩺"));
        pnlKPIContainer.add(ModernUITheme.createKPICard("DOANH THU ĐÃ THU", df.format(tongDT), "Tổng doanh thu thực tế",
                ModernUITheme.SUCCESS, "💰"));
        pnlKPIContainer.add(ModernUITheme.createKPICard("HỒ SƠ BỆNH NHÂN", tongBN + " người", "Bệnh nhân đã đăng ký",
                ModernUITheme.ACCENT, "👥"));
        pnlKPIContainer.add(ModernUITheme.createKPICard("CẢNH BÁO KHO DƯỢC", canhBaoVT + " mặt hàng", "Cần bổ sung vật tư/thuốc",
                ModernUITheme.DANGER, "⚠️"));
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
            List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();
            for (NhanVien nv : dsNV) {
                if (nv.getChucDanh() != null && (nv.getChucDanh().contains("Bác sĩ") || nv.getChucDanh().contains("Nha sĩ"))) {
                    pnlDocList.add(createDoctorItem(nv.getHoTen(), nv.getBangCapChungChi() != null ? nv.getBangCapChungChi() : "Nha sĩ", "Sẵn sàng", ModernUITheme.SUCCESS));
                    docCount++;
                    if (docCount >= 3) break;
                }
            }
        }
        pnlDocList.revalidate();
        pnlDocList.repaint();

        // 4. Tải Top dịch vụ chỉ định cao từ CSDL
        pnlServList.removeAll();
        Map<String, Double> mapDV = thongKeDAO.getDoanhThuTheoDichVu();
        double maxRev = 1;
        for (double v : mapDV.values()) {
            if (v > maxRev) maxRev = v;
        }
        int servCount = 0;
        Color[] barColors = { ModernUITheme.PRIMARY, ModernUITheme.ACCENT, ModernUITheme.SUCCESS, ModernUITheme.WARNING };
        for (Map.Entry<String, Double> entry : mapDV.entrySet()) {
            int pct = (int) Math.round((entry.getValue() / maxRev) * 100);
            Color c = barColors[servCount % barColors.length];
            pnlServList.add(createServiceProgressItem(entry.getKey(), df.format(entry.getValue()), pct, c));
            servCount++;
            if (servCount >= 4) break;
        }
        pnlServList.revalidate();
        pnlServList.repaint();
    }
}
