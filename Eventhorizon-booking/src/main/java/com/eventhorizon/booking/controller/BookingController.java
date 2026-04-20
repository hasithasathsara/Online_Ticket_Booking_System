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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

// Handles all web requests related to bookings
@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Folder location to save uploaded bank slips
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // Show the user's bookings
    @GetMapping
    public String viewMyBookings(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        List<Booking> bookings = bookingRepository.findByUserId(loggedInUser.getId());
        model.addAttribute("bookings", bookings);
        return "bookings";
    }

    // STEP 1: Redirect user to the new checkout page to see the total price
    @PostMapping("/checkout")
    public String proceedToCheckout(@RequestParam Long eventId,
                                    @RequestParam int quantity,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        Event event = eventOpt.get();

        if (event.getAvailableTickets() < quantity) {
            redirectAttributes.addFlashAttribute("error", "Only " + event.getAvailableTickets() + " tickets available!");
            return "redirect:/events";
        }

        // Fix: Use BigDecimal multiply() for exact money calculation
        BigDecimal totalPrice = event.getPrice().multiply(BigDecimal.valueOf(quantity));

        model.addAttribute("event", event);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", totalPrice);

        return "checkout";
    }

    // STEP 2: Save the booking and upload the payment slip image
    @PostMapping("/create")
    public String createBooking(@RequestParam Long eventId,
                                @RequestParam int quantity,
                                @RequestParam("slipImage") MultipartFile slipImage,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/users/login";

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) return "redirect:/events";

        Event event = eventOpt.get();
        String slipFilePath = null;

        // Process the uploaded slip image
        if (!slipImage.isEmpty()) {
            try {
                // Add timestamp to prevent replacing old files with the same name
                String originalFileName = StringUtils.cleanPath(slipImage.getOriginalFilename());
                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath); // Create folder if it doesn't exist
                }

                // Copy the image to the folder
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(slipImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save this path to the database
                slipFilePath = "/uploads/" + uniqueFileName;

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Could not upload the payment slip.");
                return "redirect:/events";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment slip is required!");
            return "redirect:/events";
        }

        // Create the booking with the image path
        Booking booking = new Booking(
                loggedInUser.getId(),
                eventId,
                event.getTitle(),
                quantity,
                slipFilePath
        );
        bookingRepository.save(booking);

        // Reduce available event tickets
        event.setAvailableTickets(event.getAvailableTickets() - quantity);
        eventRepository.save(event);

        redirectAttributes.addFlashAttribute("success", "Payment uploaded! Awaiting admin approval.");
        return "redirect:/bookings";
    }

    // Admin approves the booking and generates actual tickets
    @PostMapping("/approve")
    public String approveBooking(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found!");
            return "redirect:/admin";
        }

        Booking booking = bookingOpt.get();
        booking.approve();
        bookingRepository.save(booking);

        // Create actual tickets for the user
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

        redirectAttributes.addFlashAttribute("success", "Payment verified and Booking approved!");
        return "redirect:/admin";
    }

    // Admin rejects the booking and restores ticket count
    @PostMapping("/reject")
    public String rejectBooking(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            return "redirect:/admin";
        }

        Booking booking = bookingOpt.get();
        booking.reject();
        bookingRepository.save(booking);

        // Give tickets back to the event
        Optional<Event> eventOpt = eventRepository.findById(booking.getEventId());
        eventOpt.ifPresent(event -> {
            event.setAvailableTickets(event.getAvailableTickets() + booking.getQuantity());
            eventRepository.save(event);
        });

        redirectAttributes.addFlashAttribute("success", "Booking rejected. Invalid payment.");
        return "redirect:/admin";
    }

    // User cancels their own booking manually
    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.cancel();
            bookingRepository.save(booking);

            // Give tickets back to the event
            Optional<Event> eventOpt = eventRepository.findById(booking.getEventId());
            eventOpt.ifPresent(event -> {
                event.setAvailableTickets(event.getAvailableTickets() + booking.getQuantity());
                eventRepository.save(event);
            });
            redirectAttributes.addFlashAttribute("success", "Booking cancelled.");
        }
        return "redirect:/bookings";
    }
}