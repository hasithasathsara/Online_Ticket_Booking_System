package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// =============================================
// USER.JAVA — Member 01
// OOP Concept: ENCAPSULATION
// All fields are PRIVATE — only accessible
// through public getters and setters
// =============================================

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    // ── PRIVATE FIELDS (Encapsulation) ──────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String userType = "regular";

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── CONSTRUCTORS ────────────────────────────
    public User() {}

    public User(String name, String email, String password) {
        this.name      = name;
        this.email     = email;
        this.password  = password;
        this.userType  = "regular";
        this.createdAt = LocalDateTime.now();
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getPassword()            { return password; }
    public void setPassword(String p)      { this.password = p; }

    public String getUserType()            { return userType; }
    public void setUserType(String t)      { this.userType = t; }

    public LocalDateTime getCreatedAt()    { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    // ── METHOD (overridden in child classes = Polymorphism) ──
    public String getDetails() {
        return "User: " + name + " | Email: " + email + " | Type: " + userType;
    }

    public String getDashboardTitle() {
        return "My Dashboard";
    }
}
