package com.javandroid.hotelbookingsystem.repository;

import com.javandroid.hotelbookingsystem.model.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Retrieve all bookings for a customer
    public List<Booking> getBookingsByCustomerId(int customerId) {
        String sql = "SELECT * FROM bookings WHERE customer_id = ?";
        return jdbcTemplate.query(sql, new BookingRowMapper(), customerId);
    }

    // ✅ Retrieve a booking by ID (Fix `orElseThrow()` issue)
    public Optional<Booking> getBookingById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new BookingRowMapper(), id));
        } catch (Exception e) {
            return Optional.empty(); // Return empty if booking not found
        }
    }

    // ✅ Save a new booking
    public void saveBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, booking.getCustomerId(), booking.getRoomId(), booking.getCheckInDate(), booking.getCheckOutDate());
    }

    // ✅ Delete a booking
    public void deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ✅ Row Mapper for Booking object
    private static class BookingRowMapper implements RowMapper<Booking> {
        @Override
        public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Booking(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    rs.getInt("room_id"),
                    rs.getDate("booking_date"),
                    rs.getDate("check_in_date"),
                    rs.getDate("check_out_date")
            );
        }
    }
}
