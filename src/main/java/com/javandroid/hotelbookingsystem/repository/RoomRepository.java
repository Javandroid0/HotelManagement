package com.javandroid.hotelbookingsystem.repository;

import com.javandroid.hotelbookingsystem.model.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RoomRepository {
    private final JdbcTemplate jdbcTemplate;

    public RoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms";
        return jdbcTemplate.query(sql, new RoomRowMapper());
    }

    public List<Room> findAvailableRooms() {
        String sql = "SELECT * FROM rooms WHERE status = 'Available'";
        return jdbcTemplate.query(sql, new RoomRowMapper());
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new RoomRowMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, roomId);
    }

    // ✅ Save a new room
    public int saveRoom(Room room) {
        String sql = "INSERT INTO rooms (type, price, status) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, room.getType(), room.getPrice(), room.getStatus());
    }

    // ✅ Delete a room
    public int deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // ✅ Row Mapper for Room object
    private static class RoomRowMapper implements RowMapper<Room> {
        @Override
        public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Room(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getDouble("price"),
                    rs.getString("status")
            );
        }
    }
}
