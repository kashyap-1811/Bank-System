package com.kashyap.BankSystem.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 68)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "phone_no", unique = true)
    private long phoneNo;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @ManyToMany(mappedBy = "customers")
    private Set<BankAccount> accounts = new HashSet<>();

    public Customer() {}

    public Customer(String email, String password, boolean enabled, String name, String city, long phoneNo, String role) {
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.name = name;
        this.city = city;
        this.phoneNo = phoneNo;
        this.role = role;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public long getPhoneNo() { return phoneNo; }
    public void setPhoneNo(long phoneNo) { this.phoneNo = phoneNo; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<BankAccount> getAccounts() { return accounts; }
    public void setAccounts(Set<BankAccount> accounts) { this.accounts = accounts; }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", phoneNo=" + phoneNo +
                ", role='" + role + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
