package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// PREMIUMBOOKING.JAVA — Member 03
// OOP Concept: INHERITANCE + POLYMORPHISM
// PremiumBooking EXTENDS Booking
// Adds: prioritySeat, vipLounge access
// =============================================

@Entity
@Table(name = "premium_bookings")
public class PremiumBooking extends Booking {

    @Column
    private boolean prioritySeat = true;

    @Column
    private boolean vipLoungeAccess = false;

    @Column
    private String seatNumber;

    // ── CONSTRUCTORS ─────────────────────────────
    public PremiumBooking() {
        super();
        setBookingType("premium");
    }

    public PremiumBooking(Long userId, Long eventId, String eventTitle,
                          int quantity, boolean vipLounge) {
        super(userId, eventId, eventTitle, quantity);
        this.prioritySeat    = true;
        this.vipLoungeAccess = vipLounge;
        setBookingType("premium");
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public boolean isPrioritySeat()              { return prioritySeat; }
    public void setPrioritySeat(boolean p)       { this.prioritySeat = p; }

    public boolean isVipLoungeAccess()           { return vipLoungeAccess; }
    public void setVipLoungeAccess(boolean v)    { this.vipLoungeAccess = v; }

    public String getSeatNumber()                { return seatNumber; }
    public void setSeatNumber(String s)          { this.seatNumber = s; }

    // ── POLYMORPHISM — overrides parent ──────────
    @Override
    public String getDetails() {
        return "VIP Booking #" + getId()
             + " | Event: " + getEventTitle()
             + " | Qty: " + getQuantity()
             + " | Priority Seat: Yes"
             + " | VIP Lounge: " + (vipLoungeAccess ? "Yes" : "No")
             + " | Status: " + getStatus().toUpperCase();
    }
}
