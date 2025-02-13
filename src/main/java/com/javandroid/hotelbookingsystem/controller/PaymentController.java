package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.model.Payment;
import com.javandroid.hotelbookingsystem.service.BookingService;
import com.javandroid.hotelbookingsystem.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    // ✅ Display all payments for the logged-in customer
    @GetMapping
    public String getCustomerPayments(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized access to payments page");
            return "redirect:/auth/login";
        }

        List<Payment> payments = paymentService.getPaymentsByCustomerId(loggedUser.getId());
        model.addAttribute("payments", payments);
        return "payments"; // Returns payments.html
    }

    // ✅ Show payment form with available bookings
    @GetMapping("/pay")
    public String showPaymentForm(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        List<Booking> bookings = bookingService.getBookingsByCustomerId(loggedUser.getId());
        model.addAttribute("bookings", bookings);
        return "payment-form"; // Returns payment-form.html
    }

    // ✅ Process a payment for a booking
    @PostMapping("/process")
    public String processPayment(@RequestParam int bookingId, @RequestParam String paymentMethod, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        // Ensure the booking belongs to the logged-in customer
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking.getCustomerId() != loggedUser.getId()) {
            logger.warn("Unauthorized payment attempt for Booking ID {}", bookingId);
            return "redirect:/payments?error=Unauthorized action";
        }

        paymentService.processPayment(bookingId, paymentMethod);
        logger.info("Payment processed for Booking ID {} by Customer ID {}", bookingId, loggedUser.getId());
        return "redirect:/payments?success=Payment completed";
    }
    // ✅ Delete (Refund) a payment
    @GetMapping("/delete/{paymentId}")
    public String refundPayment(@PathVariable int paymentId, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized attempt to refund payment ID {}", paymentId);
            return "redirect:/auth/login";
        }

        Payment payment = paymentService.getPaymentById(paymentId);

        // ✅ Ensure only the owner of the booking can refund the payment
        Booking booking = bookingService.getBookingById(payment.getBookingId());
        if (booking.getCustomerId() != loggedUser.getId()) {
            logger.warn("Unauthorized refund attempt for Payment ID {}", paymentId);
            return "redirect:/payments?error=Unauthorized action";
        }

        paymentService.refundPaymentByBookingId(payment.getBookingId());
        logger.info("Payment ID {} refunded successfully for Customer ID {}", paymentId, loggedUser.getId());

        return "redirect:/payments?success=Payment refunded";
    }

}
