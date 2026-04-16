package com.eventhorizon.booking.repository;

import com.eventhorizon.booking.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEventId(Long eventId);

    // Aluthin add kala - User ge ID eken reviews ganna
    List<Review> findByUserId(Long userId);
}