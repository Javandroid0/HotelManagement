package com.javandroid.hotelbookingsystem.repository;



import com.javandroid.hotelbookingsystem.model.AdditionalService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdditionalServiceRepository {
    private final JdbcTemplate jdbcTemplate;

    public AdditionalServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Retrieve all additional services
    public List<AdditionalService> getAllServices() {
        String sql = "SELECT * FROM additional_services";
        return jdbcTemplate.query(sql, new AdditionalServiceRowMapper());
    }

    // ✅ Find an additional service by ID
    public AdditionalService getServiceById(int id) {
        String sql = "SELECT * FROM additional_services WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new AdditionalServiceRowMapper(), id);
    }

    // ✅ Save a new additional service
    public int saveService(AdditionalService service) {
        String sql = "INSERT INTO additional_services (name, price) VALUES (?, ?)";
        return jdbcTemplate.update(sql, service.getName(), service.getPrice());
    }

    // ✅ Delete an additional service
    public int deleteService(int id) {
        String sql = "DELETE FROM additional_services WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // ✅ Link a service to a booking (Many-to-Many)
    public int addServiceToBooking(int bookingId, int serviceId) {
        String sql = "INSERT INTO booking_services (booking_id, service_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, bookingId, serviceId);
    }

    // ✅ Remove a service from a booking
    public int removeServiceFromBooking(int bookingId, int serviceId) {
        String sql = "DELETE FROM booking_services WHERE booking_id = ? AND service_id = ?";
        return jdbcTemplate.update(sql, bookingId, serviceId);
    }

    // ✅ Get all services for a booking
    public List<AdditionalService> getServicesByBookingId(int bookingId) {
        String sql = "SELECT s.* FROM additional_services s " +
                "JOIN booking_services bs ON s.id = bs.service_id " +
                "WHERE bs.booking_id = ?";
        return jdbcTemplate.query(sql, new AdditionalServiceRowMapper(), bookingId);
    }

    // ✅ Row Mapper for AdditionalService object
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

