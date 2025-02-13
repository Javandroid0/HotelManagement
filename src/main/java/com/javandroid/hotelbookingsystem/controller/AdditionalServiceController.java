package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.AdditionalService;
import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.service.AdditionalServiceService;
import com.javandroid.hotelbookingsystem.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/additional-services")
public class AdditionalServiceController {
    private final AdditionalServiceService additionalServiceService;
    private final BookingService bookingService;
    private static final Logger logger = LoggerFactory.getLogger(AdditionalServiceController.class);

    public AdditionalServiceController(AdditionalServiceService additionalServiceService, BookingService bookingService) {
        this.additionalServiceService = additionalServiceService;
        this.bookingService = bookingService;
    }

    // ✅ Show available additional services
    @GetMapping
    public String getAvailableServices(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        List<AdditionalService> services = additionalServiceService.getAllServices();
        List<Booking> bookings = bookingService.getBookingsByCustomerId(loggedUser.getId());

        model.addAttribute("services", services);
        model.addAttribute("bookings", bookings);
        return "additional-services"; // Returns additional-services.html
    }

    // ✅ Request an additional service for a booking
    @PostMapping("/request")
    public String requestService(@RequestParam int bookingId, @RequestParam int serviceId, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        // Ensure the booking belongs to the logged-in customer
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking.getCustomerId() != loggedUser.getId()) {
            logger.warn("Unauthorized request attempt for Booking ID {}", bookingId);
            return "redirect:/additional-services?error=Unauthorized action";
        }

        additionalServiceService.addServiceToBooking(bookingId, serviceId);
        logger.info("Service ID {} added to Booking ID {} by Customer ID {}", serviceId, bookingId, loggedUser.getId());
        return "redirect:/additional-services?success=Service added";
    }

    // ✅ Remove an additional service from a booking
    @GetMapping("/remove")
    public String removeService(@RequestParam int bookingId, @RequestParam int serviceId, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        // Ensure the booking belongs to the logged-in customer
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking.getCustomerId() != loggedUser.getId()) {
            logger.warn("Unauthorized service removal attempt for Booking ID {}", bookingId);
            return "redirect:/additional-services?error=Unauthorized action";
        }

        additionalServiceService.removeServiceFromBooking(bookingId, serviceId);
        logger.info("Service ID {} removed from Booking ID {} by Customer ID {}", serviceId, bookingId, loggedUser.getId());
        return "redirect:/additional-services?success=Service removed";
    }

}
