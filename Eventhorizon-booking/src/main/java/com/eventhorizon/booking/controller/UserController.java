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

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new RegularUser());
        return "auth";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already registered!");
            return "redirect:/users/register";
        }
        RegularUser newUser = new RegularUser(name, email, password);
        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("success", "Account created! Please login.");
        return "redirect:/users/login";
    }

    // UPDATED: Dynamic Login URL passed to view
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginAction", "/users/login");
        return "auth";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password!");
            return "redirect:/users/login";
        }

        session.setAttribute("loggedInUser", userOpt.get());
        session.setAttribute("userId", userOpt.get().getId());
        session.setAttribute("userName", userOpt.get().getName());

        redirectAttributes.addFlashAttribute("success", "Welcome back, " + userOpt.get().getName() + "!");
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        Optional<User> userOpt = userRepository.findById(loggedInUser.getId());
        userOpt.ifPresent(u -> model.addAttribute("user", u));
        return "profile";
    }

    @GetMapping("/all")
    public String listAllUsers(HttpSession session, Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/update")
    public String showUpdateForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/users/profile";
        }

        User user = userOpt.get();
        user.setName(name);
        user.setEmail(email);
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }
        userRepository.save(user);

        session.setAttribute("loggedInUser", user);
        session.setAttribute("userName", user.getName());

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/users/profile";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Account deleted successfully.");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}