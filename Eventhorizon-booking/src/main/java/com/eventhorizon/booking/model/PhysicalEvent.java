package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// PHYSICALEVENT.JAVA — Member 02
// OOP Concept: INHERITANCE + POLYMORPHISM
// PhysicalEvent EXTENDS Event
// Adds: venueCapacity, parkingAvailable
// =============================================

@Entity
@Table(name = "physical_events")
public class PhysicalEvent extends Event {

    @Column
    private int venueCapacity;

    @Column
    private boolean parkingAvailable = false;

    @Column
    private String venueAddress;

    @Column
    private String mapLink;

    // ── CONSTRUCTORS ─────────────────────────────
    public PhysicalEvent() {
        super();
        setEventType("physical");
    }

    public PhysicalEvent(String title, String category, String venue,
                         java.time.LocalDateTime eventDate,
                         int totalTickets, java.math.BigDecimal price,
                         int venueCapacity) {
        super(title, category, venue, eventDate, totalTickets, price);
        this.venueCapacity = venueCapacity;
        setEventType("physical");
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public int getVenueCapacity()               { return venueCapacity; }
    public void setVenueCapacity(int c)         { this.venueCapacity = c; }

    public boolean isParkingAvailable()         { return parkingAvailable; }
    public void setParkingAvailable(boolean p)  { this.parkingAvailable = p; }

    public String getVenueAddress()             { return venueAddress; }
    public void setVenueAddress(String a)       { this.venueAddress = a; }

    public String getMapLink()                  { return mapLink; }
    public void setMapLink(String m)            { this.mapLink = m; }

    // ── POLYMORPHISM — overrides parent method ───
    @Override
    public String getDetails() {
        return "Physical Event: " + getTitle()
             + " | Venue: " + getVenue()
             + " | Capacity: " + venueCapacity
             + " | Parking: " + (parkingAvailable ? "Yes" : "No")
             + " | Price: $" + getPrice();
    }
}
