package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.model.BookingServices;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingServiceRowMapper implements RowMapper<BookingServices> {
    @Override
    public BookingServices mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BookingServices(
                rs.getInt("booking_id"),
                rs.getInt("service_id")// Service price from additional_services table
        );
    }
}
