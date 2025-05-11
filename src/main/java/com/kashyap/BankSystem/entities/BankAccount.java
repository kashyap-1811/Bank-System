package com.kashyap.BankSystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bankaccount")
@Inheritance(strategy = InheritanceType.JOINED)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountno")
    private long accountNo;

    @Column(name = "balance", nullable = false)
    private double balance;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "openingDate")
    private Date openingDate;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "customer_accounts",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    )
    @JsonIgnore
    private Set<Customer> customers = new HashSet<>();

    public BankAccount() {}

    public BankAccount(double balance, boolean active, Date openingDate) {
        this.balance = balance;
        this.active = active;
        this.openingDate = openingDate;
    }

    public long getAccountNo() { return accountNo; }
    public void setAccountNo(long accountNo) { this.accountNo = accountNo; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Date getOpeningDate() { return openingDate; }
    public void setOpeningDate(Date openingDate) { this.openingDate = openingDate; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public Set<Customer> getCustomers() { return customers; }
    public void setCustomers(Set<Customer> customers) { this.customers = customers; }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }
}
