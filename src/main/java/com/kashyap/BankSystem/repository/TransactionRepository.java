package com.kashyap.BankSystem.repository;

import com.kashyap.BankSystem.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Fetch transactions by sender account number (fixed property path)
    List<Transaction> findByAccount_AccountNoOrderByTimestampDesc(Long accountNumber);

    // Fetch transactions by receiver account number
    List<Transaction> findByReceiverAccount_AccountNoOrderByTimestampDesc(Long accountNumber);

}
