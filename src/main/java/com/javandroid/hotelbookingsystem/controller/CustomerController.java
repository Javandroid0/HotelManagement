package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ✅ Display customer profile
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized access to profile page");
            return "redirect:/auth/login";
        }

        model.addAttribute("customer", loggedUser);
        return "customer-profile"; // Returns customer-profile.html
    }

    // ✅ Show profile edit form
    @GetMapping("/edit")
    public String showEditProfileForm(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized access to edit profile page");
            return "redirect:/auth/login";
        }

        model.addAttribute("customer", loggedUser);
        return "edit-profile"; // Returns edit-profile.html
    }

    // ✅ Handle profile update
    @PostMapping("/update")
    public String updateProfile(@RequestParam String name, @RequestParam String email, @RequestParam String phone,@RequestParam String address, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized attempt to update profile");
            return "redirect:/auth/login";
        }

        customerService.updateCustomer(loggedUser.getId(), name, email, phone,address);
        loggedUser.setName(name);
        loggedUser.setEmail(email);
        loggedUser.setPhone(phone);
        loggedUser.setAddress(address);
        session.setAttribute("loggedUser", loggedUser);

        logger.info("Customer ID {} updated profile successfully", loggedUser.getId());
        return "redirect:/customer/profile";
    }

    // ✅ Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("Customer {} logged out", session.getAttribute("loggedUser"));
        session.invalidate();
        return "redirect:/auth/login";
    }
}
