package com.kashyap.BankSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    private final DataSource dataSource;

    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
//------------------------------------------------------------------------------------------------------------------------

    //JDBC Role Based Security
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        // Fetch user credentials from customers table
        manager.setUsersByUsernameQuery(
                "SELECT email, password, enabled FROM customers WHERE email = ?"
        );

        // Fetch roles from customers table
        manager.setAuthoritiesByUsernameQuery(
                "SELECT email, role FROM customers WHERE email = ?"
        );

        return manager;
    }
//------------------------------------------------------------------------------------------------------------------------

    //End points security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                // Customer Endpoints
                .requestMatchers(HttpMethod.POST, "/customers/add").hasRole("MANAGER") //1 add customer
                .requestMatchers(HttpMethod.GET, "/customers/{customerId}").hasAnyRole("CUSTOMER","MANAGER") //2 get customer
                .requestMatchers(HttpMethod.PUT, "/customers/{customerId}").hasRole("CUSTOMER") // 3update customer
                .requestMatchers(HttpMethod.DELETE, "/customers/{customerId}").hasRole("MANAGER") //4 delete customer
//                .requestMatchers(HttpMethod.GET, "/customers/bxn {customerId}/accounts").hasRole("CUSTOMER") //5 get accounts

                // Bank Account Endpoints
                .requestMatchers(HttpMethod.POST, "/accounts/{customerId}/create/{type}").hasRole("MANAGER") //6 create account
                .requestMatchers(HttpMethod.POST, "/accounts/{accountId}/add-customer/{customerId}").hasRole("MANAGER") //7 add customer to accout
                .requestMatchers(HttpMethod.DELETE, "/accounts/{accountId}/remove-customer/{customerId}").hasRole("MANAGER") //8 remove customer from account
                .requestMatchers(HttpMethod.DELETE, "/accounts/{accountId}/delete").hasRole("MANAGER") //9 delete account
                .requestMatchers(HttpMethod.GET, "/accounts/{accountId}").hasAnyRole("CUSTOMER","MANAGER")//10 get account by id
                .requestMatchers(HttpMethod.GET, "/accounts/customer/{customerId}").hasAnyRole("CUSTOMER", "MANAGER")//11 get all account of a customer
                .requestMatchers(HttpMethod.GET, "accounts/all").hasRole("MANAGER")//12 get all accounts
                .requestMatchers(HttpMethod.PATCH, "/accounts/{accountId}/status").hasRole("MANAGER")//13 change account status

                // Transactions Endpoints
                .requestMatchers(HttpMethod.POST, "/transactions/credit/{accountNumber}").hasRole("MANAGER") //14 credit amount
                .requestMatchers(HttpMethod.POST, "/transactions/debit/{accountNumber}").hasRole("MANAGER") //15 debit amount
                .requestMatchers(HttpMethod.POST, "/transactions/transfer").hasAnyRole("CUSTOMER","MANAGER") //16 transfer money
                .requestMatchers(HttpMethod.GET, "/transactions/all").hasRole("MANAGER") //17 get all transactions
                .requestMatchers(HttpMethod.GET, "/transactions/account/{accountNumber}").hasAnyRole("CUSTOMER", "MANAGER") //18 get all transaction of an accout
                .requestMatchers(HttpMethod.GET, "/transactions/{transactionId}").hasAnyRole("CUSTOMER", "MANAGER") //19 get transaction by id

                .anyRequest().authenticated()
        );

        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
//------------------------------------------------------------------------------------------------------------------------

    //Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//------------------------------------------------------------------------------------------------------------------------
}
