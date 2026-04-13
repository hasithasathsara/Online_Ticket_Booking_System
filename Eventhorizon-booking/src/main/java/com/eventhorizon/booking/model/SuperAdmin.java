package com.eventhorizon.booking.model;

import jakarta.persistence.*;

// =============================================
// SUPERADMIN.JAVA — Member 05
// OOP Concept: INHERITANCE (2 levels!)
// SuperAdmin → AdminUser → User
// This is a chain of 3 classes!
// =============================================

@Entity
@Table(name = "super_admins")
public class SuperAdmin extends AdminUser {

    @Column
    private boolean canDeleteSystem = true;

    @Column
    private boolean canManageAdmins = true;

    @Column
    private String systemAccessLevel = "full";

    // ── CONSTRUCTORS ─────────────────────────────
    public SuperAdmin() {
        super();
        setAdminLevel("super");
        setPermissions("all");
    }

    public SuperAdmin(String name, String email, String password) {
        super(name, email, password, "super", "all");
        this.canDeleteSystem  = true;
        this.canManageAdmins  = true;
        this.systemAccessLevel = "full";
    }

    // ── SUPER ADMIN ONLY METHODS ─────────────────
    public void resetSystem() {
        System.out.println("System reset by Super Admin: " + getName());
    }

    public boolean canCreateNewAdmins() {
        return canManageAdmins;
    }

    // ── GETTERS AND SETTERS ──────────────────────
    public boolean isCanDeleteSystem()          { return canDeleteSystem; }
    public void setCanDeleteSystem(boolean c)   { this.canDeleteSystem = c; }

    public boolean isCanManageAdmins()          { return canManageAdmins; }
    public void setCanManageAdmins(boolean c)   { this.canManageAdmins = c; }

    public String getSystemAccessLevel()        { return systemAccessLevel; }
    public void setSystemAccessLevel(String s)  { this.systemAccessLevel = s; }

    // ── POLYMORPHISM ─────────────────────────────
    @Override
    public String getDetails() {
        return "SUPER ADMIN: " + getName()
             + " | Access: " + systemAccessLevel
             + " | Can manage admins: " + canManageAdmins
             + " | Can delete system: " + canDeleteSystem;
    }

    @Override
    public String getDashboardTitle() {
        return "Super Admin Control Panel";
    }
}
