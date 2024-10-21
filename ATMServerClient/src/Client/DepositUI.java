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
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class DepositUI {
    private JFrame frame;
    private JTextField amountField;
    private JPasswordField passwordField;
    private Database database;
    private String username;

    public DepositUI(String username) {
        this.username = username;
        this.database = new Database();
        this.createUI();
    }

    private void createUI() {
        this.frame = new JFrame("ATM - Nạp Tiền");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(400, 250);
        this.frame.setLayout(null);
        this.frame.getContentPane().setBackground(new Color(230, 240, 255));

        JLabel userLabel = new JLabel("Xin chào, " + this.username + "!");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setBounds(10, 20, 300, 25);
        userLabel.setForeground(new Color(50, 100, 150));
        this.frame.add(userLabel);

        JLabel amountLabel = new JLabel("Số Tiền Nạp:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setBounds(10, 60, 100, 25);
        this.frame.add(amountLabel);

        this.amountField = new JTextField(15);
        this.amountField.setBounds(120, 60, 250, 25);
        this.frame.add(this.amountField);

        JLabel passwordLabel = new JLabel("Mật Khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(10, 100, 100, 25);
        this.frame.add(passwordLabel);

        this.passwordField = new JPasswordField(15);
        this.passwordField.setBounds(120, 100, 250, 25);
        this.frame.add(this.passwordField);

        JButton depositButton = new JButton("Nạp Tiền");
        depositButton.setBounds(10, 140, 360, 30);
        depositButton.setBackground(new Color(34, 139, 34));
        depositButton.setForeground(Color.WHITE);
        this.frame.add(depositButton);

        depositButton.addActionListener((e) -> {
            try {
                double amount = Double.parseDouble(this.amountField.getText());
                String password = new String(this.passwordField.getPassword());

                if (this.database.isValidCredentials(this.username, password)) {
                    if (this.depositAndSync(amount)) {
                        JOptionPane.showMessageDialog(this.frame, "Nạp tiền thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this.frame, "Nạp tiền thất bại.", "Thông Báo", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Mật khẩu không đúng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this.frame, "Vui lòng nhập đúng định dạng số cho số tiền.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this.frame, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        this.frame.setVisible(true);
    }

    private boolean depositAndSync(double amount) {
        boolean success = this.database.deposit(this.username, amount);
        if (success) {
            syncDepositWithServers(amount); // Gửi yêu cầu nạp tiền đến các server
        }
        return success;
    }

    private void syncDepositWithServers(double amount) {
        String[] serverAddresses = {
                "192.168.1.18", // Địa chỉ IP của server 2
                "192.168.1.19", // Địa chỉ IP của server 3
        };

        for (String serverAddress : serverAddresses) {
            try (Socket socket = new Socket(serverAddress, 12346)) { // Sử dụng cổng 12346 cho các server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("DEPOSIT"); // Gửi lệnh nạp tiền
                out.println(this.username);
                out.println(amount); // Gửi số tiền nạp
            } catch (IOException e) {
                System.err.println("Không thể đồng bộ hóa với server " + serverAddress);
                e.printStackTrace();
            }
        }
    }
}
