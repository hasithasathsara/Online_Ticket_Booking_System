package com.eventhorizon.booking.controller;

import com.eventhorizon.booking.model.Review;
import com.eventhorizon.booking.model.User;
import com.eventhorizon.booking.repository.EventRepository;
import com.eventhorizon.booking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public String viewAllReviews(Model model) {
        List<Review> reviews = reviewRepository.findAll();
        model.addAttribute("reviews", reviews);
        model.addAttribute("events", eventRepository.findAll());
        return "reviews";
    }

    @GetMapping("/event/{eventId}")
    public String viewEventReviews(@PathVariable Long eventId, Model model) {
        List<Review> reviews = reviewRepository.findByEventId(eventId);
        model.addAttribute("reviews", reviews);
        eventRepository.findById(eventId).ifPresent(e -> model.addAttribute("event", e));
        return "reviews";
    }

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long eventId,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // Block Admins from submitting reviews
        if (session.getAttribute("isAdmin") != null) {
            redirectAttributes.addFlashAttribute("error", "Admins cannot submit reviews.");
            return "redirect:/reviews";
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        String eventTitle = eventRepository.findById(eventId)
                .map(e -> e.getTitle()).orElse("Unknown Event");

        Review review = new Review(
                loggedInUser.getId(),
                eventId,
                loggedInUser.getName(),
                eventTitle,
                rating,
                comment
        );
        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("success", "Review submitted! Thank you.");
        return "redirect:/reviews";
    }

    @PostMapping("/edit")
    public String editReview(@RequestParam Long id,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Review not found!");
            return "redirect:/reviews";
        }

        Review review = reviewOpt.get();

        // Only the exact owner can edit their review
        if (!review.getUserId().equals(loggedInUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You can only edit your own reviews!");
            return "redirect:/reviews";
        }

        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("success", "Review updated!");
        return "redirect:/reviews";
    }

    @PostMapping("/delete")
    public String deleteReview(@RequestParam Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        boolean isAdmin = session.getAttribute("isAdmin") != null;
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Must be logged in as either Admin or User
        if (!isAdmin && loggedInUser == null) {
            return "redirect:/users/login";
        }

        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Review not found!");
            return "redirect:/reviews";
        }

        Review review = reviewOpt.get();

        // Allow deletion IF the user is an Admin OR the user is the owner of the review
        if (isAdmin || (loggedInUser != null && review.getUserId().equals(loggedInUser.getId()))) {
            reviewRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Review deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Permission denied! You cannot delete this.");
        }

        return "redirect:/reviews";
    }
}