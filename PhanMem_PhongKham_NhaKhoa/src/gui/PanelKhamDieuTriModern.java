package gui;

import dao.DichVuDAO;
import dao.NhaSiDAO;
import dao.ThuocDAO;
import entity.DichVu;
import entity.HoSoBenhAn;
import entity.TaiKhoan;
import entity.Thuoc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Giao diện Quản lý Khám và Điều trị Nha khoa dành cho Nha sĩ (Bác sĩ).
 * Thực hiện trọn vẹn quy trình 5 Use Cases chuẩn y khoa:
 * - Tiếp nhận và khám lâm sàng bệnh nhân (Sơ đồ răng FDI 32 răng tương tác màu)
 * - Chỉ định cận lâm sàng và chẩn đoán bệnh
 * - Tư vấn, lập kế hoạch điều trị, thực hiện thủ thuật và kê đơn thuốc
 * - Tái khám, đánh giá đáp ứng và hoàn tất theo dõi điều trị
 */
public class PanelKhamDieuTriModern extends JPanel {

    private final TaiKhoan user;
    private final NhaSiDAO dao = new NhaSiDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final ThuocDAO thuocDAO = new ThuocDAO();

    // Models cho các bảng
    private final DefaultTableModel queueModel = new DefaultTableModel(new Object[]{
            "Ngày", "Giờ", "Mã BN", "Bệnh nhân", "Điện thoại", "Tiền sử / Dị ứng", "Lý do khám", "Trạng thái", "Mã lịch"
    }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final DefaultTableModel historyModel = new DefaultTableModel(new Object[]{
            "Ngày khám", "Nha sĩ", "Chẩn đoán", "Kết quả điều trị", "Trạng thái", "Mã hồ sơ"
    }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final DefaultTableModel planModel = new DefaultTableModel(new Object[]{"Hạng mục điều trị", "Hoàn thành"}, 0) {
        @Override
        public Class<?> getColumnClass(int column) {
            return column == 1 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0 || column == 1;
        }
    };

    private final DefaultTableModel trackingModel = new DefaultTableModel(new Object[]{
            "Bệnh nhân", "Chẩn đoán", "Trạng thái", "Ngày hẹn tái khám", "Giờ", "Mã hồ sơ"
    }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    // Sơ đồ răng tương tác
    private final Map<String, JButton> toothButtons = new LinkedHashMap<>();
    private final Map<String, String> toothStates = new LinkedHashMap<>();

    // Các thành phần form nhập liệu
    private final JTextArea symptoms = area(3);
    private final JTextArea exam = area(4);
    private final JTextArea imagingOrder = area(3);
    private final JTextArea imagingResult = area(3);
    private final JTextArea diagnosis = area(3);
    private final JTextArea consultation = area(3);
    private final JTextArea procedure = area(3);
    private final JTextArea outcome = area(3);
    private final JTextArea prescription = area(4);
    private final JTextArea aftercare = area(3);
    private final JTextArea recheckAssessment = area(4);

    private final JTextField teeth = ModernUITheme.createTextField(20);
    private final JTextField planItem = ModernUITheme.createTextField(20);
    private final JTextField followUpDate = ModernUITheme.createTextField(11);
    private final JTextField followUpTime = ModernUITheme.createTextField(6);
    private final JTextField recheckDate = ModernUITheme.createTextField(11);
    private final JTextField recheckTime = ModernUITheme.createTextField(6);

    private final JCheckBox consent = new JCheckBox("Bệnh nhân đã được tư vấn đầy đủ và đồng ý với phác đồ/can thiệp");
    private final JCheckBox allergyReviewed = new JCheckBox("Đã đối chiếu an toàn dị ứng & điều kiện dùng thuốc");
    private final JCheckBox scheduleFollowUp = new JCheckBox("Thiết lập lịch hẹn tái khám / theo dõi định kỳ");
    private final JCheckBox scheduleAfterRecheck = new JCheckBox("Đặt lịch tái khám / theo dõi tiếp");

    private final JComboBox<String> toothCondition = new JComboBox<>(new String[]{
            "Sâu răng", "Phục hồi", "Mão răng", "Điều trị tủy", "Mất răng", "Bình thường"
    });

    private final JComboBox<String> nextAction = new JComboBox<>(new String[]{
            "Tiếp tục kế hoạch điều trị", "Điều chỉnh kế hoạch điều trị", "Chuyển sang hoàn tất điều trị"
    });

    private final JLabel status = new JLabel("Đang điều trị");

    // UI Controls
    private JTable queue, history, planTable, planReviewTable, trackingTable;
    private JTabbedPane useCaseTabs;
    private JLabel patientInfo, recordInfo, lblAllergyAlert, testResultHint;
    private JPanel pnlAlertContainer;
    private JTextField txtSearchQueue;
    private JComboBox<String> cboFilterQueue;
    private JLabel lblQueueCount;

    private String selectedAppointment, selectedPatient, selectedRecord, selectedAllergies = "", selectedPatientName = "";
    private String existingFollowUpId;
    private boolean completedRecord;

    private final DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");

    public PanelKhamDieuTriModern(TaiKhoan user) {
        this.user = user;
        setLayout(new BorderLayout(12, 12));
        setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 18, 18, 18));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createQueuePanel(), createEncounterPanel());
        split.setResizeWeight(0.32);
        split.setDividerSize(7);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        loadQueue();
    }

    public void refresh() {
        loadQueue();
        loadTracking();
    }

    /**
     * Điều hướng trực tiếp đến Use Case tương ứng khi bác sĩ click menu bên Sidebar
     */
    public void showUseCase(String useCaseId) {
        if (useCaseTabs == null || useCaseId == null) return;
        if (useCaseId.endsWith("TIEP_NHAN")) useCaseTabs.setSelectedIndex(0);
        else if (useCaseId.endsWith("CHAN_DOAN")) useCaseTabs.setSelectedIndex(1);
        else if (useCaseId.endsWith("DIEU_TRI")) useCaseTabs.setSelectedIndex(2);
        else if (useCaseId.endsWith("TAI_KHAM")) {
            useCaseTabs.setSelectedIndex(3);
            try { if (selectedPatient != null) loadHistory(); } catch (Exception ignored) {}
        }
        else if (useCaseId.endsWith("HOAN_TAT")) {
            useCaseTabs.setSelectedIndex(4);
            loadTracking();
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        JPanel title = new JPanel(new GridLayout(2, 1, 0, 3));
        title.setOpaque(false);
        JLabel heading = new JLabel("QUY TRÌNH KHÁM & ĐIỀU TRỊ NHA KHOA");
        heading.setFont(ModernUITheme.FONT_HERO);
        heading.setForeground(ModernUITheme.TEXT_MAIN);

        String docName = (user != null && user.getHoTenNV() != null) ? user.getHoTenNV() : "Nha sĩ";
        JLabel subtitle = new JLabel("Bác sĩ phụ trách: " + docName + " | Quy trình khám và điều trị nha khoa");
        subtitle.setFont(ModernUITheme.FONT_SMALL);
        subtitle.setForeground(ModernUITheme.TEXT_MUTED);

        title.add(heading);
        title.add(subtitle);
        panel.add(title, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnPrint = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.REPORT, 14, Color.WHITE),
                "In Toa Thuốc & Bệnh Án", ModernUITheme.ACCENT, Color.WHITE);
        btnPrint.addActionListener(e -> inToaThuocVaBenhAn());

        JButton reload = ModernUITheme.createOutlineIconButton(new AppIcon(AppIcon.Type.REFRESH, 13, ModernUITheme.TEXT_MUTED),
                "Tải lại lịch", ModernUITheme.TEXT_MUTED);
        reload.addActionListener(e -> {
            loadQueue();
            loadTracking();
        });

        actions.add(btnPrint);
        actions.add(reload);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel createQueuePanel() {
        JPanel panel = ModernUITheme.createCardPanel();
        panel.setLayout(new BorderLayout(0, 8));

        // Header hàng đợi
        JPanel top = new JPanel(new BorderLayout(8, 6));
        top.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel heading = new JLabel("DANH SÁCH BỆNH NHÂN KHÁM");
        heading.setFont(ModernUITheme.FONT_TITLE);
        heading.setForeground(ModernUITheme.TEXT_MAIN);

        lblQueueCount = ModernUITheme.createBadge("0 ca khám", ModernUITheme.PRIMARY_LIGHT, ModernUITheme.PRIMARY);
        titleRow.add(heading, BorderLayout.WEST);
        titleRow.add(lblQueueCount, BorderLayout.EAST);
        top.add(titleRow, BorderLayout.NORTH);

        // Filter & Search
        JPanel filterRow = new JPanel(new BorderLayout(6, 0));
        filterRow.setOpaque(false);

        txtSearchQueue = ModernUITheme.createTextField(12);
        txtSearchQueue.setToolTipText("Tìm theo tên, SĐT hoặc mã BN...");
        txtSearchQueue.addActionListener(e -> locDanhSachCaKham());

        cboFilterQueue = new JComboBox<>(new String[]{"Tất cả ca khám", "Hôm nay", "Chờ khám & Đang khám"});
        cboFilterQueue.setFont(ModernUITheme.FONT_SMALL);
        cboFilterQueue.addActionListener(e -> locDanhSachCaKham());

        JButton btnSearch = new JButton(new AppIcon(AppIcon.Type.SEARCH, 13, ModernUITheme.PRIMARY));
        btnSearch.setContentAreaFilled(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> locDanhSachCaKham());

        JPanel searchWrap = new JPanel(new BorderLayout(4, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(txtSearchQueue, BorderLayout.CENTER);
        searchWrap.add(btnSearch, BorderLayout.EAST);

        filterRow.add(searchWrap, BorderLayout.CENTER);
        filterRow.add(cboFilterQueue, BorderLayout.EAST);
        top.add(filterRow, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);

        queue = new JTable(queueModel);
        ModernUITheme.formatModernTable(queue);
        queue.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Căn lề & độ rộng cột
        if (queue.getColumnModel().getColumnCount() >= 9) {
            queue.getColumnModel().getColumn(0).setPreferredWidth(85);  // Ngày
            queue.getColumnModel().getColumn(1).setPreferredWidth(55);  // Giờ
            queue.getColumnModel().getColumn(2).setPreferredWidth(65);  // Mã BN
            queue.getColumnModel().getColumn(3).setPreferredWidth(140); // Tên
            queue.getColumnModel().getColumn(4).setPreferredWidth(95);  // SĐT
            queue.getColumnModel().getColumn(5).setPreferredWidth(130); // Tiền sử/dị ứng
            queue.getColumnModel().getColumn(6).setPreferredWidth(160); // Lý do
            queue.getColumnModel().getColumn(7).setPreferredWidth(90);  // Trạng thái
            queue.getColumnModel().getColumn(8).setPreferredWidth(75);  // Mã lịch
        }

        queue.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && queue.getSelectedRow() >= 0) {
                selectAppointment(queue.convertRowIndexToModel(queue.getSelectedRow()));
            }
        });

        panel.add(new JScrollPane(queue), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEncounterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        // Patient banner container
        JPanel patientCard = ModernUITheme.createCardPanel();
        patientCard.setLayout(new BorderLayout(8, 6));

        patientInfo = new JLabel("Vui lòng chọn bệnh nhân / ca khám từ danh sách bên trái để bắt đầu");
        patientInfo.setFont(ModernUITheme.FONT_TITLE);
        patientInfo.setForeground(ModernUITheme.PRIMARY_DARK);

        recordInfo = new JLabel("Chưa mở hồ sơ bệnh án");
        recordInfo.setFont(ModernUITheme.FONT_SMALL);
        recordInfo.setForeground(ModernUITheme.TEXT_MUTED);

        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 3));
        labels.setOpaque(false);
        labels.add(patientInfo);
        labels.add(recordInfo);
        patientCard.add(labels, BorderLayout.CENTER);

