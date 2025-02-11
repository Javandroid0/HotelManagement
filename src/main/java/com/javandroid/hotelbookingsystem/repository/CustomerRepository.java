package com.javandroid.hotelbookingsystem.repository;



//import com.hotelmanagement.model.Customer;
import com.javandroid.hotelbookingsystem.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Customer> getAllCustomers() {
        return jdbcTemplate.query("SELECT * FROM customers",
                (rs, rowNum) -> new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
    }

    public int saveCustomer(Customer customer) {
        return jdbcTemplate.update(
                "INSERT INTO customers (name, email, phone, address) VALUES (?, ?, ?, ?)",
                customer.getName(), customer.getEmail(), customer.getPhone(), customer.getAddress()
        );
    }
}
