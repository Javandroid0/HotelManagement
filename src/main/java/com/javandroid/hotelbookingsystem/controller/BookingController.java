package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ✅ Display bookings list in Thymeleaf
    @GetMapping
    public String getAllBookings(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "booking"; // Returns bookings.html
    }

    // ✅ Display form for new booking
    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("booking", new Booking());
        return "booking-form"; // Returns booking-form.html
    }

    // ✅ Handle booking form submission
    @PostMapping
    public String createBooking(
            @RequestParam int customerId,
            @RequestParam int roomId,
            @RequestParam Date checkInDate,
            @RequestParam Date checkOutDate) {

        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setRoomId(roomId);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        bookingService.createBooking(booking);

        return "redirect:/bookings"; // Redirects to bookings list
    }

    // ✅ Delete a booking
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable int id) {
        bookingService.deleteBooking(id);
        return "redirect:/bookings";
    }
}
