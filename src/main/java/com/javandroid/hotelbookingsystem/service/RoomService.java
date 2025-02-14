package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.Room;
import com.javandroid.hotelbookingsystem.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    //Retrieve all available rooms
    public List<Room> getAvailableRooms() {
        logger.info("Fetching all available rooms...");
        return roomRepository.findAvailableRooms();
    }

    //Get a room by ID
    public Room getRoomById(int id) {
        logger.info("Fetching room with ID {}", id);
        Room room = roomRepository.getRoomById(id);
        if (room == null) {
            logger.error("Room with ID {} not found", id);
            throw new ResourceNotFoundException("Room with ID " + id + " not found.");
        }
        return room;
    }

    //Mark room as "Available" when a booking is deleted
    @Transactional
    public void markRoomAsAvailable(int roomId) {
        logger.info("Marking room ID {} as 'Available'", roomId);
        Room room = getRoomById(roomId);

        if (!"Booked".equalsIgnoreCase(room.getStatus())) {
            logger.warn("Room ID {} is already available.", roomId);
            return;
        }

        roomRepository.updateRoomStatus(roomId, "Available");
    }

    //Update room status manually
    @Transactional
    public void updateRoomStatus(int roomId, String status) {
        logger.info("Updating room ID {} to status '{}'", roomId, status);
        if (!status.equalsIgnoreCase("Available") && !status.equalsIgnoreCase("Booked")) {
            logger.error("Invalid status: {}", status);
            throw new IllegalArgumentException("Invalid status. Use 'Available' or 'Booked'.");
        }
        roomRepository.updateRoomStatus(roomId, status);
    }

    //Add a new room
    @Transactional
    public void addRoom(Room room) {
        logger.info("Adding new room: Type - {}, Price - {}", room.getType(), room.getPrice());
        room.setStatus("Available"); // Ensure new rooms start as Available
        roomRepository.saveRoom(room);
    }

    //Delete a room
    @Transactional
    public void deleteRoom(int roomId) {
        logger.warn("Deleting room with ID {}", roomId);
        roomRepository.deleteRoom(roomId);
    }
}
