package com.kashyap.BankSystem.services;

import com.kashyap.BankSystem.entities.BankAccount;
import com.kashyap.BankSystem.entities.Transaction;
import com.kashyap.BankSystem.repository.BankAccountRepository;
import com.kashyap.BankSystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;

    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }
//------------------------------------------------------------------------------------------------------------------------

    //14 Credit amount to account
    public Transaction creditAmount(Long accountNumber, double amount) {
        BankAccount account = bankAccountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);

        Transaction transaction = new Transaction(amount, "CREDIT", account, null);
        transaction.setTimestamp(LocalDateTime.now());

        bankAccountRepository.save(account);
        return transactionRepository.save(transaction);
    }
//------------------------------------------------------------------------------------------------------------------------

    //15 Debit amount from account
    public Transaction debitAmount(Long accountNumber, double amount) {
        BankAccount account = bankAccountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for debit");
        }

        account.setBalance(account.getBalance() - amount);

        Transaction transaction = new Transaction(amount, "DEBIT", account, null);
        transaction.setTimestamp(LocalDateTime.now());

        bankAccountRepository.save(account);
        return transactionRepository.save(transaction);
    }
//------------------------------------------------------------------------------------------------------------------------

    //16 Transfer amount from one account to another
    public Transaction transferAmount(Long fromAccountNumber, Long toAccountNumber, double amount) {
        BankAccount fromAccount = bankAccountRepository.findById(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        BankAccount toAccount = bankAccountRepository.findById(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for transfer");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        Transaction transaction = new Transaction(amount, "TRANSFER", fromAccount, toAccount);
        transaction.setTimestamp(LocalDateTime.now());

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
        return transactionRepository.save(transaction);
    }
//------------------------------------------------------------------------------------------------------------------------

    //17 get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
//------------------------------------------------------------------------------------------------------------------------

    //18 Read all transactions for a particular account
    public List<Transaction> getTransactionsForAccount(Long accountNumber) {
        return transactionRepository.findByAccount_AccountNoOrderByTimestampDesc(accountNumber);
    }
    
    public List<Transaction> findreceiverAccount(long accountnumber)
    {
    	return transactionRepository.findByReceiverAccount_AccountNoOrderByTimestampDesc(accountnumber);
    }
//------------------------------------------------------------------------------------------------------------------------

    //19 Get Transaction Details by Id
    public Optional<Transaction> getTransactionbyId(Long id)
    {
        return transactionRepository.findById(id);
    }
//------------------------------------------------------------------------------------------------------------------------
}
