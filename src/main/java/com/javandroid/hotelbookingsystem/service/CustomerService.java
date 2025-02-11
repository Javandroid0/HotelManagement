package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ✅ Retrieve all customers
    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers...");
        return customerRepository.getAllCustomers();
    }

    // ✅ Add a new customer
    public void addCustomer(Customer customer) {
        logger.info("Adding new customer: {}", customer.getName());
        customerRepository.saveCustomer(customer);
    }
}
