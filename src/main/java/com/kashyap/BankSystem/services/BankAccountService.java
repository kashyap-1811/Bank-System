package com.kashyap.BankSystem.services;

import com.kashyap.BankSystem.entities.BankAccount;
import com.kashyap.BankSystem.entities.CurrentAccount;
import com.kashyap.BankSystem.entities.Customer;
import com.kashyap.BankSystem.entities.SavingsAccount;
import com.kashyap.BankSystem.repository.BankAccountRepository;
import com.kashyap.BankSystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, CustomerRepository customerRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
    }

//------------------------------------------------------------------------------------------------------------------------

    //6 Create a Savings or Current account
    public BankAccount createAccount(Long customerId, String type, BankAccount account, Double extraValue) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        BankAccount newAccount;
        if ("savings".equalsIgnoreCase(type))
        {
            newAccount = new SavingsAccount(account.getBalance(), account.isActive(), account.getOpeningDate(), extraValue);
        }
        else if ("current".equalsIgnoreCase(type))
        {
            newAccount = new CurrentAccount(account.getBalance(), account.isActive(), account.getOpeningDate(), extraValue);
        }
        else
        {
            throw new RuntimeException("Invalid account type");
        }

        newAccount.getCustomers().add(customer);
        customer.getAccounts().add(newAccount);

        return bankAccountRepository.save(newAccount);
    }
// ------------------------------------------------------------------------------------------------------------------------

    //7 Add customer to an existing account
    public BankAccount addCustomerToAccount(long accountId, Long customerId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (account.getCustomers().contains(customer)) {
            throw new RuntimeException("Customer is already associated with this account");
        }

        account.getCustomers().add(customer);
        customer.getAccounts().add(account);

        return bankAccountRepository.save(account);
    }
//------------------------------------------------------------------------------------------------------------------------

    //8 Remove customer from an account
    public BankAccount removeCustomerFromAccount(long accountId, Long customerId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (!account.getCustomers().contains(customer)) {
            throw new RuntimeException("Customer is not associated with this account");
        }

        account.getCustomers().remove(customer);
        customer.getAccounts().remove(account);

        return bankAccountRepository.save(account);
    }
//------------------------------------------------------------------------------------------------------------------------

    //9 Delete an account
    public void deleteAccount(long accountId) {
        if (!bankAccountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }
        bankAccountRepository.deleteById(accountId);
    }
//------------------------------------------------------------------------------------------------------------------------

    //10 Get account by ID
    public BankAccount getAccountById(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

//------------------------------------------------------------------------------------------------------------------------

    //11 Update account status (Active/Inactive)
    public BankAccount updateAccountStatus(Long accountId, boolean isActive) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setActive(isActive);
        return bankAccountRepository.save(account);
    }
//------------------------------------------------------------------------------------------------------------------------

    //12 Get accounts for a particular customer
    public Set<BankAccount> getAccountsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customer.getAccounts();
    }
//------------------------------------------------------------------------------------------------------------------------

    //13 Get all accounts
    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }
//------------------------------------------------------------------------------------------------------------------------
}
