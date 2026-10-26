package gui;

import dao.TaiKhoanDAO;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Giao diện đăng nhập hệ thống - Thiết kế Basic, đơn giản, thanh lịch
 * Hỗ trợ phân quyền chọn vị trí công tác và xác thực tài khoản chính xác theo
 * vị trí
 */
public class LoginModern extends JFrame {

    private JComboBox<String> cboVaiTro;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JLabel lblHintAccount;
    private JButton btnLogin, btnExit;

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public LoginModern() {
        setTitle("Đăng Nhập - Hệ Thống Quản Lý Phòng Khám Nha Khoa ABC");
        setSize(540, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Nền chính màu xám nhạt basic, sạch sẽ
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(241, 245, 249));
        setContentPane(mainPanel);

        // Khung form đăng nhập trắng tinh tế ở giữa màn hình
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(28, 36, 28, 36)));
        cardPanel.setPreferredSize(new Dimension(460, 520));

        // --- TIÊU ĐỀ ---
        JLabel lblClinicName = new JLabel("PHÒNG KHÁM NHA KHOA ABC");
        lblClinicName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblClinicName.setForeground(new Color(15, 23, 42));
        lblClinicName.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblClinicName);

        cardPanel.add(Box.createVerticalStrut(4));

        JLabel lblSub = new JLabel("HỆ THỐNG QUẢN TRỊ & ĐIỀU HÀNH");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblSub);

        cardPanel.add(Box.createVerticalStrut(6));

        JLabel lblLoginTitle = new JLabel("ĐĂNG NHẬP THEO VỊ TRÍ CÔNG TÁC");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLoginTitle.setForeground(new Color(2, 132, 199));
        lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblLoginTitle);

        cardPanel.add(Box.createVerticalStrut(18));
        cardPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        cardPanel.add(Box.createVerticalStrut(16));

        // --- 1. CHỌN VỊ TRÍ ---
        addLabel(cardPanel, "Vị trí làm việc:");
        cboVaiTro = new JComboBox<>(new String[] {
                "Quản lý",
                "Nha sĩ",
                "Lễ tân",
                "Thu ngân",
                "Y tá"
        });
        cboVaiTro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboVaiTro.setBackground(Color.WHITE);
        cboVaiTro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cboVaiTro.setPreferredSize(new Dimension(380, 38));
        cboVaiTro.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(cboVaiTro);

        cardPanel.add(Box.createVerticalStrut(12));

        // --- 2. TÊN ĐĂNG NHẬP ---
        addLabel(cardPanel, "Tên đăng nhập:");
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(6, 10, 6, 10)));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setPreferredSize(new Dimension(380, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtUsername);

        cardPanel.add(Box.createVerticalStrut(12));

        // --- 3. MẬT KHẨU ---
        addLabel(cardPanel, "Mật khẩu:");
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(6, 10, 6, 10)));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setPreferredSize(new Dimension(380, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtPassword);

        // Hiển thị mật khẩu
        cardPanel.add(Box.createVerticalStrut(6));
        chkShowPass = new JCheckBox("Hiển thị mật khẩu");
        chkShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPass.setForeground(new Color(100, 116, 139));
        chkShowPass.setOpaque(false);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        char defaultEcho = txtPassword.getEchoChar();
        chkShowPass.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : defaultEcho);
        });
        cardPanel.add(chkShowPass);

        cardPanel.add(Box.createVerticalStrut(10));

        // --- KHUNG GỢI Ý TÀI KHOẢN MẪU CHO TỪNG VỊ TRÍ ---
        JPanel pnlHint = new JPanel(new BorderLayout());
        pnlHint.setBackground(new Color(248, 250, 252));
        pnlHint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 10, 6, 10)));
        pnlHint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pnlHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHintAccount = new JLabel();
        lblHintAccount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHintAccount.setForeground(new Color(71, 85, 105));
        pnlHint.add(lblHintAccount, BorderLayout.CENTER);
        cardPanel.add(pnlHint);

        cardPanel.add(Box.createVerticalStrut(16));

        // --- NÚT BẤM (ĐĂNG NHẬP & THOÁT) ---
        JPanel pnlBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBtns.setOpaque(false);
        pnlBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnExit = new JButton("THOÁT");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExit.setBackground(new Color(241, 245, 249));
        btnExit.setForeground(new Color(71, 85, 105));
        btnExit.setFocusPainted(false);
        btnExit.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(e -> System.exit(0));

        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(2, 132, 199));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(3, 105, 161), 1));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());

        pnlBtns.add(btnExit);
        pnlBtns.add(btnLogin);
        cardPanel.add(pnlBtns);

        cardPanel.add(Box.createVerticalStrut(12));

        JLabel lblNotice = new JLabel("Lưu ý: Đăng nhập đúng tên tài khoản và mật khẩu của vị trí đã chọn.",
                SwingConstants.CENTER);
        lblNotice.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNotice.setForeground(new Color(148, 163, 184));
        lblNotice.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblNotice);

        mainPanel.add(cardPanel);

        // Sự kiện khi đổi vị trí ở ComboBox -> Cập nhật tài khoản mẫu và gợi ý
        cboVaiTro.addActionListener(e -> capNhatGoiYTheoVaiTro());
        capNhatGoiYTheoVaiTro();

        // Phím Enter để đăng nhập
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

    private void addLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
    }

    /**
     * Cập nhật gợi ý tài khoản mẫu tương ứng với vị trí đã chọn
     */
    private void capNhatGoiYTheoVaiTro() {
        String role = (String) cboVaiTro.getSelectedItem();
        if (role == null)
            return;

        switch (role) {
            case "Quản lý":
                txtUsername.setText("admin");
                txtPassword.setText("123456");
                lblHintAccount.setText("<html><b>Gợi ý tài khoản Quản lý:</b> admin (Mật khẩu: 123456)</html>");
                break;
            case "Nha sĩ":
                txtUsername.setText("bacsi_tuan");
                txtPassword.setText("123456");
                lblHintAccount
                        .setText("<html><b>Gợi ý tài khoản Nha sĩ:</b> bacsi_tuan / nhasi (Mật khẩu: 123456)</html>");
                break;
            case "Lễ tân":
                txtUsername.setText("letan_tram");
                txtPassword.setText("123456");
                lblHintAccount
                        .setText("<html><b>Gợi ý tài khoản Lễ tân:</b> letan_tram / letan (Mật khẩu: 123456)</html>");
                break;
            case "Thu ngân":
                txtUsername.setText("thungan_phuong");
                txtPassword.setText("123456");
                lblHintAccount.setText(
                        "<html><b>Gợi ý tài khoản Thu ngân:</b> thungan_phuong / thungan (Mật khẩu: 123456)</html>");
                break;
            case "Y tá":
                txtUsername.setText("yta_oanh");
                txtPassword.setText("123456");
                lblHintAccount.setText("<html><b>Gợi ý tài khoản Y tá:</b> yta_oanh / yta (Mật khẩu: 123456)</html>");
                break;
        }
    }

    /**
     * Xử lý xác thực đăng nhập:
     * 1. Kiểm tra tài khoản & mật khẩu
     * 2. Kiểm tra tài khoản có thuộc đúng vị trí đã chọn hay không
     */
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

        // 1. Kiểm tra thông tin đăng nhập trong CSDL / hệ thống
        TaiKhoan user = taiKhoanDAO.checkLogin(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác!\nVui lòng kiểm tra lại thông tin.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        // 2. Kiểm tra trạng thái tài khoản
        if ("Bị khóa".equalsIgnoreCase(user.getTrangThai())) {
            JOptionPane.showMessageDialog(this,
                    "Tài khoản này hiện đang bị khóa! Vui lòng liên hệ ban quản trị.",
                    "Tài khoản bị khóa", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Phân quyền: Kiểm tra tài khoản có thuộc đúng vị trí (vai trò) đã chọn hay
        // không
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

        // Cập nhật tên vai trò chuẩn theo vị trí đã chọn
        user.setTenVaiTro(selectedRole);

        // Đăng nhập thành công -> Mở giao diện tương ứng với các chức năng của vị trí
        // đó
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
