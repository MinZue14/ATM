package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ATMServer {
    private static final int PORT = 12345;
    private static Connection connection;
    private static Lock lock = new ReentrantLock();

    public ATMServer() {
    }

    public static void main(String[] args) {
        connectToDatabase();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                (new Thread(new ClientHandler(clientSocket))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToDatabase() {
        String dbUrl = "jdbc:mysql://localhost:3306/atm";
        String user = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(dbUrl, user, password);
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
                String action = this.in.readLine();

                switch (action) {
                    case "LOGIN":
                        this.handleLogin();
                        break;
                    case "TRANSFER":
                        this.handleTransfer();
                        break;
                    case "SYNC":
                        this.handleSync(this.in.readLine(), this.in.readLine(), Double.parseDouble(this.in.readLine()));
                        break;
                    case "SYNC_USER":
                        this.handleSyncUser(this.in.readLine(), this.in.readLine(), this.in.readLine());
                        break;
                    case "SYNC_TRANSACTION":
                        this.handleSyncTransaction(this.in.readLine(), this.in.readLine(), Double.parseDouble(this.in.readLine()));
                        break;
                    default:
                        this.out.println("INVALID_ACTION");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    this.clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private void handleLogin() {
            try {
                String username = this.in.readLine();
                String password = this.in.readLine();

                // Kiểm tra thông tin đăng nhập
                PreparedStatement stmt = ATMServer.connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                // Kiểm tra nếu tài khoản đã được đăng nhập ở server khác
                if (rs.next()) {
                    String serverId = rs.getString("server_id");
                    if (serverId != null && !serverId.isEmpty()) {
                        // Nếu serverId không null và không rỗng, có nghĩa là tài khoản đã đăng nhập ở server khác
                        this.out.println("LOGIN_FAIL: Tài khoản đã được đăng nhập ở server khác.");
                    } else {
                        // Nếu đăng nhập thành công, đánh dấu tài khoản là đã đăng nhập và lưu server_id
                        String currentServerId = "Server1"; // Thay đổi để phù hợp với ID của máy chủ hiện tại
                        PreparedStatement updateStmt = ATMServer.connection.prepareStatement("UPDATE users SET is_logged_in = 1, server_id = ? WHERE username = ?");
                        updateStmt.setString(1, currentServerId);
                        updateStmt.setString(2, username);
                        updateStmt.executeUpdate();
                        this.out.println("LOGIN_SUCCESS");
                    }
                } else {
                    this.out.println("LOGIN_FAIL: Tên đăng nhập hoặc mật khẩu không đúng.");
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void handleTransfer() {
            try {
                String fromAccount = this.in.readLine();
                String toAccount = this.in.readLine();
                double amount = Double.parseDouble(this.in.readLine());
                ATMServer.lock.lock();

                try {
                    // Cập nhật giao dịch
                    PreparedStatement transactionStmt = ATMServer.connection.prepareStatement("INSERT INTO transactions (from_account, to_account, amount) VALUES (?, ?, ?)");
                    transactionStmt.setString(1, fromAccount);
                    transactionStmt.setString(2, toAccount);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();

                    // Cập nhật số dư tài khoản
                    PreparedStatement stmt1 = ATMServer.connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE username = ?");
                    stmt1.setDouble(1, amount);
                    stmt1.setString(2, fromAccount);
                    int affectedRows1 = stmt1.executeUpdate();

                    if (affectedRows1 > 0) {
                        PreparedStatement stmt2 = ATMServer.connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE username = ?");
                        stmt2.setDouble(1, amount);
                        stmt2.setString(2, toAccount);
                        int affectedRows2 = stmt2.executeUpdate();
                        if (affectedRows2 > 0) {
                            this.out.println("TRANSFER_SUCCESS");
                            this.syncWithOtherServers(fromAccount, toAccount, amount);
                        } else {
                            this.out.println("TRANSFER_FAIL");
                        }
                    } else {
                        this.out.println("TRANSFER_FAIL");
                    }
                } finally {
                    ATMServer.lock.unlock();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void syncWithOtherServers(String fromAccount, String toAccount, double amount) {
            try {
                // Đồng bộ hóa giao dịch với server 2
                try (Socket server2Socket = new Socket("192.168.1.18", 12346)) {
                    PrintWriter out2 = new PrintWriter(server2Socket.getOutputStream(), true);
                    out2.println("SYNC_TRANSACTION");
                    out2.println(fromAccount);
                    out2.println(toAccount);
                    out2.println(amount);
                } catch (IOException e) {
                    System.out.println("Không thể kết nối tới server 2: " + e.getMessage());
                }

                // Đồng bộ hóa giao dịch với server 3
                try (Socket server3Socket = new Socket("192.168.1.19", 12347)) {
                    PrintWriter out3 = new PrintWriter(server3Socket.getOutputStream(), true);
                    out3.println("SYNC_TRANSACTION");
                    out3.println(fromAccount);
                    out3.println(toAccount);
                    out3.println(amount);
                } catch (IOException e) {
                    System.out.println("Không thể kết nối tới server 3: " + e.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleSync(String fromAccount, String toAccount, double amount) {
            // Xử lý đồng bộ hóa giao dịch giữa các máy chủ khác nếu cần
            System.out.println("Syncing transfer from " + fromAccount + " to " + toAccount + " for amount: " + amount);
        }

        private void handleSyncUser(String action, String username, String password) {
            try {
                switch (action) {
                    case "ADD":
                        PreparedStatement addStmt = ATMServer.connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                        addStmt.setString(1, username);
                        addStmt.setString(2, password);
                        addStmt.executeUpdate();
                        System.out.println("User added: " + username);
                        break;
                    case "UPDATE":
                        PreparedStatement updateStmt = ATMServer.connection.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
                        updateStmt.setString(1, password);
                        updateStmt.setString(2, username);
                        updateStmt.executeUpdate();
                        System.out.println("User updated: " + username);
                        break;
                    case "DELETE":
                        PreparedStatement deleteStmt = ATMServer.connection.prepareStatement("DELETE FROM users WHERE username = ?");
                        deleteStmt.setString(1, username);
                        deleteStmt.executeUpdate();
                        System.out.println("User deleted: " + username);
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void syncUser(String action, String username, String password) {
            try {
                // Kết nối tới server 2
                try (Socket server2Socket = new Socket("192.168.1.18", 12346)) {
                    PrintWriter out2 = new PrintWriter(server2Socket.getOutputStream(), true);
                    out2.println("SYNC_USER");
                    out2.println(action); // Hành động: "ADD", "UPDATE", hoặc "DELETE"
                    out2.println(username);
                    if (!action.equals("DELETE")) {
                        out2.println(password); // Gửi password chỉ khi không xóa
                    }
                } catch (IOException e) {
                    System.out.println("Không thể kết nối tới server 2: " + e.getMessage());
                }

                // Kết nối tới server 3
                try (Socket server3Socket = new Socket("192.168.1.19", 12347)) {
                    PrintWriter out3 = new PrintWriter(server3Socket.getOutputStream(), true);
                    out3.println("SYNC_USER");
                    out3.println(action);
                    out3.println(username);
                    if (!action.equals("DELETE")) {
                        out3.println(password);
                    }
                } catch (IOException e) {
                    System.out.println("Không thể kết nối tới server 3: " + e.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void handleSyncTransaction(String fromAccount, String toAccount, double amount) {
            try {
                // Cập nhật giao dịch trong cơ sở dữ liệu
                PreparedStatement transactionStmt = ATMServer.connection.prepareStatement("INSERT INTO transactions (from_account, to_account, amount) VALUES (?, ?, ?)");
                transactionStmt.setString(1, fromAccount);
                transactionStmt.setString(2, toAccount);
                transactionStmt.setDouble(3, amount);
                transactionStmt.executeUpdate();

                // Cập nhật số dư tài khoản
                PreparedStatement stmt1 = ATMServer.connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE username = ?");
                stmt1.setDouble(1, amount);
                stmt1.setString(2, fromAccount);
                stmt1.executeUpdate();

                PreparedStatement stmt2 = ATMServer.connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE username = ?");
                stmt2.setDouble(1, amount);
                stmt2.setString(2, toAccount);
                stmt2.executeUpdate();

                System.out.println("Sync completed: " + fromAccount + " transferred " + amount + " to " + toAccount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
