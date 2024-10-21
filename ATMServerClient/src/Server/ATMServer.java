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

        try {
            ServerSocket serverSocket = new ServerSocket(12345);

            try {
                System.out.println("Server is running on port 12345");

                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + String.valueOf(clientSocket.getInetAddress()));
                    (new Thread(new ClientHandler(clientSocket))).start();
                }
            } catch (Throwable var5) {
                try {
                    serverSocket.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }

    private static void connectToDatabase() {
        String dbUrl = "jdbc:mysql://localhost:3306/atm";
        String user = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(dbUrl, user, password);
            System.out.println("Connected to database.");
        } catch (SQLException var4) {
            var4.printStackTrace();
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
                switch (this.in.readLine()) {
                    case "LOGIN":
                        this.handleLogin();
                        break;
                    case "TRANSFER":
                        this.handleTransfer();
                        break;
                    default:
                        this.out.println("INVALID_ACTION");
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            } finally {
                try {
                    this.clientSocket.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }

            }

        }

        private void handleLogin() {
            try {
                String username = this.in.readLine();
                String password = this.in.readLine();
                PreparedStatement stmt = ATMServer.connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    this.out.println("LOGIN_SUCCESS");
                } else {
                    this.out.println("LOGIN_FAIL");
                }
            } catch (IOException | SQLException var5) {
                var5.printStackTrace();
            }

        }

        private void handleTransfer() {
            try {
                String fromAccount = this.in.readLine();
                String toAccount = this.in.readLine();
                double amount = Double.parseDouble(this.in.readLine());
                ATMServer.lock.lock();

                try {
                    PreparedStatement stmt1 = ATMServer.connection.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE username = ?");
                    stmt1.setDouble(1, amount);
                    stmt1.setString(2, fromAccount);
                    int affectedRows1 = stmt1.executeUpdate();
                    if (affectedRows1 > 0) {
                        PreparedStatement stmt2 = ATMServer.connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE username = ?");
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
            } catch (IOException | SQLException var13) {
                var13.printStackTrace();
            }

        }

        private void syncWithOtherServers(String fromAccount, String toAccount, double amount) {
            try {
                Socket server2Socket = new Socket("server2_IP_address", 12346);

                try {
                    Socket server3Socket = new Socket("server3_IP_address", 12347);

                    try {
                        PrintWriter out2 = new PrintWriter(server2Socket.getOutputStream(), true);
                        out2.println("SYNC");
                        out2.println(fromAccount);
                        out2.println(toAccount);
                        out2.println(amount);
                        PrintWriter out3 = new PrintWriter(server3Socket.getOutputStream(), true);
                        out3.println("SYNC");
                        out3.println(fromAccount);
                        out3.println(toAccount);
                        out3.println(amount);
                    } catch (Throwable var11) {
                        try {
                            server3Socket.close();
                        } catch (Throwable var10) {
                            var11.addSuppressed(var10);
                        }

                        throw var11;
                    }

                    server3Socket.close();
                } catch (Throwable var12) {
                    try {
                        server2Socket.close();
                    } catch (Throwable var9) {
                        var12.addSuppressed(var9);
                    }

                    throw var12;
                }

                server2Socket.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }
    }
}
