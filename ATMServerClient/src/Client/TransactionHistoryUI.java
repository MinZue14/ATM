package Client;

import database.Database;
import database.Transaction;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TransactionHistoryUI {
    private JFrame frame;
    private String username;
    private Database database;

    public TransactionHistoryUI(String username) {
        this.username = username;
        this.database = new Database();
        this.createUI();
    }

    private void createUI() {
        this.frame = new JFrame("ATM - Lịch Sử Giao Dịch");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(600, 400);
        this.frame.setLayout(null);

        String[] columns = new String[]{"Thời Gian", "Tài Khoản Gửi", "Tài Khoản Nhận", "Số Tiền", "Ghi Chú"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        // Lấy lịch sử giao dịch từ database local
        List<Transaction> transactions = this.database.getTransactionHistory(this.username);
        for (Transaction transaction : transactions) {
            model.addRow(new Object[]{
                    transaction.getDate(),
                    transaction.getFromAccount(),
                    transaction.getToAccount(),
                    transaction.getAmount(),
                    transaction.getNote()
            });
        }

        // Đồng bộ hóa với các server
        syncTransactionHistoryWithServers(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 10, 560, 340);
        this.frame.add(scrollPane);
        this.frame.setVisible(true);
    }

    private void syncTransactionHistoryWithServers(DefaultTableModel model) {
        String[] serverAddresses = {
                "192.168.1.18", // Địa chỉ IP của server 2
                "192.168.1.19", // Địa chỉ IP của server 3
        };

        for (String serverAddress : serverAddresses) {
            try (Socket socket = new Socket(serverAddress, 12346); // Sử dụng cổng 12346 cho các server
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Gửi yêu cầu lấy lịch sử giao dịch
                socket.getOutputStream().write(("GET_TRANSACTION_HISTORY\n" + this.username + "\n").getBytes());
                socket.getOutputStream().flush();

                String response;
                while ((response = in.readLine()) != null) {
                    // Phân tích dữ liệu trả về từ server
                    String[] data = response.split(",");
                    if (data.length == 5) { // Đảm bảo có đủ dữ liệu
                        model.addRow(new Object[]{
                                data[0], // Thời gian
                                data[1], // Tài khoản gửi
                                data[2], // Tài khoản nhận
                                data[3], // Số tiền
                                data[4]  // Ghi chú
                        });
                    }
                }
            } catch (IOException e) {
                System.err.println("Không thể đồng bộ hóa với server " + serverAddress);
                e.printStackTrace();
            }
        }
    }
}
