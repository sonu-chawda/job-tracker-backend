package com.jobtracker.repository;

import com.jobtracker.model.StatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByApplicationIdOrderByChangedAtDesc(Long applicationId);
    void deleteByApplicationId(Long applicationId);
}
