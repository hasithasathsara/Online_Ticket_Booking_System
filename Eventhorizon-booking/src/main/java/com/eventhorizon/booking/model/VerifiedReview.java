package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// VERIFIEDREVIEW.JAVA — Member 06
// OOP Concept: INHERITANCE + POLYMORPHISM
// VerifiedReview EXTENDS Review
// Adds: verificationBadge, verifiedBy
// =============================================

@Entity
@Table(name = "verified_reviews")
public class VerifiedReview extends Review {

    @Column
    private String verifiedBy = "system";

    @Column
    private String verificationNote;

    @Column
    private boolean showBlueTick = true;

    // ── CONSTRUCTORS ─────────────────────────────
    public VerifiedReview() {
        super();
        setVerified(true);
    }

    public VerifiedReview(Long userId, Long eventId, String userName,
                          String eventTitle, int rating, String comment,
                          String verifiedBy) {
        super(userId, eventId, userName, eventTitle, rating, comment);
        this.verifiedBy  = verifiedBy;
        this.showBlueTick = true;
        setVerified(true);
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public String getVerifiedBy()               { return verifiedBy; }
    public void setVerifiedBy(String v)         { this.verifiedBy = v; }

    public String getVerificationNote()         { return verificationNote; }
    public void setVerificationNote(String n)   { this.verificationNote = n; }

    public boolean isShowBlueTick()             { return showBlueTick; }
    public void setShowBlueTick(boolean s)      { this.showBlueTick = s; }

    // ── POLYMORPHISM — shows blue tick for verified ──
    @Override
    public String display() {
        String tick = showBlueTick ? " ✅ Verified" : "";
        return getStars() + tick + " | " + getUserName()
             + " reviewed " + getEventTitle() + ": " + getComment();
    }
}
