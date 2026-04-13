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

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

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
        return "events";
    }

    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) return "redirect:/events";
        model.addAttribute("event", eventOpt.get());
        return "events";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        model.addAttribute("event", new PhysicalEvent());
        return "admin";
    }

    @PostMapping("/create")
    public String createEvent(@RequestParam String title,
                              @RequestParam String category,
                              @RequestParam String venue,
                              @RequestParam String description,
                              @RequestParam String eventDate,
                              @RequestParam int totalTickets,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) Integer venueCapacity,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";

        PhysicalEvent event = new PhysicalEvent();
        event.setTitle(title);
        event.setCategory(category);
        event.setVenue(venue);
        event.setDescription(description);
        event.setEventDate(LocalDateTime.parse(eventDate.replace("T", "T").length() == 16
                        ? eventDate + ":00" : eventDate,
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        event.setTotalTickets(totalTickets);
        event.setAvailableTickets(totalTickets); // මුලින් හදද්දී Total = Available
        event.setPrice(price);
        if (venueCapacity != null) event.setVenueCapacity(venueCapacity);

        eventRepository.save(event);
        redirectAttributes.addFlashAttribute("success", "Event created successfully!");
        return "redirect:/admin";
    }

    @GetMapping("/admin-edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) return "redirect:/admin";

        model.addAttribute("event", eventOpt.get());
        return "edit-event";
    }

    // 🌟 මෙතනින් Automatic Math එක අයින් කරලා, අලුත් availableTickets parameter එක දැම්මා 🌟
    @PostMapping("/admin-update")
    public String updateEvent(@RequestParam Long id,
                              @RequestParam String title,
                              @RequestParam String category,
                              @RequestParam String venue,
                              @RequestParam String description,
                              @RequestParam int totalTickets,
                              @RequestParam int availableTickets,
                              @RequestParam BigDecimal price,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";

        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found!");
            return "redirect:/admin";
        }

        Event event = eventOpt.get();
        event.setTitle(title);
        event.setCategory(category);
        event.setVenue(venue);
        event.setDescription(description);

        // Admin දෙන ගාණ කෙලින්ම Database එකට දානවා!
        event.setTotalTickets(totalTickets);
        event.setAvailableTickets(availableTickets);

        event.setPrice(price);
        eventRepository.save(event);

        redirectAttributes.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteEvent(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        eventRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Event deleted successfully!");
        return "redirect:/admin";
    }
}