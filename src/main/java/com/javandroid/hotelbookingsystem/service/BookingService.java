package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.model.BookingServices;
import com.javandroid.hotelbookingsystem.model.Room;
import com.javandroid.hotelbookingsystem.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);


    public BookingService(BookingRepository bookingRepository, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
    }

    //Retrieve all bookings for a customer
    public List<Booking> getBookingsByCustomerId(int customerId) {
        logger.info("Fetching bookings for customer ID {}", customerId);
        return bookingRepository.getBookingsByCustomerId(customerId);
    }

    //Get a booking by ID
    public Booking getBookingById(int id) {
        logger.info("Fetching booking with ID {}", id);
        return bookingRepository.getBookingById(id)
                .orElseThrow(() -> {
                    logger.error("Booking with ID {} not found", id);
                    return new ResourceNotFoundException("Booking with ID " + id + " not found.");
                });
    }

    //Create a new booking and mark room as "Booked"
    @Transactional
    public void createBooking(Booking booking) {
        logger.info("Creating a new booking for Customer ID: {}", booking.getCustomerId());

        // Fetch room details
        Room room = roomService.getRoomById(booking.getRoomId());

        // Check if the room is available
        if (!"Available".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Room ID " + booking.getRoomId() + " is already booked.");
        }

        // Validate check-in and check-out dates
        if (booking.getCheckInDate().after(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        // Save booking and update room status
        bookingRepository.saveBooking(booking);
        roomService.updateRoomStatus(booking.getRoomId(), "Booked");

        logger.info("Booking created successfully! Room ID {} is now marked as Booked.", booking.getRoomId());
    }



    public List<BookingServices> getBookingServicesByCustomerId(int customerId) {
        logger.info("Fetching additional services for Customer ID {}", customerId);
        return bookingRepository.getBookingServicesByCustomerId(customerId);
    }

    //Delete a booking and mark room as "Available"
    @Transactional
    public void deleteBooking(int id) {
        logger.warn("Deleting booking with ID {}", id);
        Booking booking = getBookingById(id);

        // Delete booking and update room status
        bookingRepository.deleteBooking(id);

        roomService.updateRoomStatus(booking.getRoomId(), "Available");

        logger.info("Booking ID {} deleted. Room ID {} is now marked as Available.", id, booking.getRoomId());
    }
}
