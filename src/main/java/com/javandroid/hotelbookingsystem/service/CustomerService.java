package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ✅ Get a customer by ID
    public Customer getCustomerById(int id) {
        logger.info("Fetching customer with ID {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", id);
                    return new ResourceNotFoundException("Customer with ID " + id + " not found.");
                });
    }

    // ✅ Update customer information
    @Transactional
    public void updateCustomer(int id, String name, String email, String phone,String address) {
        logger.info("Updating customer ID {}: Name={}, Email={}, Phone={}, Address={}", id, name, email, phone,address);

        Customer customer = getCustomerById(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);

        customerRepository.updateCustomer(id, name, email, phone,address);
        logger.info("Customer ID {} updated successfully.", id);
    }
}
