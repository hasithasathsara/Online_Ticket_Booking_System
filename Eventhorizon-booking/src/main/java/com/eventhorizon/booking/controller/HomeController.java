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
        model.addAttribute("featuredEvents", eventRepository.findAll()
            .stream().limit(3).toList());
        model.addAttribute("totalEvents", eventRepository.count());
        model.addAttribute("recentReviews", reviewRepository.findAll()
            .stream().limit(6).toList());
        return "index"; // → templates/index.html
    }
}
