package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.model.Payment;
import com.javandroid.hotelbookingsystem.model.Room;
import com.javandroid.hotelbookingsystem.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final RoomService roomService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, BookingService bookingService, RoomService roomService) {
        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    // ✅ Retrieve all payments for a customer
    public List<Payment> getPaymentsByCustomerId(int customerId) {
        logger.info("Fetching all payments for customer ID {}", customerId);
        return paymentRepository.getPaymentsByCustomerId(customerId);
    }

    // ✅ Get a payment by booking ID
    public Payment getPaymentByBookingId(int bookingId) {
        logger.info("Fetching payment for Booking ID {}", bookingId);
        return paymentRepository.getPaymentByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for Booking ID " + bookingId + " not found."));
    }
    // ✅ Get payment by ID
    public Payment getPaymentById(int paymentId) {
        logger.info("Fetching payment with ID {}", paymentId);
        return paymentRepository.getPaymentById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment with ID {} not found", paymentId);
                    return new ResourceNotFoundException("Payment with ID " + paymentId + " not found.");
                });
    }

    // ✅ Process a payment for a booking
    @Transactional
    public void processPayment(int bookingId, String paymentMethod) {
        logger.info("Processing payment for Booking ID: {}", bookingId);

        // Fetch the booking details
        Booking booking = bookingService.getBookingById(bookingId);
        Room room = roomService.getRoomById(booking.getRoomId());

        // Calculate total cost based on room price and stay duration
        long daysStayed = ChronoUnit.DAYS.between(
                booking.getCheckInDate().toLocalDate(),
                booking.getCheckOutDate().toLocalDate()
        );
        if (daysStayed <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        double totalAmount = room.getPrice() * daysStayed;
        logger.info("Total payment amount for Booking ID {} is {}", bookingId, totalAmount);

        // Create and save payment
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));

        paymentRepository.savePayment(payment);
    }

    // ✅ Refund payment when a booking is canceled
    @Transactional
    public void refundPaymentByBookingId(int bookingId) {
        logger.warn("Rolling back payment for Booking ID {}", bookingId);

        int deleted = paymentRepository.deletePaymentByBookingId(bookingId);
        if (deleted > 0) {
            logger.info("Payment for Booking ID {} refunded successfully.", bookingId);
        } else {
            logger.warn("No payment found for Booking ID {}", bookingId);
        }
    }
}
