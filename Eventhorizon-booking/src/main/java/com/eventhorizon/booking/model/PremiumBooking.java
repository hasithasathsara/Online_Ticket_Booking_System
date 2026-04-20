package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// PremiumBooking inherits from Booking (OOP: Inheritance)
@Entity
@Table(name = "premium_bookings")
public class PremiumBooking extends Booking {

    @Column
    private boolean prioritySeat = true;

    @Column
    private boolean vipLoungeAccess = false;

    @Column
    private String seatNumber;

    // Default constructor
    public PremiumBooking() {
        super();
        setBookingType("premium");
    }

    // Constructor that passes the payment slip to the parent Booking class
    public PremiumBooking(Long userId, Long eventId, String eventTitle,
                          int quantity, String paymentSlip, boolean vipLounge) {

        // Pass all 5 variables to the Parent class
        super(userId, eventId, eventTitle, quantity, paymentSlip);

        this.prioritySeat = true;
        this.vipLoungeAccess = vipLounge;
        setBookingType("premium");
    }

    // Getters and Setters
    public boolean isPrioritySeat()              { return prioritySeat; }
    public void setPrioritySeat(boolean p)       { this.prioritySeat = p; }

    public boolean isVipLoungeAccess()           { return vipLoungeAccess; }
    public void setVipLoungeAccess(boolean v)    { this.vipLoungeAccess = v; }

    public String getSeatNumber()                { return seatNumber; }
    public void setSeatNumber(String s)          { this.seatNumber = s; }

    // Overrides the parent method to show VIP details (OOP: Polymorphism)
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