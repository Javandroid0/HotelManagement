package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Payment;
import com.javandroid.hotelbookingsystem.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ Display all payments
    @GetMapping
    public String getAllPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payments"; // Returns payments.html
    }

    // ✅ Show form to process a payment
    @GetMapping("/process")
    public String showPaymentForm() {
        return "payment-form"; // Returns payment-form.html
    }

    // ✅ Handle payment processing
    @PostMapping("/process")
    public String processPayment(@RequestParam int bookingId, @RequestParam String paymentMethod) {
        paymentService.processPayment(bookingId, paymentMethod);
        return "redirect:/payments"; // Redirect to payments list after processing
    }

    // ✅ Delete a payment
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable int id) {
        paymentService.deletePayment(id);
        return "redirect:/payments";
    }
}
