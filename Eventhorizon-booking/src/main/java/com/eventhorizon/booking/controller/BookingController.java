package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.Booking;
import com.eventhorizon.booking.model.Event;
import com.eventhorizon.booking.model.Ticket;
import com.eventhorizon.booking.model.User;
import com.eventhorizon.booking.repository.BookingRepository;
import com.eventhorizon.booking.repository.EventRepository;
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
// BOOKINGCONTROLLER.JAVA — Member 03
// CREATE  → /bookings/create  (POST)
// READ    → /bookings         (GET)
// UPDATE  → /bookings/approve (POST)
// DELETE  → /bookings/cancel  (POST)
// =============================================

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // ── READ — View my bookings ──────────────────
    @GetMapping
    public String viewMyBookings(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        List<Booking> bookings = bookingRepository.findByUserId(loggedInUser.getId());
        model.addAttribute("bookings", bookings);
        return "bookings"; // → templates/bookings.html
    }

    // ── READ — View all bookings (admin only) ────
    @GetMapping("/all")
    public String viewAllBookings(HttpSession session, Model model) {
        List<Booking> bookings = bookingRepository.findAll();
        List<Booking> pending  = bookingRepository.findByStatus("pending");
        model.addAttribute("bookings", bookings);
        model.addAttribute("pendingBookings", pending);
        return "admin"; // → templates/admin.html
    }

    // ── CREATE — Book tickets for an event ───────
    @PostMapping("/create")
    public String createBooking(@RequestParam Long eventId,
                                @RequestParam int quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        Event event = eventOpt.get();

        // Check availability
        if (event.getAvailableTickets() < quantity) {
            redirectAttributes.addFlashAttribute("error",
                "Only " + event.getAvailableTickets() + " tickets available!");
            return "redirect:/events";
        }

        // Create booking
        Booking booking = new Booking(
            loggedInUser.getId(),
            eventId,
            event.getTitle(),
            quantity
        );
        bookingRepository.save(booking);

        // Reduce available tickets
        event.setAvailableTickets(event.getAvailableTickets() - quantity);
        eventRepository.save(event);

        redirectAttributes.addFlashAttribute("success",
            "Booking submitted! Awaiting admin approval.");
        return "redirect:/bookings";
    }

    // ── UPDATE — Admin approves booking ──────────
    @PostMapping("/approve")
    public String approveBooking(@RequestParam Long id,
                                 RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found!");
            return "redirect:/bookings/all";
        }

        Booking booking = bookingOpt.get();
        booking.approve(); // Uses the approve() method in Booking.java
        bookingRepository.save(booking);

        // Auto-generate tickets when approved
        for (int i = 0; i < booking.getQuantity(); i++) {
            Ticket ticket = new Ticket(
                booking.getId(),
                booking.getUserId(),
                "User #" + booking.getUserId(),
                booking.getEventTitle(),
                "Check event page"
            );
            ticketRepository.save(ticket);
        }

        redirectAttributes.addFlashAttribute("success",
            "Booking approved! Tickets generated.");
        return "redirect:/bookings/all";
    }

    // ── UPDATE — Admin rejects booking ───────────
    @PostMapping("/reject")
    public String rejectBooking(@RequestParam Long id,
                                RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found!");
            return "redirect:/bookings/all";
        }

        Booking booking = bookingOpt.get();
        booking.reject();
        bookingRepository.save(booking);

        // Restore tickets back to event
        Optional<Event> eventOpt = eventRepository.findById(booking.getEventId());
        eventOpt.ifPresent(event -> {
            event.setAvailableTickets(event.getAvailableTickets() + booking.getQuantity());
            eventRepository.save(event);
        });

        redirectAttributes.addFlashAttribute("success", "Booking rejected.");
        return "redirect:/bookings/all";
    }

    // ── DELETE — User cancels their own booking ──
    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found!");
            return "redirect:/bookings";
        }

        Booking booking = bookingOpt.get();
        booking.cancel();
        bookingRepository.save(booking);

        // Restore tickets to event
        Optional<Event> eventOpt = eventRepository.findById(booking.getEventId());
        eventOpt.ifPresent(event -> {
            event.setAvailableTickets(event.getAvailableTickets() + booking.getQuantity());
            eventRepository.save(event);
        });

        redirectAttributes.addFlashAttribute("success", "Booking cancelled.");
        return "redirect:/bookings";
    }
}
