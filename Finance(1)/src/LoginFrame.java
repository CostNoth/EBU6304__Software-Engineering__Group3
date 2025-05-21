import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel usernameLabel, passwordLabel;
    private JPanel panel;

    public LoginFrame() {
        // 设置窗口标题和大小
        setTitle("登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示窗口

        // 设置背景色
        getContentPane().setBackground(new Color(35, 35, 35));

        // 创建面板并设置布局
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(35, 35, 35));

        // 设置GridBagLayout约束
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 设置每个组件之间的间距

        // 用户名标签
        usernameLabel = new JLabel("用户名");
        usernameLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        // 用户名输入框
        usernameField = new JTextField(20);

        usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        usernameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        // 密码标签
        passwordLabel = new JLabel("密码");
        passwordLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        // 密码输入框
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        passwordField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        // 登录按钮
        loginButton = new JButton("登录");
        loginButton.setBackground(new Color(40, 150, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        // 登录按钮点击事件
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (isValidUser(username, password)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "登录成功!");
                    new MainFrame(); // 登录成功后跳转到主界面
                    dispose(); // 关闭登录窗口
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "用户名或密码错误!", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 将面板添加到框架
        add(panel);

        setVisible(true);
    }

    // 验证用户输入的用户名和密码
    private boolean isValidUser(String username, String password) {
        // 从文件验证用户
        try (BufferedReader br = new BufferedReader(new FileReader("src/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].equals(username) && credentials[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 主方法启动应用
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
