package Client;

import database.Database;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.*;

public class MainUI {
    private JFrame frame;
    private String username;
    private Database database;
    private JLabel balanceLabel;

    public MainUI(String username) {
        this.username = username;
        this.database = new Database();
        this.createUI();
    }

    private void createUI() {
        this.frame = new JFrame("ATM - Giao Diện Chính");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(400, 300);
        this.frame.setLayout(null);
        this.frame.getContentPane().setBackground(new Color(230, 240, 255));

        // Nhãn chào mừng
        JLabel welcomeLabel = new JLabel("Chào mừng, " + this.username);
        welcomeLabel.setBounds(10, 20, 200, 25);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(50, 100, 150));

        // Nhãn trạng thái
        JLabel statusLabel = new JLabel("Trạng Thái: Đang hoạt động");
        statusLabel.setBounds(10, 60, 200, 25);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(34, 139, 34));

        // Nhãn số dư hiện tại
        double balance = this.database.getBalance(this.username);
        this.balanceLabel = new JLabel("Số Dư Hiện Tại: " + this.formatCurrency(balance) + " VND");
        this.balanceLabel.setBounds(10, 110, 300, 25);
        this.balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        this.balanceLabel.setForeground(new Color(0, 51, 102));

        // Các nút chức năng
        JButton depositButton = new JButton("Nạp Tiền");
        depositButton.setBounds(10, 140, 150, 25);
        depositButton.setBackground(new Color(173, 216, 230));

        JButton withdrawButton = new JButton("Rút Tiền");
        withdrawButton.setBounds(170, 140, 150, 25);
        withdrawButton.setBackground(new Color(173, 216, 230));

        JButton transferButton = new JButton("Chuyển Khoản");
        transferButton.setBounds(10, 170, 150, 25);
        transferButton.setBackground(new Color(173, 216, 230));

        JButton historyButton = new JButton("Lịch Sử Giao Dịch");
        historyButton.setBounds(170, 170, 150, 25);
        historyButton.setBackground(new Color(173, 216, 230));

        JButton reloadButton = new JButton("Cập Nhật Số Dư");
        reloadButton.setBounds(10, 200, 310, 25);
        reloadButton.setBackground(new Color(255, 204, 153));

        JButton logoutButton = new JButton("Đăng Xuất");
        logoutButton.setBounds(10, 230, 310, 25);
        logoutButton.setBackground(new Color(255, 102, 102));

        // Thêm các thành phần vào frame
        this.frame.add(welcomeLabel);
        this.frame.add(statusLabel);
        this.frame.add(this.balanceLabel);
        this.frame.add(depositButton);
        this.frame.add(withdrawButton);
        this.frame.add(transferButton);
        this.frame.add(historyButton);
        this.frame.add(reloadButton);
        this.frame.add(logoutButton);

        // Hành động cho các nút
        depositButton.addActionListener((e) -> new DepositUI(this.username));
        withdrawButton.addActionListener((e) -> new WithdrawUI(this.username));
        transferButton.addActionListener((e) -> new TransferUI(this.username));
        historyButton.addActionListener((e) -> new TransactionHistoryUI(this.username));

        // Nút Cập Nhật Số Dư
        reloadButton.addActionListener((e) -> {
            double newBalance = this.database.getBalance(this.username);
            this.balanceLabel.setText("Số Dư Hiện Tại: " + this.formatCurrency(newBalance) + " VND");
        });

        // Nút Đăng Xuất
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                database.setUserLoggedIn(username, false);
                JOptionPane.showMessageDialog(frame, "Đăng xuất thành công!");
                new LoginRegisterUI(); // Quay lại màn hình đăng nhập
                frame.dispose(); // Đóng màn hình hiện tại
            }
        });

        this.frame.setVisible(true);
    }

    // Phương thức format số thành chuỗi định dạng tiền tệ
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance();
        return formatter.format(amount);
    }
}
