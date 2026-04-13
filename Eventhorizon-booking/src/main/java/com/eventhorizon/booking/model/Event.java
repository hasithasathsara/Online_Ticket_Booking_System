package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// =============================================
// EVENT.JAVA — Member 02
// OOP Concept: ENCAPSULATION
// All fields are PRIVATE
// =============================================

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String category;

    @Column
    private String venue;

    @Column
    private String description;

    @Column
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private int totalTickets;

    @Column(nullable = false)
    private int availableTickets;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private String imageUrl;

    @Column
    private String eventType = "physical"; // physical or online

    // ── CONSTRUCTORS ─────────────────────────────
    public Event() {}

    public Event(String title, String category, String venue,
                 LocalDateTime eventDate, int totalTickets, BigDecimal price) {
        this.title           = title;
        this.category        = category;
        this.venue           = venue;
        this.eventDate       = eventDate;
        this.totalTickets    = totalTickets;
        this.availableTickets = totalTickets;
        this.price           = price;
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    public String getCategory()                 { return category; }
    public void setCategory(String category)    { this.category = category; }

    public String getVenue()                    { return venue; }
    public void setVenue(String venue)          { this.venue = venue; }

    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }

    public LocalDateTime getEventDate()         { return eventDate; }
    public void setEventDate(LocalDateTime d)   { this.eventDate = d; }

    public int getTotalTickets()                { return totalTickets; }
    public void setTotalTickets(int t)          { this.totalTickets = t; }

    public int getAvailableTickets()            { return availableTickets; }
    public void setAvailableTickets(int t)      { this.availableTickets = t; }

    public BigDecimal getPrice()                { return price; }
    public void setPrice(BigDecimal price)      { this.price = price; }

    public String getImageUrl()                 { return imageUrl; }
    public void setImageUrl(String url)         { this.imageUrl = url; }

    public String getEventType()                { return eventType; }
    public void setEventType(String type)       { this.eventType = type; }

    // ── METHOD (overridden in child = Polymorphism) ──
    public String getDetails() {
        return "Event: " + title + " | Venue: " + venue
             + " | Date: " + eventDate + " | Price: $" + price
             + " | Available: " + availableTickets;
    }

    public boolean hasTicketsAvailable() {
        return availableTickets > 0;
    }
}
