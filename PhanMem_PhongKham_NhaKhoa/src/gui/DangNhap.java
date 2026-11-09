package gui;

import connectDB.ConnectDB;
import connectDB.PasswordUtils;
import dao.NhanVienDAO;
import entity.NhanVien;
import entity.PhienDangNhap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Màn hình Đăng Nhập Hệ Thống Phòng Khám Nha Khoa ABC.
 * Chuẩn hóa Giai đoạn 5:
 * - Đăng nhập bằng MÃ NHÂN VIÊN + MẬT KHẨU.
 * - Loại bỏ hoàn toàn ComboBox chọn vai trò (vai trò được xác định tự động từ
 * CSDL).
 * - Sử dụng PhienDangNhap để lưu thông tin nhân viên đăng nhập cho toàn bộ
 * phiên làm việc.
 */
public class DangNhap extends JFrame {

    private JTextField txtMaNV;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JButton btnLogin, btnExit;

    private BufferedImage imgLogin;
    private BufferedImage imgLogo;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public DangNhap() {
        setTitle("Đăng Nhập Hệ Thống - Nha Khoa ABC");
        setSize(880, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadImages();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // 1. CỘT TRÁI: BANNER HÌNH ẢNH NHA KHOA
        JPanel pnlBanner = createBannerPanel();
        mainPanel.add(pnlBanner, BorderLayout.WEST);

        // 2. CỘT PHẢI: FORM ĐĂNG NHẬP CHUYÊN NGHIỆP
        JPanel pnlFormContainer = createFormPanel();
        mainPanel.add(pnlFormContainer, BorderLayout.CENTER);

        // Thiết lập phím Enter mặc định
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };

        txtMaNV.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        getRootPane().setDefaultButton(btnLogin);
    }

