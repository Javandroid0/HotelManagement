package com.javandroid.hotelbookingsystem.model;


public class AdditionalService {
    private int id;
    private String name;
    private double price;

    // Constructors
    public AdditionalService() {}
    public AdditionalService(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
