package com.jobtracker.service;

import com.jobtracker.dto.Dtos.*;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.model.*;
import com.jobtracker.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ApplicationService {
    private final JobApplicationRepository appRepo;
    private final StatusHistoryRepository historyRepo;
    private final UserRepository userRepo;

    private User user(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private JobApplication owned(Long id, String email) {
        return appRepo.findByIdAndUserId(id, user(email).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    private void copy(ApplicationRequest r, JobApplication a) {
        a.setCompany(r.company()); a.setJobTitle(r.jobTitle()); a.setJobUrl(r.jobUrl());
        a.setLocation(r.location()); a.setSalary(r.salary()); a.setAppliedDate(r.appliedDate());
        a.setFollowUpDate(r.followUpDate()); a.setNotes(r.notes()); a.setJobDescription(r.jobDescription());
    }

    @Transactional
    public ApplicationResponse create(String email, ApplicationRequest r) {
        JobApplication a = new JobApplication();
        a.setUser(user(email));
        copy(r, a);
        a.setStatus(r.status() != null ? r.status() : ApplicationStatus.WISHLIST);
        a = appRepo.save(a);
        saveHistory(a, null, a.getStatus());
        return ApplicationResponse.from(a);
    }

    public PageResponse<ApplicationResponse> list(String email, ApplicationStatus status, int page, int size, String sortBy, String dir) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), sort);
        Long uid = user(email).getId();
        Page<JobApplication> p = status == null ? appRepo.findByUserId(uid, pageable)
                : appRepo.findByUserIdAndStatus(uid, status, pageable);
        return new PageResponse<>(p.getContent().stream().map(ApplicationResponse::from).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    public ApplicationResponse get(Long id, String email) { return ApplicationResponse.from(owned(id, email)); }

    @Transactional
    public ApplicationResponse update(Long id, String email, ApplicationRequest r) {
        JobApplication a = owned(id, email);
        copy(r, a);
        if (r.status() != null && r.status() != a.getStatus()) {
            saveHistory(a, a.getStatus(), r.status());
            a.setStatus(r.status());
        }
        return ApplicationResponse.from(appRepo.save(a));
    }

    // Status change + history: dono ek transaction mein, ya dono hon ya koi nahi
    @Transactional
    public ApplicationResponse changeStatus(Long id, String email, ApplicationStatus newStatus) {
        JobApplication a = owned(id, email);
        if (a.getStatus() != newStatus) {
            saveHistory(a, a.getStatus(), newStatus);
            a.setStatus(newStatus);
            if (newStatus == ApplicationStatus.APPLIED && a.getAppliedDate() == null) a.setAppliedDate(java.time.LocalDate.now());
        }
        return ApplicationResponse.from(appRepo.save(a));
    }

    @Transactional
    public void delete(Long id, String email) {
        JobApplication a = owned(id, email);
        historyRepo.deleteByApplicationId(a.getId());
        appRepo.delete(a);
    }

    public List<HistoryResponse> history(Long id, String email) {
        owned(id, email);
        return historyRepo.findByApplicationIdOrderByChangedAtDesc(id).stream()
                .map(h -> new HistoryResponse(h.getOldStatus(), h.getNewStatus(), h.getChangedAt())).toList();
    }

    public Map<String, Object> stats(String email) {
        Long uid = user(email).getId();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ApplicationStatus s : ApplicationStatus.values()) byStatus.put(s.name(), appRepo.countByUserIdAndStatus(uid, s));
        return Map.of("total", appRepo.countByUserId(uid), "byStatus", byStatus);
    }

    private void saveHistory(JobApplication a, ApplicationStatus oldS, ApplicationStatus newS) {
        StatusHistory h = new StatusHistory();
        h.setApplication(a); h.setOldStatus(oldS); h.setNewStatus(newS);
        historyRepo.save(h);
    }
}
