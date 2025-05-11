package com.kashyap.BankSystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "type", nullable = false)
    private String type; // CREDIT, DEBIT, TRANSFER

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private BankAccount account;

    @ManyToOne
    @JoinColumn(name = "receiver_account_no", referencedColumnName = "accountno", nullable = true)
//    @Column(name="receiver_account_no", nullable=true)
    private BankAccount receiverAccount;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(double amount, String type, BankAccount account, BankAccount receiverAccount) {
        this.amount = amount;
        this.type = type;
        this.account = account;
        this.receiverAccount = receiverAccount;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public BankAccount getAccount() { return account; }
    public void setAccount(BankAccount account) { this.account = account; }

    public BankAccount getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(BankAccount receiverAccount) { this.receiverAccount = receiverAccount; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", account=" + (account != null ? account.getAccountNo() : "null") +
                ", receiverAccount=" + (getReceiverAccount() != null ? getReceiverAccount().getAccountNo() : "null") +
                '}';
    }
}