    private void loadImages() {
        String[] possibleLoginPaths = {
                "PhanMem_PhongKham_NhaKhoa/images/Login.jpg",
                "images/Login.jpg",
                "../images/Login.jpg"
        };
        for (String p : possibleLoginPaths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    imgLogin = ImageIO.read(f);
                    break;
                } catch (Exception ignored) {
                }
            }
        }

        String[] possibleLogoPaths = {
                "PhanMem_PhongKham_NhaKhoa/images/dental_logo.png",
                "images/dental_logo.png",
                "../images/dental_logo.png"
        };
        for (String p : possibleLogoPaths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    imgLogo = ImageIO.read(f);
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    private JPanel createBannerPanel() {
        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                if (imgLogin != null) {
                    double imgW = imgLogin.getWidth();
                    double imgH = imgLogin.getHeight();
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(imgLogin, x, y, drawW, drawH, null);
                } else {
                    GradientPaint grad = new GradientPaint(
                            0, 0, UITheme.SIDEBAR_BG,
                            w, h, UITheme.PRIMARY_DARK);
                    g2.setPaint(grad);
                    g2.fillRect(0, 0, w, h);
                }
                g2.dispose();
            }
        };
        banner.setPreferredSize(new Dimension(380, 0));
        return banner;
    }

    private JPanel createFormPanel() {
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(420, 460));
        card.setBorder(new EmptyBorder(25, 30, 20, 30));

        // LOGO + TIÊU ĐỀ FORM
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (imgLogo != null) {
            Image scaledLogo = imgLogo.getScaledInstance(42, 42, Image.SCALE_SMOOTH);
            pnlHeader.add(new JLabel(new ImageIcon(scaledLogo)));
        } else {
            JLabel lblIco = new JLabel("[ABC]");
            lblIco.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblIco.setForeground(UITheme.PRIMARY);
            pnlHeader.add(lblIco);
        }

        JPanel pnlHeaderText = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlHeaderText.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Đăng nhập bằng Mã nhân viên và mật khẩu");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        pnlHeaderText.add(lblTitle);
        pnlHeaderText.add(lblSub);
        pnlHeader.add(pnlHeaderText);

        card.add(pnlHeader);
        card.add(Box.createVerticalStrut(20));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(20));

        // 1. MÃ NHÂN VIÊN
        addLabel(card, "Mã nhân viên:", AppIcon.Type.STAFF);
        txtMaNV = UITheme.createTextField(20);
        txtMaNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtMaNV.setPreferredSize(new Dimension(360, 40));
        txtMaNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtMaNV.setToolTipText("Nhập mã nhân viên: QL001 (Quản lý), BS001 (Bác sĩ), LT001 (Lễ tân), YT001 (Y tá)...");
        txtMaNV.setText("");
        card.add(txtMaNV);

        card.add(Box.createVerticalStrut(14));

        // 2. MẬT KHẨU
        addLabel(card, "Mật khẩu:", AppIcon.Type.LOCK);
        txtPassword = new JPasswordField("");
        txtPassword.setFont(UITheme.FONT_REGULAR);
        txtPassword.setCaretColor(UITheme.PRIMARY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setPreferredSize(new Dimension(360, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtPassword);

        // HIỂN THỊ MẬT KHẨU
        card.add(Box.createVerticalStrut(6));
        chkShowPass = new JCheckBox("Hiển thị mật khẩu");
        chkShowPass.setFont(UITheme.FONT_SMALL);
        chkShowPass.setForeground(UITheme.TEXT_MUTED);
        chkShowPass.setOpaque(false);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        char defaultEcho = txtPassword.getEchoChar();
        chkShowPass.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : defaultEcho);
        });
        card.add(chkShowPass);

        card.add(Box.createVerticalStrut(22));

        // NÚT BẤM (ĐĂNG NHẬP & THOÁT)
        JPanel pnlBtns = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlBtns.setOpaque(false);
        pnlBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pnlBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnExit = UITheme.createOutlineButton("Thoát", UITheme.TEXT_MUTED);
        btnExit.addActionListener(e -> System.exit(0));

        btnLogin = UITheme.createButton("Đăng nhập", UITheme.PRIMARY, Color.WHITE);
        btnLogin.addActionListener(e -> handleLogin());

        pnlBtns.add(btnExit);
        pnlBtns.add(btnLogin);
        card.add(pnlBtns);

        // Trạng thái kết nối CSDL
        JPanel pnlDbStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
        pnlDbStatus.setOpaque(false);
        pnlDbStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblDbStatus = new JLabel("Đang kiểm tra kết nối CSDL...");
        lblDbStatus.setFont(UITheme.FONT_TINY);
        lblDbStatus.setForeground(UITheme.TEXT_MUTED);
        pnlDbStatus.add(lblDbStatus);
        card.add(pnlDbStatus);

        new Thread(() -> {
            boolean ok = ConnectDB.isConnected();
            if (!ok) {
                java.sql.Connection c = ConnectDB.getConnection();
                ok = (c != null);
            }
            final boolean isConnected = ok;
            SwingUtilities.invokeLater(() -> {
                if (isConnected) {
                    lblDbStatus.setText("● CSDL SQL Server: QLPHONGKHAM (Đã kết nối)");
                    lblDbStatus.setForeground(new Color(22, 101, 52));
                } else {
                    lblDbStatus.setText("● CSDL SQL Server: Kết nối thất bại!");
                    lblDbStatus.setForeground(UITheme.DANGER);
                }
            });
        }).start();

        pnlRight.add(card);
        return pnlRight;
    }

    private void addLabel(JPanel panel, String text, AppIcon.Type iconType) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIco = new JLabel(new AppIcon(iconType, 13, UITheme.PRIMARY));
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BOLD);
        label.setForeground(UITheme.TEXT_REGULAR);

        pnl.add(lblIco);
        pnl.add(label);
        panel.add(pnl);
        panel.add(Box.createVerticalStrut(5));
    }

    private void handleLogin() {
        String maNV = txtMaNV.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (maNV.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ Mã nhân viên và mật khẩu!",
                    "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Kiểm tra kết nối CSDL SQL Server thật (Chỉ nhận dữ liệu thật, không chạy
        // Demo)
        if (!ConnectDB.isConnected() && ConnectDB.getConnection() == null) {
            JOptionPane.showMessageDialog(this,
                    "Kết nối SQL Server thất bại!\n\n" +
                            "Hệ thống chỉ hoạt động với cơ sở dữ liệu thật.\n" +
                            "Vui lòng khởi động SQL Server (dịch vụ MSSQLSERVER), kiểm tra CSDL 'QLPHONGKHAM' và thử lại.",
                    "Kết nối SQL thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Xác thực thông tin trực tiếp từ CSDL thật qua NhanVienDAO.dangNhap
        NhanVien nhanVien = nhanVienDAO.dangNhap(maNV, password);

        if (nhanVien == null) {
            JOptionPane.showMessageDialog(this,
                    "Mã nhân viên hoặc mật khẩu không chính xác trong CSDL!\n\nVui lòng kiểm tra lại thông tin đăng nhập.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if ("Nghỉ việc".equalsIgnoreCase(nhanVien.getTrangThai())
                || "Bị khóa".equalsIgnoreCase(nhanVien.getTrangThai())) {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản của nhân viên [" + nhanVien.getHoTen() + "] đang ở trạng thái '"
                            + nhanVien.getTrangThai() + "'!\nVui lòng liên hệ ban quản trị.",
                    "Tài khoản bị hạn chế", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // BẢO MẬT GIAI ĐOẠN 2: Nếu đăng nhập bằng mật khẩu mặc định 123 -> Bắt buộc đổi
        // mật khẩu mới
        if (PasswordUtils.isDefaultPassword(password)) {
            boolean doiThanhCong = yeuCauDoiMatKhau(nhanVien);
            if (!doiThanhCong) {
                JOptionPane.showMessageDialog(this,
                        "Bạn chưa hoàn thành đổi mật khẩu mới.\nVui lòng đổi mật khẩu để đăng nhập vào phần mềm!",
                        "Đăng nhập bị từ chối", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Lưu thông tin vào phiên làm việc hiện tại
        PhienDangNhap.setNhanVien(nhanVien);

        // Mở màn hình chính với nhân viên đã xác thực
        this.dispose();
        new TrangChu(nhanVien).setVisible(true);
    }

    /**
     * Hộp thoại bắt buộc đổi mật khẩu đối với tài khoản mới hoặc tài khoản vừa được
     * reset về 123
     */
    private boolean yeuCauDoiMatKhau(NhanVien nhanVien) {
        JOptionPane.showMessageDialog(this,
                "Tài khoản [" + nhanVien.getMaNV() + " - " + nhanVien.getHoTen()
                        + "] đang sử dụng mật khẩu mặc định (123).\n" +
                        "Vì lý do an toàn bảo mật, bạn PHẢI đổi mật khẩu mới để tiếp tục sử dụng hệ thống!",
                "Yêu cầu đổi mật khẩu bảo mật", JOptionPane.WARNING_MESSAGE);

        JDialog dialog = new JDialog(this, "Đổi Mật Khẩu Bắt Buộc - Nha Khoa ABC", true);
        dialog.setSize(440, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("THIẾT LẬP MẬT KHẨU MỚI CHO TÀI KHOẢN");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        pnl.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 12));
        pnlForm.setOpaque(false);

        JPasswordField txtNewPass = new JPasswordField();
        JPasswordField txtConfirmPass = new JPasswordField();

        pnlForm.add(new JLabel("Mật khẩu mới (*):"));
        pnlForm.add(txtNewPass);
        pnlForm.add(new JLabel("Xác nhận mật khẩu (*):"));
        pnlForm.add(txtConfirmPass);
        pnl.add(pnlForm, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);
        JButton btnHuy = UITheme.createOutlineButton("Hủy bỏ", UITheme.TEXT_MUTED);
        JButton btnXacNhan = UITheme.createButton("Lưu mật khẩu mới", UITheme.PRIMARY, Color.WHITE);

        final boolean[] success = { false };

        btnHuy.addActionListener(e -> dialog.dispose());

        btnXacNhan.addActionListener(e -> {
            String newP = new String(txtNewPass.getPassword()).trim();
            String confP = new String(txtConfirmPass.getPassword()).trim();
            if (newP.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu mới không được để trống!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
                txtNewPass.requestFocus();
                return;
            }
            if (newP.length() < 3) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu phải có độ dài tối thiểu 3 ký tự!", "Nhắc nhở",
                        JOptionPane.WARNING_MESSAGE);
                txtNewPass.requestFocus();
                return;
            }
            if ("123".equals(newP) || "123456".equals(newP)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu mới không được trùng với mật khẩu mặc định (123)!",
                        "Lỗi bảo mật", JOptionPane.ERROR_MESSAGE);
                txtNewPass.requestFocus();
                return;
            }
            if (!newP.equals(confP)) {
                JOptionPane.showMessageDialog(dialog, "Xác nhận mật khẩu không trùng khớp!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                txtConfirmPass.requestFocus();
                return;
            }

            boolean ok = new dao.TaiKhoanDAO().doiMatKhau(nhanVien.getMaNV(), newP);
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thành công! Chào mừng bạn truy cập hệ thống.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                success[0] = true;
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Không thể cập nhật mật khẩu vào CSDL!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnXacNhan);
        pnl.add(pnlBtns, BorderLayout.SOUTH);

        dialog.setContentPane(pnl);
        dialog.setVisible(true);

        return success[0];
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new DangNhap().setVisible(true);
        });
    }
}
