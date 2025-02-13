package com.javandroid.hotelbookingsystem.model;


import java.sql.Date;
import java.util.List;

public class Booking {
    private int id;
    private int customerId;
    private int roomId;
    private Date bookingDate;
    private Date checkInDate;
    private Date checkOutDate;
    private List<AdditionalService> additionalServices;

    // Constructors
    public Booking() {}
    public Booking(int id, int customerId, int roomId, Date bookingDate, Date checkInDate, Date checkOutDate) {
        this.id = id;
        this.customerId = customerId;
        this.roomId = roomId;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }


    public List<AdditionalService> getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(List<AdditionalService> additionalServices) {
        this.additionalServices = additionalServices;
    }
}
