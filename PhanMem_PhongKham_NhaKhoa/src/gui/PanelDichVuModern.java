package gui;

import dao.DichVuDAO;
import dao.ThongKeDAO;
import entity.DichVu;
import entity.LichSuGiaDichVu;
import entity.TrangThaiConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện Dịch Vụ & Bảng Giá Thủ Thuật Nha Khoa
 * Hỗ trợ phân quyền, quản lý nhóm dịch vụ, lịch sử thay đổi giá, sort số chuẩn, xuất CSV/In
 */
public class PanelDichVuModern extends JPanel {

    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final DecimalFormat dfTien = new DecimalFormat("#,###");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtMaDV, txtTenDV, txtDonGia, txtThoiGian, txtMoTa, txtTimKiem;
    private JComboBox<String> cbNhomDichVu, cbTrangThai;
    private JComboBox<String> cbLocNhom, cbLocTrangThai;
    private JButton btnThem, btnCapNhat, btnDoiTrangThai, btnLamMoi, btnTimKiem, btnLichSuGia, btnXuatCSV, btnIn;
    private JLabel lblStatusLoading;

    private final String[] NHOM_DICH_VU_LIST = {
        "Khám & tư vấn",
        "Nhổ răng",
        "Trám & nội nha",
        "Phục hình sứ",
        "Implant",
        "Chỉnh nha",
        "Tẩy trắng",
        "Nha chu",
        "Khác"
    };

    private boolean isUpdatingPriceText = false;
    private double currentSelectedOldPrice = -1;

