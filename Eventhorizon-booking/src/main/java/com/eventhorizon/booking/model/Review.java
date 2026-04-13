package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// =============================================
// REVIEW.JAVA — Member 06
// OOP Concept: ENCAPSULATION
// Private rating (1-5) and comment fields
// =============================================

@Entity
@Table(name = "reviews")
@Inheritance(strategy = InheritanceType.JOINED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long eventId;

    @Column
    private String userName;

    @Column
    private String eventTitle;

    @Column(nullable = false)
    private int rating; // 1 to 5

    @Column(length = 1000)
    private String comment;

    @Column
    private boolean isVerified = false;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── CONSTRUCTORS ─────────────────────────────
    public Review() {}

    public Review(Long userId, Long eventId, String userName,
                  String eventTitle, int rating, String comment) {
        this.userId     = userId;
        this.eventId    = eventId;
        this.userName   = userName;
        this.eventTitle = eventTitle;
        this.rating     = validateRating(rating);
        this.comment    = comment;
        this.createdAt  = LocalDateTime.now();
    }

    // ── PRIVATE VALIDATION (Abstraction) ─────────
    private int validateRating(int rating) {
        if (rating < 1) return 1;
        if (rating > 5) return 5;
        return rating;
    }

    public String getStars() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }

    public Long getEventId()                    { return eventId; }
    public void setEventId(Long eventId)        { this.eventId = eventId; }

    public String getUserName()                 { return userName; }
    public void setUserName(String n)           { this.userName = n; }

    public String getEventTitle()               { return eventTitle; }
    public void setEventTitle(String t)         { this.eventTitle = t; }

    public int getRating()                      { return rating; }
    public void setRating(int rating)           { this.rating = validateRating(rating); }

    public String getComment()                  { return comment; }
    public void setComment(String comment)      { this.comment = comment; }

    public boolean isVerified()                 { return isVerified; }
    public void setVerified(boolean verified)   { this.isVerified = verified; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }

    // ── POLYMORPHISM ─────────────────────────────
    public String display() {
        return getStars() + " | " + userName
             + " reviewed " + eventTitle + ": " + comment;
    }
}
