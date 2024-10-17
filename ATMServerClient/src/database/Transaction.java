package database;

import java.util.Date;

public class Transaction {
    private int id;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private Date date;
    private String note;

    public Transaction(int id, String fromAccount, String toAccount, double amount, Date date, String note) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return this.id;
    }

    public String getFromAccount() {
        return this.fromAccount;
    }

    public String getToAccount() {
        return this.toAccount;
    }

    public double getAmount() {
        return this.amount;
    }

    public Date getDate() {
        return this.date;
    }

    public String getNote() {
        return this.note;
    }
}
