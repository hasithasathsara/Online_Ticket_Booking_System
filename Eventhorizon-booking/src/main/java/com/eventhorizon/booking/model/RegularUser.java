package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// REGULARUSER.JAVA — Member 01
// OOP Concept: INHERITANCE + POLYMORPHISM
// RegularUser EXTENDS User
// Gets all fields and methods from User
// AND adds its own extra field: membershipType
// =============================================

@Entity
@Table(name = "regular_users")
public class RegularUser extends User {

    // ── EXTRA FIELD (not in parent class) ───────
    @Column
    private String membershipType = "basic"; // basic or premium

    @Column
    private int totalBookings = 0;

    // ── CONSTRUCTORS ─────────────────────────────
    public RegularUser() {
        super();
        setUserType("regular");
    }

    public RegularUser(String name, String email, String password) {
        super(name, email, password);
        this.membershipType = "basic";
        setUserType("regular");
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public String getMembershipType()           { return membershipType; }
    public void setMembershipType(String type)  { this.membershipType = type; }

    public int getTotalBookings()               { return totalBookings; }
    public void setTotalBookings(int total)     { this.totalBookings = total; }

    // ── POLYMORPHISM — same method, different behaviour ──
    // This OVERRIDES the getDetails() method in User.java
    @Override
    public String getDetails() {
        return "Regular User: " + getName()
             + " | Email: " + getEmail()
             + " | Membership: " + membershipType
             + " | Bookings: " + totalBookings;
    }

    @Override
    public String getDashboardTitle() {
        return "My Bookings & Tickets";
    }
}
