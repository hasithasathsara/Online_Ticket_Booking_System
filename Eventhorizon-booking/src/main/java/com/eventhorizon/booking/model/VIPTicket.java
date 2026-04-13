package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// VIPTICKET.JAVA — Member 04
// OOP Concept: INHERITANCE + POLYMORPHISM
// VIPTicket EXTENDS Ticket
// Adds: loungeAccess, meetAndGreet
// =============================================

@Entity
@Table(name = "vip_tickets")
public class VIPTicket extends Ticket {

    @Column
    private boolean loungeAccess = true;

    @Column
    private boolean meetAndGreet = false;

    @Column
    private String vipSeatNumber;

    @Column
    private String specialPerks;

    // ── CONSTRUCTORS ─────────────────────────────
    public VIPTicket() {
        super();
        setTicketType("vip");
    }

    public VIPTicket(Long bookingId, Long userId, String userName,
                     String eventTitle, String eventDate, boolean meetAndGreet) {
        super(bookingId, userId, userName, eventTitle, eventDate);
        this.loungeAccess = true;
        this.meetAndGreet = meetAndGreet;
        setTicketType("vip");
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public boolean isLoungeAccess()             { return loungeAccess; }
    public void setLoungeAccess(boolean l)      { this.loungeAccess = l; }

    public boolean isMeetAndGreet()             { return meetAndGreet; }
    public void setMeetAndGreet(boolean m)      { this.meetAndGreet = m; }

    public String getVipSeatNumber()            { return vipSeatNumber; }
    public void setVipSeatNumber(String s)      { this.vipSeatNumber = s; }

    public String getSpecialPerks()             { return specialPerks; }
    public void setSpecialPerks(String p)       { this.specialPerks = p; }

    // ── POLYMORPHISM — VIP display with badge ────
    @Override
    public String display() {
        return "⭐ VIP Ticket: " + getTicketCode()
             + " | Event: " + getEventTitle()
             + " | Lounge: " + (loungeAccess ? "Yes" : "No")
             + " | Meet & Greet: " + (meetAndGreet ? "Yes" : "No")
             + " | Status: " + getStatus().toUpperCase();
    }
}
