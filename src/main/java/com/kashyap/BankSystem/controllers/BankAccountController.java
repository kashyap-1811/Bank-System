package com.kashyap.BankSystem.controllers;

import com.kashyap.BankSystem.entities.BankAccount;
import com.kashyap.BankSystem.entities.Customer;
import com.kashyap.BankSystem.services.BankAccountService;
import com.kashyap.BankSystem.services.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final CustomerService customerService;

    public BankAccountController(BankAccountService bankAccountService, CustomerService customerService) {
        this.bankAccountService = bankAccountService;
        this.customerService = customerService;
    }
//------------------------------------------------------------------------------------------------------------------------

    //6 Create Account for customer
    @PostMapping("/{customerId}/create/{type}")
    public BankAccount createAccount(@RequestBody BankAccount account, @RequestParam Double extraValue,
            @PathVariable Long customerId, @PathVariable String type) {
        BankAccount createdAccount = bankAccountService.createAccount(customerId, type, account, extraValue);
        return createdAccount;
    }
//------------------------------------------------------------------------------------------------------------------------

    //7 Link Customer to account
    @PostMapping("/{accountId}/add-customer/{customerId}")
    public String addCustomerToAccount(@PathVariable long accountId, @PathVariable Long customerId) {
        bankAccountService.addCustomerToAccount(accountId, customerId);
        return "Customer " + customerId + " has added the account " + accountId;
    }
//------------------------------------------------------------------------------------------------------------------------

    //8 Remove Customer from Account
    @DeleteMapping("/{accountId}/remove-customer/{customerId}")
    public String removeCustomerFromAccount(@PathVariable long accountId, @PathVariable Long customerId) {
        bankAccountService.removeCustomerFromAccount(accountId, customerId);
        return "Account_id " + accountId + " has removed the customer " + customerId;
    }
//------------------------------------------------------------------------------------------------------------------------

    //9 Delete Account
    @DeleteMapping("/{accountId}/delete")
    public String deleteAccount(@PathVariable long accountId) {
        bankAccountService.deleteAccount(accountId);
        return "Account " + accountId +  " deleted successfully";
    }
//------------------------------------------------------------------------------------------------------------------------

    //10 Get Account by id
    @GetMapping("/{accountId}")
    public BankAccount getAccountById(@PathVariable Long accountId) {
        authorizeAccountAccess(accountId);
        return bankAccountService.getAccountById(accountId);
    }
//------------------------------------------------------------------------------------------------------------------------

    //11 Update Account Status by id
    @PatchMapping("/{accountId}/status")
    public BankAccount updateAccountStatus(@PathVariable Long accountId, @RequestParam boolean isActive) {
        BankAccount updatedAccount = bankAccountService.updateAccountStatus(accountId, isActive);
        return updatedAccount;
    }
//------------------------------------------------------------------------------------------------------------------------
    
    //12 Get all accounts by customer
    @GetMapping("/customer/{customerId}")
    public Set<BankAccount> getAccountsForCustomer(@PathVariable Long customerId) {
        authorizeSelf(customerId);
        Set<BankAccount> accounts = bankAccountService.getAccountsForCustomer(customerId);
        return accounts;
    }
//------------------------------------------------------------------------------------------------------------------------

    //13 Get all accounts
    @GetMapping("/all")
    public List<BankAccount> getAllAccounts()
    {
    	List<BankAccount> accounts = bankAccountService.getAllAccounts();
    	return accounts;
    }
//------------------------------------------------------------------------------------------------------------------------
   
    //
    private void authorizeSelf(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();
        Customer loggedInCustomer = customerService.getCustomer(loggedInEmail);
        
        if (!customerService.getCustomer(loggedInEmail).getCustomerId().equals(customerId) && !loggedInCustomer.getRole().equals("ROLE_MANAGER")) {
            throw new SecurityException("You are not authorized to access this account.");
        }
    }
//------------------------------------------------------------------------------------------------------------------------

    //
    private void authorizeAccountAccess(long accountId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();
        Customer loggedInCustomer = customerService.getCustomer(loggedInEmail);

        Set<BankAccount> accounts = bankAccountService.getAccountsForCustomer(loggedInCustomer.getCustomerId());

        boolean isAuthorized = accounts.stream().anyMatch(account -> account.getAccountNo() == accountId);

        if (!isAuthorized && !loggedInCustomer.getRole().equals("ROLE_MANAGER")) {
            throw new SecurityException("You are not authorized to access this account.");
        }
    }
//------------------------------------------------------------------------------------------------------------------------
}