    public PanelDichVuModern() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        initUI();
        taiDuLieu();
    }

    private void initUI() {
        // 1. HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("DANH MỤC DỊCH VỤ & BẢNG GIÁ ĐIỀU TRỊ");
        lblTitle.setFont(ModernUITheme.FONT_HERO);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Quản lý danh mục kỹ thuật nha khoa, quy định giá niêm yết, phân nhóm và theo dõi lịch sử điều chỉnh giá");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Header Action: Xuất CSV, In, Tải lại
        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlHeaderActions.setOpaque(false);

        lblStatusLoading = new JLabel("");
        lblStatusLoading.setFont(ModernUITheme.FONT_SMALL);
        lblStatusLoading.setForeground(ModernUITheme.PRIMARY);

        btnLichSuGia = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REPORT, 14, ModernUITheme.PRIMARY), "Lịch sử giá", ModernUITheme.PRIMARY);
        btnXuatCSV = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.SAVE, 14, ModernUITheme.TEXT_MUTED), "Xuất CSV", ModernUITheme.TEXT_MUTED);
        btnIn = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.PRINT, 14, ModernUITheme.TEXT_MUTED), "In bảng giá", ModernUITheme.TEXT_MUTED);
        JButton btnTaiLai = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 14, ModernUITheme.TEXT_MUTED), "Tải lại", ModernUITheme.TEXT_MUTED);

        pnlHeaderActions.add(lblStatusLoading);
        pnlHeaderActions.add(btnLichSuGia);
        pnlHeaderActions.add(btnXuatCSV);
        pnlHeaderActions.add(btnIn);
        pnlHeaderActions.add(btnTaiLai);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. MAIN SPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.62);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);

        // TOP: BẢNG DỊCH VỤ + THANH BỘ LỌC
        JPanel pnlTableCard = ModernUITheme.createCardPanel();
        pnlTableCard.setLayout(new BorderLayout(0, 10));

        // Filter Bar
        JPanel pnlFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFilterBar.setOpaque(false);

        txtTimKiem = ModernUITheme.createTextField(16);
        txtTimKiem.setToolTipText("Tìm kiếm theo mã, tên hoặc mô tả dịch vụ...");
        btnTimKiem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SEARCH, 14, Color.WHITE), "Tìm", ModernUITheme.PRIMARY, Color.WHITE);

        List<String> listNhomFilter = new ArrayList<>();
        listNhomFilter.add("Tất cả nhóm kỹ thuật");
        for (String s : NHOM_DICH_VU_LIST) listNhomFilter.add(s);
        cbLocNhom = new JComboBox<>(listNhomFilter.toArray(new String[0]));

        cbLocTrangThai = new JComboBox<>(new String[]{
            "Tất cả trạng thái",
            TrangThaiConstants.DV_DANG_CUNG_CAP,
            TrangThaiConstants.DV_TAM_NGUNG
        });

        pnlFilterBar.add(new JLabel("Tra cứu:"));
        pnlFilterBar.add(txtTimKiem);
        pnlFilterBar.add(btnTimKiem);
        pnlFilterBar.add(new JLabel("  Nhóm:"));
        pnlFilterBar.add(cbLocNhom);
        pnlFilterBar.add(new JLabel("  Trạng thái:"));
        pnlFilterBar.add(cbLocTrangThai);
        pnlTableCard.add(pnlFilterBar, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] cols = {
            "Mã DV", "Tên Dịch Vụ Nha Khoa", "Nhóm Kỹ Thuật", "Đơn Giá Niêm Yết", "Thời Gian (Phút)",
            "Lượt Sử Dụng", "Doanh Thu 30 Ngày", "Trạng Thái", "Mô Tả Kỹ Thuật"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 3: // Đơn giá
                    case 6: // Doanh thu 30 ngày
                        return Double.class;
                    case 4: // Thời gian
                    case 5: // Lượt sử dụng
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };

        table = new JTable(tableModel);
        ModernUITheme.formatModernTable(table);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Render định dạng tiền tệ và căn lề cho các cột số
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Number) {
                    setText(dfTien.format(((Number) value).doubleValue()) + " đ");
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // Render màu trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String st = value != null ? value.toString() : "";
                    if (TrangThaiConstants.DV_DANG_CUNG_CAP.equalsIgnoreCase(st)) {
                        setForeground(ModernUITheme.SUCCESS);
                        setFont(ModernUITheme.FONT_BOLD);
                    } else {
                        setForeground(ModernUITheme.DANGER);
                        setFont(ModernUITheme.FONT_REGULAR);
                    }
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT));
        pnlTableCard.add(scrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(pnlTableCard);

        // BOTTOM: FORM CARD
        JPanel pnlFormCard = ModernUITheme.createCardPanel();
        pnlFormCard.setLayout(new BorderLayout(12, 12));

        JLabel lblFormTitle = new JLabel("THÔNG TIN CHI TIẾT & ĐIỀU CHỈNH BẢNG GIÁ");
        lblFormTitle.setFont(ModernUITheme.FONT_TITLE);
        lblFormTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlFormCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 4, 16, 10));
        pnlFields.setOpaque(false);

        txtMaDV = ModernUITheme.createTextField(10);
        txtMaDV.setEditable(false);
        txtMaDV.setBackground(new Color(241, 245, 249));

        txtTenDV = ModernUITheme.createTextField(20);
        cbNhomDichVu = new JComboBox<>(NHOM_DICH_VU_LIST);

        txtDonGia = ModernUITheme.createTextField(14);
        txtDonGia.setToolTipText("Nhập số tiền VNĐ. Tự động thêm dấu phân cách.");
        txtThoiGian = ModernUITheme.createTextField(8);
        txtThoiGian.setText("30");

        cbTrangThai = new JComboBox<>(new String[]{
            TrangThaiConstants.DV_DANG_CUNG_CAP,
            TrangThaiConstants.DV_TAM_NGUNG
        });

        txtMoTa = ModernUITheme.createTextField(25);

        pnlFields.add(createFieldItem("Mã dịch vụ:", txtMaDV));
        pnlFields.add(createFieldItem("Tên dịch vụ nha khoa (*):", txtTenDV));
        pnlFields.add(createFieldItem("Nhóm kỹ thuật chuyên môn (*):", cbNhomDichVu));
        pnlFields.add(createFieldItem("Đơn giá niêm yết (VNĐ) (*):", txtDonGia));
        pnlFields.add(createFieldItem("Thời gian thực hiện (5 - 600 phút):", txtThoiGian));
        pnlFields.add(createFieldItem("Trạng thái cung cấp:", cbTrangThai));
        pnlFields.add(createFieldItem("Mô tả kỹ thuật điều trị:", txtMoTa));

        // Ô trống bổ sung để đều lưới 2x4
        JPanel pnlEmpty = new JPanel();
        pnlEmpty.setOpaque(false);
        pnlFields.add(pnlEmpty);

        pnlFormCard.add(pnlFields, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnThem = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 15, Color.WHITE), "Thêm dịch vụ", ModernUITheme.PRIMARY, Color.WHITE);
        btnCapNhat = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.SAVE, 15, Color.WHITE), "Lưu điều chỉnh", ModernUITheme.ACCENT, Color.WHITE);
        btnDoiTrangThai = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.DELETE, 15, Color.WHITE), "Ngừng dịch vụ", ModernUITheme.DANGER, Color.WHITE);
        btnLamMoi = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 15, ModernUITheme.TEXT_MUTED), "Làm mới form", ModernUITheme.TEXT_MUTED);

        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnDoiTrangThai);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnThem);
        pnlFormCard.add(pnlButtons, BorderLayout.SOUTH);

        splitPane.setBottomComponent(pnlFormCard);
        add(splitPane, BorderLayout.CENTER);

        // Bắt sự kiện tự format số tiền khi gõ
        setupPriceAutoFormat();

        // Kiểm tra phân quyền: Chỉ Quản lý mới được Thêm / Sửa / Ngừng
        apDungPhanQuyen();

        // Sự kiện tương tác
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                napFormTuBang(modelRow);
            }
        });

        btnThem.addActionListener(e -> xuLyThem());
        btnCapNhat.addActionListener(e -> xuLyCapNhat());
        btnDoiTrangThai.addActionListener(e -> xuLyDoiTrangThai());
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        btnTimKiem.addActionListener(e -> apDungBoLoc());
        txtTimKiem.addActionListener(e -> apDungBoLoc());
        cbLocNhom.addActionListener(e -> apDungBoLoc());
        cbLocTrangThai.addActionListener(e -> apDungBoLoc());

        btnTaiLai.addActionListener(e -> {
            txtTimKiem.setText("");
            cbLocNhom.setSelectedIndex(0);
            cbLocTrangThai.setSelectedIndex(0);
            taiDuLieu();
        });

        btnLichSuGia.addActionListener(e -> hienThiDialogLichSuGia());
        btnXuatCSV.addActionListener(e -> xuatBangGiaCSV());
        btnIn.addActionListener(e -> inBangGia());
    }

    private void apDungPhanQuyen() {
        boolean isQL = SessionManager.isQuanLy();
        if (!isQL) {
            btnThem.setEnabled(false);
            btnCapNhat.setEnabled(false);
            btnDoiTrangThai.setEnabled(false);
            btnThem.setToolTipText("Chỉ Quản lý mới có quyền tạo mới dịch vụ");
            btnCapNhat.setToolTipText("Chỉ Quản lý mới có quyền điều chỉnh giá dịch vụ");
            btnDoiTrangThai.setToolTipText("Chỉ Quản lý mới có quyền ngừng/kích hoạt dịch vụ");
        }
    }

    private void setupPriceAutoFormat() {
        txtDonGia.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                formatPrice();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                formatPrice();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                formatPrice();
            }

            private void formatPrice() {
                if (isUpdatingPriceText) return;
                SwingUtilities.invokeLater(() -> {
                    String raw = txtDonGia.getText().replaceAll("[^0-9]", "");
                    if (raw.isEmpty()) return;
                    try {
                        isUpdatingPriceText = true;
                        long val = Long.parseLong(raw);
                        String formatted = dfTien.format(val);
                        if (!formatted.equals(txtDonGia.getText())) {
                            txtDonGia.setText(formatted);
                        }
                    } catch (Exception ignored) {
                    } finally {
                        isUpdatingPriceText = false;
                    }
                });
            }
        });
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

    public void taiDuLieu() {
        lblStatusLoading.setText("Đang tải dữ liệu...");
        SwingWorker<List<DichVu>, Void> worker = new SwingWorker<List<DichVu>, Void>() {
            @Override
            protected List<DichVu> doInBackground() {
                return dichVuDAO.getAllDichVu();
            }

            @Override
            protected void done() {
                try {
                    List<DichVu> list = get();
                    tableModel.setRowCount(0);
                    for (DichVu dv : list) {
                        int luotDung = dichVuDAO.getSoLuotSuDung(dv.getMaDichVu());
                        double doanhThu = dichVuDAO.getDoanhThu30Ngay(dv.getMaDichVu());

                        tableModel.addRow(new Object[]{
                            dv.getMaDichVu(),
                            dv.getTenDichVu(),
                            dv.getNhomDichVu() != null ? dv.getNhomDichVu() : "Khám & tư vấn",
                            dv.getGiaNiemYet(),
                            dv.getThoiGianDuKien(),
                            luotDung,
                            doanhThu,
                            dv.getTrangThai() != null ? dv.getTrangThai() : TrangThaiConstants.DV_DANG_CUNG_CAP,
                            dv.getMoTa() != null ? dv.getMoTa() : ""
                        });
                    }
                    xoaTrangForm();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PanelDichVuModern.this, "Lỗi khi tải dữ liệu dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    lblStatusLoading.setText("");
                }
            }
        };
        worker.execute();
    }

    private void napFormTuBang(int modelRow) {
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) return;

        txtMaDV.setText(tableModel.getValueAt(modelRow, 0).toString());
        txtTenDV.setText(tableModel.getValueAt(modelRow, 1).toString());

        String nhom = tableModel.getValueAt(modelRow, 2) != null ? tableModel.getValueAt(modelRow, 2).toString() : "Khám & tư vấn";
        cbNhomDichVu.setSelectedItem(nhom);

        double gia = (Double) tableModel.getValueAt(modelRow, 3);
        currentSelectedOldPrice = gia;
        isUpdatingPriceText = true;
        txtDonGia.setText(dfTien.format(gia));
        isUpdatingPriceText = false;

        txtThoiGian.setText(tableModel.getValueAt(modelRow, 4) != null ? tableModel.getValueAt(modelRow, 4).toString() : "30");

        String trangThai = tableModel.getValueAt(modelRow, 7) != null ? tableModel.getValueAt(modelRow, 7).toString() : TrangThaiConstants.DV_DANG_CUNG_CAP;
        cbTrangThai.setSelectedItem(trangThai);

        txtMoTa.setText(tableModel.getValueAt(modelRow, 8) != null ? tableModel.getValueAt(modelRow, 8).toString() : "");

        // Đổi trạng thái nút ngừng / kích hoạt lại
        if (TrangThaiConstants.DV_TAM_NGUNG.equalsIgnoreCase(trangThai)) {
            btnDoiTrangThai.setText("Kích hoạt lại");
            btnDoiTrangThai.setBackground(ModernUITheme.SUCCESS);
        } else {
            btnDoiTrangThai.setText("Ngừng dịch vụ");
            btnDoiTrangThai.setBackground(ModernUITheme.DANGER);
        }
    }

    private void xoaTrangForm() {
        txtMaDV.setText(dichVuDAO.sinhMaDVMoi());
        txtTenDV.setText("");
        cbNhomDichVu.setSelectedIndex(0);
        isUpdatingPriceText = true;
        txtDonGia.setText("");
        isUpdatingPriceText = false;
        txtThoiGian.setText("30");
        cbTrangThai.setSelectedItem(TrangThaiConstants.DV_DANG_CUNG_CAP);
        txtMoTa.setText("");
        currentSelectedOldPrice = -1;

        btnDoiTrangThai.setText("Ngừng dịch vụ");
        btnDoiTrangThai.setBackground(ModernUITheme.DANGER);
        table.clearSelection();
    }

    private void apDungBoLoc() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String nhomChon = cbLocNhom.getSelectedItem() != null ? cbLocNhom.getSelectedItem().toString() : "Tất cả nhóm kỹ thuật";
        String ttChon = cbLocTrangThai.getSelectedItem() != null ? cbLocTrangThai.getSelectedItem().toString() : "Tất cả trạng thái";

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                // Lọc nhóm
                if (!"Tất cả nhóm kỹ thuật".equals(nhomChon)) {
                    String nhom = entry.getStringValue(2);
                    if (!nhomChon.equalsIgnoreCase(nhom)) return false;
                }
                // Lọc trạng thái
                if (!"Tất cả trạng thái".equals(ttChon)) {
                    String tt = entry.getStringValue(7);
                    if (!ttChon.equalsIgnoreCase(tt)) return false;
                }
                // Lọc từ khóa (Mã, Tên, Mô tả)
                if (!keyword.isEmpty()) {
                    String ma = entry.getStringValue(0).toLowerCase();
                    String ten = entry.getStringValue(1).toLowerCase();
                    String moTa = entry.getStringValue(8).toLowerCase();
                    return ma.contains(keyword) || ten.contains(keyword) || moTa.contains(keyword);
                }
                return true;
            }
        };
        rowSorter.setRowFilter(rf);
    }

    private boolean kiemTraHopLe(boolean isThemMoi) {
        String tenDV = txtTenDV.getText().trim();
        if (tenDV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên dịch vụ nha khoa không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenDV.requestFocus();
            return false;
        }

        String maDV = txtMaDV.getText().trim();
        if (dichVuDAO.kiemTraTenDichVuTrung(tenDV, isThemMoi ? null : maDV)) {
            JOptionPane.showMessageDialog(this, "Tên dịch vụ \"" + tenDV + "\" đã tồn tại trong hệ thống! Vui lòng đặt tên khác.", "Trùng tên dịch vụ", JOptionPane.WARNING_MESSAGE);
            txtTenDV.requestFocus();
            return false;
        }

        String dgStr = txtDonGia.getText().trim().replaceAll("[^0-9]", "");
        if (dgStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn giá niêm yết không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return false;
        }
        try {
            double dg = Double.parseDouble(dgStr);
            if (dg <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá niêm yết phải lớn hơn 0 VNĐ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtDonGia.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return false;
        }

        String tgStr = txtThoiGian.getText().trim();
        try {
            int tg = Integer.parseInt(tgStr);
            if (tg < 5 || tg > 600) {
                JOptionPane.showMessageDialog(this, "Thời gian thực hiện dự kiến phải từ 5 đến 600 phút!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtThoiGian.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thời gian dự kiến phải là số phút nguyên dương!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtThoiGian.requestFocus();
            return false;
        }

        return true;
    }

    private void xuLyThem() {
        if (!SessionManager.isQuanLy()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện thao tác này!", "Từ chối phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraHopLe(true)) return;

        String maDV = txtMaDV.getText().trim();
        if (maDV.isEmpty()) {
            maDV = dichVuDAO.sinhMaDVMoi();
        }
        String tenDV = txtTenDV.getText().trim();
        String nhomDV = cbNhomDichVu.getSelectedItem() != null ? cbNhomDichVu.getSelectedItem().toString() : "Khám & tư vấn";
        double donGia = Double.parseDouble(txtDonGia.getText().trim().replaceAll("[^0-9]", ""));
        int thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : TrangThaiConstants.DV_DANG_CUNG_CAP;
        String moTa = txtMoTa.getText().trim();

        DichVu dv = new DichVu(maDV, tenDV, donGia, thoiGian, trangThai, moTa, nhomDV);
        boolean thanhCong = dichVuDAO.themDichVu(dv);
        if (thanhCong) {
            // Ghi lịch sử giá khởi điểm
            dichVuDAO.ghiLichSuGia(maDV, 0, donGia, SessionManager.getTenDangNhapHienTai(), "Tạo mới dịch vụ");
            ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "Thêm dịch vụ", "Mã DV: " + maDV + " (" + tenDV + ")");

            JOptionPane.showMessageDialog(this, "Thêm dịch vụ [" + maDV + " - " + tenDV + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm dịch vụ. Vui lòng kiểm tra lại kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        if (!SessionManager.isQuanLy()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện thao tác này!", "Từ chối phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        String maDV = txtMaDV.getText().trim();
        if (selectedRow < 0 && (maDV.isEmpty() || maDV.equals(dichVuDAO.sinhMaDVMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dịch vụ trong danh sách để cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraHopLe(false)) return;

        String tenDV = txtTenDV.getText().trim();
        String nhomDV = cbNhomDichVu.getSelectedItem() != null ? cbNhomDichVu.getSelectedItem().toString() : "Khám & tư vấn";
        double donGiaMoi = Double.parseDouble(txtDonGia.getText().trim().replaceAll("[^0-9]", ""));
        int thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
        String trangThai = cbTrangThai.getSelectedItem() != null ? cbTrangThai.getSelectedItem().toString() : TrangThaiConstants.DV_DANG_CUNG_CAP;
        String moTa = txtMoTa.getText().trim();

        // Kiểm tra biến động giá chênh lệch > 50%
        if (currentSelectedOldPrice > 0 && Math.abs(donGiaMoi - currentSelectedOldPrice) / currentSelectedOldPrice > 0.5) {
            double phanTram = Math.abs(donGiaMoi - currentSelectedOldPrice) / currentSelectedOldPrice * 100;
            String msg = String.format("CẢNH BÁO: Mức giá mới (%s đ) chênh lệch %.1f%% so với giá cũ (%s đ)!\nBạn có chắc chắn muốn áp dụng mức giá niêm yết này không?",
                    dfTien.format(donGiaMoi), phanTram, dfTien.format(currentSelectedOldPrice));
            int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác nhận chênh lệch giá lớn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Nếu giá thay đổi, hỏi lý do điều chỉnh
        String lyDo = "Điều chỉnh giá định kỳ";
        if (currentSelectedOldPrice > 0 && currentSelectedOldPrice != donGiaMoi) {
            String inputLyDo = JOptionPane.showInputDialog(this, "Nhập lý do thay đổi đơn giá (để lưu vào lịch sử giá):", "Lý do đổi giá", JOptionPane.QUESTION_MESSAGE);
            if (inputLyDo != null && !inputLyDo.trim().isEmpty()) {
                lyDo = inputLyDo.trim();
            }
        }

        DichVu dv = new DichVu(maDV, tenDV, donGiaMoi, thoiGian, trangThai, moTa, nhomDV);
        boolean thanhCong = dichVuDAO.capNhatDichVu(dv);
        if (thanhCong) {
            if (currentSelectedOldPrice > 0 && currentSelectedOldPrice != donGiaMoi) {
                dichVuDAO.ghiLichSuGia(maDV, currentSelectedOldPrice, donGiaMoi, SessionManager.getTenDangNhapHienTai(), lyDo);
            }
            ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "Cập nhật dịch vụ", "Mã DV: " + maDV + " (" + tenDV + ")");

            JOptionPane.showMessageDialog(this, "Cập nhật thông tin dịch vụ [" + maDV + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieu();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng kiểm tra lại kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyDoiTrangThai() {
        if (!SessionManager.isQuanLy()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện thao tác này!", "Từ chối phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = table.getSelectedRow();
        String maDV = txtMaDV.getText().trim();
        if (selectedRow < 0 && (maDV.isEmpty() || maDV.equals(dichVuDAO.sinhMaDVMoi()))) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dịch vụ trong danh sách!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            maDV = tableModel.getValueAt(modelRow, 0).toString();
        }

        DichVu dv = dichVuDAO.getDichVuByMa(maDV);
        if (dv == null) return;

        boolean isDangCungCap = TrangThaiConstants.DV_DANG_CUNG_CAP.equalsIgnoreCase(dv.getTrangThai());

        if (isDangCungCap) {
            // Đang hoạt động -> Chuyển sang Tạm ngừng hoặc Xóa
            boolean daDung = dichVuDAO.daTungSuDung(maDV);
            String message;
            if (daDung) {
                message = "Dịch vụ [" + maDV + " - " + dv.getTenDichVu() + "] ĐÃ TỪNG ĐƯỢC SỬ DỤNG trong điều trị/hóa đơn.\n" +
                          "Hệ thống sẽ chuyển trạng thái sang \"Tạm ngừng cung cấp\" để bảo toàn dữ liệu lịch sử.\n" +
                          "Bạn có muốn tiếp tục?";
            } else {
                message = "Dịch vụ [" + maDV + " - " + dv.getTenDichVu() + "] CHƯA TỪNG ĐƯỢC SỬ DỤNG.\n" +
                          "Bạn có chắc muốn xóa hoặc tạm ngừng dịch vụ này?";
            }

            int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận ngừng dịch vụ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean kq = dichVuDAO.xoaDichVu(maDV);
                if (kq) {
                    ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "Ngừng/Xóa DV", "Mã DV: " + maDV);
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái ngừng dịch vụ [" + maDV + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieu();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật dịch vụ này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Đang tạm ngừng -> Kích hoạt lại
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn kích hoạt lại dịch vụ [" + maDV + " - " + dv.getTenDichVu() + "] sang trạng thái \"Đang cung cấp\"?",
                    "Kích hoạt lại dịch vụ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean kq = dichVuDAO.chuyenTrangThai(maDV, TrangThaiConstants.DV_DANG_CUNG_CAP);
                if (kq) {
                    ThongKeDAO.ghiLog(SessionManager.getTenDangNhapHienTai(), "Kích hoạt lại DV", "Mã DV: " + maDV);
                    JOptionPane.showMessageDialog(this, "Dịch vụ [" + maDV + "] đã được kích hoạt cung cấp trở lại!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieu();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể kích hoạt lại dịch vụ này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void hienThiDialogLichSuGia() {
        int selectedRow = table.getSelectedRow();
        String maDV = txtMaDV.getText().trim();
        String tenDV = txtTenDV.getText().trim();

        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            maDV = tableModel.getValueAt(modelRow, 0).toString();
            tenDV = tableModel.getValueAt(modelRow, 1).toString();
        }

        if (maDV.isEmpty() || maDV.equals(dichVuDAO.sinhMaDVMoi())) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dịch vụ trong bảng để xem lịch sử điều chỉnh giá!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "LỊCH SỬ THAY ĐỔI GIÁ - " + maDV + " (" + tenDV + ")", true);
        dlg.setSize(750, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(12, 12));

        JPanel pnlContent = new JPanel(new BorderLayout(0, 10));
        pnlContent.setBackground(ModernUITheme.BG_MAIN);
        pnlContent.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblDlgTitle = new JLabel("NHẬT KÝ ĐIỀU CHỈNH GIÁ NIÊM YẾT: " + tenDV.toUpperCase());
        lblDlgTitle.setFont(ModernUITheme.FONT_TITLE);
        lblDlgTitle.setForeground(ModernUITheme.PRIMARY_DARK);
        pnlContent.add(lblDlgTitle, BorderLayout.NORTH);

        String[] subCols = {"STT", "Giá Cũ (VNĐ)", "Giá Mới (VNĐ)", "Mức Chênh Lệch", "Thời Gian Áp Dụng", "Người Thay Đổi", "Lý Do"};
        DefaultTableModel subModel = new DefaultTableModel(subCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable subTable = new JTable(subModel);
        ModernUITheme.formatModernTable(subTable);

        List<LichSuGiaDichVu> listLS = dichVuDAO.getLichSuGia(maDV);
        if (listLS.isEmpty()) {
            subModel.addRow(new Object[]{"-", "-", "-", "-", "Chưa có lần thay đổi giá nào", "-", "-"});
        } else {
            int stt = 1;
            for (LichSuGiaDichVu ls : listLS) {
                double chenhLech = ls.getGiaMoi() - ls.getGiaCu();
                String clStr = (chenhLech >= 0 ? "+" : "") + dfTien.format(chenhLech) + " đ";
                subModel.addRow(new Object[]{
                    stt++,
                    dfTien.format(ls.getGiaCu()) + " đ",
                    dfTien.format(ls.getGiaMoi()) + " đ",
                    clStr,
                    ls.getNgayHieuLuc() != null ? sdf.format(ls.getNgayHieuLuc()) : "",
                    ls.getNguoiThayDoi() != null ? ls.getNguoiThayDoi() : "",
                    ls.getLyDo() != null ? ls.getLyDo() : ""
                });
            }
        }

        pnlContent.add(new JScrollPane(subTable), BorderLayout.CENTER);

        JButton btnDong = ModernUITheme.createOutlineIconButton(null, "Đóng", ModernUITheme.TEXT_MUTED);
        btnDong.addActionListener(e -> dlg.dispose());
        JPanel pnlDong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlDong.setOpaque(false);
        pnlDong.add(btnDong);
        pnlContent.add(pnlDong, BorderLayout.SOUTH);

        dlg.add(pnlContent);
        dlg.setVisible(true);
    }

    private void xuatBangGiaCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file Bảng giá dịch vụ");
        chooser.setSelectedFile(new File("Bang_Gia_Dich_Vu_" + System.currentTimeMillis() + ".csv"));

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8))) {
                // Ghi UTF-8 BOM
                pw.write('\ufeff');

                pw.println("BẢNG GIÁ NIÊM YẾT DỊCH VỤ NHA KHOA ABC");
                pw.println("Thời gian xuất: " + sdf.format(new java.util.Date()));
                pw.println();

                // Header
                pw.println("Mã DV,Tên dịch vụ,Nhóm kỹ thuật,Đơn giá niêm yết (VNĐ),Thời gian (Phút),Lượt dùng,Doanh thu 30 ngày (VNĐ),Trạng thái,Mô tả");

                for (int i = 0; i < table.getRowCount(); i++) {
                    int mr = table.convertRowIndexToModel(i);
                    String ma = tableModel.getValueAt(mr, 0).toString();
                    String ten = "\"" + tableModel.getValueAt(mr, 1).toString().replace("\"", "\"\"") + "\"";
                    String nhom = "\"" + tableModel.getValueAt(mr, 2).toString().replace("\"", "\"\"") + "\"";
                    double gia = (Double) tableModel.getValueAt(mr, 3);
                    int tg = (Integer) tableModel.getValueAt(mr, 4);
                    int luot = (Integer) tableModel.getValueAt(mr, 5);
                    double dt = (Double) tableModel.getValueAt(mr, 6);
                    String tt = tableModel.getValueAt(mr, 7).toString();
                    String moTa = "\"" + (tableModel.getValueAt(mr, 8) != null ? tableModel.getValueAt(mr, 8).toString().replace("\"", "\"\"") : "") + "\"";

                    pw.printf("%s,%s,%s,%.0f,%d,%d,%.0f,%s,%s\n", ma, ten, nhom, gia, tg, luot, dt, tt, moTa);
                }

                JOptionPane.showMessageDialog(this, "Xuất file CSV thành công: " + fileToSave.getAbsolutePath(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu file CSV: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void inBangGia() {
        try {
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat("BẢNG GIÁ DỊCH VỤ NHA KHOA ABC"),
                    new java.text.MessageFormat("Trang {0}"),
                    true, null, true, null);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Đã gửi lệnh in bảng giá thành công!", "In ấn", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this, "Lỗi in: " + pe.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        taiDuLieu();
    }
}
