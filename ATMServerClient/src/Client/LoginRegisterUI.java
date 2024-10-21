package Client;

import database.Database;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginRegisterUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Database database = new Database();

    public LoginRegisterUI() {
        this.createUI();
    }

    private void createUI() {
        this.frame = new JFrame("ATM - Login");
        this.frame.setSize(400, 300);
        this.frame.setDefaultCloseOperation(3);
        this.frame.setLayout((LayoutManager)null);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 25);
        this.frame.add(userLabel);
        this.usernameField = new JTextField(20);
        this.usernameField.setBounds(150, 50, 200, 25);
        this.frame.add(this.usernameField);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 25);
        this.frame.add(passwordLabel);
        this.passwordField = new JPasswordField(20);
        this.passwordField.setBounds(150, 100, 200, 25);
        this.frame.add(this.passwordField);
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 150, 100, 25);
        this.frame.add(loginButton);
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(250, 150, 100, 25);
        this.frame.add(registerButton);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = LoginRegisterUI.this.usernameField.getText();
                String password = new String(LoginRegisterUI.this.passwordField.getPassword());
                String currentServerId = "server1"; // Thay thế bằng ID của server hiện tại

                // Kiểm tra nếu tài khoản đang hoạt động trên server khác
                if (LoginRegisterUI.this.database.isUserLoggedIn(username, currentServerId)) {
                    JOptionPane.showMessageDialog(LoginRegisterUI.this.frame, "Tài khoản này đang được đăng nhập ở nơi khác.");
                    return; // Không cho phép đăng nhập
                }

                // Nếu tài khoản chưa đăng nhập, tiến hành xác thực
                if (LoginRegisterUI.this.database.authenticateUser(username, password)) {
                    // Đăng nhập thành công, cập nhật trạng thái tài khoản
                    LoginRegisterUI.this.database.setUserLoggedIn(username, true, currentServerId);
                    JOptionPane.showMessageDialog(LoginRegisterUI.this.frame, "Đăng nhập thành công!");
                    new MainUI(username);
                    LoginRegisterUI.this.frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginRegisterUI.this.frame, "Tên đăng nhập hoặc mật khẩu không đúng.");
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = LoginRegisterUI.this.usernameField.getText();
                String password = new String(LoginRegisterUI.this.passwordField.getPassword());
                if (LoginRegisterUI.this.database.addUser(username, password)) {
                    JOptionPane.showMessageDialog(LoginRegisterUI.this.frame, "Đăng ký thành công!");
                } else {
                    JOptionPane.showMessageDialog(LoginRegisterUI.this.frame, "Tên đăng nhập đã tồn tại.");
                }

            }
        });
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginRegisterUI();
    }
}
