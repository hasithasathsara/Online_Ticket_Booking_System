package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.AdminUser;
import com.eventhorizon.booking.repository.AdminRepository;
import com.eventhorizon.booking.repository.BookingRepository;
import com.eventhorizon.booking.repository.EventRepository;
import com.eventhorizon.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @jakarta.annotation.PostConstruct
    public void initSuperAdmin() {
        try {
            if (!adminRepository.existsByEmail("admin@eventhorizon.com")) {
                AdminUser superAdmin = new AdminUser(
                        "Super Admin", "admin@eventhorizon.com", "admin123", "super", "all"
                );
                adminRepository.save(superAdmin);
                System.out.println("Super Admin created successfully!");
            }
        } catch (Exception e) {
            System.out.println("Super admin already exists!");
        }
    }

    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        Object adminObj = session.getAttribute("loggedInAdmin");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (adminObj == null || isAdmin == null || !isAdmin)
            return "redirect:/admin/login";

        AdminUser admin = (AdminUser) adminObj;

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalEvents", eventRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("pendingCount", bookingRepository.findByStatus("pending").size());

        model.addAttribute("admin", admin);
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("allEvents", eventRepository.findAll());
        model.addAttribute("allBookings", bookingRepository.findAll());
        model.addAttribute("pendingBookings", bookingRepository.findByStatus("pending"));
        model.addAttribute("allAdmins", adminRepository.findAll());

        return "admin";
    }

    @GetMapping("/login")
    public String showAdminLogin(Model model) {
        model.addAttribute("loginAction", "/admin/login");
        return "auth";
    }

    @PostMapping("/login")
    public String processAdminLogin(@RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Optional<AdminUser> adminOpt = adminRepository.findByEmail(email);

        if (adminOpt.isEmpty() || !adminOpt.get().getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid admin credentials!");
            return "redirect:/admin/login";
        }

        // Only save as Admin
        session.setAttribute("loggedInAdmin", adminOpt.get());
        session.setAttribute("isAdmin", true);

        redirectAttributes.addFlashAttribute("success", "Welcome, Admin " + adminOpt.get().getName() + "!");
        return "redirect:/admin";
    }

    @PostMapping("/create")
    public String createAdmin(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String permissions,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (currentAdmin == null || !"super".equals(currentAdmin.getAdminLevel())) {
            return "redirect:/admin/login";
        }

        if (adminRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already registered!");
            return "redirect:/admin";
        }

        AdminUser newAdmin = new AdminUser(name, email, password, "moderator", permissions);
        adminRepository.save(newAdmin);

        redirectAttributes.addFlashAttribute("success", "Admin account created!");
        return "redirect:/admin";
    }

    // SUPER ADMIN UPDATING A SUB-ADMIN
    @PostMapping("/update")
    public String updateAdmin(@RequestParam Long id,
                              @RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String permissions,
                              RedirectAttributes redirectAttributes) {
        Optional<AdminUser> adminOpt = adminRepository.findById(id);
        if (adminOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Admin not found!");
            return "redirect:/admin";
        }

        AdminUser admin = adminOpt.get();
        admin.setName(name);
        admin.setEmail(email); // Super Admin can change email
        admin.setPermissions(permissions);
        adminRepository.save(admin);

        redirectAttributes.addFlashAttribute("success", "Admin updated successfully!");
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteAdmin(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        adminRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Admin removed.");
        return "redirect:/admin";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ==========================================
    // NEW METHODS ADDED FOR PROFILE & EDITING
    // ==========================================

    // 1. Show Admin's own profile
    @GetMapping("/profile")
    public String showAdminProfile(HttpSession session, Model model) {
        AdminUser admin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (admin == null) return "redirect:/admin/login";

        model.addAttribute("admin", admin);
        return "admin-profile";
    }

    // 2. Update Admin's own profile (No Email Update Allowed Here)
    @PostMapping("/profile/update")
    public String updateOwnProfile(@RequestParam String name,
                                   @RequestParam(required = false) String password,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (currentAdmin == null) return "redirect:/admin/login";

        // Only Name and Password can be updated by the user themselves
        currentAdmin.setName(name);
        if (password != null && !password.isEmpty()) {
            currentAdmin.setPassword(password);
        }
        adminRepository.save(currentAdmin);

        session.setAttribute("loggedInAdmin", currentAdmin);
        redirectAttributes.addFlashAttribute("success", "Your profile was updated successfully!");
        return "redirect:/admin/profile";
    }

    // 3. Super Admin fetching Sub-Admin details for the Edit Form
    @GetMapping("/edit-subadmin/{id}")
    public String showEditAdminForm(@PathVariable Long id, Model model, HttpSession session) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");

        // Block if not Super Admin
        if (currentAdmin == null || !"super".equals(currentAdmin.getAdminLevel())) {
            return "redirect:/admin";
        }

        Optional<AdminUser> targetAdmin = adminRepository.findById(id);
        if (targetAdmin.isPresent()) {
            model.addAttribute("targetAdmin", targetAdmin.get());
            return "edit-admin";
        }

        return "redirect:/admin";
    }
}