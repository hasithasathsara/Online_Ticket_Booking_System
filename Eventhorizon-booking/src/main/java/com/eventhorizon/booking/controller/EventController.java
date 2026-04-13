package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.Event;
import com.eventhorizon.booking.model.PhysicalEvent;
import com.eventhorizon.booking.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// =============================================
// EVENTCONTROLLER.JAVA — Member 02
// CREATE  → /events/create  (POST)
// READ    → /events         (GET)
// UPDATE  → /events/update  (POST)
// DELETE  → /events/delete  (POST)
// =============================================

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // ── READ — Show all events ───────────────────
    @GetMapping
    public String listEvents(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String category,
                             Model model) {
        List<Event> events;

        if (search != null && !search.isEmpty()) {
            events = eventRepository.findByTitleContainingIgnoreCase(search);
        } else if (category != null && !category.isEmpty()) {
            events = eventRepository.findByCategory(category);
        } else {
            events = eventRepository.findAll();
        }

        model.addAttribute("events", events);
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        return "events"; // → templates/events.html
    }

    // ── READ — View single event ─────────────────
    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) return "redirect:/events";
        model.addAttribute("event", eventOpt.get());
        return "events";
    }

    // ── CREATE — Show create form (admin only) ───
    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null)
            return "redirect:/users/login";
        model.addAttribute("event", new PhysicalEvent());
        return "admin";
    }

    // ── CREATE — Process new event ───────────────
    @PostMapping("/create")
    public String createEvent(@RequestParam String title,
                              @RequestParam String category,
                              @RequestParam String venue,
                              @RequestParam String description,
                              @RequestParam String eventDate,
                              @RequestParam int totalTickets,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) Integer venueCapacity,
                              RedirectAttributes redirectAttributes) {

        PhysicalEvent event = new PhysicalEvent();
        event.setTitle(title);
        event.setCategory(category);
        event.setVenue(venue);
        event.setDescription(description);
        event.setEventDate(LocalDateTime.parse(eventDate.replace("T", "T").length() == 16
                ? eventDate + ":00" : eventDate,
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        event.setTotalTickets(totalTickets);
        event.setAvailableTickets(totalTickets);
        event.setPrice(price);
        if (venueCapacity != null) event.setVenueCapacity(venueCapacity);

        eventRepository.save(event);
        redirectAttributes.addFlashAttribute("success", "Event created successfully!");
        return "redirect:/events";
    }

    // ── UPDATE — Show update form ─────────────────
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null)
            return "redirect:/users/login";
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) return "redirect:/events";
        model.addAttribute("event", eventOpt.get());
        return "admin";
    }

    // ── UPDATE — Process update ───────────────────
    @PostMapping("/update")
    public String updateEvent(@RequestParam Long id,
                              @RequestParam String title,
                              @RequestParam String category,
                              @RequestParam String venue,
                              @RequestParam String description,
                              @RequestParam int totalTickets,
                              @RequestParam BigDecimal price,
                              RedirectAttributes redirectAttributes) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        Event event = eventOpt.get();
        event.setTitle(title);
        event.setCategory(category);
        event.setVenue(venue);
        event.setDescription(description);
        event.setTotalTickets(totalTickets);
        event.setPrice(price);
        eventRepository.save(event);

        redirectAttributes.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/events";
    }

    // ── DELETE — Delete event ─────────────────────
    @PostMapping("/delete")
    public String deleteEvent(@RequestParam Long id,
                              RedirectAttributes redirectAttributes) {
        eventRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Event deleted successfully!");
        return "redirect:/events";
    }
}
