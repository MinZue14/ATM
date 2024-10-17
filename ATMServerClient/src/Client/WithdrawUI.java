package Client;

import database.Database;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class WithdrawUI {
    private JFrame frame;
    private JTextField amountField;
    private JTextField accountField;
    private JPasswordField passwordField;
    private Database database;
    private String username;

    public WithdrawUI(String username) {
        this.username = username;
        this.database = new Database();
        this.createUI();
    }

    private void createUI() {
        this.frame = new JFrame("ATM - Rút Tiền");
        this.frame.setDefaultCloseOperation(2);
        this.frame.setSize(400, 250);
        this.frame.setLayout((LayoutManager)null);
        this.frame.getContentPane().setBackground(new Color(230, 240, 255));
        JLabel userLabel = new JLabel("Xin chào, " + this.username + "!");
        userLabel.setFont(new Font("Arial", 1, 16));
        userLabel.setBounds(10, 20, 300, 25);
        userLabel.setForeground(new Color(50, 100, 150));
        this.frame.add(userLabel);
        JLabel amountLabel = new JLabel("Số Tiền Rút:");
        amountLabel.setFont(new Font("Arial", 0, 14));
        amountLabel.setBounds(10, 60, 100, 25);
        this.frame.add(amountLabel);
        this.amountField = new JTextField(15);
        this.amountField.setBounds(120, 60, 250, 25);
        this.frame.add(this.amountField);
        JLabel accountLabel = new JLabel("Tên Tài Khoản:");
        accountLabel.setFont(new Font("Arial", 0, 14));
        accountLabel.setBounds(10, 100, 100, 25);
        this.frame.add(accountLabel);
        this.accountField = new JTextField(15);
        this.accountField.setBounds(120, 100, 250, 25);
        this.frame.add(this.accountField);
        JLabel passwordLabel = new JLabel("Mật Khẩu:");
        passwordLabel.setFont(new Font("Arial", 0, 14));
        passwordLabel.setBounds(10, 140, 100, 25);
        this.frame.add(passwordLabel);
        this.passwordField = new JPasswordField(15);
        this.passwordField.setBounds(120, 140, 250, 25);
        this.frame.add(this.passwordField);
        JButton withdrawButton = new JButton("Rút Tiền");
        withdrawButton.setBounds(10, 180, 360, 30);
        withdrawButton.setBackground(new Color(34, 139, 34));
        withdrawButton.setForeground(Color.WHITE);
        this.frame.add(withdrawButton);
        withdrawButton.addActionListener((e) -> {
            try {
                double amount = Double.parseDouble(this.amountField.getText());
                String account = this.accountField.getText();
                String password = new String(this.passwordField.getPassword());
                if (amount <= 0.0) {
                    JOptionPane.showMessageDialog(this.frame, "Số tiền rút phải lớn hơn 0.", "Lỗi", 0);
                    return;
                }

                if (this.database.isValidCredentials(account, password)) {
                    if (this.database.withdraw(account, amount)) {
                        JOptionPane.showMessageDialog(this.frame, "Rút tiền thành công!", "Thông báo", 1);
                    } else {
                        JOptionPane.showMessageDialog(this.frame, "Rút tiền thất bại. Kiểm tra số dư tài khoản.", "Lỗi", 0);
                    }
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Tài khoản hoặc mật khẩu không đúng.", "Lỗi", 0);
                }
            } catch (NumberFormatException var6) {
                JOptionPane.showMessageDialog(this.frame, "Vui lòng nhập đúng định dạng số cho số tiền.", "Lỗi", 0);
            } catch (Exception var7) {
                JOptionPane.showMessageDialog(this.frame, "Đã xảy ra lỗi: " + var7.getMessage(), "Lỗi", 0);
                var7.printStackTrace();
            }

        });
        this.frame.setVisible(true);
    }
}
