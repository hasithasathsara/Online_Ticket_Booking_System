package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// =============================================
// BOOKING.JAVA — Member 03
// OOP Concept: ENCAPSULATION + ABSTRACTION
// Private status field — only changeable
// through specific methods (not direct set)
// =============================================

@Entity
@Table(name = "bookings")
@Inheritance(strategy = InheritanceType.JOINED)
public class Booking {

    // ── PRIVATE FIELDS ───────────────────────────
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
    private String status = "pending"; // pending, approved, rejected

    @Column
    private LocalDateTime bookedAt = LocalDateTime.now();

    @Column
    private String bookingType = "regular";

    // ── CONSTRUCTORS ─────────────────────────────
    public Booking() {}

    public Booking(Long userId, Long eventId, String eventTitle, int quantity) {
        this.userId     = userId;
        this.eventId    = eventId;
        this.eventTitle = eventTitle;
        this.quantity   = quantity;
        this.status     = "pending";
        this.bookedAt   = LocalDateTime.now();
    }

    // ── ABSTRACTION — business logic hidden here ──
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

    public boolean isPending()  { return "pending".equals(status); }
    public boolean isApproved() { return "approved".equals(status); }
    public boolean isRejected() { return "rejected".equals(status); }

    // ── GETTERS AND SETTERS ──────────────────────
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

    // ── POLYMORPHISM ─────────────────────────────
    public String getDetails() {
        return "Booking #" + id + " | Event: " + eventTitle
             + " | Qty: " + quantity + " | Status: " + status.toUpperCase();
    }
}
