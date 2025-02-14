package com.javandroid.hotelbookingsystem.repository;

import com.javandroid.hotelbookingsystem.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Retrieve all payments for a customer
    public List<Payment> getPaymentsByCustomerId(int customerId) {
        String sql = "SELECT p.* FROM payments p JOIN bookings b ON p.booking_id = b.id WHERE b.customer_id = ?";
        return jdbcTemplate.query(sql, new PaymentRowMapper(), customerId);
    }

    //Get payment by ID
    public Optional<Payment> getPaymentById(int paymentId) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new PaymentRowMapper(), paymentId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    //Retrieve a payment by booking ID
    public Optional<Payment> getPaymentByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new PaymentRowMapper(), bookingId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    //Save a new payment
    public void savePayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, payment_date) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, payment.getBookingId(), payment.getAmount(), payment.getPaymentMethod(), payment.getPaymentDate());
    }

    //Delete a payment when booking is canceled
    public int deletePaymentByBookingId(int bookingId) {
        String sql = "DELETE FROM payments WHERE booking_id = ?";
        return jdbcTemplate.update(sql, bookingId);
    }

    public double getTotalPaymentsByCustomerId(int customerId) {
        String sql = "SELECT SUM(amount) FROM payments WHERE booking_id IN " +
                "(SELECT id FROM bookings WHERE customer_id = ?)";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Double.class, customerId)).orElse(0.0);
    }


    //Row Mapper for Payment object
    private static class PaymentRowMapper implements RowMapper<Payment> {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Payment(
                    rs.getInt("id"),
                    rs.getInt("booking_id"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("payment_date"),
                    rs.getString("payment_method")

            );
        }
    }
}
