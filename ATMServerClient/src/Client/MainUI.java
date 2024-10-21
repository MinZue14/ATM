package Client;

import database.Database;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

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

        JLabel welcomeLabel = new JLabel("Chào mừng, " + this.username);
        welcomeLabel.setBounds(10, 20, 200, 25);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(50, 100, 150));

        JLabel statusLabel = new JLabel("Trạng Thái: Đang hoạt động");
        statusLabel.setBounds(10, 60, 200, 25);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(34, 139, 34));

        double balance = this.database.getBalance(this.username);
        this.balanceLabel = new JLabel("Số Dư Hiện Tại: " + this.formatCurrency(balance) + " VND");
        this.balanceLabel.setBounds(10, 110, 300, 25);
        this.balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
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

        JButton logoutButton = new JButton("Đăng Xuất");
        logoutButton.setBounds(10, 230, 310, 25);
        logoutButton.setBackground(new Color(255, 102, 102));

        this.frame.add(welcomeLabel);
        this.frame.add(statusLabel);
        this.frame.add(this.balanceLabel);
        this.frame.add(depositButton);
        this.frame.add(withdrawButton);
        this.frame.add(transferButton);
        this.frame.add(historyButton);
        this.frame.add(reloadButton);
        this.frame.add(logoutButton);

        depositButton.addActionListener(e -> new DepositUI(this.username));
        withdrawButton.addActionListener(e -> new WithdrawUI(this.username));
        transferButton.addActionListener(e -> new TransferUI(this.username));
        historyButton.addActionListener(e -> new TransactionHistoryUI(this.username));

        reloadButton.addActionListener(e -> {
            syncBalanceWithServers(); // Đồng bộ hóa số dư với các server
        });

        logoutButton.addActionListener(e -> {
//            this.database.deleteServerId(this.username); // Xóa server_id từ database
            this.frame.dispose(); // Đóng cửa sổ
        });

        this.frame.setVisible(true);
    }

    private void syncBalanceWithServers() {
        String[] serverAddresses = {
                "192.168.1.18", // Địa chỉ IP của server 2
                "192.168.1.19", // Địa chỉ IP của server 3
        };

        for (String serverAddress : serverAddresses) {
            try (Socket socket = new Socket(serverAddress, 12346)) { // Sử dụng cổng 12346 cho các server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("SYNC_BALANCE"); // Gửi lệnh đồng bộ hóa số dư
                out.println(this.username);
                double balance = this.database.getBalance(this.username);
                out.println(balance); // Gửi số dư hiện tại
            } catch (IOException e) {
                System.err.println("Không thể đồng bộ hóa với server " + serverAddress);
                e.printStackTrace();
            }
        }

        // Cập nhật số dư mới sau khi đồng bộ hóa
        double newBalance = this.database.getBalance(this.username);
        this.balanceLabel.setText("Số Dư Hiện Tại: " + this.formatCurrency(newBalance) + " VND");
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance();
        return formatter.format(amount);
    }
}
