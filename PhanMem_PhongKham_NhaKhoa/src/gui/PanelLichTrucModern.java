package gui;

import dao.CaTrucDAO;
import dao.LichHenDAO;
import dao.NhanVienDAO;
import dao.ThongKeDAO;
import entity.CaTruc;
import entity.LichHen;
import entity.NhanVien;
import entity.PhanCongCaTruc;
import entity.TrangThaiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Giao diện Lịch Trực & Ca Khám - Hoàn thiện Module 5
 * Tab 1: Phân công ca trực (kiểm tra trùng ca, điểm danh theo hằng số trạng thái)
 * Tab 2: Lịch tuần (lưới 7 ngày x 3 ca, click phân công nhanh)
 * Tab 3: Lịch hẹn khám (quản lý, xác nhận, hủy, chuyển hàng đợi)
 * Method public selectLichHen(String) để TrangChuModern điều hướng
 */
public class PanelLichTrucModern extends JPanel {

    private final CaTrucDAO caTrucDAO = new CaTrucDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final LichHenDAO lichHenDAO = new LichHenDAO();

    private JTabbedPane tabbedPane;

    // === Tab 1: Phân công ca trực ===
    private JTable tblPhanCong;
    private DefaultTableModel modelPhanCong;
    // Hidden data columns: maPhanCong (int), maNV, maCa stored as client property
    private JComboBox<String> cbNhanVien, cbCaTruc, cbVaiTro;
    private JTextField txtGhiChu, txtTimKiem;
    private JButton btnPhanCong, btnHuyPhanCong, btnLamMoi, btnLoc, btnDiemDanh, btnThemCa;
    private JLabel lblLoadingPhanCong;

    // === Tab 2: Lịch tuần ===
    private JPanel pnlTuanGrid;
    private JLabel lblTuanRange;
    private LocalDate currentWeekStart;

    // === Tab 3: Lịch hẹn khám ===
    private JTable tblLichHen;
    private DefaultTableModel modelLichHen;
    private JComboBox<String> cbLocNgayLH, cbLocBacSiLH, cbLocTrangThaiLH;
    private DatePickerField dpLocNgayLH;
    private JLabel lblLoadingLH;

    private static final int COL_PC_MA = 0;      // mã phân công (ẩn)
    private static final int COL_PC_MANV = 1;    // mã NV (ẩn)
    private static final int COL_PC_MACA = 2;    // mã ca (ẩn)
    private static final int COL_PC_HOTEN = 3;
    private static final int COL_PC_TENCA = 4;
    private static final int COL_PC_VAITRO = 5;
    private static final int COL_PC_DIEMDANH = 6;
    private static final int COL_PC_GHICHU = 7;

    private static final int COL_LH_MA = 0;
    private static final int COL_LH_MABN = 1;
    private static final int COL_LH_MABS = 2;
    private static final int COL_LH_TENBN = 3;
    private static final int COL_LH_SDT = 4;
    private static final int COL_LH_TENBS = 5;
    private static final int COL_LH_NGAY = 6;
    private static final int COL_LH_GIO = 7;
    private static final int COL_LH_LYDO = 8;
    private static final int COL_LH_TRANGTHAI = 9;

