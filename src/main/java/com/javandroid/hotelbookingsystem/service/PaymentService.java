package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.AdditionalService;
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
    private final AdditionalServiceService additionalServiceService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, BookingService bookingService,
                          RoomService roomService, AdditionalServiceService additionalServiceService) {
        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.additionalServiceService = additionalServiceService;
    }

    //Retrieve all payments for a customer
    public List<Payment> getPaymentsByCustomerId(int customerId) {
        logger.info("Fetching all payments for customer ID {}", customerId);
        return paymentRepository.getPaymentsByCustomerId(customerId);
    }

    //Get payment by ID
    public Payment getPaymentById(int paymentId) {
        logger.info("Fetching payment with ID {}", paymentId);
        return paymentRepository.getPaymentById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment with ID {} not found", paymentId);
                    return new ResourceNotFoundException("Payment with ID " + paymentId + " not found.");
                });
    }



    @Transactional
    public void processPayment(int bookingId, String paymentMethod) {
        logger.info("Processing payment for Booking ID: {}", bookingId);

        //Fetch booking details
        Booking booking = bookingService.getBookingById(bookingId);
        int customerId = booking.getCustomerId();

        //Calculate total amount for this booking
        double totalAmount = calculateTotalAmount(booking);
        logger.info("Total payment amount for Booking ID {} is {}", bookingId, totalAmount);

        //Create and save payment
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));

        paymentRepository.savePayment(payment);
        logger.info("Payment successful for Booking ID {}", bookingId);
    }

    @Transactional
    public void processServicePayment(int bookingId, int serviceId, String paymentMethod) {
        logger.info("Processing service payment for Booking ID {} and Service ID {} using {}",
                bookingId, serviceId, paymentMethod);

        AdditionalService service = additionalServiceService.getServiceById(serviceId);
        double serviceAmount = service.getPrice();

        // Create and save payment
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(serviceAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));

        paymentRepository.savePayment(payment);
        logger.info("Service Payment successful for Booking ID {} and Service ID {} using {}",
                bookingId, serviceId, paymentMethod);
    }



    //Calculate customer's outstanding debt by comparing total cost and total payments
    public double calculateDebt(int customerId) {
        logger.info("Calculating total debt for Customer ID {}", customerId);

        //Get all bookings for the customer
        List<Booking> customerBookings = bookingService.getBookingsByCustomerId(customerId);

        double totalCost = 0.0;
        double totalPaid = paymentRepository.getTotalPaymentsByCustomerId(customerId);

        for (Booking booking : customerBookings) {
            double bookingAmount = calculateTotalAmount(booking); //Get total cost for each booking
            totalCost += bookingAmount;
            logger.info("Booking ID {} - Total Cost: {}", booking.getId(), bookingAmount);
        }

        logger.info("Total Cost for Customer ID {}: {}", customerId, totalCost);
        logger.info("Total Paid for Customer ID {}: {}", customerId, totalPaid);

        double debt = totalCost - totalPaid;
        logger.info("Outstanding Debt for Customer ID {}: {}", customerId, debt);

        return Math.max(debt, 0); //Ensure debt is never negative
    }


    //Separate method to calculate total amount
    public double calculateTotalAmount(Booking booking) {
        Room room = roomService.getRoomById(booking.getRoomId());

        // Calculate total stay duration in days
        long daysStayed = ChronoUnit.DAYS.between(
                booking.getCheckInDate().toLocalDate(),
                booking.getCheckOutDate().toLocalDate()
        );
        if (daysStayed <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        // Calculate room cost
        double roomCost = room.getPrice() * daysStayed;
        logger.info("Room cost for Booking ID {}: {}", booking.getId(), roomCost);

        // Fetch additional services linked to this booking
        List<AdditionalService> additionalServices = additionalServiceService.getServicesForBooking(booking.getId());

        // Calculate additional services cost
        double additionalServicesCost = additionalServices.stream()
                .mapToDouble(AdditionalService::getPrice)
                .sum();
        logger.info("Additional services cost for Booking ID {}: {}", booking.getId(), additionalServicesCost);

        // Return total cost (room cost + additional services)
        return roomCost + additionalServicesCost;
    }

    //Refund payment when a booking is canceled
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
