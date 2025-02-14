package com.javandroid.hotelbookingsystem.repository;

import com.javandroid.hotelbookingsystem.model.AdditionalService;
import com.javandroid.hotelbookingsystem.model.Booking;
import com.javandroid.hotelbookingsystem.service.BookingService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AdditionalServiceRepository {
    private final JdbcTemplate jdbcTemplate;

    public AdditionalServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Retrieve all available services
    public List<AdditionalService> getAllServices() {
        String sql = "SELECT * FROM additional_services";
        return jdbcTemplate.query(sql, new AdditionalServiceRowMapper());
    }

    //Get a service by ID
    public Optional<AdditionalService> getServiceById(int id) {
        String sql = "SELECT * FROM additional_services WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new AdditionalServiceRowMapper(), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<AdditionalService> getServicesForCustomer(int customerId) {
        String sql = "SELECT s.*, bs.booking_id FROM additional_services s " +
                "JOIN booking_services bs ON s.id = bs.service_id " +
                "JOIN bookings b ON bs.booking_id = b.id " +
                "WHERE b.customer_id = ?";
        return jdbcTemplate.query(sql, new AdditionalServiceRowMapper(), customerId);
    }

    //Request an additional service for a booking
    public void addServiceToBooking(int bookingId, int serviceId) {
        String sql = "INSERT INTO booking_services (booking_id, service_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, bookingId, serviceId);
    }

    //Get services requested for a specific booking
    public List<AdditionalService> getServicesForBooking(int bookingId) {
        String sql = "SELECT s.* FROM additional_services s " +
                "JOIN booking_services bs ON s.id = bs.service_id " +
                "WHERE bs.booking_id = ?";
        return jdbcTemplate.query(sql, new AdditionalServiceRowMapper(), bookingId);
    }


    //Remove an additional service from a booking
    public void removeServiceFromBooking(int bookingId, int serviceId) {
        String sql = "DELETE FROM booking_services WHERE booking_id = ? AND service_id = ?";
        jdbcTemplate.update(sql, bookingId, serviceId);
    }


    //Row Mapper for AdditionalService object
    private static class AdditionalServiceRowMapper implements RowMapper<AdditionalService> {
        @Override
        public AdditionalService mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AdditionalService(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
            );
        }
    }
}
