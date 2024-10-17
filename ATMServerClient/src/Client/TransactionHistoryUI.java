package Client;

import database.Database;
import database.Transaction;
import java.awt.LayoutManager;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
        this.frame.setDefaultCloseOperation(2);
        this.frame.setSize(600, 400);
        this.frame.setLayout((LayoutManager)null);
        String[] columns = new String[]{"Thời Gian", "Tài Khoản Gửi", "Tài Khoản Nhận", "Số Tiền", "Ghi Chú"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        Iterator var4 = this.database.getTransactionHistory(this.username).iterator();

        while(var4.hasNext()) {
            Transaction transaction = (Transaction)var4.next();
            model.addRow(new Object[]{transaction.getDate(), transaction.getFromAccount(), transaction.getToAccount(), transaction.getAmount(), transaction.getNote()});
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 10, 560, 340);
        this.frame.add(scrollPane);
        this.frame.setVisible(true);
    }
}
