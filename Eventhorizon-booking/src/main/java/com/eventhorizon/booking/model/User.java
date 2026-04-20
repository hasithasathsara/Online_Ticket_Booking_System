package com.eventhorizon.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Base class for all users in the system
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Contact number of the user
    @Column
    private String phoneNumber;

    // Defines the role (e.g., regular)
    @Column
    private String userType = "regular";

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor for JPA
    public User() {}

    // Constructor to create a new user
    public User(String name, String email, String password) {
        this.name      = name;
        this.email     = email;
        this.password  = password;
        this.userType  = "regular";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUserType() { return userType; }
    public void setUserType(String t) { this.userType = t; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    // Returns user summary
    public String getDetails() {
        return "User: " + name + " | Email: " + email + " | Type: " + userType;
    }

    public String getDashboardTitle() {
        return "My Dashboard";
    }
}