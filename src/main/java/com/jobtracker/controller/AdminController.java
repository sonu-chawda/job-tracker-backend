package com.jobtracker.controller;

import com.jobtracker.dto.Dtos.UserSummary;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.model.User;
import com.jobtracker.repository.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserRepository userRepo;
    private final JobApplicationRepository appRepo;

    private UserSummary map(User u) { return new UserSummary(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.isActive()); }

    @GetMapping("/users")
    public List<UserSummary> users() { return userRepo.findAll().stream().map(this::map).toList(); }

    @PatchMapping("/users/{id}/toggle-active")
    public UserSummary toggle(@PathVariable Long id) {
        User u = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setActive(!u.isActive());
        return map(userRepo.save(u));
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() { return Map.of("totalUsers", userRepo.count(), "totalApplications", appRepo.count()); }
}
