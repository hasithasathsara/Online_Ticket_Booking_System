package com.eventhorizon.booking.repository;

import com.eventhorizon.booking.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmail(String email);
    List<AdminUser> findByAdminLevel(String adminLevel);
    boolean existsByEmail(String email);
}
