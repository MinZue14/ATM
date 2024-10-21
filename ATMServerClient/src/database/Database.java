package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection connection;

    public Database() {
        try {
            String url = "jdbc:mysql://localhost:3306/atm";
            String user = "root";
            String password = "";
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }
    public boolean isUserLoggedIn(String username, String serverId) {
        String query = "SELECT * FROM users WHERE username = ? AND server_id = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, serverId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Nếu có kết quả, có nghĩa là người dùng đang đăng nhập
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void setUserLoggedIn(String username, boolean loggedIn, String serverId) {
        try {
            if (loggedIn) {
                // Thêm bản ghi mới vào bảng user_sessions khi người dùng đăng nhập
                String insertQuery = "INSERT INTO users (username, server_id) VALUES (?, ?)";
                PreparedStatement insertStatement = this.connection.prepareStatement(insertQuery);
                insertStatement.setString(1, username);
                insertStatement.setString(2, serverId);
                insertStatement.executeUpdate();
            } else {
                // Xóa bản ghi khi người dùng đăng xuất
                String deleteQuery = "DELETE FROM users WHERE username = ? AND server_id = ?";
                PreparedStatement deleteStatement = this.connection.prepareStatement(deleteQuery);
                deleteStatement.setString(1, username);
                deleteStatement.setString(2, serverId);
                deleteStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addUser(String username, String password) {
        try {
            String query = "INSERT INTO users (username, password, balance) VALUES (?, ?, ?)";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setDouble(3, 0.0);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public boolean isValidCredentials(String account, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, account);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public int getAccountIdByName(String accountName) {
        int accountId = -1;
        String query = "SELECT id FROM users WHERE username = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(query);

            try {
                statement.setString(1, accountName);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    accountId = resultSet.getInt("id");
                }
            } catch (Throwable var8) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if (statement != null) {
                statement.close();
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return accountId;
    }

    public boolean accountExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(query);

            label52: {
                boolean var5;
                try {
                    statement.setString(1, username);
                    ResultSet resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        break label52;
                    }

                    var5 = resultSet.getInt(1) > 0;
                } catch (Throwable var7) {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }

                    throw var7;
                }

                if (statement != null) {
                    statement.close();
                }

                return var5;
            }

            if (statement != null) {
                statement.close();
            }
        } catch (SQLException var8) {
            var8.printStackTrace();
        }

        return false;
    }

    public boolean transfer(String fromAccount, String toAccount, double amount, String note) {
        boolean var12;
        try {
            this.connection.setAutoCommit(false);
            if (amount <= 0.0) {
                System.out.println("Số tiền chuyển phải lớn hơn 0.");
                boolean var30 = false;
                return var30;
            }

            String withdrawQuery = "UPDATE users SET balance = balance - ? WHERE username = ?";
            PreparedStatement withdrawStatement = this.connection.prepareStatement(withdrawQuery);
            withdrawStatement.setDouble(1, amount);
            withdrawStatement.setString(2, fromAccount);
            int rowsWithdrawn = withdrawStatement.executeUpdate();
            if (rowsWithdrawn <= 0) {
                System.out.println("Không thể rút tiền từ tài khoản nguồn: " + fromAccount);
                this.connection.rollback();
                boolean var32 = false;
                return var32;
            }

            String depositQuery = "UPDATE users SET balance = balance + ? WHERE username = ?";
            PreparedStatement depositStatement = this.connection.prepareStatement(depositQuery);
            depositStatement.setDouble(1, amount);
            depositStatement.setString(2, toAccount);
            int rowsDeposited = depositStatement.executeUpdate();
            if (rowsDeposited > 0) {
                this.connection.commit();
                if (this.logTransaction(fromAccount, toAccount, amount, note)) {
                    System.out.println("Chuyển khoản thành công!");
                    var12 = true;
                    return var12;
                }

                System.out.println("Không thể ghi lại giao dịch.");
                this.connection.rollback();
                var12 = false;
                return var12;
            }

            System.out.println("Không thể gửi tiền vào tài khoản đích: " + toAccount);
            this.connection.rollback();
            var12 = false;
        } catch (SQLException var28) {
            try {
                this.connection.rollback();
            } catch (SQLException var27) {
                var27.printStackTrace();
            }

            var28.printStackTrace();
            boolean var7 = false;
            return var7;
        } finally {
            try {
                this.connection.setAutoCommit(true);
            } catch (SQLException var26) {
                var26.printStackTrace();
            }

        }

        return var12;
    }

    public double getBalance(String username) {
        double balance = 0.0;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT balance FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return balance;
    }

    public boolean deposit(String username, double amount) {
        if (amount <= 0.0) {
            System.out.println("Số tiền nạp phải lớn hơn 0.");
            return false;
        } else {
            try {
                String checkUserQuery = "SELECT id, balance FROM users WHERE username = ?";
                PreparedStatement checkUserStatement = this.connection.prepareStatement(checkUserQuery);
                checkUserStatement.setString(1, username);
                ResultSet resultSet = checkUserStatement.executeQuery();
                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    double currentBalance = resultSet.getDouble("balance");
                    double newBalance = currentBalance + amount;
                    String updateBalanceQuery = "UPDATE users SET balance = ? WHERE id = ?";
                    PreparedStatement updateBalanceStatement = this.connection.prepareStatement(updateBalanceQuery);
                    updateBalanceStatement.setDouble(1, newBalance);
                    updateBalanceStatement.setInt(2, userId);
                    int rowsUpdated = updateBalanceStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        this.logTransaction("BANK", username, amount, "NẠP TIỀN");
                        return true;
                    } else {
                        System.out.println("Không thể cập nhật số dư.");
                        return false;
                    }
                } else {
                    System.out.println("Người dùng không tồn tại.");
                    return false;
                }
            } catch (SQLException var15) {
                var15.printStackTrace();
                return false;
            }
        }
    }

    public boolean withdraw(String username, double amount) {
        if (amount <= 0.0) {
            System.out.println("Số tiền rút phải lớn hơn 0.");
            return false;
        } else {
            try {
                String checkUserQuery = "SELECT id, balance FROM users WHERE username = ?";
                PreparedStatement checkUserStatement = this.connection.prepareStatement(checkUserQuery);
                checkUserStatement.setString(1, username);
                ResultSet resultSet = checkUserStatement.executeQuery();
                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    double currentBalance = resultSet.getDouble("balance");
                    if (currentBalance >= amount) {
                        double newBalance = currentBalance - amount;
                        String updateBalanceQuery = "UPDATE users SET balance = ? WHERE id = ?";
                        PreparedStatement updateBalanceStatement = this.connection.prepareStatement(updateBalanceQuery);
                        updateBalanceStatement.setDouble(1, newBalance);
                        updateBalanceStatement.setInt(2, userId);
                        int rowsUpdated = updateBalanceStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            this.logTransaction(username, "BANK", amount, "RÚT TIỀN");
                            return true;
                        } else {
                            System.out.println("Không thể cập nhật số dư.");
                            return false;
                        }
                    } else {
                        System.out.println("Số dư không đủ để rút tiền.");
                        return false;
                    }
                } else {
                    System.out.println("Người dùng không tồn tại.");
                    return false;
                }
            } catch (SQLException var15) {
                var15.printStackTrace();
                return false;
            }
        }
    }

    public List<Transaction> getTransactionHistory(String username) {
        List<Transaction> transactions = new ArrayList();

        try {
            String query = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ?";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                int transactionId = resultSet.getInt("id");
                String fromAccount = resultSet.getString("from_account");
                String toAccount = resultSet.getString("to_account");
                double amount = resultSet.getDouble("amount");
                Timestamp timestamp = resultSet.getTimestamp("transaction_time");
                String note = resultSet.getString("note");
                transactions.add(new Transaction(transactionId, fromAccount, toAccount, amount, timestamp, note));
            }
        } catch (SQLException var13) {
            var13.printStackTrace();
        }

        return transactions;
    }

    public boolean logTransaction(String fromAccount, String toAccount, double amount, String note) {
        try {
            String query = "INSERT INTO transactions (from_account, to_account, amount, transaction_time, note) VALUES (?, ?, ?, CURRENT_TIMESTAMP(), ?)";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, fromAccount);
            statement.setString(2, toAccount);
            statement.setDouble(3, amount);
            statement.setString(4, note);
            return statement.executeUpdate() > 0;
        } catch (SQLException var8) {
            var8.printStackTrace();
            return false;
        }
    }
}
