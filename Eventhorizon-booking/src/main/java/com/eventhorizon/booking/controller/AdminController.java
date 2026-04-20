package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.AdminUser;
import com.eventhorizon.booking.model.User;
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
import java.util.List;
import java.util.stream.Collectors;

// Controller for all Admin Panel operations
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

    // Creates the default Super Admin when the application starts
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

    // Displays the Admin Dashboard with stats and tables
    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        Object adminObj = session.getAttribute("loggedInAdmin");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (adminObj == null || isAdmin == null || !isAdmin)
            return "redirect:/admin/login";

        AdminUser admin = (AdminUser) adminObj;

        // Statistics for dashboard cards
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalEvents", eventRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("pendingCount", bookingRepository.findByStatus("pending").size());

        // Separate Regular Users from Admins for display
        List<User> regularUsers = userRepository.findAll().stream()
                .filter(u -> "regular".equals(u.getUserType()))
                .collect(Collectors.toList());

        model.addAttribute("admin", admin);
        model.addAttribute("allUsers", regularUsers); // Only regular customers
        model.addAttribute("allAdmins", adminRepository.findAll()); // Only system admins
        model.addAttribute("allEvents", eventRepository.findAll());
        model.addAttribute("allBookings", bookingRepository.findAll());
        model.addAttribute("pendingBookings", bookingRepository.findByStatus("pending"));

        return "admin";
    }

    // ── ADMIN AUTHENTICATION ───────────────────────

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

        session.setAttribute("loggedInAdmin", adminOpt.get());
        session.setAttribute("isAdmin", true);

        redirectAttributes.addFlashAttribute("success", "Welcome, Admin " + adminOpt.get().getName() + "!");
        return "redirect:/admin";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ── REGULAR USER MANAGEMENT ────────────────────

    // Opens edit page for a specific regular user
    @GetMapping("/edit-user/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, HttpSession session) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (currentAdmin == null || !"super".equals(currentAdmin.getAdminLevel())) return "redirect:/admin";

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "edit-user";
        }
        return "redirect:/admin";
    }

    // Updates a regular user's details
    @PostMapping("/user/update")
    public String updateUserByAdmin(@RequestParam Long id,
                                    @RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String phoneNumber,
                                    RedirectAttributes ra) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(name);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);
            ra.addFlashAttribute("success", "User details updated successfully!");
        }
        return "redirect:/admin";
    }

    // ── SUB-ADMIN MANAGEMENT ───────────────────────

    @PostMapping("/create")
    public String createAdmin(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String permissions,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (currentAdmin == null || !"super".equals(currentAdmin.getAdminLevel())) return "redirect:/admin/login";

        if (adminRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already registered!");
            return "redirect:/admin";
        }

        AdminUser newAdmin = new AdminUser(name, email, password, "moderator", permissions);
        adminRepository.save(newAdmin);

        redirectAttributes.addFlashAttribute("success", "New Admin account created!");
        return "redirect:/admin";
    }

    @GetMapping("/edit-subadmin/{id}")
    public String showEditAdminForm(@PathVariable Long id, Model model, HttpSession session) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (currentAdmin == null || !"super".equals(currentAdmin.getAdminLevel())) return "redirect:/admin";

        Optional<AdminUser> targetAdmin = adminRepository.findById(id);
        if (targetAdmin.isPresent()) {
            model.addAttribute("targetAdmin", targetAdmin.get());
            return "edit-admin";
        }
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateAdmin(@RequestParam Long id,
                              @RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String permissions,
                              RedirectAttributes redirectAttributes) {
        Optional<AdminUser> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            admin.setName(name);
            admin.setEmail(email);
            admin.setPermissions(permissions);
            adminRepository.save(admin);
            redirectAttributes.addFlashAttribute("success", "Admin details updated!");
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteAdmin(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        adminRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Admin removed.");
        return "redirect:/admin";
    }

    // ── PERSONAL PROFILE ───────────────────────────

    @GetMapping("/profile")
    public String showAdminProfile(HttpSession session, Model model) {
        AdminUser admin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (admin == null) return "redirect:/admin/login";

        model.addAttribute("admin", admin);
        return "admin-profile";
    }

    @PostMapping("/profile/update")
    public String updateOwnProfile(@RequestParam String name,
                                   @RequestParam(required = false) String password,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        AdminUser currentAdmin = (AdminUser) session.getAttribute("loggedInAdmin");
        if (currentAdmin == null) return "redirect:/admin/login";

        currentAdmin.setName(name);
        if (password != null && !password.isEmpty()) {
            currentAdmin.setPassword(password);
        }
        adminRepository.save(currentAdmin);

        session.setAttribute("loggedInAdmin", currentAdmin);
        redirectAttributes.addFlashAttribute("success", "Your profile updated successfully!");
        return "redirect:/admin/profile";
    }
}