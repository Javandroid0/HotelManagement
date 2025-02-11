package com.javandroid.hotelbookingsystem.repository;



//import com.hotelmanagement.model.Booking;
import com.javandroid.hotelbookingsystem.model.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookingRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Retrieve all bookings
    public List<Booking> getAllBookings() {
        String sql = "SELECT * FROM bookings";
        return jdbcTemplate.query(sql, new BookingRowMapper());
    }

    // ✅ Find a booking by ID
    public Booking getBookingById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BookingRowMapper(), id);
    }

    // ✅ Save a new booking
    public int saveBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, room_id, booking_date, check_in_date, check_out_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, booking.getCustomerId(), booking.getRoomId(),
                booking.getBookingDate(), booking.getCheckInDate(), booking.getCheckOutDate());
    }

    // ✅ Delete a booking
    public int deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void updateBookingDates(int id, java.sql.Date checkIn, java.sql.Date checkOut) {
        String sql = "UPDATE bookings SET check_in_date = ?, check_out_date = ? WHERE id = ?";
        jdbcTemplate.update(sql, checkIn, checkOut, id);
    }


    // ✅ Check if a room is already booked
    public boolean isRoomBooked(int roomId, java.util.Date checkIn, java.util.Date checkOut) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND " +
                "(check_in_date < ? AND check_out_date > ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomId, checkOut, checkIn);
        return count != null && count > 0;
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
