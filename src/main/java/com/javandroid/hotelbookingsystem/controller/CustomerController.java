package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ✅ Display customers list
    @GetMapping
    public String getAllCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers"; // Load customers.html
    }

    // ✅ Show form to add a customer
    @GetMapping("/new")
    public String showCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form"; // Load customer-form.html
    }

    // ✅ Handle customer form submission
    @PostMapping
    public String addCustomer(@ModelAttribute Customer customer) {
        customerService.addCustomer(customer);
        return "redirect:/customers"; // Redirect to customers list
    }
}
