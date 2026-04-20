package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// AdminUser inherits from User class (OOP: Inheritance)
@Entity
@Table(name = "admins")
@Inheritance(strategy = InheritanceType.JOINED)
public class AdminUser extends User {

    @Column
    private String adminLevel = "moderator"; // Can be 'moderator' or 'super'

    @Column
    private String permissions = "events,bookings";

    @Column
    private boolean isActive = true;

    // Default constructor
    public AdminUser() {
        super();
        setUserType("admin"); // Setting user type to admin
    }

    // Constructor with parameters to create a new admin
    public AdminUser(String name, String email, String password,
                     String adminLevel, String permissions) {
        super(name, email, password);
        this.adminLevel  = adminLevel;
        this.permissions = permissions;
        this.isActive    = true;
        setUserType("admin");
    }

    // Logic to check if admin has a specific permission
    public boolean hasPermission(String permission) {
        if (permissions == null) return false;
        return permissions.contains(permission) || "all".equals(permissions);
    }

    // Permission check methods
    public boolean canManageEvents()   { return hasPermission("events"); }
    public boolean canManageUsers()    { return hasPermission("users"); }
    public boolean canManageBookings() { return hasPermission("bookings"); }

    // Getters and Setters
    public String getAdminLevel()               { return adminLevel; }
    public void setAdminLevel(String level)     { this.adminLevel = level; }

    public String getPermissions()              { return permissions; }
    public void setPermissions(String p)        { this.permissions = p; }

    public boolean isActive()                   { return isActive; }
    public void setActive(boolean active)       { this.isActive = active; }

    // Overriding parent method to show admin specific details (OOP: Polymorphism)
    @Override
    public String getDetails() {
        return "Admin: " + getName()
                + " | Level: " + adminLevel
                + " | Permissions: " + permissions
                + " | Active: " + isActive;
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard";
    }
}