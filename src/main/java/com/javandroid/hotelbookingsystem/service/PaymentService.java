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
import java.time.LocalDate;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    // ✅ Retrieve all payments
    public List<Payment> getAllPayments() {
        logger.info("Fetching all payments...");
        return paymentRepository.getAllPayments();
    }

    public Payment getPaymentByBookingId(int bookingId) {
        logger.info("Fetching payment for Booking ID {}", bookingId);
        return paymentRepository.getPaymentByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for Booking ID " + bookingId + " not found."));
    }




    public void processPayment(int bookingId, String paymentMethod) {
        logger.info("Processing payment for Booking ID: {}", bookingId);

        // Fetch the booking details
        Booking booking = bookingService.getBookingById(bookingId);
        Room room = roomService.getRoomById(booking.getRoomId());

        // ✅ Convert java.sql.Date to LocalDate
        LocalDate checkIn = booking.getCheckInDate().toLocalDate();
        LocalDate checkOut = booking.getCheckOutDate().toLocalDate();

        // Calculate total cost based on room price and stay duration
        long daysStayed = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (daysStayed <= 0) {
            throw new IllegalArgumentException("Invalid check-in and check-out dates.");
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


    // ✅ Delete a payment
    public void deletePayment(int id) {
        logger.warn("Deleting payment with ID {}", id);
        paymentRepository.deletePayment(id);
    }
}
