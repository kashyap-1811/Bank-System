package com.kashyap.BankSystem.controllers;

import com.kashyap.BankSystem.entities.Transaction;
import com.kashyap.BankSystem.entities.BankAccount;
import com.kashyap.BankSystem.entities.Customer;
import com.kashyap.BankSystem.services.TransactionService;
import com.kashyap.BankSystem.services.BankAccountService;
import com.kashyap.BankSystem.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final BankAccountService bankAccountService;
    private final CustomerService customerService;

    public TransactionController(TransactionService transactionService, BankAccountService bankAccountService, CustomerService customerService) {
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
        this.customerService = customerService;
    }
//------------------------------------------------------------------------------------------------------------------------

    //14 Credit amount
    @PostMapping("/credit/{accountNumber}")
    public ResponseEntity<?> creditAmount(@PathVariable Long accountNumber, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be greater than zero");
        }

        if (!isAccountEnabled(accountNumber)) {
            return ResponseEntity.badRequest().body("Transaction cannot be performed. The account is disabled.");
        }

        try {
            Transaction transaction = transactionService.creditAmount(accountNumber, amount.doubleValue());
            return ResponseEntity.ok(formatTransactionResponse(transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//------------------------------------------------------------------------------------------------------------------------

    //15 Debit amount
    @PostMapping("/debit/{accountNumber}")
    public ResponseEntity<?> debitAmount(@PathVariable Long accountNumber, @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be greater than zero");
        }

        if (!isAccountEnabled(accountNumber)) {
            return ResponseEntity.badRequest().body("Transaction cannot be performed. The account is disabled.");
        }

        try {
            Transaction transaction = transactionService.debitAmount(accountNumber, amount.doubleValue());
            return ResponseEntity.ok(formatTransactionResponse(transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
  //------------------------------------------------------------------------------------------------------------------------

    //16 Transfer amount between accounts
    @PostMapping("/transfer")
    public ResponseEntity<?> transferAmount(@RequestParam Long fromAccount, @RequestParam Long toAccount, @RequestParam BigDecimal amount) {
    	authorizeTransaction(fromAccount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be greater than zero");
        }
        if (fromAccount.equals(toAccount)) {
            return ResponseEntity.badRequest().body("Sender and receiver accounts cannot be the same");
        }

        if (!isAccountEnabled(fromAccount) || !isAccountEnabled(toAccount)) {
            return ResponseEntity.badRequest().body("Transaction cannot be performed. One or both accounts are disabled.");
        }

        try {
            Transaction transaction = transactionService.transferAmount(fromAccount, toAccount, amount.doubleValue());
            return ResponseEntity.ok(formatTransactionResponse(transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
 //------------------------------------------------------------------------------------------------------------------------

    //17  Get all transactions
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllTransactions() {
        List<Map<String, Object>> formattedTransactions = transactionService.getAllTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed()) 
                .limit(10) 
                .map(this::formatTransactionResponse) 
                .toList();
        return ResponseEntity.ok(formattedTransactions);
    }

//------------------------------------------------------------------------------------------------------------------------

    //18 Get transactions for a particular account
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getTransactionsForAccount(@PathVariable Long accountNumber) {
        authorizeTransaction(accountNumber);

        List<Transaction> transactions = transactionService.getTransactionsForAccount(accountNumber);
        
        transactions.addAll(transactionService.findreceiverAccount(accountNumber));

        if (transactions.isEmpty()) {
            return ResponseEntity.ok("No transactions found for this account");
        }

        List<Map<String, Object>> formattedTransactions = transactions.stream()
        		.sorted(Comparator.comparing(Transaction::getTimestamp).reversed()) 
                .limit(20) 
                .map(this::formatTransactionResponse)
                .toList();

        return ResponseEntity.ok(formattedTransactions);
    }
//------------------------------------------------------------------------------------------------------------------------

    //19 Get transaction by ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long transactionId) {
        Optional<Transaction> transaction = transactionService.getTransactionbyId(transactionId);
        authorizeTransaction(transaction.get().getAccount().getAccountNo());
        return ResponseEntity.ok(formatTransactionResponse(transaction.get()));
    }
//------------------------------------------------------------------------------------------------------------------------

    // Authorization method to check if the user owns the account
    private void authorizeTransaction(Long accountNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();
        
        Customer loggedInCustomer = customerService.getCustomer(loggedInEmail);

        // Get all account numbers owned by the logged-in customer
        Set<Long> accountNumbers = bankAccountService.getAccountsForCustomer(loggedInCustomer.getCustomerId())
                .stream()
                .map(BankAccount::getAccountNo)
                .collect(Collectors.toSet());

        // Allow only the owner or a manager to proceed
        if (!accountNumbers.contains(accountNumber) && !"ROLE_MANAGER".equalsIgnoreCase(loggedInCustomer.getRole())) {
            throw new SecurityException("Unauthorized: You do not have permission to access transactions for account " + accountNumber);
        }
    }
//------------------------------------------------------------------------------------------------------------------------

    // Check if the account is enabled
    private boolean isAccountEnabled(Long accountNumber) {
        BankAccount account = bankAccountService.getAccountById(accountNumber);
        return account.isActive()==true;
    }
//------------------------------------------------------------------------------------------------------------------------

    // Helper method to format transaction response
    private Map<String, Object> formatTransactionResponse(Transaction transaction) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", transaction.getId());
        response.put("amount", transaction.getAmount());
        response.put("type", transaction.getType());
        response.put("timestamp", transaction.getTimestamp());
        response.put("account", transaction.getAccount().getAccountNo());
        
//        response.put("transaction", transaction);
        
        if ("TRANSFER".equals(transaction.getType()) && transaction.getReceiverAccount() != null) {
            response.put("receiverAccount", transaction.getReceiverAccount().getAccountNo());
        } else {
            response.put("receiverAccount", null);
        }

        return response;
    }
//------------------------------------------------------------------------------------------------------------------------
}
