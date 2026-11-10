package com.jobtracker.repository;

import com.jobtracker.model.*;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Page<JobApplication> findByUserId(Long userId, Pageable pageable);
    Page<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status, Pageable pageable);
    // Ownership check: user sirf apni application dekh sake
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
    long countByUserIdAndStatus(Long userId, ApplicationStatus status);
    long countByUserId(Long userId);
}
