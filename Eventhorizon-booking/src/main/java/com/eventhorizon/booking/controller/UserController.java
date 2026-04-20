package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.User;
import com.eventhorizon.booking.model.RegularUser;
import com.eventhorizon.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

// Controller to handle user registration, login, and admin-led updates
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Show the user registration page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new RegularUser());
        return "auth";
    }

    // Process new user registration
    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String phoneNumber,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {

        // Check if both passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return "redirect:/users/login#register";
        }

        // Check if the email is already registered in the database
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "This email is already registered!");
            return "redirect:/users/login#register";
        }

        // Create new user and save phone number
        RegularUser newUser = new RegularUser(name, email, password);
        newUser.setPhoneNumber(phoneNumber);
        userRepository.save(newUser);

        redirectAttributes.addFlashAttribute("success", "Registration successful! You can now login.");
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginAction", "/users/login");
        return "auth";
    }

    // Handle user login and session management
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // Validate email and password
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password!");
            return "redirect:/users/login";
        }

        // Prevent admins from logging in through the user login page
        if ("admin".equals(userOpt.get().getUserType())) {
            redirectAttributes.addFlashAttribute("error", "Admins must use the Admin Portal.");
            return "redirect:/users/login";
        }

        // Set session variables for the logged-in user
        session.setAttribute("loggedInUser", userOpt.get());
        session.setAttribute("userId", userOpt.get().getId());
        session.setAttribute("userName", userOpt.get().getName());

        redirectAttributes.addFlashAttribute("success", "Welcome back!");
        return "redirect:/";
    }

    // View personal profile
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        model.addAttribute("user", userRepository.findById(loggedInUser.getId()).orElse(null));
        return "profile";
    }

    // Self-update profile by the user
    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(name);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            userRepository.save(user);
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("success", "Profile updated.");
        }
        return "redirect:/users/profile";
    }

    // Logout and invalidate session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ==========================================
    // ADMIN FEATURES: MANAGE OTHER USERS
    // ==========================================

    // Admin opens the edit-user form
    @GetMapping("/admin-edit/{id}")
    public String showAdminEditUserForm(@PathVariable Long id, Model model, HttpSession session) {
        // Security check: only admins can access this
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "edit-user";
        }
        return "redirect:/admin";
    }

    // Admin saves the updated user details (Fix: Added phoneNumber)
    @PostMapping("/admin-update")
    public String updateByAdmin(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String phoneNumber, // Critical fix here
                                RedirectAttributes ra) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(name);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber); // Saving phone number correctly
            userRepository.save(user);
            ra.addFlashAttribute("success", "User account updated successfully!");
        }
        return "redirect:/admin";
    }

    // Admin deletes a user account
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User account deleted.");
        return "redirect:/admin";
    }
}