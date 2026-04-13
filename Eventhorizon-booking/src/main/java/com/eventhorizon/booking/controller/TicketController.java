package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.Ticket;
import com.eventhorizon.booking.model.User;
import com.eventhorizon.booking.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

// =============================================
// TICKETCONTROLLER.JAVA — Member 04
// CREATE  → auto-generated when booking approved
// READ    → /tickets         (GET)
// UPDATE  → /tickets/use     (POST)
// DELETE  → /tickets/cancel  (POST)
// =============================================

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    // ── READ — View my tickets ───────────────────
    @GetMapping
    public String viewMyTickets(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        List<Ticket> tickets = ticketRepository.findByUserId(loggedInUser.getId());
        model.addAttribute("tickets", tickets);
        return "tickets"; // → templates/tickets.html
    }

    // ── READ — View all tickets (admin only) ─────
    @GetMapping("/all")
    public String viewAllTickets(HttpSession session, Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "admin";
    }

    // ── READ — Search ticket by code ─────────────
    @GetMapping("/search")
    public String searchTicket(@RequestParam String ticketCode,
                               Model model) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketCode(ticketCode);
        if (ticketOpt.isPresent()) {
            model.addAttribute("ticket", ticketOpt.get());
            model.addAttribute("found", true);
        } else {
            model.addAttribute("error", "Ticket not found: " + ticketCode);
            model.addAttribute("found", false);
        }
        return "tickets";
    }

    // ── UPDATE — Mark ticket as USED at entry ────
    @PostMapping("/use")
    public String markTicketAsUsed(@RequestParam Long id,
                                   RedirectAttributes redirectAttributes) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ticket not found!");
            return "redirect:/tickets/all";
        }

        Ticket ticket = ticketOpt.get();
        if (!ticket.isActive()) {
            redirectAttributes.addFlashAttribute("error",
                "Ticket is already " + ticket.getStatus() + "!");
            return "redirect:/tickets/all";
        }

        ticket.markAsUsed(); // Uses the markAsUsed() method in Ticket.java
        ticketRepository.save(ticket);

        redirectAttributes.addFlashAttribute("success",
            "Ticket " + ticket.getTicketCode() + " marked as USED!");
        return "redirect:/tickets/all";
    }

    // ── DELETE — Cancel a ticket ─────────────────
    @PostMapping("/cancel")
    public String cancelTicket(@RequestParam Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ticket not found!");
            return "redirect:/tickets";
        }

        Ticket ticket = ticketOpt.get();
        ticket.cancel(); // Uses the cancel() method in Ticket.java
        ticketRepository.save(ticket);

        redirectAttributes.addFlashAttribute("success", "Ticket cancelled.");
        return "redirect:/tickets";
    }
}
