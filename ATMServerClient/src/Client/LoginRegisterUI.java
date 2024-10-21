package Client;

import database.Database;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginRegisterUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Database database = new Database();

    public LoginRegisterUI() {
        createUI();
    }

    private void createUI() {
        frame = new JFrame("ATM - Đăng nhập");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Nhãn và trường nhập tên người dùng
        JLabel userLabel = new JLabel("Tên người dùng:");
        userLabel.setBounds(50, 50, 100, 25);
        frame.add(userLabel);
        usernameField = new JTextField(20);
        usernameField.setBounds(150, 50, 200, 25);
        frame.add(usernameField);

        // Nhãn và trường nhập mật khẩu
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setBounds(50, 100, 100, 25);
        frame.add(passwordLabel);
        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 100, 200, 25);
        frame.add(passwordField);

        // Nút đăng nhập
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(50, 150, 100, 25);
        frame.add(loginButton);

        // Nút đăng ký
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBounds(250, 150, 100, 25);
        frame.add(registerButton);

        // Xử lý sự kiện đăng nhập
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Kiểm tra nếu tài khoản đang hoạt động
                if (database.isUserLoggedIn(username)) {
                    JOptionPane.showMessageDialog(frame, "Tài khoản này đang được đăng nhập ở nơi khác.");
                    return;
                }

                // Nếu tài khoản chưa đăng nhập, tiến hành xác thực
                if (database.authenticateUser(username, password)) {
                    // Đăng nhập thành công, cập nhật trạng thái tài khoản
                    database.setUserLoggedIn(username, true);
                    JOptionPane.showMessageDialog(frame, "Đăng nhập thành công!");
                    new MainUI(username);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Tên đăng nhập hoặc mật khẩu không đúng.");
                }
            }
        });

        // Xử lý sự kiện đăng ký
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Kiểm tra xem tài khoản đã tồn tại hay chưa
                if (database.accountExists(username)) {
                    JOptionPane.showMessageDialog(frame, "Tên đăng nhập đã tồn tại.");
                    return;
                }

                if (database.addUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Đăng ký thành công!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Lỗi khi đăng ký tài khoản.");
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginRegisterUI();
    }
}