        // Thanh cảnh báo dị ứng nổi bật
        pnlAlertContainer = new JPanel(new BorderLayout());
        pnlAlertContainer.setOpaque(false);
        lblAllergyAlert = new JLabel();
        lblAllergyAlert.setOpaque(true);
        lblAllergyAlert.setBackground(new Color(254, 226, 226));
        lblAllergyAlert.setForeground(new Color(185, 28, 28));
        lblAllergyAlert.setFont(ModernUITheme.FONT_BOLD);
        lblAllergyAlert.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 68, 68), 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        lblAllergyAlert.setVisible(false);
        pnlAlertContainer.add(lblAllergyAlert, BorderLayout.CENTER);
        patientCard.add(pnlAlertContainer, BorderLayout.SOUTH);

        panel.add(patientCard, BorderLayout.NORTH);

        // Tabbed Pane cho 5 use case
        useCaseTabs = new JTabbedPane();
        useCaseTabs.setFont(ModernUITheme.FONT_BOLD);
        useCaseTabs.addTab("Tiếp nhận và khám bệnh nhân", intakeTab());
        useCaseTabs.addTab("Chẩn đoán", diagnosisTab());
        useCaseTabs.addTab("Tư vấn và thực hiện điều trị", treatmentTab());
        useCaseTabs.addTab("Tái khám và đánh giá điều trị", recheckTab());
        useCaseTabs.addTab("Hoàn tất và theo dõi điều trị", completionTab());

        useCaseTabs.addChangeListener(e -> {
            int idx = useCaseTabs.getSelectedIndex();
            if (idx == 3) {
                try { if (selectedPatient != null) loadHistory(); } catch (Exception ignored) {}
            } else if (idx == 4) {
                loadTracking();
            }
        });

        panel.add(useCaseTabs, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // TIẾP NHẬN VÀ KHÁM LÂM SÀNG
    // =========================================================================

    private JPanel intakeTab() {
        JPanel form = newForm();
        int row = 0;
        row = addField(form, row, "Lý do khám / Triệu chứng (*):", symptoms);
        row = addField(form, row, "Kết quả khám lâm sàng (*):", exam);
        row = addField(form, row, "Sơ đồ răng (FDI):", toothChart());
        row = addField(form, row, "Răng / Vùng tổn thương:", teeth);

        addSaveButton(form, row, "Lưu tiếp nhận và kết quả khám", () -> saveIntake());
        return scroll(form);
    }

    // =========================================================================
    // CHẨN ĐOÁN
    // =========================================================================

    private JPanel diagnosisTab() {
        JPanel form = newForm();
        int row = 0;
        row = addField(form, row, "Tóm tắt khám lâm sàng:", readonlyArea(exam));

        // Quick selector chỉ định cận lâm sàng
        JPanel pnlCLS = new JPanel(new BorderLayout(6, 0));
        pnlCLS.setOpaque(false);
        pnlCLS.add(imagingOrder, BorderLayout.CENTER);

        JComboBox<String> cboQuickCLS = new JComboBox<>(new String[]{
                "-- Gợi ý chỉ định cận lâm sàng --",
                "Chụp X-quang quanh chóp (Periapical)",
                "Chụp phim toàn cảnh (Panorex)",
                "Chụp CT ConeBeam 3D khảo sát xương",
                "Xét nghiệm tủy răng (Thử điện / Thử nhiệt)",
                "Xét nghiệm công thức máu & đông máu"
        });
        cboQuickCLS.addActionListener(e -> {
            String sel = (String) cboQuickCLS.getSelectedItem();
            if (sel != null && !sel.startsWith("--")) {
                String cur = imagingOrder.getText().trim();
                imagingOrder.setText(cur.isEmpty() ? sel : cur + "\n" + sel);
            }
        });
        pnlCLS.add(cboQuickCLS, BorderLayout.NORTH);
        row = addField(form, row, "Chỉ định cận lâm sàng:", pnlCLS);

        row = addField(form, row, "Kết quả cận lâm sàng:", imagingResult);

        testResultHint = new JLabel("Lưu ý: Nếu có chỉ định cận lâm sàng, cần nhập kết quả trước khi xác nhận chẩn đoán chính thức.");
        testResultHint.setForeground(ModernUITheme.TEXT_MUTED);
        testResultHint.setFont(ModernUITheme.FONT_SMALL);
        row = addField(form, row, "", testResultHint);

        row = addField(form, row, "Chẩn đoán bệnh / Vị trí tổn thương (*):", diagnosis);

        addSaveButton(form, row, "Lưu chỉ định và xác nhận chẩn đoán", () -> saveDiagnosis());
        return scroll(form);
    }

    // =========================================================================
    // TƯ VẤN, THỰC HIỆN ĐIỀU TRỊ VÀ KÊ ĐƠN THUỐC
    // =========================================================================

    private JPanel treatmentTab() {
        JPanel form = newForm();
        int row = 0;
        row = addField(form, row, "Chẩn đoán xác nhận:", readonlyArea(diagnosis));
        row = addField(form, row, "Nội dung tư vấn phương án & chi phí:", consultation);
        row = addField(form, row, "Kế hoạch điều trị (*):", planEditor());

        consent.setOpaque(false);
        consent.setFont(ModernUITheme.FONT_BOLD);
        consent.setForeground(ModernUITheme.PRIMARY_DARK);
        row = addField(form, row, "Đồng thuận điều trị (*):", consent);

        row = addField(form, row, "Can thiệp / Thủ thuật thực hiện:", procedure);
        row = addField(form, row, "Kết quả điều trị buổi này:", outcome);

        // Kê đơn thuốc với chọn nhanh
        row = addField(form, row, "Đơn thuốc & Liều dùng:", prescriptionEditor());

        allergyReviewed.setOpaque(false);
        allergyReviewed.setFont(ModernUITheme.FONT_BOLD);
        allergyReviewed.setForeground(new Color(185, 28, 28));
        row = addField(form, row, "An toàn kê đơn (*):", allergyReviewed);

        row = addField(form, row, "Hướng dẫn chăm sóc sau điều trị:", aftercare);

        // Action buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBtns.setOpaque(false);

        JButton btnInToa = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.REPORT, 14, Color.WHITE),
                "In Toa Thuốc", ModernUITheme.ACCENT, Color.WHITE);
        btnInToa.addActionListener(e -> inToaThuocVaBenhAn());

        JButton btnSave = ModernUITheme.createButton("Lưu tư vấn và điều trị", ModernUITheme.PRIMARY, Color.WHITE);
        btnSave.addActionListener(e -> saveTreatment());

        pnlBtns.add(btnInToa);
        pnlBtns.add(btnSave);

        GridBagConstraints constraints = constraints(0, row);
        constraints.gridwidth = 2;
        form.add(pnlBtns, constraints);

        return scroll(form);
    }

    private JPanel planEditor() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        // Thanh thêm hạng mục kế hoạch có chọn nhanh dịch vụ
        JPanel addPanel = new JPanel(new BorderLayout(6, 0));
        addPanel.setOpaque(false);

        JComboBox<String> cboDichVu = new JComboBox<>();
        cboDichVu.addItem("-- Chọn dịch vụ từ bảng giá để thêm nhanh --");
        try {
            for (DichVu dv : dichVuDAO.getDichVuHoatDong()) {
                cboDichVu.addItem(dv.getTenDichVu() + " (" + dfMoney.format(dv.getGiaNiemYet()) + ")");
            }
        } catch (Exception ignored) {}

        cboDichVu.addActionListener(e -> {
            String sel = (String) cboDichVu.getSelectedItem();
            if (sel != null && !sel.startsWith("--")) {
                planItem.setText(sel);
            }
        });

        JPanel inputBar = new JPanel(new BorderLayout(6, 0));
        inputBar.setOpaque(false);
        inputBar.add(planItem, BorderLayout.CENTER);

        JButton addButton = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.PLUS, 13, Color.WHITE),
                "Thêm hạng mục", ModernUITheme.PRIMARY, Color.WHITE);
        addButton.addActionListener(e -> addPlanItem());
        inputBar.add(addButton, BorderLayout.EAST);

        addPanel.add(cboDichVu, BorderLayout.NORTH);
        addPanel.add(inputBar, BorderLayout.SOUTH);

        planTable = makePlanTable();
        panel.add(addPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(planTable), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(540, 180));
        return panel;
    }

    private JPanel prescriptionEditor() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JComboBox<String> cboThuoc = new JComboBox<>();
        cboThuoc.addItem("-- Chọn thuốc mẫu từ danh mục phòng khám --");
        try {
            for (Thuoc th : thuocDAO.getAllThuoc()) {
                cboThuoc.addItem(th.getTenThuoc() + " - " + th.getCachDungMacDinh());
            }
        } catch (Exception ignored) {}

        cboThuoc.addActionListener(e -> {
            String sel = (String) cboThuoc.getSelectedItem();
            if (sel != null && !sel.startsWith("--")) {
                String cur = prescription.getText().trim();
                prescription.setText(cur.isEmpty() ? "- " + sel : cur + "\n- " + sel);
                kiemTraDiUngKeDon();
            }
        });

        panel.add(cboThuoc, BorderLayout.NORTH);
        panel.add(prescription, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // TÁI KHÁM VÀ ĐÁNH GIÁ ĐIỀU TRỊ
    // =========================================================================

    private JPanel recheckTab() {
        JPanel form = newForm();
        int row = 0;
        row = addField(form, row, "Lịch sử khám & điều trị trước đó (Nhấp đúp xem):", historyTable());
        row = addField(form, row, "Đánh giá đáp ứng & tiến triển (*):", recheckAssessment);
        row = addField(form, row, "Hướng xử lý tiếp theo (*):", nextAction);
        row = addField(form, row, "Kết quả can thiệp trước đó:", readonlyArea(outcome));

        scheduleAfterRecheck.setOpaque(false);
        scheduleAfterRecheck.setFont(ModernUITheme.FONT_BOLD);
        row = addField(form, row, "Lịch theo dõi / tái khám:", scheduleAfterRecheck);

        row = addQuickDateButtons(form, row, recheckDate, recheckTime);
        addSaveButton(form, row, "Lưu kết quả tái khám", () -> saveRecheck());
        return scroll(form);
    }

    // =========================================================================
    // HOÀN TẤT VÀ THEO DÕI ĐIỀU TRỊ
    // =========================================================================

    private JPanel completionTab() {
        JPanel form = newForm();
        int row = 0;
        row = addField(form, row, "Danh sách theo dõi điều trị (Nhấp đúp mở):", trackingTable());
        row = addField(form, row, "Rà soát trạng thái từng hạng mục:", planTable());
        row = addField(form, row, "Đánh giá tổng thể kết quả điều trị (*):", readonlyArea(outcome));
        row = addField(form, row, "Hướng dẫn dặn dò sau điều trị:", readonlyArea(aftercare));

        scheduleFollowUp.setOpaque(false);
        scheduleFollowUp.setFont(ModernUITheme.FONT_BOLD);
        row = addField(form, row, "Lịch hẹn theo dõi định kỳ:", scheduleFollowUp);

        row = addQuickDateButtons(form, row, followUpDate, followUpTime);
        row = addField(form, row, "Trạng thái hồ sơ:", status);

        JLabel note = new JLabel("<html><b>Điều kiện hoàn tất:</b> Đã có chẩn đoán, đánh giá kết quả và 100% các hạng mục kế hoạch đã hoàn thành.</html>");
        note.setForeground(ModernUITheme.TEXT_MUTED);
        note.setFont(ModernUITheme.FONT_SMALL);
        row = addField(form, row, "Yêu cầu hoàn tất:", note);

        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBtns.setOpaque(false);

        JButton btnInPhieu = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.REPORT, 14, Color.WHITE),
                "In Toa Thuốc & Bệnh Án", ModernUITheme.ACCENT, Color.WHITE);
        btnInPhieu.addActionListener(e -> inToaThuocVaBenhAn());

        JButton btnFinish = ModernUITheme.createButton("Xác nhận hoàn tất đợt điều trị", ModernUITheme.SUCCESS, Color.WHITE);
        btnFinish.addActionListener(e -> finishTreatment());

        pnlBtns.add(btnInPhieu);
        pnlBtns.add(btnFinish);

        GridBagConstraints constraints = constraints(0, row);
        constraints.gridwidth = 2;
        form.add(pnlBtns, constraints);

        return scroll(form);
    }

    private int addQuickDateButtons(JPanel form, int row, JTextField dateField, JTextField timeField) {
        JPanel dates = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dates.setOpaque(false);

        dates.add(new JLabel("Ngày:"));
        dates.add(dateField);
        dates.add(new JLabel("Giờ:"));
        dates.add(timeField);

        JButton btn7d = ModernUITheme.createOutlineButton("+7 ngày", ModernUITheme.PRIMARY);
        btn7d.setFont(ModernUITheme.FONT_TINY);
        btn7d.addActionListener(e -> setQuickDate(dateField, 7));

        JButton btn14d = ModernUITheme.createOutlineButton("+14 ngày", ModernUITheme.PRIMARY);
        btn14d.setFont(ModernUITheme.FONT_TINY);
        btn14d.addActionListener(e -> setQuickDate(dateField, 14));

        JButton btn1m = ModernUITheme.createOutlineButton("+1 tháng", ModernUITheme.PRIMARY);
        btn1m.setFont(ModernUITheme.FONT_TINY);
        btn1m.addActionListener(e -> setQuickDate(dateField, 30));

        JButton btn6m = ModernUITheme.createOutlineButton("+6 tháng", ModernUITheme.PRIMARY);
        btn6m.setFont(ModernUITheme.FONT_TINY);
        btn6m.addActionListener(e -> setQuickDate(dateField, 180));

        dates.add(btn7d);
        dates.add(btn14d);
        dates.add(btn1m);
        dates.add(btn6m);

        return addField(form, row, "", dates);
    }

    private void setQuickDate(JTextField dateField, int daysToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysToAdd);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(sdf.format(cal.getTime()));
    }

    // =========================================================================
    // SƠ ĐỒ RĂNG FDI TƯƠNG TÁC
    // =========================================================================

    private JComponent toothChart() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JPanel chart = new JPanel(new GridLayout(2, 16, 2, 2));
        chart.setOpaque(false);

        // Hàm trên: R18->R11 | R21->R28
        for (int n = 18; n >= 11; n--) chart.add(toothButton(String.valueOf(n)));
        for (int n = 21; n <= 28; n++) chart.add(toothButton(String.valueOf(n)));
        // Hàm dưới: R48->R41 | R31->R38
        for (int n = 48; n >= 41; n--) chart.add(toothButton(String.valueOf(n)));
        for (int n = 31; n <= 38; n++) chart.add(toothButton(String.valueOf(n)));

        // Bảng chú giải màu & Thao tác
        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.setOpaque(false);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        legend.setOpaque(false);
        legend.add(new JLabel("Tình trạng răng:"));
        legend.add(toothCondition);

        JButton btnAssignCondition = ModernUITheme.createOutlineButton("Gán cho răng đang chọn", ModernUITheme.PRIMARY);
        btnAssignCondition.setFont(ModernUITheme.FONT_TINY);
        btnAssignCondition.addActionListener(e -> ganTinhTrangChoRangChon());
        legend.add(btnAssignCondition);

        JButton btnResetTeeth = ModernUITheme.createOutlineButton("Tất cả bình thường", ModernUITheme.TEXT_MUTED);
        btnResetTeeth.setFont(ModernUITheme.FONT_TINY);
        btnResetTeeth.addActionListener(e -> resetAllTeethNormal());
        legend.add(btnResetTeeth);

        bottom.add(legend, BorderLayout.NORTH);

        // Dải màu chú giải
        JPanel colorLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colorLegend.setOpaque(false);
        colorLegend.add(createLegendItem("Bình thường", new Color(198, 246, 213), new Color(34, 84, 61)));
        colorLegend.add(createLegendItem("Sâu răng", new Color(254, 215, 215), new Color(155, 44, 44)));
        colorLegend.add(createLegendItem("Điều trị tủy", new Color(233, 216, 253), new Color(85, 60, 154)));
        colorLegend.add(createLegendItem("Mão sứ", new Color(254, 252, 191), new Color(116, 66, 16)));
        colorLegend.add(createLegendItem("Phục hồi / Trám", new Color(190, 227, 248), new Color(44, 82, 130)));
        colorLegend.add(createLegendItem("Mất răng", new Color(203, 213, 225), new Color(71, 85, 105)));

        bottom.add(colorLegend, BorderLayout.SOUTH);

        wrapper.add(chart, BorderLayout.CENTER);
        wrapper.add(bottom, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel createLegendItem(String title, Color bg, Color fg) {
        JLabel lbl = new JLabel(" " + title + " ");
        lbl.setFont(ModernUITheme.FONT_TINY);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setBorder(BorderFactory.createLineBorder(fg.brighter(), 1, true));
        return lbl;
    }

    private JButton toothButton(String number) {
        JButton button = new JButton(number);
        button.setMargin(new Insets(2, 2, 2, 2));
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        toothButtons.put(number, button);
        button.setToolTipText("Nhấp trái để chọn/bỏ chọn răng; nhấp phải để đổi tình trạng nhanh.");

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    toothStates.put(number, String.valueOf(toothCondition.getSelectedItem()));
                    updateToothButton(number);
                }
            }
        });

        button.addActionListener(e -> {
            Set<String> selected = new LinkedHashSet<>();
            for (String value : teeth.getText().split(",")) {
                if (!value.trim().isEmpty()) selected.add(value.trim());
            }
            String tooth = "Răng " + number;
            if (!selected.add(tooth)) selected.remove(tooth);
            teeth.setText(String.join(", ", selected));
        });
        return button;
    }

    private void updateToothButton(String number) {
        JButton button = toothButtons.get(number);
        String condition = toothStates.get(number);
        if (button == null) return;

        if (condition == null) {
            button.setOpaque(false);
            button.setBackground(null);
            button.setForeground(Color.BLACK);
            button.setToolTipText("Răng " + number + ": Chưa đánh giá");
            return;
        }

        button.setOpaque(true);
        switch (condition) {
            case "Bình thường":
                button.setBackground(new Color(198, 246, 213));
                button.setForeground(new Color(34, 84, 61));
                break;
            case "Sâu răng":
                button.setBackground(new Color(254, 215, 215));
                button.setForeground(new Color(155, 44, 44));
                break;
            case "Điều trị tủy":
                button.setBackground(new Color(233, 216, 253));
                button.setForeground(new Color(85, 60, 154));
                break;
            case "Mão răng":
                button.setBackground(new Color(254, 252, 191));
                button.setForeground(new Color(116, 66, 16));
                break;
            case "Phục hồi":
                button.setBackground(new Color(190, 227, 248));
                button.setForeground(new Color(44, 82, 130));
                break;
            case "Mất răng":
                button.setBackground(new Color(203, 213, 225));
                button.setForeground(new Color(71, 85, 105));
                break;
            default:
                button.setBackground(new Color(254, 243, 199));
                button.setForeground(Color.BLACK);
                break;
        }
        button.setToolTipText("Răng " + number + ": " + condition);
    }

    private void ganTinhTrangChoRangChon() {
        String cond = (String) toothCondition.getSelectedItem();
        String currentTeeth = teeth.getText().trim();
        if (currentTeeth.isEmpty()) {
            showError("Vui lòng nhấp chọn ít nhất một chiếc răng trên sơ đồ trước!");
            return;
        }
        for (String part : currentTeeth.split(",")) {
            String num = part.replace("Răng", "").trim();
            if (toothButtons.containsKey(num)) {
                toothStates.put(num, cond);
                updateToothButton(num);
            }
        }
    }

    private void resetAllTeethNormal() {
        for (String num : toothButtons.keySet()) {
            toothStates.put(num, "Bình thường");
            updateToothButton(num);
        }
    }

    // =========================================================================
    // QUẢN LÝ DỮ LIỆU & RÀ SOÁT DỊ ỨNG
    // =========================================================================

    private void loadQueue() {
        queueModel.setRowCount(0);
        try {
            List<NhaSiDAO.CaKham> list = dao.getCaKham(user.getMaNV());
            for (NhaSiDAO.CaKham appointment : list) {
                queueModel.addRow(new Object[]{
                        appointment.ngayHen, appointment.gioHen, appointment.maBN,
                        appointment.tenBenhNhan, appointment.soDienThoai, appointment.tienSuBenh,
                        appointment.lyDo, appointment.trangThai, appointment.maLichHen
                });
            }
            lblQueueCount.setText(list.size() + " ca khám");
        } catch (SQLException ex) {
            showError("Lỗi tải lịch khám: " + ex.getMessage());
        }
    }

    private void locDanhSachCaKham() {
        String keyword = txtSearchQueue.getText().trim().toLowerCase();
        String filter = (String) cboFilterQueue.getSelectedItem();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(new java.util.Date());

        queueModel.setRowCount(0);
        try {
            List<NhaSiDAO.CaKham> list = dao.getCaKham(user.getMaNV());
            int count = 0;
            for (NhaSiDAO.CaKham a : list) {
                boolean matchKeyword = keyword.isEmpty()
                        || (a.tenBenhNhan != null && a.tenBenhNhan.toLowerCase().contains(keyword))
                        || (a.maBN != null && a.maBN.toLowerCase().contains(keyword))
                        || (a.soDienThoai != null && a.soDienThoai.contains(keyword))
                        || (a.lyDo != null && a.lyDo.toLowerCase().contains(keyword));

                boolean matchFilter = true;
                if ("Hôm nay".equals(filter)) {
                    matchFilter = a.ngayHen != null && a.ngayHen.startsWith(todayStr);
                } else if ("Chờ khám & Đang khám".equals(filter)) {
                    matchFilter = a.trangThai != null && (a.trangThai.contains("Chờ") || a.trangThai.contains("Đang khám"));
                }

                if (matchKeyword && matchFilter) {
                    queueModel.addRow(new Object[]{
                            a.ngayHen, a.gioHen, a.maBN, a.tenBenhNhan, a.soDienThoai,
                            a.tienSuBenh, a.lyDo, a.trangThai, a.maLichHen
                    });
                    count++;
                }
            }
            lblQueueCount.setText(count + " ca khám");
        } catch (Exception ex) {
            showError("Lỗi lọc danh sách: " + ex.getMessage());
        }
    }

    private void selectAppointment(int row) {
        selectedAppointment = String.valueOf(queueModel.getValueAt(row, 8));
        selectedPatient = String.valueOf(queueModel.getValueAt(row, 2));
        selectedPatientName = valueAt(row, 3);
        selectedAllergies = valueAt(row, 5);

        // Cập nhật thông tin bệnh nhân
        patientInfo.setText(selectedPatientName + " | Mã BN: " + selectedPatient + " | SĐT: " + valueAt(row, 4)
                + " | Lịch hẹn: " + valueAt(row, 0) + " (" + valueAt(row, 1) + ")");

        // Cảnh báo dị ứng nổi bật
        if (!selectedAllergies.isEmpty() && !selectedAllergies.equalsIgnoreCase("không") && !selectedAllergies.equalsIgnoreCase("không có")) {
            lblAllergyAlert.setText("⚠️ CẢNH BÁO Y KHOA: Bệnh nhân có tiền sử / dị ứng: [" + selectedAllergies + "] - Chú ý an toàn khi kê đơn và dùng thuốc!");
            lblAllergyAlert.setVisible(true);
        } else {
            lblAllergyAlert.setText("✓ Tiền sử bệnh lý: Bình thường / Chưa ghi nhận dị ứng thuốc.");
            lblAllergyAlert.setBackground(new Color(240, 253, 244));
            lblAllergyAlert.setForeground(new Color(22, 101, 52));
            lblAllergyAlert.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(74, 222, 128), 1, true),
                    new EmptyBorder(6, 12, 6, 12)));
            lblAllergyAlert.setVisible(true);
        }

        try {
            loadHistory();
            HoSoBenhAn record = dao.getHoSoTheoLich(selectedAppointment);
            if (record == null) {
                selectedRecord = null;
                completedRecord = false;
                clearEncounterFields();
                List<HoSoBenhAn> previous = dao.getLichSu(selectedPatient);
                if (!previous.isEmpty()) populatePreviousForFollowUp(previous.get(0));
                recordInfo.setText("Hồ sơ mới cho lịch hẹn " + selectedAppointment + " — Có thể tiếp nhận & khám ngay");
            } else {
                populate(record);
            }
        } catch (SQLException ex) {
            showError("Không tải được hồ sơ bệnh nhân: " + ex.getMessage());
        }
    }

    private void kiemTraDiUngKeDon() {
        String allergy = selectedAllergies.toLowerCase();
        String medicine = prescription.getText().toLowerCase();
        if (allergy.isEmpty() || allergy.equals("không") || allergy.equals("không có")) return;

        boolean warning = false;
        String reason = "";

        // Kiểm tra Penicillin & dẫn xuất
        if (allergy.contains("penicillin") || allergy.contains("kháng sinh")) {
            if (medicine.contains("amoxicillin") || medicine.contains("augmentin") || medicine.contains("ampicillin") || medicine.contains("penicillin")) {
                warning = true;
                reason = "Bệnh nhân dị ứng Penicillin. Kháng sinh trong đơn thuộc nhóm Penicillin/Beta-lactam!";
            }
        }
        // Kiểm tra NSAID / Aspirin
        if (allergy.contains("aspirin") || allergy.contains("nsaid") || allergy.contains("giảm đau")) {
            if (medicine.contains("ibuprofen") || medicine.contains("aspirin") || medicine.contains("diclofenac") || medicine.contains("meloxicam")) {
                warning = true;
                reason = "Bệnh nhân có tiền sử dị ứng thuốc giảm đau NSAID/Aspirin!";
            }
        }

        if (warning) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ CẢNH BÁO NGUY HIỂM TƯƠNG TÁC DỊ ỨNG!\n" + reason + "\n\nVui lòng chọn loại thuốc thay thế an toàn cho bệnh nhân!",
                    "Cảnh báo dị ứng nghiêm ngặt", JOptionPane.WARNING_MESSAGE);
            allergyReviewed.setSelected(false);
        }
    }

    private String valueAt(int row, int column) {
        Object value = queueModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void loadHistory() throws SQLException {
        historyModel.setRowCount(0);
        for (HoSoBenhAn item : dao.getLichSu(selectedPatient)) {
            historyModel.addRow(new Object[]{
                    item.getNgayKham(), item.getTenNhaSi(), item.getChanDoan(),
                    item.getKetQuaDieuTri(), item.getTrangThai(), item.getMaHoSo()
            });
        }
    }

    private void loadTracking() {
        trackingModel.setRowCount(0);
        try {
            for (HoSoBenhAn h : dao.getTheoDoiDieuTri(user.getMaNV())) {
                trackingModel.addRow(new Object[]{
                        h.getTenBenhNhan() != null ? h.getTenBenhNhan() : h.getMaBN(),
                        h.getChanDoan(), h.getTrangThai(),
                        h.getNgayTaiKham(), h.getGioTaiKham(), h.getMaHoSo()
                });
            }
        } catch (SQLException ex) {
            showError("Không tải được danh sách theo dõi điều trị: " + ex.getMessage());
        }
    }

    private void populate(HoSoBenhAn h) {
        selectedRecord = h.getMaHoSo();
        existingFollowUpId = h.getMaLichHenTaiKham();
        completedRecord = isCompleted(h.getTrangThai());

        symptoms.setText(value(h.getTrieuChung()));
        exam.setText(value(h.getTinhTrangLamSang()));
        imagingOrder.setText(value(h.getChiDinhCanLamSang()));
        imagingResult.setText(value(h.getKetQuaCanLamSang()));
        diagnosis.setText(value(h.getChanDoan()));
        teeth.setText(value(h.getViTriRang()));
        loadToothMap(h.getSoDoRang());
        consultation.setText(value(h.getGhiChu()));
        loadPlan(h.getKeHoachDieuTri());
        consent.setSelected(h.isXacNhanDongY());
        procedure.setText(value(h.getNoiDungCanThiep()));
        outcome.setText(value(h.getKetQuaDieuTri()));
        prescription.setText(value(h.getDonThuoc()));
        aftercare.setText(value(h.getHuongDanSauDieuTri()));
        recheckAssessment.setText(value(h.getDanhGiaTaiKham()));
        nextAction.setSelectedItem(h.getHuongXuLyTiepTheo() == null ? nextAction.getItemAt(0) : h.getHuongXuLyTiepTheo());

        followUpDate.setText(h.getNgayTaiKham() == null ? "" : h.getNgayTaiKham().toString());
        followUpTime.setText(h.getGioTaiKham() == null ? "09:00" : h.getGioTaiKham().toString().substring(0, 5));
        scheduleFollowUp.setSelected(h.getMaLichHenTaiKham() != null || h.getNgayTaiKham() != null);
        recheckDate.setText(followUpDate.getText());
        recheckTime.setText(followUpTime.getText());
        scheduleAfterRecheck.setSelected(scheduleFollowUp.isSelected());

        status.setText(value(h.getTrangThai()));
        recordInfo.setText("Mã hồ sơ: " + selectedRecord + " | " + (h.getNgayKham() != null ? h.getNgayKham() : "Hôm nay") + " | Trạng thái: " + h.getTrangThai());
        setEditorEnabled(!completedRecord);
    }

    private void populatePreviousForFollowUp(HoSoBenhAn h) {
        String appointment = selectedAppointment;
        populate(h);
        selectedAppointment = appointment;
        selectedRecord = null;
        existingFollowUpId = null;
        completedRecord = false;
        scheduleFollowUp.setSelected(false);
        scheduleAfterRecheck.setSelected(false);
        followUpDate.setText("");
        followUpTime.setText("09:00");
        recheckDate.setText("");
        recheckTime.setText("09:00");
        setEditorEnabled(true);
    }

    private void loadPlan(String encoded) {
        planModel.setRowCount(0);
        if (encoded == null || encoded.trim().isEmpty()) return;
        for (String line : encoded.split("\\R")) {
            String[] values = line.split("\\|", 2);
            boolean done = values.length > 1 && Boolean.parseBoolean(values[1]);
            planModel.addRow(new Object[]{values[0], done});
        }
    }

    private void loadToothMap(String encoded) {
        toothStates.clear();
        for (JButton button : toothButtons.values()) {
            button.setOpaque(false);
            button.setBackground(null);
            button.setForeground(Color.BLACK);
        }
        if (encoded == null) return;
        for (String item : encoded.split(";")) {
            String[] pair = item.split("=", 2);
            if (pair.length == 2) {
                toothStates.put(pair[0], pair[1]);
                updateToothButton(pair[0]);
            }
        }
    }

    private void clearEncounterFields() {
        symptoms.setText(""); exam.setText(""); imagingOrder.setText(""); imagingResult.setText("");
        diagnosis.setText(""); consultation.setText(""); procedure.setText(""); outcome.setText("");
        prescription.setText(""); aftercare.setText(""); recheckAssessment.setText(""); teeth.setText("");
        followUpDate.setText(""); followUpTime.setText("09:00"); consent.setSelected(false);
        allergyReviewed.setSelected(false); scheduleFollowUp.setSelected(false); scheduleAfterRecheck.setSelected(false);
        recheckDate.setText(""); recheckTime.setText("09:00"); planModel.setRowCount(0);
        existingFollowUpId = null;
        toothStates.clear();
        for (String number : toothButtons.keySet()) updateToothButton(number);
        status.setText("Đang điều trị");
        setEditorEnabled(true);
    }

    private void setEditorEnabled(boolean enabled) {
        for (JComponent c : new JComponent[]{
                symptoms, exam, imagingOrder, imagingResult, diagnosis, consultation,
                procedure, outcome, prescription, aftercare, recheckAssessment, teeth, planItem, planTable, planReviewTable,
                followUpDate, followUpTime, recheckDate, recheckTime, consent, allergyReviewed,
                scheduleFollowUp, scheduleAfterRecheck, nextAction, status, toothCondition}) {
            if (c != null) c.setEnabled(enabled);
        }
        for (JButton button : toothButtons.values()) button.setEnabled(enabled);
    }

    // =========================================================================
    // LƯU CÁC BƯỚC KHÁM & ĐIỀU TRỊ
    // =========================================================================

    private void saveIntake() {
        if (!requireAppointment()) return;
        if (symptoms.getText().trim().isEmpty() || exam.getText().trim().isEmpty()) {
            showError("Vui lòng nhập lý do/triệu chứng và kết quả khám lâm sàng trước khi lưu!");
            return;
        }
        HoSoBenhAn h = createRecordObject("Đang điều trị");
        if (h.getChanDoan().isEmpty()) h.setChanDoan("Chưa xác định");
        persist(h, false, false, "Đã lưu kết quả tiếp nhận và khám lâm sàng thành công.");

        int opt = JOptionPane.showConfirmDialog(this,
                "Tiếp nhận và khám lâm sàng hoàn tất!\nBạn có muốn chuyển sang bước [Chẩn đoán bệnh]?",
                "Chuyển bước quy trình", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            useCaseTabs.setSelectedIndex(1);
        }
    }

    private void saveDiagnosis() {
        if (!requireAppointment()) return;
        if (exam.getText().trim().isEmpty()) {
            showError("Cần có kết quả khám lâm sàng trước khi chẩn đoán.");
            return;
        }
        boolean hasOrder = !imagingOrder.getText().trim().isEmpty();
        boolean hasResult = !imagingResult.getText().trim().isEmpty();

        if (hasOrder && !hasResult) {
            HoSoBenhAn h = createRecordObject("Chờ kết quả cận lâm sàng");
            if (h.getChanDoan().isEmpty() || "Chưa xác định".equals(h.getChanDoan())) h.setChanDoan("Chưa kết luận");
            persist(h, false, false, "Đã lưu chỉ định cận lâm sàng. Bệnh nhân đang chờ thực hiện chụp chiếu/xét nghiệm.");
            return;
        }

        if (diagnosis.getText().trim().isEmpty()) {
            showError("Vui lòng nhập chẩn đoán bệnh và vị trí tổn thương trước khi xác nhận!");
            return;
        }

        HoSoBenhAn h = createRecordObject("Đang điều trị");
        persist(h, false, false, "Đã xác nhận chẩn đoán bệnh vào hồ sơ bệnh nhân.");

        int opt = JOptionPane.showConfirmDialog(this,
                "Chẩn đoán đã xác nhận thành công!\nBạn có muốn chuyển sang bước [Tư vấn và kế hoạch điều trị]?",
                "Chuyển bước quy trình", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            useCaseTabs.setSelectedIndex(2);
        }
    }

    private void saveTreatment() {
        if (!requireAppointment()) return;
        if (diagnosis.getText().trim().isEmpty() || "Chưa kết luận".equalsIgnoreCase(diagnosis.getText().trim())
                || "Chưa xác định".equalsIgnoreCase(diagnosis.getText().trim())) {
            showError("Bệnh nhân cần có chẩn đoán xác định trước khi lập và thực hiện kế hoạch điều trị.");
            return;
        }
        if (!consent.isSelected()) {
            showError("Chưa ghi nhận bệnh nhân đồng ý; theo quy chế y khoa không thể tiến hành can thiệp.");
            return;
        }
        if (planModel.getRowCount() == 0) {
            showError("Vui lòng thêm ít nhất một hạng mục vào kế hoạch điều trị.");
            return;
        }

        if (!prescription.getText().trim().isEmpty()) {
            String allergy = selectedAllergies.toLowerCase();
            String medicine = prescription.getText().toLowerCase();
            if (!allergy.isEmpty() && !allergy.equals("không") && !allergy.equals("không có") && !allergyReviewed.isSelected()) {
                showError("Bệnh nhân có tiền sử/dị ứng y khoa. Bắt buộc kiểm tra an toàn thuốc và tích xác nhận đối chiếu!");
                return;
            }
            if (!allergy.isEmpty() && !allergy.equals("không") && !allergy.equals("không có")
                    && medicine.contains(allergy.replace("dị ứng", "").trim())) {
                showError("Đơn thuốc có chứa thành phần trùng với tiền sử dị ứng của bệnh nhân. Vui lòng đổi sang phương án khác!");
                return;
            }
        }

        HoSoBenhAn h = createRecordObject("Đang điều trị");
        persist(h, false, false, "Đã lưu nội dung tư vấn, kế hoạch điều trị và can thiệp thực hiện.");
    }

    private void saveRecheck() {
        if (!requireAppointment()) return;
        if (recheckAssessment.getText().trim().isEmpty()) {
            showError("Vui lòng ghi nhận đánh giá đáp ứng và tiến triển trong lần tái khám.");
            return;
        }
        String action = String.valueOf(nextAction.getSelectedItem());
        String state = action.startsWith("Chuyển sang") ? "Chờ hoàn tất" : "Đang điều trị";
        boolean schedule = scheduleAfterRecheck.isSelected();
        HoSoBenhAn h = createRecordObject(state);
        h.setHuongXuLyTiepTheo(action);
        h.setMaLichHenTaiKham(schedule ? existingFollowUpId : null);
        h.setHuyLichTaiKham(!schedule && existingFollowUpId != null);
        if (schedule && !parseFollowUp(h, recheckDate, recheckTime)) return;
        persist(h, false, schedule, "Đã lưu kết quả tái khám và hướng xử lý tiếp theo.");
    }

    private void finishTreatment() {
        if (!requireAppointment()) return;
        stopEditingPlan();

        if (diagnosis.getText().trim().isEmpty() || "Chưa kết luận".equalsIgnoreCase(diagnosis.getText().trim())
                || "Chưa xác định".equalsIgnoreCase(diagnosis.getText().trim())) {
            showError("Cần hoàn tất chẩn đoán trước khi kết thúc đợt điều trị.");
            return;
        }

        if (planModel.getRowCount() == 0) {
            showError("Chưa có hạng mục nào trong kế hoạch điều trị. Hãy lập kế hoạch trước khi hoàn tất đợt điều trị.");
            return;
        }

        if (hasIncompletePlan()) {
            showError("Còn hạng mục điều trị chưa hoàn thành. Hãy tích hoàn thành các mục đã làm trước khi kết thúc đợt!");
            return;
        }

        if (outcome.getText().trim().isEmpty()) {
            showError("Vui lòng ghi đánh giá tổng thể kết quả điều trị trước khi hoàn tất.");
            return;
        }

        boolean schedule = scheduleFollowUp.isSelected();
        HoSoBenhAn h = createRecordObject(schedule ? "Chờ tái khám" : "Hoàn thành");
        if (schedule && !parseFollowUp(h, followUpDate, followUpTime)) return;

        int answer = JOptionPane.showConfirmDialog(this,
                schedule ? "Xác nhận hoàn tất đợt điều trị và tạo lịch theo dõi định kỳ?"
                        : "Xác nhận hoàn tất đợt điều trị và khóa hồ sơ bệnh án?",
                "Xác nhận hoàn tất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        persist(h, true, schedule, schedule ? "Đã hoàn tất điều trị và tạo lịch hẹn theo dõi." : "Đợt điều trị đã hoàn tất và được lưu trữ an toàn.");
    }

    private boolean parseFollowUp(HoSoBenhAn h, JTextField dateField, JTextField timeField) {
        if (dateField.getText().trim().isEmpty() || timeField.getText().trim().isEmpty()) {
            showError("Đã chọn lịch theo dõi/tái khám. Vui lòng nhập ngày và giờ hẹn.");
            return false;
        }
        try {
            h.setNgayTaiKham(Date.valueOf(dateField.getText().trim()));
            String time = timeField.getText().trim();
            if (time.length() == 5) time += ":00";
            h.setGioTaiKham(Time.valueOf(time));
            return true;
        } catch (IllegalArgumentException ex) {
            showError("Định dạng ngày/giờ không hợp lệ. Vui lòng dùng yyyy-MM-dd và HH:mm (hoặc bấm nút chọn nhanh).");
            return false;
        }
    }

    private HoSoBenhAn createRecordObject(String state) {
        HoSoBenhAn h = new HoSoBenhAn();
        h.setMaHoSo(selectedRecord == null ? "HS" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase() : selectedRecord);
        h.setMaBN(selectedPatient);
        h.setTenBenhNhan(selectedPatientName);
        h.setMaNhaSi(user != null ? user.getMaNV() : "BS001");
        h.setTenNhaSi(user != null && user.getHoTenNV() != null ? user.getHoTenNV() : "BS.CKI Nguyễn Minh Tuấn");
        h.setMaLichHen(selectedAppointment);
        h.setMaLichHenTaiKham(scheduleFollowUp.isSelected() ? existingFollowUpId : null);
        h.setHuyLichTaiKham(!scheduleFollowUp.isSelected() && existingFollowUpId != null);
        h.setTrieuChung(symptoms.getText().trim());
        h.setChanDoan(diagnosis.getText().trim());
        h.setKetQuaDieuTri(outcome.getText().trim());
        h.setGhiChu(consultation.getText().trim());
        h.setTrangThai(state);
        h.setTinhTrangLamSang(exam.getText().trim());
        h.setChiDinhCanLamSang(imagingOrder.getText().trim());
        h.setKetQuaCanLamSang(imagingResult.getText().trim());
        h.setViTriRang(teeth.getText().trim());
        h.setSoDoRang(serializeToothMap());
        h.setKeHoachDieuTri(serializePlan());
        h.setNoiDungCanThiep(procedure.getText().trim());
        h.setDonThuoc(prescription.getText().trim());
        h.setHuongDanSauDieuTri(aftercare.getText().trim());
        h.setDanhGiaTaiKham(recheckAssessment.getText().trim());
        h.setHuongXuLyTiepTheo(String.valueOf(nextAction.getSelectedItem()));
        h.setXacNhanDongY(consent.isSelected());
        return h;
    }

    private void persist(HoSoBenhAn h, boolean finish, boolean schedule, String success) {
        if (completedRecord) {
            showError("Hồ sơ này đã hoàn tất và được khóa chỉnh sửa.");
            return;
        }
        try {
            dao.luuHoSo(h, user != null ? user.getTenDangNhap() : "bacsi_tuan", finish, schedule);
            selectedRecord = h.getMaHoSo();
            existingFollowUpId = h.getMaLichHenTaiKham();
            completedRecord = ("Hoàn thành".equals(h.getTrangThai()) || "Đã hoàn thành".equals(h.getTrangThai())) && finish;
            status.setText(value(h.getTrangThai()));
            recordInfo.setText("Mã hồ sơ: " + selectedRecord + " | Trạng thái: " + h.getTrangThai()
                    + (h.getMaLichHenTaiKham() == null ? "" : " | Lịch hẹn theo dõi: " + h.getMaLichHenTaiKham()));
            setEditorEnabled(!completedRecord);
            loadHistory();
            loadTracking();
            if (completedRecord) loadQueue();
            JOptionPane.showMessageDialog(this, success, "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Lỗi lưu hồ sơ: " + ex.getMessage());
        }
    }

    private boolean requireAppointment() {
        if (selectedAppointment == null || selectedPatient == null) {
            showError("Vui lòng chọn một ca khám từ danh sách bệnh nhân bên trái trước.");
            return false;
        }
        if (completedRecord) {
            showError("Hồ sơ đã hoàn tất và được khóa chỉnh sửa an toàn.");
            return false;
        }
        return true;
    }

    // =========================================================================
    // IN / XUẤT TOA THUỐC & BỆNH ÁN
    // =========================================================================

    private void inToaThuocVaBenhAn() {
        if (selectedPatient == null) {
            showError("Vui lòng chọn một ca khám/bệnh nhân để in toa thuốc & bệnh án!");
            return;
        }

        String docName = (user != null && user.getHoTenNV() != null) ? user.getHoTenNV() : "BS.CKI Nguyễn Minh Tuấn";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String curTimeStr = sdf.format(new java.util.Date());

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 13px; color: #1e293b; margin: 15px; } ")
                .append(".header { text-align: center; border-bottom: 2px solid #0284c7; padding-bottom: 8px; margin-bottom: 12px; } ")
                .append(".title { font-size: 18px; font-weight: bold; color: #0369a1; text-transform: uppercase; margin: 4px 0; } ")
                .append(".sub { font-size: 11px; color: #64748b; } ")
                .append(".alert { background: #fee2e2; color: #b91c1c; font-weight: bold; padding: 6px; border: 1px solid #f87171; border-radius: 4px; margin: 8px 0; } ")
                .append("table { width: 100%; border-collapse: collapse; margin: 6px 0; } ")
                .append("td { padding: 4px; vertical-align: top; } ")
                .append(".label { font-weight: bold; width: 140px; color: #334155; } ")
                .append(".section-title { font-size: 13px; font-weight: bold; color: #0284c7; border-bottom: 1px dashed #cbd5e1; padding-bottom: 3px; margin: 10px 0 4px 0; } ")
                .append(".box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 4px; padding: 8px; white-space: pre-wrap; } ")
                .append("</style></head><body>");

        html.append("<div class='header'>")
                .append("<div style='font-weight:bold; font-size:15px; color:#0f172a;'>PHÒNG KHÁM NHA KHOA ABC</div>")
                .append("<div class='sub'>Địa chỉ: 123 Nguyễn Văn Cừ, Quận 5, TP.HCM | Hotline: 1900 6868</div>")
                .append("<div class='title'>PHIẾU KHÁM & TOA THUỐC ĐIỀU TRỊ</div>")
                .append("<div class='sub'>Ngày in: ").append(curTimeStr).append(" | Mã hồ sơ: ").append(selectedRecord != null ? selectedRecord : "Mới").append("</div>")
                .append("</div>");

        if (!selectedAllergies.isEmpty() && !selectedAllergies.equalsIgnoreCase("không") && !selectedAllergies.equalsIgnoreCase("không có")) {
            html.append("<div class='alert'>⚠️ CẢNH BÁO DỊ ỨNG & TIỀN SỬ: ").append(selectedAllergies).append("</div>");
        }

        html.append("<table>")
                .append("<tr><td class='label'>Bệnh nhân:</td><td><b>").append(selectedPatientName).append("</b></td>")
                .append("<td class='label'>Mã bệnh nhân:</td><td>").append(selectedPatient).append("</td></tr>")
                .append("<tr><td class='label'>Nha sĩ phụ trách:</td><td><b>").append(docName).append("</b></td>")
                .append("<td class='label'>Mã lịch hẹn:</td><td>").append(selectedAppointment).append("</td></tr>")
                .append("<tr><td class='label'>Răng / Vị trí:</td><td>").append(teeth.getText().isEmpty() ? "Toàn hàm" : teeth.getText()).append("</td>")
                .append("<td class='label'>Trạng thái:</td><td>").append(status.getText()).append("</td></tr>")
                .append("</table>");

        html.append("<div class='section-title'>I. CHẨN ĐOÁN & THỦ THUẬT ĐIỀU TRỊ</div>")
                .append("<table>")
                .append("<tr><td class='label'>Triệu chứng:</td><td>").append(symptoms.getText()).append("</td></tr>")
                .append("<tr><td class='label'>Khám lâm sàng:</td><td>").append(exam.getText()).append("</td></tr>")
                .append("<tr><td class='label'>Chẩn đoán:</td><td><b style='color:#0369a1;'>").append(diagnosis.getText()).append("</b></td></tr>")
                .append("<tr><td class='label'>Can thiệp thực hiện:</td><td>").append(procedure.getText()).append("</td></tr>")
                .append("<tr><td class='label'>Kết quả điều trị:</td><td>").append(outcome.getText()).append("</td></tr>")
                .append("</table>");

        if (!prescription.getText().trim().isEmpty()) {
            html.append("<div class='section-title'>II. ĐƠN THUỐC ĐIỀU TRỊ</div>")
                    .append("<div class='box'>").append(prescription.getText().trim()).append("</div>");
        }

        html.append("<div class='section-title'>III. LỜI DẶN DÒ & LỊCH TÁI KHÁM</div>")
                .append("<table>")
                .append("<tr><td class='label'>Chăm sóc sau khám:</td><td>").append(aftercare.getText().isEmpty() ? "Vệ sinh răng miệng sạch sẽ, ăn đồ mềm." : aftercare.getText()).append("</td></tr>");

        String hen = followUpDate.getText().isEmpty() ? recheckDate.getText() : followUpDate.getText();
        if (!hen.isEmpty()) {
            html.append("<tr><td class='label'>Hẹn tái khám:</td><td><b style='color:#dc2626;'>").append(hen).append(" (").append(followUpTime.getText()).append(")</b></td></tr>");
        }
        html.append("</table>");

        html.append("<br><table style='margin-top:20px;'><tr>")
                .append("<td style='text-align:center; width:50%;'><b>Bệnh nhân ký nhận</b><br><span style='font-size:10px; color:#94a3b8;'>(Ký và ghi rõ họ tên)</span></td>")
                .append("<td style='text-align:center; width:50%;'><b>Bác sĩ điều trị</b><br><span style='font-size:10px; color:#94a3b8;'>(Ký và đóng dấu)</span><br><br><br><b>")
                .append(docName).append("</b></td>")
                .append("</tr></table>");

        html.append("</body></html>");

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "In Toa Thuốc & Phiếu Điều Trị", true);
        dlg.setSize(620, 680);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JEditorPane editor = new JEditorPane("text/html", html.toString());
        editor.setEditable(false);
        dlg.add(new JScrollPane(editor), BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnPrintAction = ModernUITheme.createIconButton(new AppIcon(AppIcon.Type.REPORT, 14, Color.WHITE),
                "In ra máy in (Print)", ModernUITheme.PRIMARY, Color.WHITE);
        btnPrintAction.addActionListener(e -> {
            try {
                editor.print();
            } catch (Exception ex) {
                showError("Lỗi kết nối máy in: " + ex.getMessage());
            }
        });

        JButton btnClose = ModernUITheme.createOutlineButton("Đóng", ModernUITheme.TEXT_MUTED);
        btnClose.addActionListener(e -> dlg.dispose());

        pnlBtns.add(btnPrintAction);
        pnlBtns.add(btnClose);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // =========================================================================
    // HELPER UI METHODS
    // =========================================================================

    private void stopEditingPlan() {
        if (planTable != null && planTable.isEditing()) planTable.getCellEditor().stopCellEditing();
    }

    private boolean hasIncompletePlan() {
        for (int row = 0; row < planModel.getRowCount(); row++) {
            if (!Boolean.TRUE.equals(planModel.getValueAt(row, 1))) return true;
        }
        return false;
    }

    private String serializePlan() {
        stopEditingPlan();
        StringBuilder value = new StringBuilder();
        for (int row = 0; row < planModel.getRowCount(); row++) {
            String item = String.valueOf(planModel.getValueAt(row, 0)).replace("|", "/").replace("\n", " ").trim();
            if (item.isEmpty()) continue;
            if (value.length() > 0) value.append('\n');
            value.append(item).append('|').append(Boolean.TRUE.equals(planModel.getValueAt(row, 1)));
        }
        return value.toString();
    }

    private String serializeToothMap() {
        StringBuilder value = new StringBuilder();
        for (Map.Entry<String, String> entry : toothStates.entrySet()) {
            if (value.length() > 0) value.append(';');
            value.append(entry.getKey()).append('=').append(entry.getValue().replace(";", ",").replace("=", ":"));
        }
        return value.toString();
    }

    private void addPlanItem() {
        String value = planItem.getText().trim();
        if (value.isEmpty()) return;
        planModel.addRow(new Object[]{value, Boolean.FALSE});
        planItem.setText("");
    }

    private JTable makePlanTable() {
        JTable table = new JTable(planModel);
        ModernUITheme.formatModernTable(table);
        table.setRowHeight(26);
        return table;
    }

    private JComponent planTable() {
        if (planReviewTable == null) planReviewTable = makePlanTable();
        planReviewTable.setPreferredScrollableViewportSize(new Dimension(520, 130));
        return new JScrollPane(planReviewTable);
    }

    private JComponent historyTable() {
        history = new JTable(historyModel);
        ModernUITheme.formatModernTable(history);
        history.setPreferredScrollableViewportSize(new Dimension(520, 150));
        history.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && history.getSelectedRow() >= 0) {
                    showHistoryDetail(history.convertRowIndexToModel(history.getSelectedRow()));
                }
            }
        });
        JScrollPane scroll = new JScrollPane(history);
        scroll.setPreferredSize(new Dimension(540, 180));
        return scroll;
    }

    private JComponent trackingTable() {
        if (trackingTable == null) {
            trackingTable = new JTable(trackingModel);
            ModernUITheme.formatModernTable(trackingTable);
            trackingTable.setPreferredScrollableViewportSize(new Dimension(520, 130));
            trackingTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && trackingTable.getSelectedRow() >= 0) {
                        openTrackingRecord(trackingTable.convertRowIndexToModel(trackingTable.getSelectedRow()));
                    }
                }
            });
        }
        JScrollPane scroll = new JScrollPane(trackingTable);
        scroll.setPreferredSize(new Dimension(540, 150));
        return scroll;
    }

    private void openTrackingRecord(int row) {
        try {
            HoSoBenhAn h = dao.getHoSo(String.valueOf(trackingModel.getValueAt(row, 5)));
            if (h == null) return;
            selectedPatient = h.getMaBN();
            selectedPatientName = h.getTenBenhNhan();
            selectedAppointment = h.getMaLichHen();
            selectedAllergies = "";
            selectedRecord = h.getMaHoSo();
            patientInfo.setText("Hồ sơ theo dõi: " + h.getMaHoSo() + " | Bệnh nhân: " + (selectedPatientName != null ? selectedPatientName : selectedPatient));
            populate(h);
            useCaseTabs.setSelectedIndex(4);
        } catch (SQLException ex) {
            showError("Không mở được hồ sơ theo dõi: " + ex.getMessage());
        }
    }

    private void showHistoryDetail(int row) {
        try {
            HoSoBenhAn h = dao.getHoSo(String.valueOf(historyModel.getValueAt(row, 5)));
            if (h == null) return;
            String detail = "MÃ HỒ SƠ: " + h.getMaHoSo() + "\nNgày khám: " + h.getNgayKham()
                    + "\nNha sĩ: " + h.getTenNhaSi() + "\nTrạng thái: " + h.getTrangThai()
                    + "\n\n1. Triệu chứng: " + h.getTrieuChung()
                    + "\n2. Khám lâm sàng: " + h.getTinhTrangLamSang()
                    + "\n3. Chỉ định cận lâm sàng: " + h.getChiDinhCanLamSang()
                    + "\n4. Kết quả cận lâm sàng: " + h.getKetQuaCanLamSang()
                    + "\n5. Chẩn đoán: " + h.getChanDoan()
                    + "\n6. Vị trí răng: " + h.getViTriRang()
                    + "\n7. Kế hoạch: " + h.getKeHoachDieuTri()
                    + "\n8. Can thiệp: " + h.getNoiDungCanThiep()
                    + "\n9. Kết quả: " + h.getKetQuaDieuTri()
                    + "\n10. Đơn thuốc: " + h.getDonThuoc()
                    + "\n11. Dặn dò: " + h.getHuongDanSauDieuTri()
                    + "\n12. Đánh giá tái khám: " + h.getDanhGiaTaiKham()
                    + "\n13. Hướng xử lý: " + h.getHuongXuLyTiepTheo()
                    + "\n14. Lịch hẹn tiếp: " + h.getNgayTaiKham() + " " + h.getGioTaiKham();

            JTextArea text = area(20);
            text.setText(detail);
            text.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(text), "Chi tiết hồ sơ bệnh án cũ", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Không tải được chi tiết hồ sơ: " + ex.getMessage());
        }
    }

    private JPanel newForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(12, 14, 12, 14));
        teeth.setToolTipText("Chọn răng trên sơ đồ FDI hoặc nhập tên răng, ví dụ R16, R26");
        followUpDate.setToolTipText("yyyy-MM-dd");
        followUpTime.setToolTipText("HH:mm");
        recheckDate.setToolTipText("yyyy-MM-dd");
        recheckTime.setToolTipText("HH:mm");
        return form;
    }

    private JPanel scroll(JPanel form) {
        JScrollPane pane = new JScrollPane(form);
        pane.setBorder(null);
        pane.getVerticalScrollBar().setUnitIncrement(14);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(pane, BorderLayout.CENTER);
        return wrapper;
    }

    private int addField(JPanel form, int row, String label, JComponent component) {
        GridBagConstraints left = constraints(0, row);
        left.anchor = GridBagConstraints.NORTHWEST;
        left.weightx = 0;
        left.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ModernUITheme.FONT_BOLD);
        lbl.setForeground(ModernUITheme.TEXT_MAIN);
        form.add(lbl, left);

        GridBagConstraints right = constraints(1, row++);
        right.weightx = 1;
        right.fill = GridBagConstraints.HORIZONTAL;
        form.add(component, right);
        return row;
    }

    private void addSaveButton(JPanel form, int row, String title, Runnable action) {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton save = ModernUITheme.createButton(title, ModernUITheme.PRIMARY, Color.WHITE);
        save.addActionListener(e -> action.run());
        buttons.add(save);
        GridBagConstraints constraints = constraints(0, row);
        constraints.gridwidth = 2;
        form.add(buttons, constraints);
    }

    private GridBagConstraints constraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = x == 1 ? 1 : 0;
        return c;
    }

    private JTextArea area(int rows) {
        JTextArea text = new JTextArea(rows, 28);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(ModernUITheme.FONT_REGULAR);
        text.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT),
                new EmptyBorder(5, 7, 5, 7)));
        return text;
    }

    private JScrollPane readonlyArea(JTextArea text) {
        JTextArea view = area(Math.max(2, text.getRows()));
        view.setEditable(false);
        view.setBackground(new Color(248, 250, 252));
        text.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() { view.setText(text.getText()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        view.setText(text.getText());
        return new JScrollPane(view);
    }

    private boolean isCompleted(String value) {
        return value != null && (value.equalsIgnoreCase("Hoàn thành") || value.equalsIgnoreCase("Đã hoàn thành") || value.equalsIgnoreCase("Đã hoàn tất"));
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Nghiệp vụ nha sĩ", JOptionPane.WARNING_MESSAGE);
    }
}
