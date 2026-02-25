package com.lab2.authsystem.repository;

import com.lab2.authsystem.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUserId(Long userId);
    List<Registration> findByEventId(Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}