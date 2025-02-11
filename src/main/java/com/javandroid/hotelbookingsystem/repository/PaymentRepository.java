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

    // ✅ Retrieve all payments
    public List<Payment> getAllPayments() {
        String sql = "SELECT * FROM payments";
        return jdbcTemplate.query(sql, new PaymentRowMapper());
    }

    // ✅ Find a payment by ID
    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new PaymentRowMapper(), id);
    }



    public Optional<Payment> getPaymentByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new PaymentRowMapper(), bookingId));
        } catch (Exception e) {
            return Optional.empty(); // Return empty if no payment is found
        }
    }


    // ✅ Save a new payment
    public int savePayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, payment.getBookingId(), payment.getAmount(), payment.getPaymentDate(), payment.getPaymentMethod());
    }

    // ✅ Delete a payment
    public int deletePayment(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // ✅ Row Mapper for Payment object
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
