package gui;

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
        txtMaNV.setText("QL001"); // Mặc định tài khoản quản lý
        card.add(txtMaNV);

        card.add(Box.createVerticalStrut(14));

        // 2. MẬT KHẨU
        addLabel(card, "Mật khẩu:", AppIcon.Type.LOCK);
        txtPassword = new JPasswordField("123456");
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
            boolean ok = connectDB.ConnectDB.isConnected();
            if (!ok) {
                java.sql.Connection c = connectDB.ConnectDB.getConnection();
                ok = (c != null);
            }
            final boolean isConnected = ok;
            SwingUtilities.invokeLater(() -> {
                if (isConnected) {
                    lblDbStatus.setText("● CSDL SQL Server: QLPHONGKHAM (Trực tuyến)");
                    lblDbStatus.setForeground(new Color(22, 101, 52));
                } else {
                    lblDbStatus.setText("○ Chế độ CSDL Demo / Ngoại tuyến sẵn sàng");
                    lblDbStatus.setForeground(UITheme.TEXT_MUTED);
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

        // Xác thực thông tin qua NhanVienDAO.dangNhap
        NhanVien nhanVien = nhanVienDAO.dangNhap(maNV, password);

        if (nhanVien == null) {
            JOptionPane.showMessageDialog(this,
                    "Mã nhân viên hoặc mật khẩu không chính xác!\n\n" +
                            "Gợi ý tài khoản mẫu:\n" +
                            "- Quản lý:  QL001 (Pass: 123456)\n" +
                            "- Bác sĩ:   BS001, BS002 (Pass: 123456)\n" +
                            "- Lễ tân:   LT001, LT002 (Pass: 123456)\n" +
                            "- Y tá:     YT001, YT002 (Pass: 123456)",
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

        // Lưu thông tin vào phiên làm việc hiện tại
        PhienDangNhap.setNhanVien(nhanVien);

        // Mở màn hình chính với nhân viên đã xác thực
        this.dispose();
        new TrangChu(nhanVien).setVisible(true);
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
