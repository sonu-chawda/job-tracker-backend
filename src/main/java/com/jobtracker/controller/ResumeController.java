package com.jobtracker.controller;

import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.model.Resume;
import com.jobtracker.repository.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/resume") @RequiredArgsConstructor
public class ResumeController {
    private final ResumeRepository resumeRepo;
    private final UserRepository userRepo;

    public record ResumeRequest(@NotNull String content) {}

    private Long uid(Authentication a) {
        return userRepo.findByEmail(a.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found")).getId();
    }

    @GetMapping
    public Map<String, String> get(Authentication a) {
        return Map.of("content", resumeRepo.findByUserId(uid(a)).map(Resume::getContent).orElse(""));
    }

    @PutMapping
    public Map<String, String> save(@RequestBody ResumeRequest r, Authentication a) {
        Long id = uid(a);
        Resume res = resumeRepo.findByUserId(id).orElseGet(() -> { Resume n = new Resume(); n.setUserId(id); return n; });
        res.setContent(r.content());
        res.setUpdatedAt(LocalDateTime.now());
        resumeRepo.save(res);
        return Map.of("content", res.getContent());
    }
}
