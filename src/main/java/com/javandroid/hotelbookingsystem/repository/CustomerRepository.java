package com.javandroid.hotelbookingsystem.repository;

import com.javandroid.hotelbookingsystem.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class CustomerRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Find a customer by email and phone
    public Optional<Customer> findByEmailAndPhone(String email, String phone) {
        String sql = "SELECT * FROM customers WHERE email = ? AND phone = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new CustomerRowMapper(), email, phone));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    //Find a customer by ID
    public Optional<Customer> findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new CustomerRowMapper(), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    //Register a new customer
    public void save(Customer customer) {
        String sql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getPhone());
    }

    //Update customer details
    public void updateCustomer(int id, String name, String email, String phone,String address) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, email, phone,address, id);
    }

    //Row Mapper for Customer object
    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
            );
        }
    }
}
