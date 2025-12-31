package com.example.demo.model;

public class Bus {
    private int id;
    private String busNumber;
    private String source;
    private String destination;
    private String seatType;
    private int availableSeats;
    private int totalSeats;
    private double ticketPrice;

    // Constructors
    public Bus() {}

    public Bus(int id, String busNumber, String source, String destination, String seatType, int availableSeats, int totalSeats, double ticketPrice) {
        this.id = id;
        this.busNumber = busNumber;
        this.source = source;
        this.destination = destination;
        this.seatType = seatType;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.ticketPrice = ticketPrice;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    }
    

