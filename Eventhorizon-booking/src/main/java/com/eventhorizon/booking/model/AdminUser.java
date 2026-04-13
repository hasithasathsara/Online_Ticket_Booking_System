package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// ADMINUSER.JAVA — Member 05
// OOP Concept: INHERITANCE
// AdminUser EXTENDS User (from Member 01!)
// This shows cross-member inheritance!
// =============================================

@Entity
@Table(name = "admins")
@Inheritance(strategy = InheritanceType.JOINED)
public class AdminUser extends User {

    @Column
    private String adminLevel = "moderator"; // moderator or super

    @Column
    private String permissions = "events,bookings";

    @Column
    private boolean isActive = true;

    // ── CONSTRUCTORS ─────────────────────────────
    public AdminUser() {
        super();
        setUserType("admin");
    }

    public AdminUser(String name, String email, String password,
                     String adminLevel, String permissions) {
        super(name, email, password);
        this.adminLevel  = adminLevel;
        this.permissions = permissions;
        this.isActive    = true;
        setUserType("admin");
    }

    // ── ADMIN-SPECIFIC METHODS (Abstraction) ─────
    public boolean hasPermission(String permission) {
        if (permissions == null) return false;
        return permissions.contains(permission) || "all".equals(permissions);
    }

    public boolean canManageEvents()   { return hasPermission("events"); }
    public boolean canManageUsers()    { return hasPermission("users"); }
    public boolean canManageBookings() { return hasPermission("bookings"); }

    // ── GETTERS AND SETTERS ──────────────────────
    public String getAdminLevel()               { return adminLevel; }
    public void setAdminLevel(String level)     { this.adminLevel = level; }

    public String getPermissions()              { return permissions; }
    public void setPermissions(String p)        { this.permissions = p; }

    public boolean isActive()                   { return isActive; }
    public void setActive(boolean active)       { this.isActive = active; }

    // ── POLYMORPHISM ─────────────────────────────
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
