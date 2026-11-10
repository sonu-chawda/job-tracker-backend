package com.jobtracker.repository;

import com.jobtracker.model.Resume;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUserId(Long userId);
}
