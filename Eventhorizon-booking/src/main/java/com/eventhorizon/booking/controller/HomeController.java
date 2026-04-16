package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.repository.EventRepository;
import com.eventhorizon.booking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// =============================================
// HOMECONTROLLER.JAVA — Shared
// Serves the main home page
// =============================================

@Controller
public class HomeController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Featured events 3k witarak pennamu (UI eka lassanata thiyaganna)
        model.addAttribute("featuredEvents", eventRepository.findAll()
                .stream().limit(3).toList());

        model.addAttribute("totalEvents", eventRepository.count());

        // LIMIT EKA AYIN KALA: Okkoma reviews ganna
        model.addAttribute("recentReviews", reviewRepository.findAll());

        return "index"; // → templates/index.html
    }
}