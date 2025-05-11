package com.kashyap.BankSystem.controllers;

import com.kashyap.BankSystem.entities.Customer;
import com.kashyap.BankSystem.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
//------------------------------------------------------------------------------------------------------------------------

    //1 Create customer
    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.addCustomer(customer));
    }
//------------------------------------------------------------------------------------------------------------------------

    //2 Update customer
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer) {
        authorizeSelf(customerId);
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customer));
    }
//------------------------------------------------------------------------------------------------------------------------

    //3 Get customer
    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable Long customerId) {
        authorizeSelf(customerId);
        return customerService.getCustomer(customerId);
    }
//------------------------------------------------------------------------------------------------------------------------

    //4 Delete customer
    @DeleteMapping("/{customerId}")
    public String deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return "Customer deleted successfully";
    }
//------------------------------------------------------------------------------------------------------------------------

//    //5 Get customer accounts
//    @GetMapping("/{customerId}/accounts")
//    public ResponseEntity<Set<BankAccount>> getCustomerAccounts(@PathVariable Long customerId) {
//        authorizeSelf(customerId);
//        return ResponseEntity.ok(customerService.getCustomerAccounts(customerId));
//    }
//------------------------------------------------------------------------------------------------------------------------

    private void authorizeSelf(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName(); // This is the email of the logged-in customer
        Customer customer = customerService.getCustomer(customerId);
        if (!customer.getEmail().equals(loggedInEmail)) {
            throw new SecurityException("You are not authorized to access this customer's information.");
        }
    }
//------------------------------------------------------------------------------------------------------------------------
}
