package com.kashyap.BankSystem.services;

import com.kashyap.BankSystem.entities.BankAccount;
import com.kashyap.BankSystem.entities.Customer;
import com.kashyap.BankSystem.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }
//------------------------------------------------------------------------------------------------------------------------

    //1 add Customer
    public Customer addCustomer(Customer customer) {
        String rawPassword = customer.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        customer.setPassword(encodedPassword);

        // Default role
        customer.setRole("ROLE_CUSTOMER");

        return customerRepository.save(customer);
    }
//------------------------------------------------------------------------------------------------------------------------

    //2 update customer
    public Customer updateCustomer(Long customerId, Customer updatedCustomer) {
        Optional<Customer> existingCustomerOpt = customerRepository.findById(customerId);
        if (existingCustomerOpt.isPresent())
        {
            Customer existingCustomer = existingCustomerOpt.get();
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setCity(updatedCustomer.getCity());
            existingCustomer.setPhoneNo(updatedCustomer.getPhoneNo());

            if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
                existingCustomer.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
            }

            return customerRepository.save(existingCustomer);
        } else {
            throw new RuntimeException("Customer not found");
        }
    }
//------------------------------------------------------------------------------------------------------------------------

    //3 Get Customer by id
    public Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
//------------------------------------------------------------------------------------------------------------------------

    //4 Delete Customer by id
    public void deleteCustomer(Long customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
        } else {
            throw new RuntimeException("Customer not found");
        }
    }
//------------------------------------------------------------------------------------------------------------------------

    //5 Get all accounts of a Customer by id
    public Set<BankAccount> getCustomerAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customer.getAccounts();
    }
//------------------------------------------------------------------------------------------------------------------------

    // Get Customer by Email
    public Customer getCustomer(String email)
    {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
//------------------------------------------------------------------------------------------------------------------------
}