    public PanelLichTrucModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH TRỰC & PHÂN CÔNG CA KHÁM BÁC SĨ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Điều phối nhân sự theo ca, quản lý lịch hẹn khám và theo dõi điểm danh trực tuyến");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        JButton btnTaiLaiAll = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tải lại tất cả", ModernUITheme.TEXT_MUTED);
        btnTaiLaiAll.addActionListener(e -> refreshData());
        JPanel pnlHeaderBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlHeaderBtn.setOpaque(false);
        pnlHeaderBtn.add(btnTaiLaiAll);
        pnlHeader.add(pnlHeaderBtn, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // TABBED PANE
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(ModernUITheme.FONT_BOLD);
        tabbedPane.addTab("Phân Công Ca Trực", buildTabPhanCong());
        tabbedPane.addTab("Lịch Tuần Nhân Sự", buildTabLichTuan());
        tabbedPane.addTab("Lịch Hẹn Khám", buildTabLichHen());
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 1) {
                renderLichTuan();
            } else if (idx == 2) {
                taiDuLieuLichHen();
            }
        });

        napCombobox();
        taiDuLieuPhanCong();
        currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
    }

    // ===================================================================================
    // TAB 1: PHÂN CÔNG CA TRỰC
    // ===================================================================================

    private JPanel buildTabPhanCong() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(ModernUITheme.BG_MAIN);
        pnl.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Filter bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.setOpaque(false);
        txtTimKiem = ModernUITheme.createTextField(18);
        txtTimKiem.setToolTipText("Tìm bác sĩ, ca trực, vai trò...");
        btnLoc = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 14, Color.WHITE), "Tìm", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTaiLai = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 13, ModernUITheme.TEXT_MUTED), "Tất cả", ModernUITheme.TEXT_MUTED);
        lblLoadingPhanCong = new JLabel("");
        lblLoadingPhanCong.setFont(ModernUITheme.FONT_SMALL);
        lblLoadingPhanCong.setForeground(ModernUITheme.PRIMARY);

        pnlFilter.add(new JLabel("Tìm kiếm:"));
        pnlFilter.add(txtTimKiem);
        pnlFilter.add(btnLoc);
        pnlFilter.add(btnTaiLai);
        pnlFilter.add(lblLoadingPhanCong);
        pnl.add(pnlFilter, BorderLayout.NORTH);

        // Bảng phân công - ẩn cột maPhanCong, maNV, maCa
        String[] cols = {"#", "MaNV", "MaCa", "Nhân Sự Y Tế", "Ca Trực", "Vai Trò", "Điểm Danh", "Ghi Chú"};
        modelPhanCong = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPhanCong = new JTable(modelPhanCong);
        ModernUITheme.formatModernTable(tblPhanCong);

        // Ẩn 3 cột kỹ thuật
        tblPhanCong.getColumnModel().getColumn(COL_PC_MA).setMinWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MA).setMaxWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MA).setWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MANV).setMinWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MANV).setMaxWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MANV).setWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MACA).setMinWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MACA).setMaxWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_MACA).setWidth(0);
        tblPhanCong.getColumnModel().getColumn(COL_PC_HOTEN).setPreferredWidth(200);
        tblPhanCong.getColumnModel().getColumn(COL_PC_TENCA).setPreferredWidth(180);
        tblPhanCong.getColumnModel().getColumn(COL_PC_VAITRO).setPreferredWidth(120);
        tblPhanCong.getColumnModel().getColumn(COL_PC_DIEMDANH).setPreferredWidth(120);

        // Renderer màu điểm danh
        tblPhanCong.getColumnModel().getColumn(COL_PC_DIEMDANH).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    String st = value != null ? value.toString() : "";
                    if (TrangThaiConstants.DD_DA_DEN_TRUC.equalsIgnoreCase(st) || TrangThaiConstants.DD_DANG_TRUC.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.SUCCESS);
                    } else if (TrangThaiConstants.DD_VANG_MAT.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.DANGER);
                    } else if (TrangThaiConstants.DD_HOAN_THANH_CA.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.TEXT_MUTED);
                    } else {
                        setForeground(ModernUITheme.TEXT_REGULAR);
                    }
                    setFont(ModernUITheme.FONT_REGULAR);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tblPhanCong);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(scroll, BorderLayout.CENTER);

        // Form card phân công
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(10, 10));

        JLabel lblFormTitle = new JLabel("PHÂN CÔNG NHÂN SỰ VÀO CA TRỰC PHÒNG KHÁM");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 2, 20, 10));
        pnlFields.setOpaque(false);

        cbNhanVien = new JComboBox<>();
        cbCaTruc = new JComboBox<>();
        cbVaiTro = new JComboBox<>(new String[]{
            "Bác sĩ chính", "Bác sĩ hỗ trợ", "Phụ tá ghế", "Điều dưỡng", "Kỹ thuật viên", "Lễ tân"
        });
        txtGhiChu = ModernUITheme.createTextField(28);
        txtGhiChu.setToolTipText("Vị trí ghế, nhiệm vụ cụ thể trong ca...");

        pnlFields.add(createFieldItem("Nhân sự y tế (*):", cbNhanVien));
        pnlFields.add(createFieldItem("Ca trực nha khoa (*):", cbCaTruc));
        pnlFields.add(createFieldItem("Vai trò phụ trách:", cbVaiTro));
        pnlFields.add(createFieldItem("Ghi chú vị trí / nhiệm vụ:", txtGhiChu));
        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);

        btnThemCa = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.CLOCK, 14, ModernUITheme.PRIMARY), "Tạo ca trực", ModernUITheme.PRIMARY);
        btnDiemDanh = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Điểm danh", ModernUITheme.ACCENT, Color.WHITE);
        btnHuyPhanCong = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CLOSE, 14, Color.WHITE), "Hủy phân công", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Làm mới", ModernUITheme.TEXT_MUTED);
        btnPhanCong = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 14, Color.WHITE), "Xác nhận phân công", ModernUITheme.PRIMARY, Color.WHITE);

        pnlBtns.add(btnLamMoi);
        pnlBtns.add(btnThemCa);
        pnlBtns.add(btnDiemDanh);
        pnlBtns.add(btnHuyPhanCong);
        pnlBtns.add(btnPhanCong);
        pnlFormCard.add(pnlBtns, BorderLayout.SOUTH);

        // Phân quyền
        apDungPhanQuyenPhanCong();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, pnlFormCard);
        split.setResizeWeight(0.62);
        split.setDividerSize(6);
        split.setOpaque(false);

        JPanel pnlWrap = new JPanel(new BorderLayout(0, 10));
        pnlWrap.setBackground(ModernUITheme.BG_MAIN);
        pnlWrap.add(pnlFilter, BorderLayout.NORTH);
        pnlWrap.add(split, BorderLayout.CENTER);

        // Sự kiện Tab 1
        tblPhanCong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPhanCong.getSelectedRow() != -1) {
                napFormTuBangPhanCong(tblPhanCong.getSelectedRow());
            }
        });
        btnPhanCong.addActionListener(e -> xuLyPhanCong());
        btnHuyPhanCong.addActionListener(e -> xuLyHuyPhanCong());
        btnDiemDanh.addActionListener(e -> xuLyDiemDanh());
        btnThemCa.addActionListener(e -> xuLyThemCa());
        btnLamMoi.addActionListener(e -> xoaFormPhanCong());
        btnLoc.addActionListener(e -> xuLyLocPhanCong());
        txtTimKiem.addActionListener(e -> xuLyLocPhanCong());
        btnTaiLai.addActionListener(e -> { txtTimKiem.setText(""); taiDuLieuPhanCong(); });

        return pnlWrap;
    }

    private void apDungPhanQuyenPhanCong() {
        boolean isQL = SessionManager.isQuanLy();
        if (!isQL) {
            btnPhanCong.setEnabled(false);
            btnPhanCong.setToolTipText("Chỉ Quản lý mới có quyền phân công ca trực");
            btnHuyPhanCong.setEnabled(false);
            btnHuyPhanCong.setToolTipText("Chỉ Quản lý mới có quyền hủy phân công");
            btnThemCa.setEnabled(false);
            btnThemCa.setToolTipText("Chỉ Quản lý mới có quyền tạo ca trực");
        }
    }

    private void taiDuLieuPhanCong() {
        lblLoadingPhanCong.setText("Đang tải...");
        SwingWorker<List<PhanCongCaTruc>, Void> worker = new SwingWorker<List<PhanCongCaTruc>, Void>() {
            @Override
            protected List<PhanCongCaTruc> doInBackground() {
                // Nhân sự thường chỉ xem ca của mình
                if (!SessionManager.isQuanLy() && !SessionManager.isLeTan()) {
                    String maNV = SessionManager.getMaNVHienTai();
                    if (maNV != null && !maNV.isEmpty()) {
                        return caTrucDAO.getPhanCongTheoNV(maNV);
                    }
                }
                return caTrucDAO.getAllPhanCong();
            }

            @Override
            protected void done() {
                try {
                    List<PhanCongCaTruc> list = get();
                    modelPhanCong.setRowCount(0);
                    for (PhanCongCaTruc pc : list) {
                        modelPhanCong.addRow(new Object[]{
                            pc.getMaPhanCong(),
                            pc.getMaNV(),
                            pc.getMaCa(),
                            pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV(),
                            pc.getTenCa() != null ? pc.getTenCa() : pc.getMaCa(),
                            pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "Bác sĩ chính",
                            pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : TrangThaiConstants.DD_CHUA_DEN,
                            pc.getGhiChu() != null ? pc.getGhiChu() : ""
                        });
                    }
                    xoaFormPhanCong();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelLichTrucModern.this,
                        "Lỗi khi tải dữ liệu phân công: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    lblLoadingPhanCong.setText("");
                }
            }
        };
        worker.execute();
    }

    private void napFormTuBangPhanCong(int viewRow) {
        if (viewRow < 0 || viewRow >= tblPhanCong.getRowCount()) return;

        String hoTen = modelPhanCong.getValueAt(viewRow, COL_PC_HOTEN).toString();
        for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
            if (cbNhanVien.getItemAt(i) != null && cbNhanVien.getItemAt(i).contains(hoTen)) {
                cbNhanVien.setSelectedIndex(i);
                break;
            }
        }
        String tenCa = modelPhanCong.getValueAt(viewRow, COL_PC_TENCA).toString();
        for (int i = 0; i < cbCaTruc.getItemCount(); i++) {
            String item = cbCaTruc.getItemAt(i);
            if (item != null && (item.contains(tenCa) || tenCa.contains(item.split(" - ")[0]))) {
                cbCaTruc.setSelectedIndex(i);
                break;
            }
        }
        String vaiTro = modelPhanCong.getValueAt(viewRow, COL_PC_VAITRO) != null
            ? modelPhanCong.getValueAt(viewRow, COL_PC_VAITRO).toString() : "Bác sĩ chính";
        cbVaiTro.setSelectedItem(vaiTro);
        txtGhiChu.setText(modelPhanCong.getValueAt(viewRow, COL_PC_GHICHU) != null
            ? modelPhanCong.getValueAt(viewRow, COL_PC_GHICHU).toString() : "");
    }

    private void xoaFormPhanCong() {
        if (cbNhanVien.getItemCount() > 0) cbNhanVien.setSelectedIndex(0);
        if (cbCaTruc.getItemCount() > 0) cbCaTruc.setSelectedIndex(0);
        cbVaiTro.setSelectedIndex(0);
        txtGhiChu.setText("");
        tblPhanCong.clearSelection();
    }

    private void xuLyPhanCong() {
        if (cbNhanVien.getSelectedItem() == null || cbCaTruc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự và ca trực!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemNV = cbNhanVien.getSelectedItem().toString();
        String maNV = itemNV.split(" - ")[0].trim();
        String hoTenNV = itemNV.contains(" - ") ? itemNV.substring(itemNV.indexOf(" - ") + 3).trim() : maNV;
        if (hoTenNV.contains(" (")) {
            hoTenNV = hoTenNV.substring(0, hoTenNV.indexOf(" (")).trim();
        }

        String itemCa = cbCaTruc.getSelectedItem().toString();
        if (itemCa.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Chưa có ca trực nào trong hệ thống! Vui lòng nhấn 'Tạo ca trực' để thêm ca mới.", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maCa = itemCa.split(" - ")[0].trim();
        String tenCa = itemCa.contains(" - ") ? itemCa.substring(itemCa.indexOf(" - ") + 3).trim() : maCa;

        // Kiểm tra nhân sự đã được phân công ca giao thoa chưa
        if (caTrucDAO.kiemTraTrungCaNhanVien(maNV, maCa)) {
            JOptionPane.showMessageDialog(this,
                "Nhân sự [" + hoTenNV + "] đã được phân công một ca trực khác trùng thời gian với ca [" + tenCa + "]!\nVui lòng kiểm tra lịch và chọn ca khác.",
                "Trùng lịch ca trực", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vaiTro = cbVaiTro.getSelectedItem() != null ? cbVaiTro.getSelectedItem().toString() : "Bác sĩ chính";
        String ghiChu = txtGhiChu.getText().trim();

        PhanCongCaTruc pc = new PhanCongCaTruc(0, maNV, maCa, vaiTro, TrangThaiConstants.DD_CHUA_DEN, ghiChu);
        pc.setHoTenNV(hoTenNV);
        pc.setTenCa(tenCa);

        boolean thanhCong = caTrucDAO.themPhanCong(pc);
        if (thanhCong) {
            ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "PHÂN CÔNG CA", "Phân công [" + hoTenNV + "] vào [" + tenCa + "]");
            JOptionPane.showMessageDialog(this,
                "Phân công [" + hoTenNV + "] vào ca [" + tenCa + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuPhanCong();
        } else {
            JOptionPane.showMessageDialog(this, "Phân công thất bại. Có thể đã trùng hoặc lỗi kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyPhanCong() {
        int row = tblPhanCong.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lượt phân công để hủy!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int maPC = (Integer) modelPhanCong.getValueAt(row, COL_PC_MA);
        String tenNV = modelPhanCong.getValueAt(row, COL_PC_HOTEN).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn hủy phân công ca trực của [" + tenNV + "]?",
            "Xác nhận hủy phân công", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = caTrucDAO.xoaPhanCong(maPC);
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "HỦY PHÂN CÔNG", "Hủy phân công #" + maPC + " - " + tenNV);
                JOptionPane.showMessageDialog(this, "Đã hủy phân công ca trực thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuPhanCong();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy phân công thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyDiemDanh() {
        int row = tblPhanCong.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân sự trong bảng để điểm danh!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int maPC = (Integer) modelPhanCong.getValueAt(row, COL_PC_MA);
        String maNVChon = modelPhanCong.getValueAt(row, COL_PC_MANV).toString();
        String tenNV = modelPhanCong.getValueAt(row, COL_PC_HOTEN).toString();
        String ttHienTai = modelPhanCong.getValueAt(row, COL_PC_DIEMDANH).toString();

        // Nhân sự chỉ tự điểm danh cho chính mình
        if (!SessionManager.isQuanLy() && !SessionManager.isLeTan()) {
            String maNVLogin = SessionManager.getMaNVHienTai();
            if (maNVLogin != null && !maNVLogin.equalsIgnoreCase(maNVChon)) {
                JOptionPane.showMessageDialog(this, "Bạn chỉ được tự điểm danh cho bản thân!", "Từ chối quyền", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String[] options = {
            TrangThaiConstants.DD_DA_DEN_TRUC,
            TrangThaiConstants.DD_DANG_TRUC,
            TrangThaiConstants.DD_HOAN_THANH_CA,
            TrangThaiConstants.DD_VANG_MAT,
            TrangThaiConstants.DD_DOI_CA
        };

        Object selected = JOptionPane.showInputDialog(this,
            "Chọn trạng thái điểm danh cho nhân sự [" + tenNV + "]:",
            "Điểm danh ca trực phòng khám",
            JOptionPane.QUESTION_MESSAGE, null, options, ttHienTai);

        if (selected != null) {
            boolean ok = caTrucDAO.diemDanh(maPC, selected.toString());
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "ĐIỂM DANH", tenNV + " → " + selected.toString());
                JOptionPane.showMessageDialog(this, "Cập nhật điểm danh cho [" + tenNV + "] thành: " + selected, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuPhanCong();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyLocPhanCong() {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            taiDuLieuPhanCong();
            return;
        }
        String kw = keyword.trim().toLowerCase();
        List<PhanCongCaTruc> all = caTrucDAO.getAllPhanCong();
        modelPhanCong.setRowCount(0);
        for (PhanCongCaTruc pc : all) {
            boolean match = (pc.getHoTenNV() != null && pc.getHoTenNV().toLowerCase().contains(kw)) ||
                            (pc.getMaNV() != null && pc.getMaNV().toLowerCase().contains(kw)) ||
                            (pc.getTenCa() != null && pc.getTenCa().toLowerCase().contains(kw)) ||
                            (pc.getMaCa() != null && pc.getMaCa().toLowerCase().contains(kw)) ||
                            (pc.getVaiTroTrongCa() != null && pc.getVaiTroTrongCa().toLowerCase().contains(kw));
            if (match) {
                modelPhanCong.addRow(new Object[]{
                    pc.getMaPhanCong(), pc.getMaNV(), pc.getMaCa(),
                    pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV(),
                    pc.getTenCa() != null ? pc.getTenCa() : pc.getMaCa(),
                    pc.getVaiTroTrongCa() != null ? pc.getVaiTroTrongCa() : "Bác sĩ chính",
                    pc.getTrangThaiDiemDanh() != null ? pc.getTrangThaiDiemDanh() : TrangThaiConstants.DD_CHUA_DEN,
                    pc.getGhiChu() != null ? pc.getGhiChu() : ""
                });
            }
        }
        if (modelPhanCong.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy lượt phân công nào khớp với từ khóa: \"" + keyword.trim() + "\"",
                "Kết quả tra cứu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyThemCa() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo Ca Trực Nha Khoa Mới", true);
        dlg.setSize(500, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));

        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBackground(ModernUITheme.BG_MAIN);
        pnlContent.setBorder(new EmptyBorder(16, 20, 10, 20));

        JLabel lblTitle = new JLabel("THÔNG TIN CA TRỰC MỚI");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlContent.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlGrid = new JPanel(new GridLayout(6, 2, 12, 10));
        pnlGrid.setOpaque(false);

        DatePickerField dpNgay = new DatePickerField(LocalDate.now());

        JComboBox<String> cbTenCa = new JComboBox<>(new String[]{
            "Ca Sáng (08:00 - 12:00)",
            "Ca Chiều (13:30 - 17:30)",
            "Ca Tối (18:00 - 21:00)",
            "Ca Cấp Cứu Ngoài Giờ"
        });

        JSpinner spGioBD_H = new JSpinner(new SpinnerNumberModel(8, 0, 23, 1));
        JSpinner spGioBD_M = new JSpinner(new SpinnerNumberModel(0, 0, 59, 5));
        JSpinner spGioKT_H = new JSpinner(new SpinnerNumberModel(12, 0, 23, 1));
        JSpinner spGioKT_M = new JSpinner(new SpinnerNumberModel(0, 0, 59, 5));

        cbTenCa.addActionListener(e -> {
            int idx = cbTenCa.getSelectedIndex();
            if (idx == 0) { spGioBD_H.setValue(8); spGioBD_M.setValue(0); spGioKT_H.setValue(12); spGioKT_M.setValue(0); }
            else if (idx == 1) { spGioBD_H.setValue(13); spGioBD_M.setValue(30); spGioKT_H.setValue(17); spGioKT_M.setValue(30); }
            else if (idx == 2) { spGioBD_H.setValue(18); spGioBD_M.setValue(0); spGioKT_H.setValue(21); spGioKT_M.setValue(0); }
            else { spGioBD_H.setValue(21); spGioBD_M.setValue(0); spGioKT_H.setValue(23); spGioKT_M.setValue(59); }
        });

        JSpinner spMaxBN = new JSpinner(new SpinnerNumberModel(15, 1, 100, 1));

        JPanel pnlGioBD = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGioBD.setOpaque(false);
        pnlGioBD.add(spGioBD_H);
        pnlGioBD.add(new JLabel("giờ"));
        pnlGioBD.add(spGioBD_M);
        pnlGioBD.add(new JLabel("phút"));

        JPanel pnlGioKT = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGioKT.setOpaque(false);
        pnlGioKT.add(spGioKT_H);
        pnlGioKT.add(new JLabel("giờ"));
        pnlGioKT.add(spGioKT_M);
        pnlGioKT.add(new JLabel("phút"));

        pnlGrid.add(new JLabel("Tên ca trực (*):")); pnlGrid.add(cbTenCa);
        pnlGrid.add(new JLabel("Ngày trực (*):")); pnlGrid.add(dpNgay);
        pnlGrid.add(new JLabel("Giờ bắt đầu (*):")); pnlGrid.add(pnlGioBD);
        pnlGrid.add(new JLabel("Giờ kết thúc (*):")); pnlGrid.add(pnlGioKT);
        pnlGrid.add(new JLabel("Số bệnh nhân tối đa:")); pnlGrid.add(spMaxBN);
        pnlGrid.add(new JLabel("")); pnlGrid.add(new JLabel(""));

        pnlContent.add(pnlGrid, BorderLayout.CENTER);
        dlg.add(pnlContent, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnHuy = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);
        JButton btnLuu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Lưu ca trực", ModernUITheme.PRIMARY, Color.WHITE);
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        btnHuy.addActionListener(ev -> dlg.dispose());
        btnLuu.addActionListener(ev -> {
            try {
                java.sql.Date ngay = dpNgay.getDate();
                if (ngay == null) {
                    JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày trực!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int bdH = (Integer) spGioBD_H.getValue();
                int bdM = (Integer) spGioBD_M.getValue();
                int ktH = (Integer) spGioKT_H.getValue();
                int ktM = (Integer) spGioKT_M.getValue();

                if (ktH < bdH || (ktH == bdH && ktM <= bdM)) {
                    JOptionPane.showMessageDialog(dlg, "Giờ kết thúc phải lớn hơn giờ bắt đầu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String tenCa = cbTenCa.getSelectedItem().toString();
                Time bd = Time.valueOf(String.format("%02d:%02d:00", bdH, bdM));
                Time kt = Time.valueOf(String.format("%02d:%02d:00", ktH, ktM));
                int maxBN = (Integer) spMaxBN.getValue();

                String suffix = bdH < 12 ? "SANG" : (bdH < 18 ? "CHIEU" : "TOI");
                SimpleDateFormat sdfCode = new SimpleDateFormat("yyyyMMdd");
                String maCa = "CA_" + sdfCode.format(ngay) + "_" + suffix;

                CaTruc ct = new CaTruc(maCa, tenCa, ngay, bd, kt, 1, 1, maxBN, "Đã lên lịch");
                boolean ok = caTrucDAO.themCaTruc(ct);
                if (ok) {
                    ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "TẠO CA TRỰC", "Ca: " + maCa + " (" + tenCa + ")");
                    JOptionPane.showMessageDialog(dlg, "Đã thêm ca trực [" + maCa + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    napCombobox();
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Không thể thêm ca trực. Có thể ca cùng loại trong ngày đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    // ===================================================================================
    // TAB 2: LỊCH TUẦN
    // ===================================================================================

    private JPanel buildTabLichTuan() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(ModernUITheme.BG_MAIN);
        pnl.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Navigation bar
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        pnlNav.setOpaque(false);

        JButton btnTruoc = ModernUITheme.createOutlineIconButton(null, "< Tuần trước", ModernUITheme.PRIMARY);
        JButton btnHomNay = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CLOCK, 14, Color.WHITE), "Tuần này", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnSau = ModernUITheme.createOutlineIconButton(null, "Tuần sau >", ModernUITheme.PRIMARY);

        lblTuanRange = new JLabel("", SwingConstants.CENTER);
        lblTuanRange.setFont(ModernUITheme.FONT_BOLD);
        lblTuanRange.setForeground(ModernUITheme.PRIMARY_DARK);
        lblTuanRange.setPreferredSize(new Dimension(300, 30));

        pnlNav.add(btnTruoc);
        pnlNav.add(lblTuanRange);
        pnlNav.add(btnHomNay);
        pnlNav.add(btnSau);
        pnl.add(pnlNav, BorderLayout.NORTH);

        // Lưới lịch tuần
        pnlTuanGrid = new JPanel(new BorderLayout());
        pnlTuanGrid.setBackground(ModernUITheme.BG_MAIN);

        JScrollPane scrollTuan = new JScrollPane(pnlTuanGrid);
        scrollTuan.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnl.add(scrollTuan, BorderLayout.CENTER);

        btnTruoc.addActionListener(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            renderLichTuan();
        });
        btnHomNay.addActionListener(e -> {
            currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            renderLichTuan();
        });
        btnSau.addActionListener(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            renderLichTuan();
        });

        return pnl;
    }

    private void renderLichTuan() {
        pnlTuanGrid.removeAll();

        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter dtfShort = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter dtfFull = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        lblTuanRange.setText("Tuần: " + currentWeekStart.format(dtfFull) + " — " + weekEnd.format(dtfFull));

        List<PhanCongCaTruc> allPhanCong = caTrucDAO.getAllPhanCong();

        // Build grid: 4 rows (header + 3 ca) x 8 cols (header + 7 days)
        JPanel grid = new JPanel(new GridLayout(4, 8, 2, 2));
        grid.setBackground(ModernUITheme.BG_MAIN);
        grid.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header row
        grid.add(buildGridCell("Ca / Ngày", ModernUITheme.PRIMARY_DARK, Color.WHITE, false));
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        for (int d = 0; d < 7; d++) {
            LocalDate day = currentWeekStart.plusDays(d);
            String dayLabel = days[d] + "\n" + day.format(dtfShort);
            boolean isToday = day.equals(LocalDate.now());
            Color bg = isToday ? new Color(34, 120, 200) : ModernUITheme.PRIMARY_DARK;
            grid.add(buildGridCell(days[d] + " " + day.format(dtfShort), bg, Color.WHITE, false));
        }

        // 3 ca rows
        String[][] caTemplates = {
            {"Ca Sáng", "SANG"},
            {"Ca Chiều", "CHIEU"},
            {"Ca Tối", "TOI"}
        };

        Color[] caColors = {
            new Color(255, 248, 230),
            new Color(230, 245, 255),
            new Color(235, 230, 255)
        };

        for (int ca = 0; ca < 3; ca++) {
            String tenCa = caTemplates[ca][0];
            String suffix = caTemplates[ca][1];
            Color caColor = caColors[ca];

            grid.add(buildGridCell(tenCa, new Color(80, 90, 100), Color.WHITE, false));

            for (int d = 0; d < 7; d++) {
                LocalDate day = currentWeekStart.plusDays(d);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String ngayCode = sdf.format(java.sql.Date.valueOf(day));
                String maCaSearch = "CA_" + ngayCode + "_" + suffix;

                JPanel cellPanel = new JPanel();
                cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.Y_AXIS));
                cellPanel.setBackground(caColor);
                cellPanel.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));

                for (PhanCongCaTruc pc : allPhanCong) {
                    if (maCaSearch.equalsIgnoreCase(pc.getMaCa())) {
                        JLabel lblNV = new JLabel("  " + (pc.getHoTenNV() != null ? pc.getHoTenNV() : pc.getMaNV()));
                        lblNV.setFont(new Font(ModernUITheme.FONT_REGULAR.getFamily(), Font.PLAIN, 10));
                        lblNV.setForeground(new Color(40, 60, 100));
                        cellPanel.add(lblNV);
                    }
                }

                if (cellPanel.getComponentCount() == 0) {
                    JLabel lblTrong = new JLabel("  (Trống)", SwingConstants.CENTER);
                    lblTrong.setFont(new Font(ModernUITheme.FONT_REGULAR.getFamily(), Font.ITALIC, 10));
                    lblTrong.setForeground(ModernUITheme.TEXT_MUTED);
                    cellPanel.add(lblTrong);
                }

                // Click phân công nhanh
                final String maCaClick = maCaSearch;
                cellPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (e.getClickCount() == 2 && SessionManager.isQuanLy()) {
                            // Chuyển sang tab phân công và chọn ca
                            tabbedPane.setSelectedIndex(0);
                            for (int i = 0; i < cbCaTruc.getItemCount(); i++) {
                                if (cbCaTruc.getItemAt(i) != null && cbCaTruc.getItemAt(i).startsWith(maCaClick)) {
                                    cbCaTruc.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                    }
                });

                grid.add(cellPanel);
            }
        }

        pnlTuanGrid.add(grid, BorderLayout.CENTER);
        pnlTuanGrid.revalidate();
        pnlTuanGrid.repaint();
    }

    private JPanel buildGridCell(String text, Color bg, Color fg, boolean border) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setBackground(bg);
        if (border) {
            cell.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        }
        JLabel lbl = new JLabel("<html><center>" + text.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
        lbl.setFont(ModernUITheme.FONT_BOLD);
        lbl.setForeground(fg);
        cell.add(lbl);
        return cell;
    }

    // ===================================================================================
    // TAB 3: LỊCH HẸN KHÁM
    // ===================================================================================

    private JPanel buildTabLichHen() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(ModernUITheme.BG_MAIN);
        pnl.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Filter bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.setOpaque(false);

        dpLocNgayLH = new DatePickerField(LocalDate.now());
        JButton btnLocLH = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 14, Color.WHITE), "Lọc", ModernUITheme.PRIMARY, Color.WHITE);
        JButton btnTatCaLH = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 13, ModernUITheme.TEXT_MUTED), "Tất cả ngày", ModernUITheme.TEXT_MUTED);

        List<NhanVien> listBS = nhanVienDAO.getDanhSachBacSi();
        cbLocBacSiLH = new JComboBox<>();
        cbLocBacSiLH.addItem("Tất cả bác sĩ");
        for (NhanVien bs : listBS) {
            cbLocBacSiLH.addItem(bs.getMaNV() + " - " + bs.getHoTen());
        }

        cbLocTrangThaiLH = new JComboBox<>(new String[]{
            "Tất cả trạng thái",
            TrangThaiConstants.LH_DA_LEN_LICH,
            TrangThaiConstants.LH_DA_XAC_NHAN,
            TrangThaiConstants.LH_DANG_KHAM,
            TrangThaiConstants.LH_HOAN_THANH,
            TrangThaiConstants.LH_DA_HUY,
            TrangThaiConstants.LH_KHONG_DEN
        });

        lblLoadingLH = new JLabel("");
        lblLoadingLH.setFont(ModernUITheme.FONT_SMALL);
        lblLoadingLH.setForeground(ModernUITheme.PRIMARY);

        pnlFilter.add(new JLabel("Ngày:"));
        pnlFilter.add(dpLocNgayLH);
        pnlFilter.add(new JLabel("  Bác sĩ:"));
        pnlFilter.add(cbLocBacSiLH);
        pnlFilter.add(new JLabel("  Trạng thái:"));
        pnlFilter.add(cbLocTrangThaiLH);
        pnlFilter.add(btnLocLH);
        pnlFilter.add(btnTatCaLH);
        pnlFilter.add(lblLoadingLH);

        pnl.add(pnlFilter, BorderLayout.NORTH);

        // Bảng lịch hẹn - ẩn 3 cột kỹ thuật (maLH, maBN, maBS)
        String[] colsLH = {"MaLH", "MaBN", "MaBS", "Tên Bệnh Nhân", "SĐT", "Bác Sĩ Phụ Trách", "Ngày Hẹn", "Giờ Hẹn", "Lý Do Khám", "Trạng Thái"};
        modelLichHen = new DefaultTableModel(colsLH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichHen = new JTable(modelLichHen);
        ModernUITheme.formatModernTable(tblLichHen);

        // Ẩn các cột kỹ thuật
        int[] hideCols = {COL_LH_MA, COL_LH_MABN, COL_LH_MABS};
        for (int hc : hideCols) {
            tblLichHen.getColumnModel().getColumn(hc).setMinWidth(0);
            tblLichHen.getColumnModel().getColumn(hc).setMaxWidth(0);
            tblLichHen.getColumnModel().getColumn(hc).setWidth(0);
        }
        tblLichHen.getColumnModel().getColumn(COL_LH_TENBN).setPreferredWidth(180);
        tblLichHen.getColumnModel().getColumn(COL_LH_SDT).setPreferredWidth(100);
        tblLichHen.getColumnModel().getColumn(COL_LH_TENBS).setPreferredWidth(180);
        tblLichHen.getColumnModel().getColumn(COL_LH_NGAY).setPreferredWidth(90);
        tblLichHen.getColumnModel().getColumn(COL_LH_GIO).setPreferredWidth(70);
        tblLichHen.getColumnModel().getColumn(COL_LH_LYDO).setPreferredWidth(200);
        tblLichHen.getColumnModel().getColumn(COL_LH_TRANGTHAI).setPreferredWidth(120);

        // Renderer màu trạng thái lịch hẹn
        tblLichHen.getColumnModel().getColumn(COL_LH_TRANGTHAI).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    String st = value != null ? value.toString() : "";
                    if (TrangThaiConstants.LH_DANG_KHAM.equalsIgnoreCase(st)) {
                        setForeground(new Color(0, 150, 100));
                        setFont(ModernUITheme.FONT_BOLD);
                    } else if (TrangThaiConstants.LH_DA_XAC_NHAN.equalsIgnoreCase(st) || TrangThaiConstants.LH_DA_LEN_LICH.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.PRIMARY);
                        setFont(ModernUITheme.FONT_REGULAR);
                    } else if (TrangThaiConstants.LH_DA_HUY.equalsIgnoreCase(st) || TrangThaiConstants.LH_KHONG_DEN.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.DANGER);
                        setFont(ModernUITheme.FONT_REGULAR);
                    } else if (TrangThaiConstants.LH_HOAN_THANH.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.TEXT_MUTED);
                        setFont(ModernUITheme.FONT_REGULAR);
                    } else {
                        setForeground(ModernUITheme.TEXT_REGULAR);
                        setFont(ModernUITheme.FONT_REGULAR);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollLH = new JScrollPane(tblLichHen);
        scrollLH.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));

        // Action buttons
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlActions.setOpaque(false);
        pnlActions.setBackground(ModernUITheme.BG_CARD);

        JButton btnXacNhan = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CHECK, 14, Color.WHITE), "Xác nhận lịch hẹn", ModernUITheme.SUCCESS, Color.WHITE);
        JButton btnHuyLH = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.CLOSE, 14, Color.WHITE), "Hủy lịch hẹn", ModernUITheme.DANGER, Color.WHITE);
        JButton btnDoiGio = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.CLOCK, 14, ModernUITheme.PRIMARY), "Đổi giờ hẹn", ModernUITheme.PRIMARY);
        JButton btnKhongDen = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.DELETE, 13, ModernUITheme.DANGER), "Đánh dấu không đến", ModernUITheme.DANGER);
        JButton btnChuyenHangDoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 13, ModernUITheme.ACCENT), "Chuyển hàng đợi", ModernUITheme.ACCENT);

        pnlActions.add(btnKhongDen);
        pnlActions.add(btnHuyLH);
        pnlActions.add(btnDoiGio);
        pnlActions.add(btnChuyenHangDoi);
        pnlActions.add(btnXacNhan);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(ModernUITheme.BG_CARD);
        pnlBottom.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnlBottom.add(pnlActions, BorderLayout.CENTER);

        pnl.add(scrollLH, BorderLayout.CENTER);
        pnl.add(pnlBottom, BorderLayout.SOUTH);

        // Sự kiện
        btnLocLH.addActionListener(e -> taiDuLieuLichHen());
        cbLocBacSiLH.addActionListener(e -> taiDuLieuLichHen());
        cbLocTrangThaiLH.addActionListener(e -> taiDuLieuLichHen());
        btnTatCaLH.addActionListener(e -> {
            dpLocNgayLH.setLocalDate(null);
            cbLocBacSiLH.setSelectedIndex(0);
            cbLocTrangThaiLH.setSelectedIndex(0);
            taiDuLieuLichHen();
        });

        btnXacNhan.addActionListener(e -> xuLyCapNhatTrangThaiLH(TrangThaiConstants.LH_DA_XAC_NHAN));
        btnHuyLH.addActionListener(e -> xuLyHuyLichHen());
        btnDoiGio.addActionListener(e -> xuLyDoiGioHen());
        btnKhongDen.addActionListener(e -> xuLyCapNhatTrangThaiLH(TrangThaiConstants.LH_KHONG_DEN));
        btnChuyenHangDoi.addActionListener(e -> xuLyCapNhatTrangThaiLH(TrangThaiConstants.LH_DANG_KHAM));

        return pnl;
    }

    private void taiDuLieuLichHen() {
        lblLoadingLH.setText("Đang tải...");
        SwingWorker<List<LichHen>, Void> worker = new SwingWorker<List<LichHen>, Void>() {
            @Override
            protected List<LichHen> doInBackground() {
                java.sql.Date ngayLoc = dpLocNgayLH.getDate();
                if (ngayLoc != null) {
                    return lichHenDAO.getLichHenTheoNgay(ngayLoc);
                }
                return lichHenDAO.getAllLichHen();
            }

            @Override
            protected void done() {
                try {
                    List<LichHen> list = get();
                    String bsFilter = cbLocBacSiLH.getSelectedItem() != null ? cbLocBacSiLH.getSelectedItem().toString() : "Tất cả bác sĩ";
                    String ttFilter = cbLocTrangThaiLH.getSelectedItem() != null ? cbLocTrangThaiLH.getSelectedItem().toString() : "Tất cả trạng thái";

                    modelLichHen.setRowCount(0);
                    for (LichHen lh : list) {
                        // Lọc bác sĩ
                        if (!"Tất cả bác sĩ".equals(bsFilter)) {
                            String maBSFilter = bsFilter.split(" - ")[0].trim();
                            if (!maBSFilter.equalsIgnoreCase(lh.getMaNhaSi())) continue;
                        }
                        // Lọc trạng thái
                        if (!"Tất cả trạng thái".equals(ttFilter)) {
                            if (!ttFilter.equalsIgnoreCase(lh.getTrangThai())) continue;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        modelLichHen.addRow(new Object[]{
                            lh.getMaLichHen(),
                            lh.getMaBN(),
                            lh.getMaNhaSi(),
                            lh.getTenBenhNhan() != null ? lh.getTenBenhNhan() : lh.getMaBN(),
                            lh.getSdtBenhNhan() != null ? lh.getSdtBenhNhan() : "",
                            lh.getTenNhaSi() != null ? lh.getTenNhaSi() : lh.getMaNhaSi(),
                            lh.getNgayHen() != null ? sdf.format(lh.getNgayHen()) : "",
                            lh.getGioHen() != null ? lh.getGioHen().toString().substring(0, 5) : "",
                            lh.getLyDoKham() != null ? lh.getLyDoKham() : "",
                            lh.getTrangThai() != null ? lh.getTrangThai() : TrangThaiConstants.LH_DA_LEN_LICH
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelLichTrucModern.this,
                        "Lỗi khi tải lịch hẹn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    lblLoadingLH.setText("");
                }
            }
        };
        worker.execute();
    }

    private void xuLyCapNhatTrangThaiLH(String trangThaiMoi) {
        int row = tblLichHen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch hẹn trong bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maLH = modelLichHen.getValueAt(row, COL_LH_MA).toString();
        String tenBN = modelLichHen.getValueAt(row, COL_LH_TENBN).toString();
        String ttHienTai = modelLichHen.getValueAt(row, COL_LH_TRANGTHAI).toString();

        // Kiểm tra luồng trạng thái hợp lệ
        if (TrangThaiConstants.LH_DA_HUY.equalsIgnoreCase(ttHienTai) || TrangThaiConstants.LH_HOAN_THANH.equalsIgnoreCase(ttHienTai)) {
            JOptionPane.showMessageDialog(this, "Lịch hẹn đã Hủy hoặc Hoàn thành không thể thay đổi trạng thái!", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (TrangThaiConstants.LH_DANG_KHAM.equalsIgnoreCase(trangThaiMoi) &&
            !TrangThaiConstants.LH_DA_XAC_NHAN.equalsIgnoreCase(ttHienTai) &&
            !TrangThaiConstants.LH_DA_LEN_LICH.equalsIgnoreCase(ttHienTai)) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể chuyển sang hàng đợi khi lịch hẹn ở trạng thái Đã xác nhận hoặc Đã lên lịch!", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = lichHenDAO.capNhatTrangThai(maLH, trangThaiMoi);
        if (ok) {
            ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "CẬP NHẬT LỊCH HẸN", maLH + " → " + trangThaiMoi);
            taiDuLieuLichHen();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái lịch hẹn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyLichHen() {
        int row = tblLichHen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch hẹn để hủy!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maLH = modelLichHen.getValueAt(row, COL_LH_MA).toString();
        String tenBN = modelLichHen.getValueAt(row, COL_LH_TENBN).toString();

        String lyDo = JOptionPane.showInputDialog(this,
            "Nhập lý do hủy lịch hẹn [" + maLH + " - " + tenBN + "]:", "Lý do hủy lịch", JOptionPane.QUESTION_MESSAGE);
        if (lyDo == null) return;
        if (lyDo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lý do hủy không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn hủy lịch hẹn [" + maLH + " - " + tenBN + "]?\nLý do: " + lyDo,
            "Xác nhận hủy lịch hẹn", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = lichHenDAO.capNhatTrangThai(maLH, TrangThaiConstants.LH_DA_HUY);
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "HỦY LỊCH HẸN", maLH + " - " + tenBN + " | LĐ: " + lyDo);
                JOptionPane.showMessageDialog(this, "Đã hủy lịch hẹn [" + maLH + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuLichHen();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy lịch hẹn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyDoiGioHen() {
        int row = tblLichHen.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lịch hẹn để đổi giờ!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maLH = modelLichHen.getValueAt(row, COL_LH_MA).toString();
        String tenBN = modelLichHen.getValueAt(row, COL_LH_TENBN).toString();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đổi Giờ Hẹn - " + tenBN, true);
        dlg.setSize(380, 240);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));

        JPanel pnlFields = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlFields.setBorder(new EmptyBorder(20, 24, 10, 24));

        DatePickerField dpNgayMoi = new DatePickerField(LocalDate.now());
        dpNgayMoi.setMinDate(LocalDate.now());

        String[] khungGio = new String[30];
        int[] h = {7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20, 21, 21, 8, 8};
        int[] m = {30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 30, 0, 0, 30};
        khungGio = new String[]{
            "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00",
            "18:00", "18:30", "19:00", "19:30", "20:00", "20:30"
        };
        JComboBox<String> cbGioMoi = new JComboBox<>(khungGio);

        pnlFields.add(new JLabel("Ngày hẹn mới (*):"));
        pnlFields.add(dpNgayMoi);
        pnlFields.add(new JLabel("Giờ hẹn mới (*):"));
        pnlFields.add(cbGioMoi);
        pnlFields.add(new JLabel(""));
        pnlFields.add(new JLabel(""));

        dlg.add(pnlFields, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnHuyDlg = ModernUITheme.createOutlineButton("Hủy", ModernUITheme.TEXT_MUTED);
        JButton btnXacNhan = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 14, Color.WHITE), "Xác nhận đổi giờ", ModernUITheme.PRIMARY, Color.WHITE);
        pnlBtns.add(btnHuyDlg);
        pnlBtns.add(btnXacNhan);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        btnHuyDlg.addActionListener(ev -> dlg.dispose());
        btnXacNhan.addActionListener(ev -> {
            java.sql.Date ngayMoi = dpNgayMoi.getDate();
            String gioStr = cbGioMoi.getSelectedItem() != null ? cbGioMoi.getSelectedItem().toString() : "08:00";
            if (ngayMoi == null) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn ngày hẹn mới!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = lichHenDAO.doiLichHen(maLH, ngayMoi, Time.valueOf(gioStr + ":00"));
            if (ok) {
                ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "ĐỔI GIỜ HẸN", maLH + " → " + ngayMoi + " " + gioStr);
                JOptionPane.showMessageDialog(dlg, "Đã đổi lịch hẹn [" + maLH + "] sang " + ngayMoi + " lúc " + gioStr + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuLichHen();
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Đổi giờ hẹn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.setVisible(true);
    }

    // ===================================================================================
    // PHƯƠNG THỨC CÔNG KHAI (public methods)
    // ===================================================================================

    /**
     * Điều hướng từ TrangChuModern (thanh tra cứu topbar) đến đúng lịch hẹn cụ thể.
     * Mở tab "Lịch hẹn khám" và chọn dòng có maLichHen tương ứng.
     */
    public void selectLichHen(String maLichHen) {
        if (maLichHen == null || maLichHen.trim().isEmpty()) return;

        // Chuyển sang tab Lịch hẹn (index 2)
        tabbedPane.setSelectedIndex(2);

        // Xóa bộ lọc ngày để hiện toàn bộ
        dpLocNgayLH.setLocalDate(null);
        cbLocBacSiLH.setSelectedIndex(0);
        cbLocTrangThaiLH.setSelectedIndex(0);

        // Tải và chọn dòng
        SwingWorker<List<LichHen>, Void> worker = new SwingWorker<List<LichHen>, Void>() {
            @Override
            protected List<LichHen> doInBackground() {
                return lichHenDAO.getAllLichHen();
            }

            @Override
            protected void done() {
                try {
                    List<LichHen> list = get();
                    modelLichHen.setRowCount(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    for (LichHen lh : list) {
                        modelLichHen.addRow(new Object[]{
                            lh.getMaLichHen(), lh.getMaBN(), lh.getMaNhaSi(),
                            lh.getTenBenhNhan() != null ? lh.getTenBenhNhan() : lh.getMaBN(),
                            lh.getSdtBenhNhan() != null ? lh.getSdtBenhNhan() : "",
                            lh.getTenNhaSi() != null ? lh.getTenNhaSi() : lh.getMaNhaSi(),
                            lh.getNgayHen() != null ? sdf.format(lh.getNgayHen()) : "",
                            lh.getGioHen() != null ? lh.getGioHen().toString().substring(0, 5) : "",
                            lh.getLyDoKham() != null ? lh.getLyDoKham() : "",
                            lh.getTrangThai() != null ? lh.getTrangThai() : TrangThaiConstants.LH_DA_LEN_LICH
                        });
                    }
                    // Tìm và chọn dòng
                    String maClean = maLichHen.trim();
                    for (int i = 0; i < modelLichHen.getRowCount(); i++) {
                        if (maClean.equalsIgnoreCase(modelLichHen.getValueAt(i, COL_LH_MA).toString())) {
                            tblLichHen.setRowSelectionInterval(i, i);
                            tblLichHen.scrollRectToVisible(tblLichHen.getCellRect(i, 0, true));
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("PanelLichTrucModern.selectLichHen error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Giữ tương thích ngược với code cũ đang gọi selectPhanCong từ TrangChuModern.
     * Các lời gọi mới nên dùng selectLichHen.
     */
    public void selectPhanCong(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        tabbedPane.setSelectedIndex(0);
        String kw = keyword.trim().toLowerCase();
        for (int i = 0; i < modelPhanCong.getRowCount(); i++) {
            for (int col = COL_PC_HOTEN; col < modelPhanCong.getColumnCount(); col++) {
                Object val = modelPhanCong.getValueAt(i, col);
                if (val != null && val.toString().toLowerCase().contains(kw)) {
                    tblPhanCong.setRowSelectionInterval(i, i);
                    tblPhanCong.scrollRectToVisible(tblPhanCong.getCellRect(i, 0, true));
                    napFormTuBangPhanCong(i);
                    return;
                }
            }
        }
    }

    public void refreshData() {
        taiDuLieuPhanCong();
        if (tabbedPane.getSelectedIndex() == 1) {
            renderLichTuan();
        } else if (tabbedPane.getSelectedIndex() == 2) {
            taiDuLieuLichHen();
        }
    }

    // ===================================================================================
    // TIỆN ÍCH
    // ===================================================================================

    private void napCombobox() {
        cbNhanVien.removeAllItems();
        cbCaTruc.removeAllItems();

        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();
        for (NhanVien nv : dsNV) {
            cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTen() + " (" + nv.getChucDanh() + ")");
        }

        List<CaTruc> dsCa = caTrucDAO.getAllCaTruc();
        for (CaTruc ct : dsCa) {
            String gio = "";
            if (ct.getGioBatDau() != null && ct.getGioKetThuc() != null) {
                gio = " (" + ct.getGioBatDau().toString().substring(0, 5)
                    + " - " + ct.getGioKetThuc().toString().substring(0, 5) + ")";
            }
            cbCaTruc.addItem(ct.getMaCa() + " - " + ct.getTenCa() + gio);
        }

        if (cbCaTruc.getItemCount() == 0) {
            cbCaTruc.addItem("-- Chưa có ca trực, hãy tạo ca mới --");
        }
    }

    private JPanel createFieldItem(String labelText, JComponent comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(ModernUITheme.FONT_BOLD);
        lbl.setForeground(ModernUITheme.TEXT_REGULAR);
        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);
        return pnl;
    }
}
