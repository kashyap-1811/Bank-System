package com.kashyap.BankSystem.repository;

import com.kashyap.BankSystem.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByCustomersEmail(String email); // Find accounts by customer email
    List<BankAccount> findByCustomersCustomerId(Long customerId);
}
