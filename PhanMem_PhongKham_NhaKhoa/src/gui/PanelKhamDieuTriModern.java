package gui;

import dao.NhaSiDAO;
import entity.HoSoBenhAn;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/** Quy trình khám và điều trị dành riêng cho tài khoản nha sĩ. */
public class PanelKhamDieuTriModern extends JPanel {
    private final TaiKhoan user;
    private final NhaSiDAO dao = new NhaSiDAO();
    private final DefaultTableModel queueModel = new DefaultTableModel(
            new Object[]{"Ngày", "Giờ", "Mã BN", "Bệnh nhân", "Điện thoại", "Tiền sử / dị ứng", "Lý do khám", "Trạng thái", "Lịch hẹn"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel historyModel = new DefaultTableModel(
            new Object[]{"Ngày khám", "Nha sĩ", "Chẩn đoán", "Kết quả", "Trạng thái", "Mã hồ sơ"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable queue, history;
    private JTextArea symptoms, exam, diagnosis, imaging, plan, procedure, outcome, prescription, aftercare;
    private JTextField teeth, followUp;
    private JLabel patientInfo, recordInfo;
    private JComboBox<String> status;
    private String selectedAppointment, selectedPatient, selectedRecord;
    private boolean completedRecord;

    public PanelKhamDieuTriModern(TaiKhoan user) {
        this.user = user;
        setLayout(new BorderLayout(12, 12)); setBackground(ModernUITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 18, 18, 18));
        add(header(), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, queuePanel(), encounterPanel());
        split.setResizeWeight(.32); split.setDividerSize(7); split.setBorder(null); add(split, BorderLayout.CENTER);
    }

    public void refresh() { loadQueue(); }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        JPanel title = new JPanel(new GridLayout(2,1,0,3)); title.setOpaque(false);
        JLabel h = new JLabel("KHÁM & ĐIỀU TRỊ NHA KHOA"); h.setFont(ModernUITheme.FONT_HERO); h.setForeground(ModernUITheme.TEXT_MAIN);
        JLabel sub = new JLabel("Tiếp nhận ca được phân công, ghi nhận khám lâm sàng và theo dõi toàn bộ quá trình điều trị");
        sub.setFont(ModernUITheme.FONT_SMALL); sub.setForeground(ModernUITheme.TEXT_MUTED); title.add(h); title.add(sub); p.add(title,BorderLayout.CENTER);
        JButton refresh = ModernUITheme.createOutlineButton("Tải lại lịch hôm nay", ModernUITheme.TEXT_MUTED); refresh.addActionListener(e -> loadQueue()); p.add(refresh,BorderLayout.EAST);
        return p;
    }

    private JPanel queuePanel() {
        JPanel p = ModernUITheme.createCardPanel(); p.setLayout(new BorderLayout(0,10));
        JLabel h = new JLabel("LỊCH KHÁM ĐƯỢC PHÂN CÔNG"); h.setFont(ModernUITheme.FONT_TITLE); h.setForeground(ModernUITheme.TEXT_MAIN); p.add(h,BorderLayout.NORTH);
        queue = new JTable(queueModel); ModernUITheme.formatModernTable(queue); p.add(new JScrollPane(queue),BorderLayout.CENTER);
        queue.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting() && queue.getSelectedRow()>=0) selectAppointment(queue.convertRowIndexToModel(queue.getSelectedRow())); });
        return p;
    }

    private JPanel encounterPanel() {
        JPanel p = new JPanel(new BorderLayout(0,8)); p.setOpaque(false);
        JPanel patient = ModernUITheme.createCardPanel(); patient.setLayout(new BorderLayout(8,0));
        patientInfo = new JLabel("Chọn một ca khám để bắt đầu"); patientInfo.setFont(ModernUITheme.FONT_BOLD); patientInfo.setForeground(ModernUITheme.PRIMARY_DARK);
        recordInfo = new JLabel("Hồ sơ chưa được mở"); recordInfo.setFont(ModernUITheme.FONT_SMALL); recordInfo.setForeground(ModernUITheme.TEXT_MUTED);
        JPanel labels=new JPanel(new GridLayout(2,1,0,3)); labels.setOpaque(false); labels.add(patientInfo); labels.add(recordInfo); patient.add(labels,BorderLayout.CENTER); p.add(patient,BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Khám, chẩn đoán & điều trị", editor());
        tabs.addTab("Lịch sử điều trị", historyPanel());
        p.add(tabs,BorderLayout.CENTER);
        return p;
    }

    private JPanel editor() {
        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE); form.setBorder(new EmptyBorder(8,10,8,10));
        symptoms=area(2); exam=area(3); imaging=area(2); diagnosis=area(3); plan=area(3); procedure=area(3); outcome=area(2); prescription=area(2); aftercare=area(2);
        teeth=ModernUITheme.createTextField(20); teeth.setToolTipText("VD: Răng 16, 26 hoặc vùng toàn hàm");
        followUp=ModernUITheme.createTextField(12); followUp.setToolTipText("yyyy-MM-dd");
        status=new JComboBox<>(new String[]{"Đang điều trị", "Chờ tái khám", "Hoàn thành"});
        int row=0;
        row=addField(form,row,"Lý do khám / triệu chứng:",symptoms);
        row=addField(form,row,"Khám lâm sàng / tình trạng răng:",exam);
        row=addToothChart(form,row);
        row=addField(form,row,"Răng / vùng điều trị:",teeth);
        row=addField(form,row,"Cận lâm sàng chỉ định / kết quả:",imaging);
        row=addField(form,row,"Chẩn đoán:",diagnosis);
        row=addField(form,row,"Kế hoạch điều trị:",plan);
        row=addField(form,row,"Can thiệp trong buổi khám:",procedure);
        row=addField(form,row,"Kết quả / đáp ứng:",outcome);
        row=addField(form,row,"Đơn thuốc / liều dùng:",prescription);
        row=addField(form,row,"Hướng dẫn chăm sóc sau điều trị:",aftercare);
        JPanel last=new JPanel(new GridLayout(1,4,8,0)); last.setOpaque(false); last.add(new JLabel("Ngày tái khám (yyyy-MM-dd):")); last.add(followUp); last.add(new JLabel("Trạng thái hồ sơ:")); last.add(status);
        GridBagConstraints gc=gc(0,row++); gc.gridwidth=2; form.add(last,gc);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); buttons.setOpaque(false);
        JButton clear=ModernUITheme.createOutlineButton("Hồ sơ mới",ModernUITheme.TEXT_MUTED); clear.addActionListener(e -> clearForm());
        JButton save=ModernUITheme.createButton("Lưu hồ sơ",ModernUITheme.PRIMARY,Color.WHITE); save.addActionListener(e -> save(false));
        JButton finish=ModernUITheme.createButton("Hoàn tất đợt điều trị",ModernUITheme.SUCCESS,Color.WHITE); finish.addActionListener(e -> save(true));
        buttons.add(clear); buttons.add(save); buttons.add(finish); gc=gc(0,row); gc.gridwidth=2; form.add(buttons,gc);
        JScrollPane sc=new JScrollPane(form); sc.setBorder(null); return new JPanel(new BorderLayout()){{setOpaque(false);add(sc);}};
    }

    private int addToothChart(JPanel form,int row) {
        JPanel chart=new JPanel(new GridLayout(2,16,3,3)); chart.setOpaque(false);
        for(int n=18;n>=11;n--) chart.add(toothButton(String.valueOf(n)));
        for(int n=21;n<=28;n++) chart.add(toothButton(String.valueOf(n)));
        for(int n=48;n>=41;n--) chart.add(toothButton(String.valueOf(n)));
        for(int n=31;n<=38;n++) chart.add(toothButton(String.valueOf(n)));
        return addField(form,row,"Sơ đồ răng (chọn để ghi nhận vị trí):",chart);
    }
    private JButton toothButton(String n) { JButton b=new JButton(n); b.setMargin(new Insets(2,2,2,2)); b.setToolTipText("Chọn/bỏ chọn răng "+n); b.addActionListener(e->{ java.util.Set<String> s=new java.util.LinkedHashSet<>(); for(String x:teeth.getText().split(",")) if(!x.trim().isEmpty())s.add(x.trim()); if(!s.add("Răng "+n))s.remove("Răng "+n); teeth.setText(String.join(", ",s)); }); return b; }

    private JPanel historyPanel() { JPanel p=ModernUITheme.createCardPanel(); p.setLayout(new BorderLayout(0,8)); history=new JTable(historyModel); ModernUITheme.formatModernTable(history); history.addMouseListener(new java.awt.event.MouseAdapter(){public void mouseClicked(java.awt.event.MouseEvent e){if(e.getClickCount()==2&&history.getSelectedRow()>=0)showHistoryDetail(history.convertRowIndexToModel(history.getSelectedRow()));}}); p.add(new JScrollPane(history),BorderLayout.CENTER); JLabel tip=new JLabel("Nhấp đúp một hồ sơ để xem đầy đủ thông tin khám, điều trị và dặn dò."); tip.setForeground(ModernUITheme.TEXT_MUTED); p.add(tip,BorderLayout.NORTH); return p; }
    private void showHistoryDetail(int row) { try { HoSoBenhAn h=dao.getHoSo(String.valueOf(historyModel.getValueAt(row,5))); if(h==null)return; String detail="Hồ sơ: "+h.getMaHoSo()+"\nNgày khám: "+h.getNgayKham()+"\nNha sĩ: "+h.getTenNhaSi()+"\nTrạng thái: "+h.getTrangThai()+"\n\nTriệu chứng: "+h.getTrieuChung()+"\nKhám lâm sàng: "+h.getTinhTrangLamSang()+"\nCận lâm sàng: "+h.getKetQuaCanLamSang()+"\nChẩn đoán: "+h.getChanDoan()+"\nVị trí răng: "+h.getViTriRang()+"\nKế hoạch: "+h.getKeHoachDieuTri()+"\nCan thiệp: "+h.getNoiDungCanThiep()+"\nKết quả: "+h.getKetQuaDieuTri()+"\nĐơn thuốc: "+h.getDonThuoc()+"\nHướng dẫn: "+h.getHuongDanSauDieuTri()+"\nNgày tái khám: "+h.getNgayTaiKham()+"\nGhi chú: "+h.getGhiChu(); JTextArea text=area(18); text.setText(detail); text.setEditable(false); JOptionPane.showMessageDialog(this,new JScrollPane(text),"Chi tiết bệnh án",JOptionPane.INFORMATION_MESSAGE); } catch(SQLException ex){showError("Không tải được bệnh án: "+ex.getMessage());} }
    private JTextArea area(int rows) { JTextArea a=new JTextArea(rows,24); a.setLineWrap(true); a.setWrapStyleWord(true); a.setFont(ModernUITheme.FONT_REGULAR); a.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT),new EmptyBorder(5,7,5,7))); return a; }
    private int addField(JPanel p,int row,String label,JComponent c) { GridBagConstraints a=gc(0,row); a.anchor=GridBagConstraints.NORTHWEST; a.weightx=0; a.fill=GridBagConstraints.NONE; p.add(new JLabel(label),a); GridBagConstraints b=gc(1,row++); b.weightx=1; b.fill=GridBagConstraints.HORIZONTAL; p.add(c,b); return row; }
    private GridBagConstraints gc(int x,int y) { GridBagConstraints g=new GridBagConstraints(); g.gridx=x;g.gridy=y;g.insets=new Insets(4,5,4,5);g.anchor=GridBagConstraints.WEST;g.fill=GridBagConstraints.HORIZONTAL;g.weightx=x==1?1:0;return g; }

    private void loadQueue() {
        queueModel.setRowCount(0);
        try { for(NhaSiDAO.CaKham c:dao.getCaKham(user.getMaNV())) queueModel.addRow(new Object[]{c.ngayHen,c.gioHen,c.maBN,c.tenBenhNhan,c.soDienThoai,c.tienSuBenh,c.lyDo,c.trangThai,c.maLichHen}); }
        catch(SQLException ex){ showError("Không tải được lịch khám: "+ex.getMessage()); }
    }
    private void selectAppointment(int row) {
        selectedAppointment=String.valueOf(queueModel.getValueAt(row,8)); selectedPatient=String.valueOf(queueModel.getValueAt(row,2));
        Object historyNote=queueModel.getValueAt(row,5);
        patientInfo.setText("<html>"+queueModel.getValueAt(row,3)+"  |  "+selectedPatient+"  |  "+queueModel.getValueAt(row,4)+"  |  "+queueModel.getValueAt(row,0)+" "+queueModel.getValueAt(row,1)+
                "<br>Tiền sử / dị ứng: "+(historyNote==null||historyNote.toString().trim().isEmpty()?"Chưa ghi nhận":historyNote)+"</html>");
        try { loadHistory(); HoSoBenhAn h=dao.getHoSoTheoLich(selectedAppointment); if(h==null){clearForm();selectedRecord=null;completedRecord=false;recordInfo.setText("Chưa có hồ sơ khám cho ca này");setEditorEnabled(true);}else{populate(h);} }
        catch(SQLException ex){showError("Không tải được hồ sơ: "+ex.getMessage());}
    }
    private void loadHistory() throws SQLException {
        historyModel.setRowCount(0); List<HoSoBenhAn> rows=dao.getLichSu(selectedPatient);
        for(HoSoBenhAn h:rows) historyModel.addRow(new Object[]{h.getNgayKham(),h.getTenNhaSi(),h.getChanDoan(),h.getKetQuaDieuTri(),h.getTrangThai(),h.getMaHoSo()});
    }
    private void populate(HoSoBenhAn h) { selectedRecord=h.getMaHoSo(); completedRecord="Hoàn thành".equalsIgnoreCase(h.getTrangThai())||"Hoan thanh".equalsIgnoreCase(h.getTrangThai()); symptoms.setText(h.getTrieuChung()); exam.setText(h.getTinhTrangLamSang()); imaging.setText(h.getKetQuaCanLamSang()); teeth.setText(h.getViTriRang()); diagnosis.setText(h.getChanDoan()); plan.setText(h.getKeHoachDieuTri()); procedure.setText(h.getNoiDungCanThiep()); outcome.setText(h.getKetQuaDieuTri()); prescription.setText(h.getDonThuoc()); aftercare.setText(h.getHuongDanSauDieuTri()); followUp.setText(h.getNgayTaiKham()==null?"":h.getNgayTaiKham().toString()); status.setSelectedItem(h.getTrangThai()); recordInfo.setText("Mã hồ sơ: "+selectedRecord+"  |  "+h.getNgayKham()+"  |  "+h.getTrangThai()); setEditorEnabled(!completedRecord); }
    private void clearForm() { if(symptoms==null)return; symptoms.setText("");exam.setText("");diagnosis.setText("");imaging.setText("");plan.setText("");procedure.setText("");outcome.setText("");prescription.setText("");aftercare.setText("");teeth.setText("");followUp.setText("");status.setSelectedIndex(0); }
    private void setEditorEnabled(boolean enabled) { for(JComponent c:new JComponent[]{symptoms,exam,diagnosis,imaging,plan,procedure,outcome,prescription,aftercare,teeth,followUp,status})c.setEnabled(enabled); }
    private void save(boolean finish) {
        if(selectedAppointment==null){JOptionPane.showMessageDialog(this,"Hãy chọn ca khám được phân công.","Chưa chọn ca",JOptionPane.WARNING_MESSAGE);return;}
        if(completedRecord){showError("Hồ sơ đã hoàn tất, không thể sửa hoặc ghi đè.");return;}
        if(symptoms.getText().trim().isEmpty()||diagnosis.getText().trim().isEmpty()){showError("Cần nhập triệu chứng/lý do khám và chẩn đoán trước khi lưu.");return;}
        if(finish&&outcome.getText().trim().isEmpty()){showError("Cần ghi kết quả điều trị trước khi hoàn tất hồ sơ.");return;}
        java.sql.Date recheck=null;
        if(!followUp.getText().trim().isEmpty())try{recheck=java.sql.Date.valueOf(followUp.getText().trim());}catch(IllegalArgumentException ex){showError("Ngày tái khám phải đúng định dạng yyyy-MM-dd.");return;}
        String state=finish?"Hoàn thành":(String)status.getSelectedItem();
        try { String id=selectedRecord==null?"HS"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase():selectedRecord;
            dao.luuHoSo(id,selectedPatient,selectedAppointment,user.getMaNV(),symptoms.getText().trim(),diagnosis.getText().trim(),outcome.getText().trim(),"",state,exam.getText().trim(),imaging.getText().trim(),teeth.getText().trim(),plan.getText().trim(),procedure.getText().trim(),prescription.getText().trim(),aftercare.getText().trim(),recheck,user.getTenDangNhap());
            selectedRecord=id;completedRecord="Hoàn thành".equalsIgnoreCase(state);recordInfo.setText("Mã hồ sơ: "+id+"  |  Đã lưu: "+state); if(completedRecord)setEditorEnabled(false); loadHistory();
            JOptionPane.showMessageDialog(this,finish?"Đã hoàn tất và khóa hồ sơ điều trị.":"Đã lưu hồ sơ khám.","Thành công",JOptionPane.INFORMATION_MESSAGE);
        } catch(SQLException ex){showError("Không lưu được hồ sơ: "+ex.getMessage());}
    }
    private void showError(String message){JOptionPane.showMessageDialog(this,message,"Nghiệp vụ nha sĩ",JOptionPane.WARNING_MESSAGE);}
}
