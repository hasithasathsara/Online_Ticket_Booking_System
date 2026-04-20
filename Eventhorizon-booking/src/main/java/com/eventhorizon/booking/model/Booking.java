package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Represents a ticket booking in the system
@Entity
@Table(name = "bookings")
@Inheritance(strategy = InheritanceType.JOINED)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long eventId;

    @Column
    private String eventTitle;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String status = "pending"; // pending, approved, or rejected

    @Column
    private LocalDateTime bookedAt = LocalDateTime.now();

    @Column
    private String bookingType = "regular";

    // Variable to store the uploaded bank slip image path
    @Column
    private String paymentSlip;

    // Default constructor required by JPA
    public Booking() {}

    // Constructor with 4 parameters (Fallback)
    public Booking(Long userId, Long eventId, String eventTitle, int quantity) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.quantity = quantity;
        this.status = "pending";
        this.bookedAt = LocalDateTime.now();
    }

    // Constructor with 5 parameters including the payment slip
    public Booking(Long userId, Long eventId, String eventTitle, int quantity, String paymentSlip) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.quantity = quantity;
        this.paymentSlip = paymentSlip;
        this.status = "pending";
        this.bookedAt = LocalDateTime.now();
    }

    // Methods to change booking status
    public void approve() {
        if (this.status.equals("pending")) {
            this.status = "approved";
        }
    }

    public void reject() {
        if (this.status.equals("pending")) {
            this.status = "rejected";
        }
    }

    public void cancel() {
        if (!this.status.equals("rejected")) {
            this.status = "rejected";
        }
    }

    // Status check methods
    public boolean isPending()  { return "pending".equals(status); }
    public boolean isApproved() { return "approved".equals(status); }
    public boolean isRejected() { return "rejected".equals(status); }

    // Getters and Setters
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }

    public Long getEventId()                    { return eventId; }
    public void setEventId(Long eventId)        { this.eventId = eventId; }

    public String getEventTitle()               { return eventTitle; }
    public void setEventTitle(String t)         { this.eventTitle = t; }

    public int getQuantity()                    { return quantity; }
    public void setQuantity(int quantity)       { this.quantity = quantity; }

    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }

    public LocalDateTime getBookedAt()          { return bookedAt; }
    public void setBookedAt(LocalDateTime t)    { this.bookedAt = t; }

    public String getBookingType()              { return bookingType; }
    public void setBookingType(String t)        { this.bookingType = t; }

    public String getPaymentSlip()              { return paymentSlip; }
    public void setPaymentSlip(String s)        { this.paymentSlip = s; }

    // Returns a summary of the booking
    public String getDetails() {
        return "Booking #" + id + " | Event: " + eventTitle
                + " | Qty: " + quantity + " | Status: " + status.toUpperCase();
    }
}