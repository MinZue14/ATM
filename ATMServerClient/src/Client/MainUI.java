package Client;

import database.Database;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
        this.frame.setDefaultCloseOperation(3);
        this.frame.setSize(400, 300);
        this.frame.setLayout((LayoutManager)null);
        this.frame.getContentPane().setBackground(new Color(230, 240, 255));
        JLabel welcomeLabel = new JLabel("Chào mừng, " + this.username);
        welcomeLabel.setBounds(10, 20, 200, 25);
        welcomeLabel.setFont(new Font("Arial", 1, 16));
        welcomeLabel.setForeground(new Color(50, 100, 150));
        JLabel statusLabel = new JLabel("Trạng Thái: Đang hoạt động");
        statusLabel.setBounds(10, 60, 200, 25);
        statusLabel.setFont(new Font("Arial", 0, 14));
        statusLabel.setForeground(new Color(34, 139, 34));
        double balance = this.database.getBalance(this.username);
        String var10003 = this.formatCurrency(balance);
        this.balanceLabel = new JLabel("Số Dư Hiện Tại: " + var10003 + " VND");
        this.balanceLabel.setBounds(10, 110, 300, 25);
        this.balanceLabel.setFont(new Font("Arial", 0, 14));
        this.balanceLabel.setForeground(new Color(0, 51, 102));
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
        this.frame.add(welcomeLabel);
        this.frame.add(statusLabel);
        this.frame.add(this.balanceLabel);
        this.frame.add(depositButton);
        this.frame.add(withdrawButton);
        this.frame.add(transferButton);
        this.frame.add(historyButton);
        this.frame.add(reloadButton);
        depositButton.addActionListener((e) -> {
            new DepositUI(this.username);
        });
        withdrawButton.addActionListener((e) -> {
            new WithdrawUI(this.username);
        });
        transferButton.addActionListener((e) -> {
            new TransferUI(this.username);
        });
        historyButton.addActionListener((e) -> {
            new TransactionHistoryUI(this.username);
        });
        reloadButton.addActionListener((e) -> {
            double newBalance = this.database.getBalance(this.username);
            JLabel var10000 = this.balanceLabel;
            String var10001 = this.formatCurrency(newBalance);
            var10000.setText("Số Dư Hiện Tại: " + var10001 + " VND");
        });
        this.frame.setVisible(true);
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance();
        return formatter.format(amount);
    }
}
