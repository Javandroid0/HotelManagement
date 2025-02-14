package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Returns login.html
    }

    //Handle login
    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String phone, HttpSession session) {
        Optional<Customer> customer = authService.authenticateUser(email, phone);

        if (customer.isPresent()) {
            session.setAttribute("loggedUser", customer.get());
            logger.info("User logged in: {}", customer.get().getEmail());
            return "redirect:/customer/home";
        } else {
            logger.warn("Login failed for email: {}", email);
            return "redirect:/auth/login?error=Invalid credentials";
        }
    }

    //Show registration page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Returns register.html
    }

    //Handle user registration
    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String phone, @RequestParam String address,HttpSession session) {
        try {
            Customer newCustomer = authService.registerUser(name, email, phone,address);
            session.setAttribute("loggedUser", newCustomer);
            logger.info("New user registered and logged in: {}", email);
            return "redirect:/customer/home";
        } catch (IllegalStateException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return "redirect:/auth/register?error=" + e.getMessage();
        }
    }

    //Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("User {} logged out", session.getAttribute("loggedUser"));
        session.invalidate();
        return "redirect:/auth/login";
    }
}
