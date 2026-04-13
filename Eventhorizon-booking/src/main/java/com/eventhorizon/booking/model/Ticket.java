package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

// =============================================
// TICKET.JAVA — Member 04
// OOP Concept: ENCAPSULATION
// Private ticketCode — generated automatically
// =============================================

@Entity
@Table(name = "tickets")
@Inheritance(strategy = InheritanceType.JOINED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketCode;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String userName;

    @Column
    private String eventTitle;

    @Column
    private String eventDate;

    @Column(nullable = false)
    private String status = "active"; // active, used, cancelled

    @Column
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column
    private String ticketType = "standard";

    // ── CONSTRUCTORS ─────────────────────────────
    public Ticket() {
        this.ticketCode = generateCode();
    }

    public Ticket(Long bookingId, Long userId, String userName,
                  String eventTitle, String eventDate) {
        this.ticketCode  = generateCode();
        this.bookingId   = bookingId;
        this.userId      = userId;
        this.userName    = userName;
        this.eventTitle  = eventTitle;
        this.eventDate   = eventDate;
        this.status      = "active";
        this.issuedAt    = LocalDateTime.now();
    }

    // ── PRIVATE METHOD — code generation (Abstraction) ──
    private String generateCode() {
        String uuid = UUID.randomUUID().toString().toUpperCase().replace("-","");
        return "TK-" + uuid.substring(0, 8);
    }

    public void markAsUsed() {
        if ("active".equals(this.status)) {
            this.status = "used";
        }
    }

    public void cancel() {
        if ("active".equals(this.status)) {
            this.status = "cancelled";
        }
    }

    public boolean isActive()    { return "active".equals(status); }
    public boolean isUsed()      { return "used".equals(status); }
    public boolean isCancelled() { return "cancelled".equals(status); }

    // ── GETTERS AND SETTERS ──────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public String getTicketCode()               { return ticketCode; }
    public void setTicketCode(String code)      { this.ticketCode = code; }

    public Long getBookingId()                  { return bookingId; }
    public void setBookingId(Long bookingId)    { this.bookingId = bookingId; }

    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }

    public String getUserName()                 { return userName; }
    public void setUserName(String n)           { this.userName = n; }

    public String getEventTitle()               { return eventTitle; }
    public void setEventTitle(String t)         { this.eventTitle = t; }

    public String getEventDate()                { return eventDate; }
    public void setEventDate(String d)          { this.eventDate = d; }

    public String getStatus()                   { return status; }
    public void setStatus(String status)        { this.status = status; }

    public LocalDateTime getIssuedAt()          { return issuedAt; }
    public void setIssuedAt(LocalDateTime t)    { this.issuedAt = t; }

    public String getTicketType()               { return ticketType; }
    public void setTicketType(String t)         { this.ticketType = t; }

    // ── POLYMORPHISM ─────────────────────────────
    public String display() {
        return "Ticket: " + ticketCode + " | Event: " + eventTitle
             + " | Status: " + status.toUpperCase();
    }
}
