package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.model.Customer;
import com.javandroid.hotelbookingsystem.model.Room; // Assuming you have a Room model
import com.javandroid.hotelbookingsystem.service.BookingService;
import com.javandroid.hotelbookingsystem.service.RoomService; // Assuming you have a RoomService to fetch available rooms
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final RoomService roomService; // RoomService to get available rooms

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    //Show room booking form with available rooms
    @GetMapping("/new")
    public String showBookingForm(Model model) {
        List<Room> availableRooms = roomService.getAvailableRooms(); // Get available rooms
        model.addAttribute("rooms", availableRooms); // Pass rooms to the view
        return "booking-form"; // Returns booking-form.html
    }

    //Handle room booking
    @PostMapping("/book")
    public String bookRoom(@RequestParam int roomId, @RequestParam Date checkInDate, @RequestParam Date checkOutDate, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        Booking booking = new Booking();
        booking.setCustomerId(loggedUser.getId());
        booking.setRoomId(roomId);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);

        bookingService.createBooking(booking);
        return "redirect:/bookings";
    }

    //Display all bookings for the logged-in customer
    @GetMapping
    public String getCustomerBookings(HttpSession session, Model model) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        List<Booking> bookings = bookingService.getBookingsByCustomerId(loggedUser.getId());
        model.addAttribute("bookings", bookings);
        return "customer-bookings"; // Returns customer-bookings.html
    }

    //Cancel booking (Only the logged-in customer can cancel their own booking)
    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable int id, HttpSession session) {
        Customer loggedUser = (Customer) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        Booking booking = bookingService.getBookingById(id);
        if (booking.getCustomerId() != loggedUser.getId()) {
            return "redirect:/error"; // Unauthorized access
        }

        bookingService.deleteBooking(id);
        return "redirect:/bookings";
    }
}
