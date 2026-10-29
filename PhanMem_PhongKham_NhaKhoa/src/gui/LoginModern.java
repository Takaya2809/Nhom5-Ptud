package gui;

import dao.TaiKhoanDAO;
import entity.TaiKhoan;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class LoginModern extends JFrame {

    private JComboBox<String> cboVaiTro;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JButton btnLogin, btnExit;

    private BufferedImage imgLogin;
    private BufferedImage imgLogo;

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public LoginModern() {
        setTitle("Đăng Nhập");
        setSize(880, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadImages();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // 1. CỘT TRÁI: BANNER HÌNH ẢNH NHA KHOA (LOGIN.JPG)
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
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
        cboVaiTro.addKeyListener(enterKey);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void loadImages() {
        // Tải ảnh Login.jpg từ thư mục images
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

        // Tải logo ABC
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
                    // Vẽ ảnh giữ tỷ lệ, phủ toàn bộ khung
                    double imgW = imgLogin.getWidth();
                    double imgH = imgLogin.getHeight();
                    double scale = Math.max((double) w / imgW, (double) h / imgH);
                    int drawW = (int) (imgW * scale);
                    int drawH = (int) (imgH * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(imgLogin, x, y, drawW, drawH, null);
                } else {
                    // Fallback gradient nếu chưa thấy ảnh
                    GradientPaint grad = new GradientPaint(
                            0, 0, ModernUITheme.SIDEBAR_BG,
                            w, h, ModernUITheme.PRIMARY_DARK);
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
        card.setBorder(new EmptyBorder(20, 30, 20, 30));

        // LOGO + TIÊU ĐỀ FORM
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (imgLogo != null) {
            Image scaledLogo = imgLogo.getScaledInstance(38, 38, Image.SCALE_SMOOTH);
            pnlHeader.add(new JLabel(new ImageIcon(scaledLogo)));
        } else {
            JLabel lblIco = new JLabel("[ABC]");
            lblIco.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblIco.setForeground(ModernUITheme.PRIMARY);
            pnlHeader.add(lblIco);
        }

        JPanel pnlHeaderText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlHeaderText.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setFont(ModernUITheme.FONT_TITLE);
        lblTitle.setForeground(ModernUITheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Vui lòng chọn vị trí công tác để tiếp tục");
        lblSub.setFont(ModernUITheme.FONT_SMALL);
        lblSub.setForeground(ModernUITheme.TEXT_MUTED);

        pnlHeaderText.add(lblTitle);
        pnlHeaderText.add(lblSub);
        pnlHeader.add(pnlHeaderText);
        card.add(pnlHeader);

        card.add(Box.createVerticalStrut(18));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(14));

        // 1. CHỌN VỊ TRÍ
        addLabel(card, "Vị trí làm việc:", AppIcon.Type.STAFF);
        cboVaiTro = new JComboBox<>(new String[] {
                "Quản lý",
                "Nha sĩ",
                "Lễ tân",
                "Thu ngân",
                "Y tá"
        });
        cboVaiTro.setFont(ModernUITheme.FONT_REGULAR);
        cboVaiTro.setBackground(Color.WHITE);
        cboVaiTro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cboVaiTro.setPreferredSize(new Dimension(360, 38));
        cboVaiTro.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cboVaiTro);

        card.add(Box.createVerticalStrut(12));

        // 2. TÊN ĐĂNG NHẬP
        addLabel(card, "Tên đăng nhập:", AppIcon.Type.USER);
        txtUsername = ModernUITheme.createTextField(20);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setPreferredSize(new Dimension(360, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtUsername);

        card.add(Box.createVerticalStrut(12));

        // 3. MẬT KHẨU
        addLabel(card, "Mật khẩu:", AppIcon.Type.LOCK);
        txtPassword = new JPasswordField();
        txtPassword.setFont(ModernUITheme.FONT_REGULAR);
        txtPassword.setCaretColor(ModernUITheme.PRIMARY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setPreferredSize(new Dimension(360, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtPassword);

        // HIỂN THỊ MẬT KHẨU
        card.add(Box.createVerticalStrut(6));
        chkShowPass = new JCheckBox("Hiển thị mật khẩu");
        chkShowPass.setFont(ModernUITheme.FONT_SMALL);
        chkShowPass.setForeground(ModernUITheme.TEXT_MUTED);
        chkShowPass.setOpaque(false);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        char defaultEcho = txtPassword.getEchoChar();
        chkShowPass.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : defaultEcho);
        });
        card.add(chkShowPass);

        card.add(Box.createVerticalStrut(20));

        // NÚT BẤM (ĐĂNG NHẬP & THOÁT)
        JPanel pnlBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBtns.setOpaque(false);
        pnlBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnExit = ModernUITheme.createOutlineButton("Thoát", ModernUITheme.TEXT_MUTED);
        btnExit.addActionListener(e -> System.exit(0));

        btnLogin = ModernUITheme.createButton("Đăng nhập", ModernUITheme.PRIMARY, Color.WHITE);
        btnLogin.addActionListener(e -> handleLogin());

        pnlBtns.add(btnExit);
        pnlBtns.add(btnLogin);
        card.add(pnlBtns);

        // Đổi vị trí -> Cập nhật tài khoản mẫu
        cboVaiTro.addActionListener(e -> capNhatGoiYTheoVaiTro());
        capNhatGoiYTheoVaiTro();

        pnlRight.add(card);
        return pnlRight;
    }

    private void addLabel(JPanel panel, String text, AppIcon.Type iconType) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIco = new JLabel(new AppIcon(iconType, 13, ModernUITheme.PRIMARY));
        JLabel label = new JLabel(text);
        label.setFont(ModernUITheme.FONT_BOLD);
        label.setForeground(ModernUITheme.TEXT_REGULAR);

        pnl.add(lblIco);
        pnl.add(label);
        panel.add(pnl);
        panel.add(Box.createVerticalStrut(5));
    }

    private void capNhatGoiYTheoVaiTro() {
        String role = (String) cboVaiTro.getSelectedItem();
        if (role == null)
            return;

        switch (role) {
            case "Quản lý":
                txtUsername.setText("admin");
                txtPassword.setText("123456");
                break;
            case "Nha sĩ":
                txtUsername.setText("bacsi_tuan");
                txtPassword.setText("123456");
                break;
            case "Lễ tân":
                txtUsername.setText("letan_tram");
                txtPassword.setText("123456");
                break;
            case "Thu ngân":
                txtUsername.setText("thungan_phuong");
                txtPassword.setText("123456");
                break;
            case "Y tá":
                txtUsername.setText("yta_oanh");
                txtPassword.setText("123456");
                break;
        }
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String selectedRole = (String) cboVaiTro.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!",
                    "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan user = taiKhoanDAO.checkLogin(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác!\nVui lòng kiểm tra lại thông tin.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if ("Bị khóa".equalsIgnoreCase(user.getTrangThai())) {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản này hiện đang bị khóa! Vui lòng liên hệ ban quản trị.",
                    "Tài khoản bị khóa", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean hopLe = taiKhoanDAO.kiemTraVaiTro(user, selectedRole);
        if (!hopLe) {
            String roleName = user.getTenVaiTro() != null ? user.getTenVaiTro() : user.getMaVaiTro();
            JOptionPane.showMessageDialog(this,
                    "Tài khoản '" + username + "' là tài khoản thuộc quyền [" + roleName + "],\n"
                            + "không thể đăng nhập vào vị trí [" + selectedRole + "]!\n\n"
                            + "Vui lòng chọn đúng vị trí làm việc của bạn.",
                    "Sai vị trí đăng nhập", JOptionPane.ERROR_MESSAGE);
            cboVaiTro.requestFocus();
            return;
        }

        user.setTenVaiTro(selectedRole);

        this.dispose();
        new TrangChuModern(user).setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new LoginModern().setVisible(true);
        });
    }
}