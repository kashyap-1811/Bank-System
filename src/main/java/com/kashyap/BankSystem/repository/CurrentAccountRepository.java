package com.kashyap.BankSystem.repository;

import com.kashyap.BankSystem.entities.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
}


