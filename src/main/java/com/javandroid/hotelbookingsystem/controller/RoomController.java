package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.Room;
import com.javandroid.hotelbookingsystem.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    //Display all rooms
    @GetMapping
    public String getAllRooms(Model model) {
        List<Room> rooms = roomService.getAvailableRooms();
        model.addAttribute("rooms", rooms);
        return "rooms"; // Returns rooms.html
    }

    //Show form to add a new room
    @GetMapping("/new")
    public String showRoomForm(Model model) {
        model.addAttribute("room", new Room());
        return "room-form"; // Returns room-form.html
    }

    //Handle room creation
    @PostMapping
    public String addRoom(@ModelAttribute Room room) {
        roomService.addRoom(room);
        return "redirect:/rooms"; // Redirect to rooms list
    }

    //Update room status
    @PostMapping("/{id}/status")
    public String updateRoomStatus(@PathVariable int id, @RequestParam String status) {
        roomService.updateRoomStatus(id, status);
        return "redirect:/rooms"; // Refresh room list
    }

    //Delete a room
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable int id) {
        roomService.deleteRoom(id);
        return "redirect:/rooms"; // Refresh room list
    }
}
