package com.kashyap.BankSystem.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.sql.Date;

@Entity
@Table(name = "savingsaccount")
@PrimaryKeyJoinColumn(name = "accountno")
public class SavingsAccount extends BankAccount {

    @Column(name = "minimumbalance")
    private double minimumBalance;

    public SavingsAccount() {}

    public SavingsAccount(double balance, boolean active, Date openingDate, double minimumBalance) {
        super(balance, active, openingDate);
        this.minimumBalance = minimumBalance;
    }

    public double getMinimumBalance() { return minimumBalance; }
    public void setMinimumBalance(double minimumBalance) { this.minimumBalance = minimumBalance; }
}
