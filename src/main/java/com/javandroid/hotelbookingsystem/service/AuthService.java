package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ✅ Authenticate customer (Login)
    public Optional<Customer> authenticateUser(String email, String phone) {
        logger.info("Authenticating user with email: {}", email);
        return customerRepository.findByEmailAndPhone(email, phone);
    }

    // ✅ Register a new customer
    @Transactional
    public Customer registerUser(String name, String email, String phone, String address) {
        logger.info("Registering new customer: {}", email);

        // Check if customer already exists
        Optional<Customer> existingCustomer = customerRepository.findByEmailAndPhone(email, phone);
        if (existingCustomer.isPresent()) {
            logger.warn("Customer with email {} and phone {} already exists", email, phone);
            throw new IllegalStateException("User already exists. Please log in.");
        }

        // Save new customer
        Customer newCustomer = new Customer(name, email, phone, address);
        customerRepository.save(newCustomer);

        logger.info("New customer registered successfully: {}", newCustomer.getEmail());
        return newCustomer;
    }
}
