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

public class TransferUI {
    private JFrame frame;
    private JTextField recipientAccountField;
    private JTextField amountField;
    private JTextField noteField;
    private JPasswordField passwordField;
    private Database database;
    private String username;

    public TransferUI(String username) {
        this.username = username;
        this.database = new Database();
        this.createUI();
    }

    private void createUI() {
        this.frame = new JFrame("ATM - Chuyển Khoản");
        this.frame.setDefaultCloseOperation(2);
        this.frame.setSize(550, 300);
        this.frame.setLayout((LayoutManager)null);
        this.frame.getContentPane().setBackground(new Color(230, 240, 255));
        JLabel titleLabel = new JLabel("Người Chuyển: " + this.username);
        titleLabel.setFont(new Font("Arial", 1, 16));
        titleLabel.setBounds(10, 20, 300, 25);
        titleLabel.setForeground(new Color(50, 100, 150));
        this.frame.add(titleLabel);
        JLabel recipientNameLabel = new JLabel("Tên Người Nhận:");
        recipientNameLabel.setFont(new Font("Arial", 0, 14));
        recipientNameLabel.setBounds(10, 60, 150, 25);
        this.frame.add(recipientNameLabel);
        this.recipientAccountField = new JTextField(15);
        this.recipientAccountField.setBounds(120, 60, 250, 25);
        this.frame.add(this.recipientAccountField);
        JLabel amountLabel = new JLabel("Số Tiền Chuyển:");
        amountLabel.setFont(new Font("Arial", 0, 14));
        amountLabel.setBounds(10, 100, 150, 25);
        this.frame.add(amountLabel);
        this.amountField = new JTextField(15);
        this.amountField.setBounds(120, 100, 250, 25);
        this.frame.add(this.amountField);
        JLabel noteLabel = new JLabel("Nội Dung:");
        noteLabel.setFont(new Font("Arial", 0, 14));
        noteLabel.setBounds(10, 140, 100, 25);
        this.frame.add(noteLabel);
        this.noteField = new JTextField(15);
        this.noteField.setBounds(120, 140, 250, 25);
        this.frame.add(this.noteField);
        JLabel passwordLabel = new JLabel("Mật Khẩu:");
        passwordLabel.setFont(new Font("Arial", 0, 14));
        passwordLabel.setBounds(10, 180, 100, 25);
        this.frame.add(passwordLabel);
        this.passwordField = new JPasswordField(15);
        this.passwordField.setBounds(120, 180, 250, 25);
        this.frame.add(this.passwordField);
        JButton transferButton = new JButton("Chuyển Khoản");
        transferButton.setBounds(10, 220, 360, 30);
        transferButton.setBackground(new Color(34, 139, 34));
        transferButton.setForeground(Color.WHITE);
        this.frame.add(transferButton);
        transferButton.addActionListener((e) -> {
            this.handleTransfer();
        });
        this.frame.setVisible(true);
    }

    private void handleTransfer() {
        try {
            String recipientName = this.recipientAccountField.getText().trim();
            double amount = Double.parseDouble(this.amountField.getText());
            String note = this.noteField.getText();
            String password = new String(this.passwordField.getPassword());
            if (amount <= this.database.getBalance(this.username)) {
                if (this.database.isValidCredentials(this.username, password)) {
                    if (this.database.accountExists(recipientName)) {
                        if (this.database.transfer(this.username, recipientName, amount, note)) {
                            JOptionPane.showMessageDialog(this.frame, "Chuyển khoản thành công!", "Thông báo", 1);
                        } else {
                            JOptionPane.showMessageDialog(this.frame, "Chuyển khoản thất bại.", "Lỗi", 0);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this.frame, "Người nhận không tồn tại.", "Lỗi", 0);
                    }
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Tài khoản hoặc mật khẩu không đúng.", "Lỗi", 0);
                }
            } else {
                JOptionPane.showMessageDialog(this.frame, "Số tiền chuyển phải nhỏ hơn hoặc bằng số dư.", "Lỗi", 0);
            }
        } catch (NumberFormatException var6) {
            JOptionPane.showMessageDialog(this.frame, "Vui lòng nhập đúng định dạng số cho số tiền.", "Lỗi", 0);
        } catch (Exception var7) {
            JOptionPane.showMessageDialog(this.frame, "Đã xảy ra lỗi: " + var7.getMessage(), "Lỗi", 0);
            var7.printStackTrace();
        }

    }
}
