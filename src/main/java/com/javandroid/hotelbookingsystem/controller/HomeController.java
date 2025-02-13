package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Customer;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    // ✅ Customer dashboard (only accessible if logged in)
    @GetMapping("/customer/home")
    public String customerHome(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            logger.warn("Unauthorized access attempt to customer home");
            return "redirect:/auth/login";
        }

        model.addAttribute("customer", loggedUser);
        logger.info("Customer {} accessed the dashboard", loggedUser.getEmail());
        return "customer-home"; // Returns customer-home.html
    }

    // ✅ Admin panel (accessible without authentication)
    @GetMapping("/admin")
    public String adminHome() {
        logger.info("Admin panel accessed");
        return "index"; // Returns home.html (existing admin dashboard)
    }
}
