package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.*;
import com.javandroid.hotelbookingsystem.repository.AdditionalServiceRepository;
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
    private final AdditionalServiceRepository additionalServiceService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, BookingService bookingService, AdditionalServiceRepository additionalServiceService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.additionalServiceService = additionalServiceService;
    }

    @GetMapping
    public String getCustomerPayments(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        double debt = paymentService.calculateDebt(loggedUser.getId());
        List<Booking> bookings = bookingService.getBookingsByCustomerId(loggedUser.getId());
        List<Payment> payments = paymentService.getPaymentsByCustomerId(loggedUser.getId());
        List<BookingServices> bookingServices = bookingService.getBookingServicesByCustomerId(loggedUser.getId());
        List<AdditionalService> additionalServices = additionalServiceService.getServicesForCustomer(loggedUser.getId());
        model.addAttribute("debt", debt);
        model.addAttribute("bookings", bookings);
        model.addAttribute("payments", payments);
        model.addAttribute("bookingServices", bookingServices);
        model.addAttribute("services", additionalServices);

        return "payments";
    }

    @PostMapping("/pay-service")
    public String payForService(@RequestParam int bookingId, @RequestParam int serviceId, @RequestParam String paymentMethod, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        // Process payment for additional service
        paymentService.processServicePayment(bookingId, serviceId,paymentMethod);
        return "redirect:/payments?success=Service payment completed";
    }



    //Show payment form with debt
    @GetMapping("/pay")
    public String showPaymentForm(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        //Fetch customer's debt
        double debt = paymentService.calculateDebt(loggedUser.getId());

        if (debt > 0) {
            logger.warn("Customer ID {} has an outstanding debt of ${}", loggedUser.getId(), debt);
            model.addAttribute("error", "You have an outstanding debt of $" + debt + ". Please settle your dues.");
        }

        List<Booking> bookings = bookingService.getBookingsByCustomerId(loggedUser.getId());

        model.addAttribute("debt", debt);
        model.addAttribute("bookings", bookings);
        model.addAttribute("payments", paymentService.getPaymentsByCustomerId(loggedUser.getId()));
        return "payment-form"; // Returns payment-form.html
    }


    //Process a payment for a booking
    @PostMapping("/process")
    public String processPayment(@RequestParam int bookingId, @RequestParam String paymentMethod, HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        //Process the payment
        paymentService.processPayment(bookingId, paymentMethod);
        logger.info("Payment processed for Booking ID {} by Customer ID {}", bookingId, loggedUser.getId());
        return "redirect:/payments?success=Payment completed";
    }

    //Delete (Refund) a payment
    @GetMapping("/delete/{paymentId}")
    public String refundPayment(@PathVariable int paymentId, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Unauthorized attempt to refund payment ID {}", paymentId);
            return "redirect:/auth/login";
        }

        Payment payment = paymentService.getPaymentById(paymentId);

        //Ensure only the owner of the booking can refund the payment
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
