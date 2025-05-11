package com.kashyap.BankSystem.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.sql.Date;

@Entity
@Table(name = "currentaccount")
@PrimaryKeyJoinColumn(name = "accountno")
public class CurrentAccount extends BankAccount {

    @Column(name = "overdraftlimit")
    private double overdraftLimit;

    public CurrentAccount() {}

    public CurrentAccount(double balance, boolean active, Date openingDate, double overdraftLimit) {
        super(balance, active, openingDate);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}