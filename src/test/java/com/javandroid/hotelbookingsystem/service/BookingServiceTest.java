package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.repository.BookingRepository;
import com.javandroid.hotelbookingsystem.repository.CustomerRepository;
import com.javandroid.hotelbookingsystem.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

//    @Test
//    void testCreateBooking_Success() {
//        // Arrange
//        Booking booking = new Booking(1, 1, 1, new Date(System.currentTimeMillis()),
//                new Date(System.currentTimeMillis()),
//                new Date(System.currentTimeMillis() + 86400000));
//
//        when(bookingRepository.saveBooking(any(Booking.class))).thenReturn(booking);
//
//        // Act
//        Booking savedBooking = bookingService.createBooking(booking);
//
//        // Assert
//        assertNotNull(savedBooking);
//        assertEquals(1, savedBooking.getId());
//    }

    @Test
    void testCreateBooking_CustomerNotFound() {
        // Arrange
        Booking booking = new Booking();
        booking.setCustomerId(99);  // Non-existent customer

        when(customerRepository.findById(99)).thenReturn(Optional.empty());  // Mock customer not found

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(booking));
    }


//    @Test
//    void testCreateBooking_RoomNotFound() {
//        // Arrange
//        Booking booking = new Booking();
//        booking.setRoomId(99);  // Non-existent room
//
//        when(roomRepository.findById(99)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(booking));
//    }
}
